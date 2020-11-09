package fr.she3py.iplacer;

import java.awt.image.BufferedImage;
import java.util.Collections;
import java.util.List;

import org.jetbrains.annotations.UnmodifiableView;

import fr.she3py.iplacer.util.Arguments;
import fr.she3py.iplacer.util.Color3i;

public abstract class ColorMap<G extends IGraphic> {
	private final List<G> graphics;
	
	public ColorMap(List<G> graphics) {
		this.graphics = graphics;
	}
	
	public G findNearestGraphic(Color3i color) {
		G nearest = null;
		double currentDistance = Double.MAX_VALUE;
		
		for(G graphic : graphics) {
			double distance = color.weightedDistanceToSq(graphic.getAverageColor());
			
			if(distance < currentDistance) {
				nearest = graphic;
				currentDistance = distance;
			}
		}
		
		return nearest;
	}
	
	public G findNearestGraphic(int rgb) {
		return findNearestGraphic(Color3i.from(rgb));
	}
	
	public void map(BufferedImage src, GraphicImage<G> dest) {
		Arguments.requireEqual("width", src.getWidth(), dest.getWidth());
		Arguments.requireEqual("height", src.getHeight(), dest.getHeight());
		
		int width = dest.getWidth();
		int height = dest.getHeight();
		
		for(int x = 0; x < width; ++x) {
			for(int y = 0; y < height; ++y) {
				int rgb = src.getRGB(x, y);
				int alpha = (rgb >> 24) & 0xFF;
				
				dest.set(x, y, (alpha > 64) ? findNearestGraphic(rgb) : null);
			}
		}
	}
	
	public abstract GraphicImage<G> map(BufferedImage src);
	
	@UnmodifiableView
	public List<G> getGraphics() {
		return Collections.unmodifiableList(graphics);
	}
}
