package fr.she3py.iplacer.storage;

import java.io.IOException;
import java.io.OutputStream;

import fr.she3py.iplacer.ImagePlacer;
import fr.she3py.iplacer.storage.base.CompositionStack;
import fr.she3py.iplacer.storage.base.IBinaryWriter;
import fr.she3py.iplacer.util.Arguments;

public class BinaryStreamWriter implements IBinaryWriter {
	private final OutputStream outputStream;
	private final CompositionStack stack;
	
	public BinaryStreamWriter(OutputStream outputStream) {
		Arguments.requireNonNull("outputStream", outputStream);
		
		this.outputStream = outputStream;
		this.stack = new CompositionStack();
	}
	
	@Override
	public void write(byte[] data, int offset, int length) throws IOException {
		outputStream.write(data, offset, length);
	}
	
	@Override
	public void writeByte(byte value) throws IOException {
		outputStream.write(value);
	}
	
	@Override
	public void close() throws IOException {
		outputStream.close();
		
		if(!stack.isEmpty())
			ImagePlacer.logger.warning("BinaryStreamWriter#close(): CompositionStack is not empty");
	}
	
	@Override
	public void flush() throws IOException {
		outputStream.flush();
	}
	
	@Override
	public CompositionStack stack() {
		return stack;
	}
}
