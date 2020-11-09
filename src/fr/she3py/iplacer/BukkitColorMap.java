package fr.she3py.iplacer;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.stream.Collectors;
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

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import fr.she3py.iplacer.util.Arguments;

public class BukkitColorMap extends ColorMap<Material> {
	private BukkitColorMap(Map<Material, Integer> data) {
		super(data);
	}
	
	public static BukkitColorMap buildFrom(File rsc) throws IOException {
		ImagePlacer.logger.info("Construction of a BukkitColorMap initiated");
		
		List<Material> blocks = Arrays.stream(Material.values()).filter(Material::isSolid).filter(Material::isOccluding).collect(Collectors.toList());
		blocks.removeIf(material -> {
			Block block = IRegistry.BLOCK.getOptional(new MinecraftKey(material.getKey().getNamespace(), material.getKey().getKey())).orElse(null);
			Arguments.requireNonNull("block", block);
			
			if(block instanceof BlockMonsterEggs)
				return true;
			
			// non-solid
			try {
				Field propertiesField = BlockBase.class.getDeclaredField("aB");
				propertiesField.setAccessible(true);
				
				Field isSolidField = BlockBase.Info.class.getDeclaredField("n");
				isSolidField.setAccessible(true);
				
				BlockBase.Info properties = (BlockBase.Info) propertiesField.get(block);
				//noinspection BooleanVariableAlwaysNegated
				boolean isSolid = (boolean) isSolidField.get(properties);
				
				if(!isSolid)
					return true;
			}
			catch(Exception e) {
				throw new RuntimeException(e);
			}
			
			// non-cubic
			try {
				//noinspection deprecation
				VoxelShape shape = block.b(null, null, null, (VoxelShapeCollision) null);
				
				if(shape != VoxelShapes.b())
					return true;
			}
			catch(Exception ignored) {
				return true;
			}
			
			// transparent
			try {
				//noinspection deprecation
				if(block.c_(null))
					return true;
			}
			catch(Exception ignored) {
				return true;
			}
			
			// non-model
			try {
				//noinspection deprecation
				EnumRenderType renderType = block.b((IBlockData) null);
				
				if(renderType != EnumRenderType.MODEL)
					return true;
			}
			catch(Exception ignored) {
				return true;
			}
			
			return false;
		});
		
		ImagePlacer.logger.info("Found " + blocks.size() + " blocks");
		Map<Material, Integer> data = new HashMap<>(blocks.size());
		
		ImagePlacer.logger.info("Generating colormap from: " + rsc.getAbsolutePath());
		ZipFile zipFile = new ZipFile(rsc);
		
		for(Material block : blocks) {
			int color;
			try {
				color = findMaterialColor(block, zipFile);
			}
			catch(Exception e) {
				ImagePlacer.logger.log(Level.SEVERE, "Generation failed for: " + block.getKey() + ", skipping", e);
				
				continue;
			}
			data.put(block, color);
			
			ImagePlacer.logger.info(block.getKey() + " -> (" + ((color >> 16) & 0xFF) + ", " + ((color >> 8) & 0xFF) + ", " + (color & 0xFF) + ")");
		}
		
		ImagePlacer.logger.info("Generation complete - " + data.size() + " of " + blocks.size() + " blocks mapped");
		return new BukkitColorMap(data);
	}
	
	public static BukkitColorMap build() throws IOException {
		return buildFrom(new File(ImagePlacer.plugin.getDataFolder(), "1.16.3.zip"));
	}
	
	private static int findMaterialColor(Material block, ZipFile zipFile) throws IOException {
		BufferedImage texture = findMaterialTexture(block, zipFile);
		
		long rSum = 0;
		long gSum = 0;
		long bSum = 0;
		
		int width = texture.getWidth();
		int height = texture.getHeight();
		
		for(int x = 0; x < width; ++x) {
			for(int y = 0; y < height; ++y) {
				int rgb = texture.getRGB(x, y);
				
				rSum += (rgb >> 16) & 0xFF;
				gSum += (rgb >> 8) & 0xFF;
				bSum += rgb & 0xFF;
			}
		}
		
		long size = width * height;
		int rAvg = (int) (rSum / size);
		int gAvg = (int) (gSum / size);
		int bAvg = (int) (bSum / size);
		
		return (rAvg << 16) | (gAvg << 8) | bAvg;
	}
	
