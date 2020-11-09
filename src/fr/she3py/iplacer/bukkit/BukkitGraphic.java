package fr.she3py.iplacer.bukkit;

import java.awt.image.BufferedImage;

import org.bukkit.Material;

import fr.she3py.iplacer.IGraphic;
import fr.she3py.iplacer.util.Color3i;

public class BukkitGraphic implements IGraphic {
	public final Material material;
	public final BufferedImage texture;
	private final Color3i averageColor;
	
	public BukkitGraphic(Material material, BufferedImage texture) {
		this.material = material;
		this.texture = texture;
		this.averageColor = Color3i.getAverageColor(texture);
	}
	
	@Override
	public Color3i getAverageColor() {
		return averageColor;
	}
	
	@Override
	public String toString() {
		return "BukkitGraphic[" + material.getKey() + ", " + getAverageColor() + ']';
	}
}
