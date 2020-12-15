package fr.she3py.iplacer.storage;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

import fr.she3py.iplacer.ImagePlacer;
import fr.she3py.iplacer.storage.base.CompositionStack;
import fr.she3py.iplacer.storage.base.IBinaryReader;
import fr.she3py.iplacer.util.Arguments;

public class BinaryStreamReader implements IBinaryReader {
	private final InputStream inputStream;
	private final CompositionStack stack;
	
	public BinaryStreamReader(InputStream inputStream) {
		Arguments.requireNonNull("inputStream", inputStream);
		
		this.inputStream = inputStream;
		this.stack = new CompositionStack();
	}
	
	@Override
	public void read(byte[] data, int offset, int length) throws IOException {
		if(inputStream.read(data, offset, length) != length)
			throw new EOFException();
	}
	
	@Override
	public byte readByte() throws IOException {
		int data = inputStream.read();
		
		if(data == -1)
			throw new EOFException();
		
		return (byte) data;
	}
	
	@Override
	public long remaining() throws IOException {
		return inputStream.available();
	}
	
	@Override
	public void skip(long n) throws IOException {
		if(inputStream.skip(n) != n)
			throw new EOFException();
	}
	
	@Override
	public void close() throws IOException {
		if(!stack.isEmpty())
			ImagePlacer.logger.warning("BinaryStreamReader#close(): CompositionStack is not empty");
		
		long available = 0;
		try {
			available = remaining();
		}
		catch(Exception ignored) {}
		
		if(available != 0)
			throw new IOException("Closing the stream while there is still data");
		
		inputStream.close();
	}
	
	@Override
	public CompositionStack stack() {
		return stack;
	}
}
