package fr.she3py.iplacer.minecraft;

import java.awt.image.BufferedImage;
import java.util.List;

import fr.she3py.iplacer.ColorMap;
import fr.she3py.iplacer.minecraft.MinecraftGraphic;
import fr.she3py.iplacer.minecraft.MinecraftImage;

public class MinecraftColorMap extends ColorMap<MinecraftGraphic> {
	public MinecraftColorMap(List<MinecraftGraphic> graphics) {
		super(graphics);
	}
	
	@Override
	public MinecraftImage map(BufferedImage src) {
		MinecraftImage dest = new MinecraftImage(src.getWidth(), src.getHeight());
		map(src, dest);
		
		return dest;
	}
}
