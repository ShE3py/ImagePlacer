package fr.she3py.iplacer.bukkit;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;

import fr.she3py.iplacer.ColorMap;
import fr.she3py.iplacer.ImagePlacer;
import fr.she3py.iplacer.util.Arguments;

public class BukkitColorMap extends ColorMap<BukkitGraphic> {
	BukkitColorMap(List<BukkitGraphic> graphics) {
		super(graphics);
	}
	
	@Override
	public BukkitImage map(BufferedImage src) {
		BukkitImage dest = new BukkitImage(src.getWidth(), src.getHeight());
		map(src, dest);
		
		return dest;
	}
	
	public void mapImage(File fileIn, File fileOut) throws IOException {
		BufferedImage imageIn = ImageIO.read(fileIn);
		Arguments.requireNonNull("imageIn", imageIn);
		
		BufferedImage imageOut = map(imageIn).toTiledImage();
		
		boolean ret = ImageIO.write(imageOut, "png", fileOut);
		Arguments.require(ret, "No appropriate writer was found");
	}
	
	public void mapImage(String fileIn, String fileOut) throws IOException {
		mapImage(new File(ImagePlacer.plugin.getDataFolder(), fileIn), new File(ImagePlacer.plugin.getDataFolder(), fileOut));
	}
}
