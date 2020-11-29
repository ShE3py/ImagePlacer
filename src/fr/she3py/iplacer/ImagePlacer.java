package fr.she3py.iplacer;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import fr.she3py.iplacer.minecraft.bukkit.BukkitColorMap;
import fr.she3py.iplacer.minecraft.bukkit.BukkitGraphics;
import fr.she3py.iplacer.minecraft.bukkit.BukkitImage;
import fr.she3py.iplacer.util.Arguments;

public class ImagePlacer extends JavaPlugin {
	public static ImagePlacer plugin;
	public static Logger logger;
	
	@Override
	public void onEnable() {
		plugin = this;
		logger = this.getLogger();
		
		Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
			try {
				BukkitColorMap colorMap = BukkitGraphics.createFrom("1.16.3.zip").makeColorMap();
				BufferedImage imageIn = read("in.png");
				
				BukkitImage image = colorMap.map(imageIn);
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
	
	private BufferedImage read(String fileIn) throws IOException {
		BufferedImage image = ImageIO.read(getFile(fileIn));
		Arguments.requireNonNull("image", image);
		
		return image;
	}
	
	private void write(BufferedImage imageIn, String fileOut) throws IOException {
		boolean ret = ImageIO.write(imageIn, "png", getFile(fileOut));
		Arguments.require(ret, "No appropriate writer was found");
	}
	
	private File getFile(String fileIn) {
		return new File(this.getDataFolder(), fileIn);
	}
}
