package fr.she3py.iplacer.util;

import java.util.Objects;

public class GraphicIdentifier {
	private final String namespace;
	private final String key;
	
	public GraphicIdentifier(String namespace, String key) {
		Arguments.requireNonEmpty("namespace", namespace);
		Arguments.requireNonEmpty("key", key);
		
		this.namespace = namespace;
		this.key = key;
	}
	
	public String getNamespace() {
		return namespace;
	}
	
	public String getKey() {
		return key;
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
