package fr.she3py.iplacer;

import org.jetbrains.annotations.Nullable;

import fr.she3py.iplacer.util.Arguments;

public class GraphicImage<G extends IGraphic> {
	protected final int width;
	protected final int height;
	protected final G[] data;
	
	public GraphicImage(int width, int height, G[] data) {
		Arguments.requireEqual("dataLength", data.length, width * height);
		
		this.width = width;
		this.height = height;
		this.data = data;
	}
	
	@Nullable
	public G get(int x, int y) {
		return data[x + y * width];
	}
	
	public void set(int x, int y, @Nullable G val) {
		data[x + y * width] = val;
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
}
