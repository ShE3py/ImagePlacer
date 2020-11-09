package fr.she3py.iplacer.bukkit;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipFile;

import javax.imageio.ImageIO;

import org.bukkit.Material;

import fr.she3py.iplacer.ColorMap;
import fr.she3py.iplacer.ImagePlacer;
import fr.she3py.iplacer.util.Arguments;

public class BukkitColorMap extends ColorMap<BukkitGraphic> {
	BukkitColorMap(List<BukkitGraphic> graphics) {
		super(graphics);
	}
	
	public BufferedImage mapImage(BufferedImage in) throws IOException {
		int width = in.getWidth();
		int height = in.getHeight();
		
		File file = new File(ImagePlacer.plugin.getDataFolder(), "1.16.3.zip");
		ZipFile zipFile = new ZipFile(file);
		
		BufferedImage out = new BufferedImage(width * 16, height * 16, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = out.createGraphics();
		
		Map<Material, Image> cache = new HashMap<>(255);
		
		for(int x = 0; x < width; ++x) {
			for(int y = 0; y < height; ++y) {
				int rgb = in.getRGB(x, y);
				int alpha = (rgb >> 24) & 0xFF;
				
				if(alpha <= 64)
					continue;
				
				BukkitGraphic graphic = findNearestGraphic(rgb);
				
				Image tex = cache.computeIfAbsent(graphic.getMaterial(), materialIn -> {
					try {
						return BukkitGraphics.findMaterialTexture(materialIn, file, zipFile).getScaledInstance(16, 16, Image.SCALE_SMOOTH);
					}
					catch(IOException e) {
						throw new UncheckedIOException(e);
					}
				});
				
				g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha / 255f));
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
