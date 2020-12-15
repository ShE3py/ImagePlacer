package fr.she3py.iplacer.minecraft;

import static fr.she3py.iplacer.ImagePlacer.logger;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.StreamHandler;

import javax.imageio.ImageIO;

import fr.she3py.iplacer.ImagePlacer;
import fr.she3py.iplacer.util.Arguments;

public class Main {
	static {
		System.setProperty("java.util.logging.SimpleFormatter.format", "[%1$tF %1$tT] [%4$-7s] [%2$s] %5$s %6$s%n");
		
		Logger logger = Logger.getLogger("ImagePlacer | Minecraft");
		logger.setUseParentHandlers(false);
		
		//noinspection DoubleBraceInitialization
		logger.addHandler(new ConsoleHandler() {
			{
				this.setLevel(Level.ALL);
				this.setOutputStream(System.out);
			}
		});
		
		ImagePlacer.logger = logger;
	}
	
	public static void main(String[] args) {
		try {
			MinecraftGraphics graphics = MinecraftGraphics.createFrom(
				new File("1.16.3.zip"),
				new File("graphics.manifest")
			);
			
			MinecraftColorMap colorMap = graphics.makeColorMap();
			BufferedImage imageIn = read("in.png");
			
			MinecraftImage image = colorMap.map(imageIn);
			write(image.toAverageImage(), "out-avg-2.png");
			write(image.toDistanceImage(imageIn), "out-dist-2.png");
			write(image.toTiledImage(), "out-tiled-2.png");
			
			logger.info(colorMap.getGraphics().toString());
		}
		catch(Exception e) {
			logger.log(Level.SEVERE, "Uncaught exception", e);
		}
	}
	
	private static BufferedImage read(String fileIn) throws IOException {
		BufferedImage image = ImageIO.read(new File(fileIn));
		Arguments.requireNonNull("image", image);
		
		return image;
	}
	
	private static void write(BufferedImage imageIn, String fileOut) throws IOException {
		boolean ret = ImageIO.write(imageIn, "png", new File(fileOut));
		Arguments.require(ret, "No appropriate writer was found");
	}
}