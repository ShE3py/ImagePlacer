package fr.she3py.iplacer.storage.base;

import java.io.IOException;

@FunctionalInterface
public interface IDataReader<T> {
	public T read(IBinaryReader reader) throws IOException;
}
