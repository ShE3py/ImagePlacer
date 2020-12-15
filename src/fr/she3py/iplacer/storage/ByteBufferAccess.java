package fr.she3py.iplacer.storage;

import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;

import fr.she3py.iplacer.storage.base.CompositionStack;
import fr.she3py.iplacer.storage.base.IBinaryReader;
import fr.she3py.iplacer.storage.base.IBinaryWriter;


public class ByteBufferAccess implements IBinaryReader, IBinaryWriter {
	private final ByteBuffer buff;
	private final CompositionStack stack;
	private int written;
	
	private ByteBufferAccess(ByteBuffer buff, int written) {
		this.buff = buff;
		this.stack = new CompositionStack();
		this.written = written;
	}
	
	public static ByteBufferAccess of(ByteBuffer buff, int written) {
		return new ByteBufferAccess(buff, written);
	}
	
	public static ByteBufferAccess of(ByteBuffer buff) {
		return of(buff, buff.position());
	}
	
	public static ByteBufferAccess allocate(int capacity) {
		return of(ByteBuffer.allocate(capacity), 0);
	}
	
	public static ByteBufferAccess wrap(byte[] buff, int written) {
		ByteBufferAccess access = allocate(buff.length);
		access.buff.put(buff);
		access.buff.position(written);
		access.written = written;
		
		return access;
	}
	
	@Override
	public void write(byte[] data, int offset, int length) throws BufferOverflowException {
		buff.put(data, offset, length);
		
		written += length;
	}
	
	@Override
	public void writeByte(byte value) throws BufferOverflowException {
		buff.put(value);
		
		++written;
	}
	
	@Override
	public void read(byte[] data, int offset, int length) throws BufferUnderflowException {
		if(length > remaining())
			throw new BufferUnderflowException();
		
		buff.get(data, offset, length);
	}
	
	@Override
	public byte readByte() throws BufferUnderflowException {
		if(remaining() == 0)
			throw new BufferUnderflowException();
		
		return buff.get();
	}
	
	@Override
	public long remaining() {
		return Math.max(written - buff.position(), 0);
	}
	
	public int available() {
		return buff.remaining();
	}
	
	public int position() {
		return buff.position();
	}
	
	public int capacity() {
		return buff.capacity();
	}
	
	@Override
	public void skip(long n) {
		seek(buff.position() + (int) n);
	}
	
	public ByteBufferAccess seek(int n) {
		if(n > written)
			throw new BufferUnderflowException();
		
		buff.position(n);
		return this;
	}
	
	public void clear() {
		buff.clear();
		stack.clear();
		written = 0;
	}
	
	@Override
	public CompositionStack stack() {
		return stack;
	}
	
	@Override
	public void close() {
	
	}
}
