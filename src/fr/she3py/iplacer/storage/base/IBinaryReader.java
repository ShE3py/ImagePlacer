package fr.she3py.iplacer.storage.base;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

import org.jetbrains.annotations.Nullable;

import fr.she3py.iplacer.storage.base.IBinarySerializable.SerializeWithChecksum;
import fr.she3py.iplacer.util.Arguments;

public interface IBinaryReader extends AutoCloseable {
	public void read(byte[] data, int offset, int length) throws IOException;
	public CompositionStack stack();
	
	public default void read(byte[] data) throws IOException {
		read(data, 0, data.length);
	}
	
	public default byte[] readBytes(int length) throws IOException {
		byte[] buff = new byte[length];
		
		read(buff);
		return buff;
	}
	
	public default byte readByte() throws IOException {
		return readBytes(1)[0];
	}
	
	public default short readShort() throws IOException {
		byte[] buff = readBytes(2);
		
		return (short) ((buff[0] << 8) | (buff[1] & 0xFF));
	}
	
	public default int readInteger() throws IOException {
		byte[] buff = readBytes(4);
		
		return (buff[0] << 24) | ((buff[1] & 0xFF) << 16) | ((buff[2] & 0xFF) << 8) | (buff[3] & 0xFF);
	}
	
	public default long readLong() throws IOException {
		byte[] buff = readBytes(8);
		
		return (((long) buff[0] & 0xFF) << 56) | (((long) buff[1] & 0xFF) << 48) | (((long) buff[2] & 0xFF) << 40) | (((long) buff[3] & 0xFF) << 32) | (((long) buff[4] & 0xFF) << 24) | (((long) buff[5] & 0xFF) << 16) | (((long) buff[6] & 0xFF) << 8) | ((long) buff[7] & 0xFF);
	}
	
	public default float readFloat() throws IOException {
		return ByteBuffer.wrap(readBytes(4)).getFloat();
	}
	
	public default double readDouble() throws IOException {
		return ByteBuffer.wrap(readBytes(8)).getDouble();
	}
	
	public default String readString(Charset charset) throws IOException {
		byte[] zero = new byte[(int) Math.ceil(charset.newEncoder().averageBytesPerChar())];
		int zeroCount = 0;
		
		ByteArrayOutputStream buff = new ByteArrayOutputStream();
		
		while(zeroCount != zero.length) {
			byte b = readByte();
			
			if(b == (byte) '\0') {
				++zeroCount;
				
				continue;
			}
			
			if(zeroCount != 0) {
				buff.write(zero, 0, zeroCount);
				zeroCount = 0;
			}
			
			buff.write(b);
		}
		
		return buff.toString(charset.name());
	}
	
	public default String readStringASCII() throws IOException {
		return readString(StandardCharsets.US_ASCII);
	}
	
	public default String readStringUTF8() throws IOException {
		return readString(StandardCharsets.UTF_8);
	}
	
	public default UUID readUUID() throws IOException {
		return new UUID(readLong(), readLong());
	}
	
	public default <E, C extends Collection<E>> C readCollection(Function<Integer, C> cCtor, IDataReader<E> dataReader) throws IOException {
		int count = readInteger();
		
		C collection = cCtor.apply(count); // initialCapacity
		while(count-- != 0)
			collection.add(dataReader.read(this));
		
		return collection;
	}
	
	@Nullable
	public default <E> E readIndex(List<E> list) throws IOException {
		int index = readInteger();
		if(index == -1)
			return null;
		
		return list.get(index);
	}
	
	public default <K, V, M extends Map<K, V>> M readMap(Function<Integer, M> mapCtor, IDataReader<K> keyReader, IDataReader<V> valueReader) throws IOException {
		int count = readInteger();
		
		M map = mapCtor.apply(count); // initialCapacity
		while(count-- != 0) {
			K key = keyReader.read(this);
			V value = valueReader.read(this);
			
			map.put(key, value);
		}
		
		return map;
	}
	
	public default <T extends IBinarySerializable> T readObject(Class<T> klass) throws IOException {
		T value;
		
		try {
			Constructor<T> ctor = klass.getConstructor(IBinaryReader.class);
			
			value = ctor.newInstance(this);
		}
		catch(InvocationTargetException e) {
			Throwable thrown = e.getTargetException();
			
			if(thrown instanceof IOException)
				throw (IOException) thrown;
			
			if(thrown instanceof RuntimeException)
				throw (RuntimeException) thrown;
			
			throw new RuntimeException("The constructor throws an exception", e);
		}
		catch(NoSuchMethodException | IllegalAccessException | IllegalArgumentException | InstantiationException | SecurityException e) {
			throw new IOException("Unable to invoke the constructor", e);
		}
		
		if(klass.isAnnotationPresent(SerializeWithChecksum.class)) {
			int checksum = readInteger();
			Arguments.requireEqual("checksum", value.hashCode(), checksum);
		}
		
		return value;
	}
	
	public static <T extends IBinarySerializable> IDataReader<T> objectReader(Class<T> klass) {
		return reader -> reader.readObject(klass);
	}
	
	public default void skip(long n) throws IOException {
		while(n > 0)
			n -= readBytes((int) Long.min(n, Integer.MAX_VALUE)).length;
	}
	
	public default long remaining() throws IOException {
		throw new UnsupportedOperationException();
	}
	
	public default boolean canRead(int n) throws IOException {
		return remaining() >= n;
	}
	
	@Override
	public default void close() throws IOException { }
}
