package ovh.excale.mc;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import ovh.excale.mc.advcraft.CraftingRecipe;
import ovh.excale.mc.advcraft.exceptions.ArgumentParseException;
import ovh.excale.mc.advcraft.exceptions.MissingArgumentException;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Logger;

public class AdvancedCrafting extends JavaPlugin {

	private static Plugin plugin;
	private static File craftDir;
	private static boolean debug;

	public static Plugin plugin() {
		return plugin;
	}

	@Override
	public void onEnable() {
		super.onEnable();
		Logger logger = getLogger();
		plugin = this;

		File configFile = new File(getDataFolder(), "config.yml");
		if(!configFile.exists())
			saveDefaultConfig();

		debug = getConfig().getBoolean("debug");
		craftDir = new File(getDataFolder(), "crafts");

		if(!craftDir.exists() && !craftDir.mkdirs() || !craftDir.isDirectory()) {
			getLogger().severe("Couldn't get crafts dir, aborting all actions.");
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

				if(true) {

					StringWriter sw = new StringWriter();
					PrintWriter pw = new PrintWriter(sw);
					e.printStackTrace(pw);

					logger.severe(sw.toString());
				}
			}
	}

}
