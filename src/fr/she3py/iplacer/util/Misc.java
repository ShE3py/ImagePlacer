package fr.she3py.iplacer.util;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Misc {
	public static Path withInfix(Path original, String infix) {
		return original.resolveSibling(
			original.getFileName().toString()
					.replaceFirst("(.*?)(\\.[^.]+)?$", "$1" + infix + "$2")
		);
	}
	
	public static File withInfix(File original, String infix) {
		return withInfix(Paths.get(original.toURI()), infix).toFile();
	}
}
