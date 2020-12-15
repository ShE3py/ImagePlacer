package fr.she3py.iplacer.storage.base;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.jetbrains.annotations.Nullable;

import fr.she3py.iplacer.storage.base.IBinarySerializable.SerializeWithChecksum;

public interface IBinaryWriter extends AutoCloseable {
	public void write(byte[] data, int offset, int length) throws IOException;
	public CompositionStack stack();
	
	public default void write(byte[] data) throws IOException {
		write(data, 0, data.length);
	}
	
	public default void writeByte(byte value) throws IOException {
		write(new byte[] { value }, 0, 1);
	}
	
	public default void writeShort(short value) throws IOException {
		write(new byte[] {
			(byte) (value >> 8),
			(byte) (value     )
		});
	}
	
	public default void writeInteger(int value) throws IOException {
		write(new byte[] {
			(byte) (value >> 24),
			(byte) (value >> 16),
			(byte) (value >>  8),
			(byte) (value      )
		});
	}
	
	public default void writeLong(long value) throws IOException {
		write(new byte[] {
			(byte) (value >> 56),
			(byte) (value >> 48),
			(byte) (value >> 40),
			(byte) (value >> 32),
			(byte) (value >> 24),
			(byte) (value >> 16),
			(byte) (value >>  8),
			(byte) (value      )
		});
	}
	
	public default void writeFloat(float value) throws IOException {
		write(ByteBuffer.allocate(4).putFloat(value).array());
	}
	
	public default void writeDouble(double value) throws IOException {
		write(ByteBuffer.allocate(8).putDouble(value).array());
	}
	
	public default void writeString(String value, Charset charset) throws IOException {
		if(charset == StandardCharsets.UTF_16BE || charset == StandardCharsets.UTF_16LE)
			throw new UnsupportedEncodingException(charset.displayName());
		
		CharsetEncoder encoder = charset.newEncoder();
		if(!encoder.canEncode(value))
			throw new UnsupportedOperationException("Cannot encode \"" + value + "\" in " + charset.displayName());
		
		write(value.getBytes(charset));
		write(new byte[(int) Math.ceil(encoder.averageBytesPerChar())]);
	}
	
	public default void writeStringASCII(String value) throws IOException {
		writeString(value, StandardCharsets.US_ASCII);
	}
	
	public default void writeStringUTF8(String value) throws IOException {
		writeString(value, StandardCharsets.UTF_8);
	}
	
	public default void writeUUID(UUID value) throws IOException {
		writeLong(value.getMostSignificantBits());
		writeLong(value.getLeastSignificantBits());
	}
	
	public default <E> void writeCollection(Collection<E> collection, IDataWriter<E> dataWriter) throws IOException {
		writeInteger(collection.size());
		
		for(E data : collection)
			dataWriter.write(this, data);
	}
	
	public default <E> void writeIndex(List<E> list, @Nullable E object) throws IOException {
		if(object == null)
			writeInteger(-1);
		else
			writeInteger(list.indexOf(object));
	}
	
	public default <K, V> void writeMap(Map<K, V> map, IDataWriter<K> keyWriter, IDataWriter<V> valueWriter) throws IOException {
		writeInteger(map.size());
		
		for(Map.Entry<K, V> entry : map.entrySet()) {
			keyWriter.write(this, entry.getKey());
			valueWriter.write(this, entry.getValue());
		}
	}
	
	public default void writeObject(IBinarySerializable object) throws IOException {
		object.serialize(this);
		
		if(object.getClass().isAnnotationPresent(SerializeWithChecksum.class))
			writeInteger(object.hashCode());
	}
	
	public default void flush() throws IOException { }
	
	@Override
	public default void close() throws IOException { }
}
