package fr.she3py.iplacer.minecraft;

import java.awt.image.BufferedImage;

import fr.she3py.iplacer.IGraphic;
import fr.she3py.iplacer.util.Color3i;
import fr.she3py.iplacer.util.GraphicIdentifier;

public class MinecraftGraphic implements IGraphic {
	public final GraphicIdentifier identifier;
	public final BufferedImage texture;
	private final Color3i averageColor;
	
	public MinecraftGraphic(GraphicIdentifier identifier, BufferedImage texture) {
		this.identifier = identifier;
		this.texture = texture;
		this.averageColor = Color3i.getAverageColor(texture);
	}
	
	@Override
	public Color3i getAverageColor() {
		return averageColor;
	}
	
	@Override
	public GraphicIdentifier getIdentifier() {
		return identifier;
	}
	
	@Override
	public String toString() {
		return "BukkitGraphic[" + identifier.getKey() + ", " + getAverageColor() + ']';
	}
}
