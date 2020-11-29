package fr.she3py.iplacer.minecraft.bukkit;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

import fr.she3py.iplacer.ImagePlacer;
import fr.she3py.iplacer.minecraft.MinecraftGraphics;
import fr.she3py.iplacer.util.GraphicIdentifier;

public class BukkitGraphics {
	public static MinecraftGraphics createFrom(File rsc) throws IOException {
		ImagePlacer.logger.info("Looking for compatible materials");
		List<Material> materials = findMaterials().collect(Collectors.toList());
		
		ImagePlacer.logger.info("Found " + materials.size() + " materials");
		return MinecraftGraphics.createFrom(
			rsc,
			materials.stream()
					 .map(material -> new GraphicIdentifier(material.getKey().getNamespace(), material.getKey().getKey()))
					 .collect(Collectors.toList())
		);
	}
	
	public static MinecraftGraphics createFrom(String file) throws IOException {
		return createFrom(new File(BukkitImagePlacer.plugin.getDataFolder(), file));
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
