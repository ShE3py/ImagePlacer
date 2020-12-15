package fr.she3py.iplacer.util;

import java.io.IOException;
import java.util.Objects;

import fr.she3py.iplacer.storage.base.IBinaryReader;
import fr.she3py.iplacer.storage.base.IBinarySerializable;
import fr.she3py.iplacer.storage.base.IBinaryWriter;

public class GraphicIdentifier implements IBinarySerializable {
	private final String namespace;
	private final String key;
	
	public GraphicIdentifier(String namespace, String key) {
		Arguments.requireNonEmpty("namespace", namespace);
		Arguments.requireNonEmpty("key", key);
		
		this.namespace = namespace;
		this.key = key;
	}
	
	public GraphicIdentifier(IBinaryReader reader) throws IOException {
		this(reader.readStringASCII(), reader.readStringASCII());
	}
	
	public String getNamespace() {
		return namespace;
	}
	
	public String getKey() {
		return key;
	}
	
	@Override
	public void serialize(IBinaryWriter writer) throws IOException {
		writer.writeStringASCII(namespace);
		writer.writeStringASCII(key);
	}
	
	@Override
	public boolean equals(Object obj) {
		if(this == obj)
			return true;
		
		if(obj == null || this.getClass() != obj.getClass())
			return false;
		
		GraphicIdentifier that = (GraphicIdentifier) obj;
		return namespace.equals(that.namespace) && key.equals(that.key);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(namespace, key);
	}
	
	@Override
	public String toString() {
		return namespace + ':' + key;
	}
}
