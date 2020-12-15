package fr.she3py.iplacer.storage;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import fr.she3py.iplacer.ImagePlacer;
import fr.she3py.iplacer.util.Arguments;
import fr.she3py.iplacer.util.Misc;

public class BinaryFileReader extends BinaryStreamReader {
	public BinaryFileReader(File file) throws FileNotFoundException {
		super(new BufferedInputStream(new FileInputStream(file)));
	}
	
	public static BinaryFileReader safeWriteReader(File file) throws IOException {
		File oldFile = Misc.withInfix(file, "-old");
		File intermediateFile = Misc.withInfix(file, "-new");
		
		if(!file.exists() && oldFile.exists()) {
			ImagePlacer.logger.warning("Last write of \"" + file.getName() + "\" failed, restoring...");
			
			Files.copy(oldFile.toPath(), file.toPath(), StandardCopyOption.COPY_ATTRIBUTES);
			
			Arguments.require(oldFile.delete(), "Cannot delete \"" + oldFile.getName() + '"');
		}
		
		if(intermediateFile.exists())
			Arguments.require(intermediateFile.delete(), "Cannot delete \"" + intermediateFile.getName() + '"');
		
		return new BinaryFileReader(file);
	}
}
