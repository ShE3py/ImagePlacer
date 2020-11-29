package fr.she3py.iplacer.minecraft;

import static fr.she3py.iplacer.ImagePlacer.logger;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.imageio.ImageIO;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import fr.she3py.iplacer.util.Arguments;
import fr.she3py.iplacer.util.GraphicIdentifier;

public class MinecraftGraphics {
	private final List<MinecraftGraphic> graphics;
	private final MinecraftConstructionCaches constructionCaches;
	
	private MinecraftGraphics(int initialCapacity) {
		this.graphics = new ArrayList<>(initialCapacity);
		this.constructionCaches = new MinecraftConstructionCaches(initialCapacity);
	}
	
	public static MinecraftGraphics createFrom(File rsc, List<GraphicIdentifier> identifiers) throws IOException {
		MinecraftGraphics graphics = new MinecraftGraphics(identifiers.size());
		
		logger.info("Creating graphics for Minecraft");
		ZipFile zipFile = new ZipFile(rsc);
		
		for(GraphicIdentifier identifier : identifiers) {
			try {
				MinecraftGraphic graphic = graphics.createGraphicFor(identifier, zipFile);
				
				logger.info("Generated: " + graphic);
			}
			catch(Exception e) {
				logger.log(Level.SEVERE, "Generation failed for: " + identifier.getKey() + ", skipping", e);
			}
		}
		
		zipFile.close();
		graphics.constructionCaches.clear();
		
		logger.info("Generation complete - " + graphics.size() + " of " + identifiers.size() + " blocks mapped");
		return graphics;
	}
	
	private MinecraftGraphic createGraphicFor(GraphicIdentifier identifier, ZipFile zipFile) throws IOException {
		MinecraftGraphic graphic = new MinecraftGraphic(identifier, findMaterialTexture(identifier, zipFile, constructionCaches));
		
		this.graphics.add(graphic);
		return graphic;
	}
	
	private static BufferedImage findMaterialTexture(GraphicIdentifier identifier, ZipFile zipFile, MinecraftConstructionCaches caches) throws IOException {
		JsonObject model = parseBlockModel(identifier, zipFile, caches);
		String texturePath = findTexturePath(identifier, model, caches);
		
		String namespace;
		if(texturePath.contains(":")) {
			namespace = texturePath.substring(0, texturePath.indexOf(':'));
			texturePath = texturePath.substring(namespace.length() + 1);
		}
		else {
			namespace = identifier.getNamespace();
		}
		
		return readTexture(zipFile, namespace, texturePath, caches);
	}
	
	private static JsonObject parseBlockModel(GraphicIdentifier identifier, ZipFile zipFile, MinecraftConstructionCaches caches) throws IOException {
		if(caches.blockModels.containsKey(identifier))
			return caches.blockModels.get(identifier);
		
		ZipEntry blockModel = zipFile.getEntry("assets/" + identifier.getNamespace() + "/models/block/" + identifier.getKey() + ".json");
		Arguments.require(blockModel != null, UnsupportedOperationException::new, "Model not found");
		
		JsonObject result = new JsonParser().parse(new InputStreamReader(zipFile.getInputStream(blockModel))).getAsJsonObject();
		caches.blockModels.put(identifier, result);
		
		return result;
	}
	
	@NotNull
	private static String findTexturePath(GraphicIdentifier identifier, JsonObject model, MinecraftConstructionCaches caches) {
		return caches.texturePaths.computeIfAbsent(identifier, keyIn -> {
			String parent = model.get("parent").getAsString();
			if(!parent.contains(":"))
				parent = keyIn.getNamespace() + ":" + parent;
			
			JsonObject textures = model.get("textures").getAsJsonObject();
			
			switch(parent) {
				case "minecraft:block/cube_all":
				case "minecraft:block/leaves":
					return textures.get("all").getAsString();
				
				case "minecraft:block/cube_column":
					return textures.get("end").getAsString();
				
				case "minecraft:block/cube_top":
				case "minecraft:block/cube_bottom_top":
				case "minecraft:block/orientable":
				case "minecraft:block/orientable_with_bottom":
				case "minecraft:block/block":
					if(keyIn.toString().equals("minecraft:dried_kelp_block"))
						return textures.get("up").getAsString(); // custom model
					
					return textures.get("top").getAsString();
				
				case "minecraft:block/cube":
				case "minecraft:block/cube_directional":
					return textures.get("up").getAsString();
				
				case "minecraft:block/template_single_face":
					return textures.get("texture").getAsString();
				
				case "minecraft:block/template_command_block":
					return textures.get("side").getAsString();
				
				case "minecraft:block/template_glazed_terracotta":
					return textures.get("pattern").getAsString();
				
				default:
					Arguments.fail(UnsupportedOperationException::new, parent);
					return null;
			}
		});
	}
	
	private static BufferedImage readTexture(ZipFile zipFile, String namespace, String texturePath, MinecraftConstructionCaches caches) throws IOException {
		final String cacheKey = namespace + texturePath;
		if(caches.textures.containsKey(cacheKey))
			return caches.textures.get(cacheKey);
		
		ZipEntry blockTexture = zipFile.getEntry("assets/" + namespace + "/textures/" + texturePath + ".png");
		Arguments.require(blockTexture != null, "Texture not found: " + namespace + ":" + texturePath);
		
		BufferedImage texture = ImageIO.read(zipFile.getInputStream(blockTexture));
		Arguments.requireNonNull("texture", texture);
		
		JsonObject meta = readTextureMeta(zipFile, namespace, texturePath, caches);
		if(meta != null) {
			Arguments.requireEqual("size", meta.size(), 1);
			Arguments.requireNonNull("animation", meta.get("animation"));
			
			int width = texture.getWidth();
			int height = texture.getHeight();
			Arguments.require(width <= height, UnsupportedOperationException::new, "landscape orientation");
			
			return texture.getSubimage(0, 0, width, width);
		}
		
		caches.textures.put(cacheKey, texture);
		return texture;
	}
	
	@Nullable
	private static JsonObject readTextureMeta(ZipFile zipFile, String namespace, String texturePath, MinecraftConstructionCaches caches) throws IOException {
		final String cacheKey = namespace + texturePath;
		if(caches.textureMetas.containsKey(cacheKey))
			return caches.textureMetas.get(cacheKey);
		
		ZipEntry meta = zipFile.getEntry("assets/" + namespace + "/textures/" + texturePath + ".png.mcmeta");
		if(meta == null)
			return null;
		
		JsonObject result = new JsonParser().parse(new InputStreamReader(zipFile.getInputStream(meta))).getAsJsonObject();
		caches.textureMetas.put(cacheKey, result);
		
		return result;
	}
	
	public MinecraftColorMap makeColorMap() {
		return new MinecraftColorMap(graphics);
	}
	
	public boolean isEmpty() {
		return graphics.isEmpty();
	}
	
	public int size() {
		return graphics.size();
	}
}
