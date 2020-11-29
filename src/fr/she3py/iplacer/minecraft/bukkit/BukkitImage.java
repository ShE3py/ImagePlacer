package fr.she3py.iplacer.minecraft.bukkit;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;

import fr.she3py.iplacer.GraphicImage;

public class BukkitImage extends GraphicImage<BukkitGraphic> {
	public BukkitImage(int width, int height, BukkitGraphic[] data) {
		super(width, height, data);
	}
	
	public BukkitImage(int width, int height) {
		super(width, height, new BukkitGraphic[width * height]);
	}
	
	public BufferedImage toTiledImage() {
		BufferedImage out = new BufferedImage(width * 16, height * 16, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = out.createGraphics();
		
		Map<Material, Image> cache = new HashMap<>(255);
		
		for(int x = 0; x < width; ++x) {
			for(int y = 0; y < height; ++y) {
				BukkitGraphic graphic = get(x, y);
				if(graphic == null)
					continue;
				
				Image tex = cache.computeIfAbsent(graphic.material, materialIn -> graphic.texture.getScaledInstance(16, 16, Image.SCALE_SMOOTH));
				g2d.drawImage(tex, x * 16, y * 16, null);
			}
		}
		
		g2d.dispose();
		return out;
	}
	
	public BufferedImage toAverageImage() {
		BufferedImage out = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		
		for(int x = 0; x < width; ++x) {
			for(int y = 0; y < height; ++y) {
				BukkitGraphic graphic = get(x, y);
				if(graphic == null)
					continue;
				
				out.setRGB(x, y, graphic.getAverageColor().toRGB() | (0xFF << 24));
			}
		}
		
		return out;
	}
	
	public BufferedImage toDistanceImage(BufferedImage imageIn) {
		BufferedImage out = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		
		double max = Double.MIN_VALUE;
		for(int x = 0; x < width; ++x) {
			for(int y = 0; y < height; ++y) {
				BukkitGraphic graphic = get(x, y);
				if(graphic == null)
					continue;
				
				double distance = graphic.getAverageColor().distanceTo(imageIn.getRGB(x, y));
				if(distance > max)
					max = distance;
			}
		}
		
		
		for(int x = 0; x < width; ++x) {
			for(int y = 0; y < height; ++y) {
				BukkitGraphic graphic = get(x, y);
				if(graphic == null)
					continue;
				
				double distance = graphic.getAverageColor().distanceTo(imageIn.getRGB(x, y));
				float factor = (float) (distance / max);
				
				int r = (int) Math.floor(factor * 255);
				int g = (int) Math.floor(factor * 255);
				int b = (int) Math.floor(factor * 255);
				
				out.setRGB(x, y, (r << 16) | (g << 8) | b);
			}
		}
		
		return out;
	}
}
