package fr.she3py.iplacer;

import fr.she3py.iplacer.storage.base.IBinarySerializable;
import fr.she3py.iplacer.util.Color3i;
import fr.she3py.iplacer.util.GraphicIdentifier;

public interface IGraphic extends IBinarySerializable {
	public Color3i getAverageColor();
	public GraphicIdentifier getIdentifier();
}
