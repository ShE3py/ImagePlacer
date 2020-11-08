package fr.she3py.iplacer;

import java.util.Collections;
import java.util.Map;

import org.jetbrains.annotations.UnmodifiableView;

import fr.she3py.iplacer.util.Arguments;

public class ColorMap<T> {
	private final Map<T, Integer> data;
	
	public ColorMap(Map<T, Integer> data) {
		this.data = data;
	}
	
	public static double getColorDistanceSq(int r1, int g1, int b1, int r2, int g2, int b2) {
		int deltaR = r1 - r2;
		int deltaG = g1 - g2;
		int deltaB = b1 - b2;
		
		double redmean = (r1 + r2) / 2d;
		double rWeight = 2 + redmean / 256;
		double gWeight = 4d;
		double bWeight = 2 + (255 - redmean) / 256;
		
		return (rWeight * deltaR * deltaR) + (gWeight * deltaG * deltaG) + (bWeight * deltaB * deltaB);
	}
	
	public static double getColorDistance(int r1, int g1, int b1, int r2, int g2, int b2) {
		return Math.sqrt(getColorDistanceSq(r1, g1, b1, r2, g2, b2));
	}
	
	public T getNearestTexture(int r, int g, int b) {
		Arguments.require(r >= 0 && r <= 255, "Red component out of range: " + r);
		Arguments.require(g >= 0 && g <= 255, "Green component out of range: " + g);
		Arguments.require(b >= 0 && b <= 255, "Blue component out of range: " + b);
		
		T nearest = null;
		double currentDistance = Double.MAX_VALUE;
		
		for(Map.Entry<T, Integer> entry : data.entrySet()) {
			int entryColor = entry.getValue();
			double entryDistance = getColorDistanceSq(r, g, b, (entryColor >> 16) & 0xFF, (entryColor >> 8) & 0xFF, entryColor & 0xFF);
			
			if(entryDistance < currentDistance) {
				nearest = entry.getKey();
				currentDistance = entryDistance;
			}
		}
		
		return nearest;
	}
	
	public T getNearestTexture(int rgb) {
		return getNearestTexture((rgb >> 16) & 0xFF, (rgb >> 8) & 0xFF, rgb & 0xFF);
	}
	
	@UnmodifiableView
	public Map<T, Integer> getData() {
		return Collections.unmodifiableMap(data);
	}
}
