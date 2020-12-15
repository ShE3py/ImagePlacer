package fr.she3py.iplacer.minecraft;

import java.awt.image.BufferedImage;
import java.io.IOException;

import fr.she3py.iplacer.IGraphic;
import fr.she3py.iplacer.storage.base.IBinaryWriter;
import fr.she3py.iplacer.util.Arguments;
import fr.she3py.iplacer.util.Color3i;
import fr.she3py.iplacer.util.GraphicIdentifier;

public class MinecraftGraphic implements IGraphic {
	public final GraphicIdentifier identifier;
	public final BufferedImage texture;
	private final Color3i averageColor;
	
	public MinecraftGraphic(GraphicIdentifier identifier, BufferedImage texture) {
		Arguments.requireNonNull("identifier", identifier);
		
		this.identifier = identifier;
		this.texture = texture;
		this.averageColor = Color3i.getAverageColor(texture);
	}
	
	@Override
	public void serialize(IBinaryWriter writer) throws IOException {
		identifier.serialize(writer);
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
	public boolean equals(Object obj) {
		if(this == obj)
			return true;
		
		if(obj == null || this.getClass() != obj.getClass())
			return false;
		
		MinecraftGraphic other = (MinecraftGraphic) obj;
		return identifier.equals(other.identifier)
				   && texture.equals(other.texture)
				   && averageColor.equals(other.averageColor);
	}
	
	@Override
	public int hashCode() {
		return identifier.hashCode();
	}
	
	@Override
	public String toString() {
		return "BukkitGraphic[" + identifier.getKey() + ", " + getAverageColor() + ']';
	}
}
