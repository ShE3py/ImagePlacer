package fr.she3py.iplacer.util;

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
	
	public static double weightedDistanceToSq(int r1, int g1, int b1, int r2, int g2, int b2) {
		int deltaR = r2 - r1;
		int deltaG = g2 - g1;
		int deltaB = b2 - b1;
		
		double redmean = (r1 + r2) / 2d;
		double rWeight = 2 + redmean / 256;
		double gWeight = 4d;
		double bWeight = 2 + (255 - redmean) / 256;
		
		return (rWeight * deltaR * deltaR) + (gWeight * deltaG * deltaG) + (bWeight * deltaB * deltaB);
	}
	
	public static double weightedDistanceToSq(Color3i from, Color3i to) {
		return weightedDistanceToSq(from.r, from.g, from.b, to.r, to.g, to.b);
	}
	
	public static double weightedDistanceTo(int r1, int g1, int b1, int r2, int g2, int b2) {
		return Math.sqrt(weightedDistanceToSq(r1, g1, b1, r2, g2, b2));
	}
	
	public static double weightedDistanceTo(Color3i from, Color3i to) {
		return Math.sqrt(weightedDistanceToSq(from, to));
	}
	
	public double weightedDistanceToSq(Color3i to) {
		return weightedDistanceToSq(this, to);
	}
	
	public double weightedDistanceToSq(int r, int g, int b) {
		return weightedDistanceToSq(new Color3i(r, g, b));
	}
	
	public double weightedDistanceToSq(int rgb) {
		return weightedDistanceToSq(new Color3i(rgb));
	}
	
	public double weightedDistanceTo(Color3i to) {
		return weightedDistanceTo(this, to);
	}
	
	public double weightedDistanceTo(int r, int g, int b) {
		return weightedDistanceTo(new Color3i(r, g, b));
	}
	
	public double weightedDistanceTo(int rgb) {
		return weightedDistanceTo(new Color3i(rgb));
	}
	
	public int toRGB() {
		return (r << 16) | (g << 8) | b;
	}
}
