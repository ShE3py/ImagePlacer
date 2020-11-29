package fr.she3py.iplacer.minecraft;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonObject;

import fr.she3py.iplacer.util.GraphicIdentifier;

public class MinecraftConstructionCaches {
	public final Map<GraphicIdentifier, JsonObject> blockModels;
	public final Map<GraphicIdentifier, String> texturePaths;
	public final Map<String, BufferedImage> textures;
	public final Map<String, JsonObject> textureMetas;
	
	public MinecraftConstructionCaches(int initialCapacity) {
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
