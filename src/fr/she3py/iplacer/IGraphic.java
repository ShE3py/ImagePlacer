package fr.she3py.iplacer;

import fr.she3py.iplacer.util.Color3i;
import fr.she3py.iplacer.util.GraphicIdentifier;

public interface IGraphic {
	public Color3i getAverageColor();
	public GraphicIdentifier getIdentifier();
}
