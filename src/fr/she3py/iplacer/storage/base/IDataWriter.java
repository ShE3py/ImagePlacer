package fr.she3py.iplacer.storage.base;

import java.io.IOException;

@FunctionalInterface
public interface IDataWriter<T> {
	public void write(IBinaryWriter writer, T value) throws IOException;
}
