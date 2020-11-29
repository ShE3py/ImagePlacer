package fr.she3py.iplacer.minecraft.bukkit;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

import javax.imageio.ImageIO;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import fr.she3py.iplacer.ImagePlacer;
import fr.she3py.iplacer.minecraft.MinecraftColorMap;
import fr.she3py.iplacer.minecraft.MinecraftImage;
import fr.she3py.iplacer.util.Arguments;

public class BukkitImagePlacer extends JavaPlugin {
	public static BukkitImagePlacer plugin;
	
	@Override
	public void onEnable() {
		plugin = this;
		ImagePlacer.logger = this.getLogger();
		
		Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
			try {
				MinecraftColorMap colorMap = BukkitGraphics.createFrom("1.16.3.zip").makeColorMap();
				BufferedImage imageIn = read("in.png");
				
				MinecraftImage image = colorMap.map(imageIn);
				write(image.toAverageImage(), "out-avg.png");
				write(image.toDistanceImage(imageIn), "out-dist.png");
				write(image.toTiledImage(), "out-tiled.png");
				
				ImagePlacer.logger.info(colorMap.getGraphics().toString());
			}
			catch(IOException e) {
				ImagePlacer.logger.log(Level.SEVERE, "", e);
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
