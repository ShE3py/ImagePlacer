package fr.she3py.iplacer;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import fr.she3py.iplacer.bukkit.BukkitColorMap;

public class ImagePlacer extends JavaPlugin {
	public static ImagePlacer plugin;
	public static Logger logger;
	
	@Override
	public void onEnable() {
		plugin = this;
		logger = this.getLogger();
		
		Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
			try {
				BukkitColorMap colorMap = BukkitColorMap.build();
				colorMap.mapImage("in.png", "out.png");
				
				logger.info(colorMap.getGraphics().toString());
			}
			catch(IOException e) {
				logger.log(Level.SEVERE, "", e);
			}
		});
	}
}
