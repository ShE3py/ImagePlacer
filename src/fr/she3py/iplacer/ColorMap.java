package fr.she3py.iplacer;

import java.util.Collections;
import java.util.List;

import org.jetbrains.annotations.UnmodifiableView;

import fr.she3py.iplacer.util.Color3i;

public class ColorMap<G extends IGraphic> {
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
	
	@UnmodifiableView
	public List<G> getGraphics() {
		return Collections.unmodifiableList(graphics);
	}
}
