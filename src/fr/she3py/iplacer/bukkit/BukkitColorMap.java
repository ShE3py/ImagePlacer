package fr.she3py.iplacer.bukkit;

import java.awt.image.BufferedImage;
import java.util.List;

import fr.she3py.iplacer.ColorMap;

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
}
