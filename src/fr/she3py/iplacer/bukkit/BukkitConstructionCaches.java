package fr.she3py.iplacer.bukkit;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.NamespacedKey;

import com.google.gson.JsonObject;

public class BukkitConstructionCaches {
	public final Map<NamespacedKey, JsonObject> blockModels;
	public final Map<NamespacedKey, String> texturePaths;
	public final Map<String, BufferedImage> textures;
	public final Map<String, JsonObject> textureMetas;
	
	public BukkitConstructionCaches(int initialCapacity) {
		this.blockModels = new HashMap<>(initialCapacity);
		this.texturePaths = new HashMap<>(initialCapacity);
		this.textures = new HashMap<>(initialCapacity);
		this.textureMetas = new HashMap<>(initialCapacity);
	}
	
	public void clear() {
		blockModels.clear();
		texturePaths.clear();
		textures.clear();
		textureMetas.clear();
	}
}