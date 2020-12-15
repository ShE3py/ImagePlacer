package fr.she3py.iplacer.minecraft.bukkit;

import static fr.she3py.iplacer.ImagePlacer.logger;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

import javax.imageio.ImageIO;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import fr.she3py.iplacer.minecraft.MinecraftColorMap;
import fr.she3py.iplacer.minecraft.MinecraftGraphics;
import fr.she3py.iplacer.minecraft.MinecraftImage;
import fr.she3py.iplacer.util.Arguments;

public class BukkitImagePlacer extends JavaPlugin {
	public static BukkitImagePlacer plugin;
	
	@Override
	public void onEnable() {
		plugin = this;
		logger = this.getLogger();
		
		Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
			try {
				MinecraftGraphics graphics = BukkitGraphics.createFrom("1.16.3.zip");
				graphics.saveManifest(getFile("graphics.manifest"));
				
				MinecraftColorMap colorMap = graphics.makeColorMap();
				BufferedImage imageIn = read("in.png");
				
				MinecraftImage image = colorMap.map(imageIn);
				write(image.toAverageImage(), "out-avg.png");
				write(image.toDistanceImage(imageIn), "out-dist.png");
				write(image.toTiledImage(), "out-tiled.png");
				
				logger.info(colorMap.getGraphics().toString());
			}
			catch(IOException e) {
				logger.log(Level.SEVERE, "", e);
			}
		});
	}
	
	private static BufferedImage read(String fileIn) throws IOException {
		BufferedImage image = ImageIO.read(getFile(fileIn));
		Arguments.requireNonNull("image", image);
		
		return image;
	}
	
	private static void write(BufferedImage imageIn, String fileOut) throws IOException {
		boolean ret = ImageIO.write(imageIn, "png", getFile(fileOut));
		Arguments.require(ret, "No appropriate writer was found");
	}
	
	public static File getFile(String fileIn) {
		return new File(plugin.getDataFolder(), fileIn);
	}
}