	private static BufferedImage findMaterialTexture(Material block, ZipFile zipFile) throws IOException {
		NamespacedKey key = block.getKey();
		
		JsonObject model = parseBlockModel(key, zipFile);
		String texturePath = findTexturePath(model, key);
		
		String namespace;
		if(texturePath.contains(":")) {
			namespace = texturePath.substring(0, texturePath.indexOf(':'));
			texturePath = texturePath.substring(namespace.length() + 1);
		}
		else {
			namespace = key.getNamespace();
		}
		
		return readTexture(zipFile, namespace, texturePath);
	}
	
	private static JsonObject parseBlockModel(NamespacedKey key, ZipFile zipFile) throws IOException {
		ZipEntry blockModel = zipFile.getEntry("assets/" + key.getNamespace() + "/models/block/" + key.getKey() + ".json");
		Arguments.require(blockModel != null, UnsupportedOperationException::new, "Model not found");
		
		return new JsonParser().parse(new InputStreamReader(zipFile.getInputStream(blockModel))).getAsJsonObject();
	}
	
	@NotNull
	private static String findTexturePath(JsonObject model, NamespacedKey key) {
		String parent = model.get("parent").getAsString();
		if(!parent.contains(":"))
			parent = key.getNamespace() + ":" + parent;
		
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
				return textures.get("top").getAsString();
				
			case "minecraft:block/cube":
			case "minecraft:block/cube_directional":
				if(key.toString().equals("minecraft:dried_kelp_block"))
					return textures.get("up").getAsString(); // custom model
				
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
	}
	
	private static BufferedImage readTexture(ZipFile zipFile, String namespace, String texturePath) throws IOException {
		ZipEntry blockTexture = zipFile.getEntry("assets/" + namespace + "/textures/" + texturePath + ".png");
		Arguments.require(blockTexture != null, "Texture not found: " + namespace + ":" + texturePath);
		
		BufferedImage texture = ImageIO.read(zipFile.getInputStream(blockTexture));
		Arguments.requireNonNull("texture", texture);
		
		return texture;
	}
	
	public BufferedImage mapImage(BufferedImage in) throws IOException {
		int width = in.getWidth();
		int height = in.getHeight();
		
		ZipFile zipFile = new ZipFile(new File(ImagePlacer.plugin.getDataFolder(), "1.16.3.zip"));
		
		BufferedImage out = new BufferedImage(width * 16, height * 16, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2d = out.createGraphics();
		
		Map<Material, Image> cache = new HashMap<>(255);
		
		for(int x = 0; x < width; ++x) {
			for(int y = 0; y < height; ++y) {
				int rgb = in.getRGB(x, y);
				Material material = findNearestTexture(rgb);
				
				Image tex = cache.computeIfAbsent(material, materialIn -> {
					try {
						return findMaterialTexture(materialIn, zipFile).getScaledInstance(16, 16, Image.SCALE_SMOOTH);
					}
					catch(IOException e) {
						throw new UncheckedIOException(e);
					}
				});
				
				g2d.drawImage(tex, x * 16, y * 16, null);
			}
		}
		
		g2d.dispose();
		return out;
	}
	
	public void mapImage(File in, File out) throws IOException {
		BufferedImage imageIn = ImageIO.read(in);
		Arguments.requireNonNull("imageIn", imageIn);
		
		BufferedImage imageOut = mapImage(imageIn);
		
		boolean ret = ImageIO.write(imageOut, "png", out);
		Arguments.require(ret, "No appropriate writer was found");
	}
	
	public void mapImage(String fileIn, String fileOut) throws IOException {
		mapImage(new File(ImagePlacer.plugin.getDataFolder(), fileIn), new File(ImagePlacer.plugin.getDataFolder(), fileOut));
	}
}
