package fr.she3py.iplacer.storage;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import fr.she3py.iplacer.util.Arguments;
import fr.she3py.iplacer.util.Misc;

public class BinaryFileWriter extends BinaryStreamWriter {
	public BinaryFileWriter(File file) throws IOException {
		super(new BufferedOutputStream(new FileOutputStream(file)));
	}
	
	public static Safe safeWriter(File file) throws IOException {
		File parent = file.getParentFile();
		if(parent != null && !parent.exists())
			Arguments.require(parent.mkdirs(), "Unable to create the parent directory");
		
		File oldFile = Misc.withInfix(file, "-old");
		File intermediateFile = Misc.withInfix(file, "-new");
		
		if(file.exists()) {
			Files.copy(file.toPath(), oldFile.toPath(), StandardCopyOption.COPY_ATTRIBUTES, StandardCopyOption.REPLACE_EXISTING);
			
			Arguments.require(file.delete(), "Unable to delete \"" + file.getName() + "\" for overwriting");
		}
		
		return new BinaryFileWriter.Safe(file, intermediateFile, oldFile);
	}
	
	public static class Safe extends BinaryFileWriter {
		private final File targetFile;
		private final File intermediateFile;
		private final File oldFile;
		
		private Safe(File targetFile, File intermediateFile, File oldFile) throws IOException {
			super(intermediateFile);
			
			this.targetFile = targetFile;
			this.intermediateFile = intermediateFile;
			this.oldFile = oldFile;
		}
		
		public void terminate() throws IOException {
			this.close();
			
			Files.copy(intermediateFile.toPath(), targetFile.toPath(), StandardCopyOption.COPY_ATTRIBUTES);
			
			Arguments.require(intermediateFile.delete(), "Cannot delete \"" + intermediateFile.getName() + '"');
			Arguments.require(!oldFile.exists() || oldFile.delete(), "Cannot delete \"" + oldFile.getName() + '"');
		}
	}
}
