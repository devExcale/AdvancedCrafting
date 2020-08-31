package ovh.excale.mc;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.logging.Logger;

public class AdvancedCrafting extends JavaPlugin {

	private static Plugin plugin;

	public static Plugin plugin() {
		return plugin;
	}

	private File craftDir;

	@SuppressWarnings("ConstantConditions")
	@Override
	public void onEnable() {
		super.onEnable();
		Logger logger = getLogger();
		plugin = this;

		craftDir = new File(getDataFolder(), "crafts");

		if(!craftDir.exists() && !craftDir.mkdirs() || !craftDir.isDirectory()) {
			getLogger().severe("Couldn't get crafts dir, aborting all actions.");
			return;
		}

		File[] crafts = craftDir.listFiles(pathname -> !pathname.isDirectory() && pathname.getName().endsWith(".yml"));

		for(File file : crafts) {
			FileConfiguration craftConf = YamlConfiguration.loadConfiguration(file);
			String craftName = file.getName().replaceAll(".yml$", "");

			if(craftConf.contains("AdvancedCrafting")) {

				String namespace, key, displayName;
				namespace = craftConf.getString("AdvancedCrafting.Namespace");
				key = craftConf.getString("AdvancedCrafting.Key");

				if(!craftConf.contains("AdvancedCrafting.Recipe")) {
					logger.warning("Missing field AdvancedCrafting.Recipe in recipe " + craftName);
					continue;
				}

				String[] shape = (String[]) craftConf.getStringList("AdvancedCrafting.Recipe.Shape").toArray();
				if(shape.length != 3)
					;

			} else if(craftConf.contains("ShapelessRecipe")) {

			} else
				logger.warning("Couldn't load recipe " + craftName);

		}
	}

}
