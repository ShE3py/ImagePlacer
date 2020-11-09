package fr.she3py.iplacer.bukkit;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.imageio.ImageIO;

import net.minecraft.server.v1_16_R2.Block;
import net.minecraft.server.v1_16_R2.BlockBase;
import net.minecraft.server.v1_16_R2.BlockMonsterEggs;
import net.minecraft.server.v1_16_R2.EnumRenderType;
import net.minecraft.server.v1_16_R2.IBlockData;
import net.minecraft.server.v1_16_R2.IRegistry;
import net.minecraft.server.v1_16_R2.MinecraftKey;
import net.minecraft.server.v1_16_R2.VoxelShape;
import net.minecraft.server.v1_16_R2.VoxelShapeCollision;
import net.minecraft.server.v1_16_R2.VoxelShapes;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import fr.she3py.iplacer.ImagePlacer;
import fr.she3py.iplacer.util.Arguments;

public class BukkitGraphics {
	private final List<BukkitGraphic> graphics;
	private final BukkitConstructionCaches constructionCaches;
	
	private BukkitGraphics(int initialCapacity) {
		this.graphics = new ArrayList<>(initialCapacity);
		this.constructionCaches = new BukkitConstructionCaches(initialCapacity);
	}
	
	public static BukkitGraphics createFrom(File rsc) throws IOException {
		ImagePlacer.logger.info("Looking for compatible materials");
		List<Material> materials = findMaterials().collect(Collectors.toList());
		
		ImagePlacer.logger.info("Found " + materials.size() + " materials");
		BukkitGraphics graphics = new BukkitGraphics(materials.size());
		
		ImagePlacer.logger.info("Creating graphics for Bukkit");
		ZipFile zipFile = new ZipFile(rsc);
		
		for(Material material : materials) {
			try {
				BukkitGraphic graphic = graphics.createGraphicFor(material, zipFile);
				
				ImagePlacer.logger.info("Generated: " + graphic);
			}
			catch(Exception e) {
				ImagePlacer.logger.log(Level.SEVERE, "Generation failed for: " + material.getKey() + ", skipping", e);
			}
		}
		
		zipFile.close();
		graphics.constructionCaches.clear();
		
		ImagePlacer.logger.info("Generation complete - " + graphics.size() + " of " + materials.size() + " blocks mapped");
		return graphics;
	}
	
	public static BukkitGraphics createFrom(String file) throws IOException {
		return createFrom(new File(ImagePlacer.plugin.getDataFolder(), file));
	}
	
	private BukkitGraphic createGraphicFor(Material material, ZipFile zipFile) throws IOException {
		BukkitGraphic graphic = new BukkitGraphic(material, findMaterialTexture(material, zipFile, constructionCaches));
		
		this.graphics.add(graphic);
		return graphic;
	}
	
	private static BufferedImage findMaterialTexture(Material material, ZipFile zipFile, BukkitConstructionCaches caches) throws IOException {
		NamespacedKey key = material.getKey();
		
		JsonObject model = parseBlockModel(key, zipFile, caches);
		String texturePath = findTexturePath(key, model, caches);
		
		String namespace;
		if(texturePath.contains(":")) {
			namespace = texturePath.substring(0, texturePath.indexOf(':'));
			texturePath = texturePath.substring(namespace.length() + 1);
		}
		else {
			namespace = key.getNamespace();
		}
		
		return readTexture(zipFile, namespace, texturePath, caches);
	}
	
	private static JsonObject parseBlockModel(NamespacedKey key, ZipFile zipFile, BukkitConstructionCaches caches) throws IOException {
		if(caches.blockModels.containsKey(key))
			return caches.blockModels.get(key);
		
		ZipEntry blockModel = zipFile.getEntry("assets/" + key.getNamespace() + "/models/block/" + key.getKey() + ".json");
		Arguments.require(blockModel != null, UnsupportedOperationException::new, "Model not found");
		
		JsonObject result = new JsonParser().parse(new InputStreamReader(zipFile.getInputStream(blockModel))).getAsJsonObject();
		caches.blockModels.put(key, result);
		
		return result;
	}
	
	@NotNull
	private static String findTexturePath(NamespacedKey key, JsonObject model, BukkitConstructionCaches caches) {
		return caches.texturePaths.computeIfAbsent(key, keyIn -> {
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
	
	private static BufferedImage readTexture(ZipFile zipFile, String namespace, String texturePath, BukkitConstructionCaches caches) throws IOException {
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
	private static JsonObject readTextureMeta(ZipFile zipFile, String namespace, String texturePath, BukkitConstructionCaches caches) throws IOException {
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
	
	public BukkitColorMap makeColorMap() {
		return new BukkitColorMap(graphics);
	}
	
	public boolean isEmpty() {
		return graphics.isEmpty();
	}
	
	public int size() {
		return graphics.size();
	}
	
	private static Stream<Material> findMaterials() {
		return Arrays.stream(Material.values())
					 .filter(Material::isSolid)
					 .filter(Material::isOccluding)
					 .map(material -> IRegistry.BLOCK.getOptional(new MinecraftKey(material.getKey().getNamespace(), material.getKey().getKey())).get())
					 .filter(BukkitGraphics::hasTexture)
					 .filter(BukkitGraphics::isSolid)
					 .filter(BukkitGraphics::isCubic)
					 .filter(BukkitGraphics::isOpaque)
					 .filter(BukkitGraphics::hasModel)
					 .map(block -> Material.matchMaterial(IRegistry.BLOCK.getKey(block).getKey()));
	}
	
	private static boolean hasTexture(Block block) {
		return !(block instanceof BlockMonsterEggs);
	}
	
	private static boolean isSolid(Block block) {
		try {
			Field propertiesField = BlockBase.class.getDeclaredField("aB");
			propertiesField.setAccessible(true);
			
			Field isSolidField = BlockBase.Info.class.getDeclaredField("n");
			isSolidField.setAccessible(true);
			
			BlockBase.Info properties = (BlockBase.Info) propertiesField.get(block);
			return (boolean) isSolidField.get(properties);
		}
		catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	private static boolean isCubic(Block block) {
		try {
			//noinspection deprecation
			VoxelShape shape = block.b(null, null, null, (VoxelShapeCollision) null);
			
			return shape == VoxelShapes.b();
		}
		catch(Exception ignored) {
			return false;
		}
	}
	
	private static boolean isOpaque(Block block) {
		try {
			//noinspection deprecation
			return !block.c_(null);
		}
		catch(Exception ignored) {
			return false;
		}
	}
	
	private static boolean hasModel(Block block) {
		try {
			//noinspection deprecation
			EnumRenderType renderType = block.b((IBlockData) null);
			
			return renderType == EnumRenderType.MODEL;
		}
		catch(Exception ignored) {
			return false;
		}
	}
}
