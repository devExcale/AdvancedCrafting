package ovh.excale.mc;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import ovh.excale.mc.advcraft.CraftingRecipe;
import ovh.excale.mc.advcraft.exceptions.ArgumentParseException;
import ovh.excale.mc.advcraft.exceptions.MissingArgumentException;

import java.io.*;
import java.util.logging.Logger;

public class AdvancedCrafting extends JavaPlugin {

	private static Plugin plugin;
	private static File craftDir;
	private static boolean debug;

	private static String[] EXAMPLES = new String[] {
			"/examples/Shaped.yml", "/examples/Potion Shapeless.yml"
	};

	public static Plugin plugin() {
		return plugin;
	}

	@Override
	public void onEnable() {
		super.onEnable();
		Logger logger = getLogger();
		plugin = this;

		File configFile = new File(getDataFolder(), "config.yml");
		if(!configFile.exists()) {
			logger.info("Couldn't find config file, creating a new one");
			saveDefaultConfig();
		}

		debug = getConfig().getBoolean("debug");

		File exDir = new File(getDataFolder(), "examples");
		if(!exDir.exists() && !exDir.mkdirs() || !exDir.isDirectory())
			logger.warning("Couldn't get examples dir, aborting examples loading");
		else {

			for(String fileName : EXAMPLES) {
				try(InputStream is = getClass().getResourceAsStream(fileName); OutputStream os = new FileOutputStream(new File(
						getDataFolder(),
						fileName.substring(1)))) {

					int length;
					byte[] buffer = new byte[1024];
					while ((length = is.read(buffer)) > 0) {
						os.write(buffer, 0, length);
					}

				} catch(Exception e) {
					logger.severe(stringStackTrace(e));
				}
			}
		}

		craftDir = new File(getDataFolder(), "crafts");
		if(!craftDir.exists() && !craftDir.mkdirs() || !craftDir.isDirectory()) {
			logger.severe("Couldn't get crafts dir, aborting all actions");
			return;
		}

		File[] crafts = craftDir.listFiles(pathname -> !pathname.isDirectory() && pathname.getName().endsWith(".yml"));

		for(File file : crafts)
			try {

				CraftingRecipe recipe = CraftingRecipe.parse(file);
				if(recipe == null)
					logger.warning("Couldn't recognise file " + file.getName() + " as a recipe");
				else {
					recipe.register();
					logger.info("Loaded recipe " + recipe.getKey());
				}

			} catch(ArgumentParseException | MissingArgumentException e) {
				logger.warning(e.getMessage());

				if(debug)
					logger.severe(stringStackTrace(e));
			}
	}

	private static String stringStackTrace(Exception e) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		return sw.toString();
	}

}
