package fr.she3py.iplacer.bukkit;

import org.bukkit.Material;

import fr.she3py.iplacer.IGraphic;
import fr.she3py.iplacer.util.Color3i;

public class BukkitGraphic implements IGraphic {
	private final Material material;
	private final Color3i color;
	
	public BukkitGraphic(Material material, Color3i color) {
		this.material = material;
		this.color = color;
	}
	
	public Material getMaterial() {
		return material;
	}
	
	@Override
	public Color3i getAverageColor() {
		return color;
	}
}
