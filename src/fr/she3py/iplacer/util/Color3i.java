package fr.she3py.iplacer.util;

import java.awt.image.BufferedImage;
import java.util.Objects;

public class Color3i {
	public int r;
	public int g;
	public int b;
	
	public Color3i(int r, int g, int b) {
		Arguments.require(r >= 0 && r <= 255, "Red component out of range: " + r);
		Arguments.require(g >= 0 && g <= 255, "Green component out of range: " + g);
		Arguments.require(b >= 0 && b <= 255, "Blue component out of range: " + b);
		
		this.r = r;
		this.g = g;
		this.b = b;
	}
	
	public Color3i(int rgb) {
		this((rgb >> 16) & 0xFF, (rgb >> 8) & 0xFF, rgb & 0xFF);
	}
	
	public static Color3i from(int rgb) {
		return new Color3i(rgb);
	}
	
	public static double getDistanceSq(int r1, int g1, int b1, int r2, int g2, int b2) {
		int deltaR = r2 - r1;
		int deltaG = g2 - g1;
		int deltaB = b2 - b1;
		
		return (deltaR * deltaR) + (deltaG * deltaG) + (deltaB * deltaB);
	}
	
	public static Color3i getAverageColor(BufferedImage image) {
		Arguments.requireNonNull("image", image);
		
		long rSum = 0;
		long gSum = 0;
		long bSum = 0;
		
		int width = image.getWidth();
		int height = image.getHeight();
		
		for(int x = 0; x < width; ++x) {
			for(int y = 0; y < height; ++y) {
				int rgb = image.getRGB(x, y);
				
				rSum += (rgb >> 16) & 0xFF;
				gSum += (rgb >> 8) & 0xFF;
				bSum += rgb & 0xFF;
			}
		}
		
		long size = (long) width * height;
		int rAvg = (int) (rSum / size);
		int gAvg = (int) (gSum / size);
		int bAvg = (int) (bSum / size);
		
		return new Color3i(rAvg, gAvg, bAvg);
	}
	
	public static double getDistanceSq(Color3i from, Color3i to) {
		return getDistanceSq(from.r, from.g, from.b, to.r, to.g, to.b);
	}
	
	public static double getDistance(int r1, int g1, int b1, int r2, int g2, int b2) {
		return Math.sqrt(getDistanceSq(r1, g1, b1, r2, g2, b2));
	}
	
	public static double getDistance(Color3i from, Color3i to) {
		return Math.sqrt(getDistanceSq(from, to));
	}
	
	public double distanceToSq(Color3i to) {
		return getDistanceSq(this, to);
	}
	
	public double distanceToSq(int r, int g, int b) {
		return distanceToSq(new Color3i(r, g, b));
	}
	
	public double distanceToSq(int rgb) {
		return distanceToSq(new Color3i(rgb));
	}
	
	public double distanceTo(Color3i to) {
		return getDistance(this, to);
	}
	
	public double distanceTo(int r, int g, int b) {
		return distanceTo(new Color3i(r, g, b));
	}
	
	public double distanceTo(int rgb) {
		return distanceTo(new Color3i(rgb));
	}
	
	public int toRGB() {
		return (r << 16) | (g << 8) | b;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(this == obj)
			return true;
		
		if(obj == null || this.getClass() != obj.getClass())
			return false;
		
		Color3i other = (Color3i) obj;
		return r == other.r
				   && g == other.g
				   && b == other.b;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(r, g, b);
	}
	
	@Override
	public String toString() {
		return "Color3i[" + "r=" + r + ", g=" + g + ", b=" + b + ']';
	}
}
