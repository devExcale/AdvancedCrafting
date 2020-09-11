package ovh.excale.mc.advcraft;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.Nullable;
import ovh.excale.mc.AdvancedCrafting;
import ovh.excale.mc.advcraft.exceptions.ArgumentParseException;
import ovh.excale.mc.advcraft.exceptions.MissingArgumentException;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class CraftingRecipe {

	private final Type type;
	private final NamespacedKey key;
	private final HashMap<Enchantment, Integer> enchantments;
	private final HashMap<String, Material> materials;
	private String[] shape;
	private ItemStack result;

	public static @Nullable CraftingRecipe parse(File file) throws MissingArgumentException, ArgumentParseException {
		CraftingRecipe recipe = null;

		if(!file.isDirectory() && file.getName().endsWith(".yml")) {
			FileConfiguration conf = YamlConfiguration.loadConfiguration(file);

			if(conf.contains("AdvancedCrafting")) {

				String key = conf.getString("AdvancedCrafting.Id"), typeString = conf.getString("AdvancedCrafting.Type");
				Type type;

				try {
					Objects.requireNonNull(key);
				} catch(NullPointerException e) {
					throw new MissingArgumentException(file, "Key", e);
				}

				try {
					type = Type.valueOf(Objects.requireNonNull(typeString).toUpperCase());
				} catch(NullPointerException e) {
					throw new MissingArgumentException(file, "Type", e);
				} catch(IllegalArgumentException e) {
					throw new ArgumentParseException(file, "Type", typeString, e);
				}

				recipe = new CraftingRecipe(type, new NamespacedKey(AdvancedCrafting.plugin(), key));

				if(type.equals(Type.SHAPED)) {

					List<String> shape = conf.getStringList("AdvancedCrafting.Recipe.Shape");
					if(shape.size() == 0)
						throw new MissingArgumentException(recipe, "Shape");
					recipe.shape = shape.toArray(new String[3]);

					String materialName;
					for(String s : recipe.shape)
						if(s != null)
							for(String c : s.split("")) {
								if(c.equals(" "))
									continue;

								materialName = conf.getString("AdvancedCrafting.Recipe.Ingredients." + c);

								try {
									recipe.materials.put(c, Material.valueOf(Objects.requireNonNull(materialName).toUpperCase()));
								} catch(NullPointerException e) {
									throw new MissingArgumentException(recipe, "Ingredients." + c, e);
								} catch(IllegalArgumentException e) {
									throw new ArgumentParseException(recipe, "Ingredients." + c, materialName, e);
								}
							}
				} else {
					String materialName = null;
					try {

						List<String> materials = conf.getStringList("AdvancedCrafting.Recipe.Ingredients");
						if(materials.size() == 0)
							throw new MissingArgumentException(recipe, "AdvancedCrafting.Recipe.Ingredients");


						for(String m : materials) {
							materialName = m;
							recipe.materials.put(materialName, Material.valueOf(materialName.toUpperCase()));
						}

					} catch(IllegalArgumentException e) {
						throw new ArgumentParseException(recipe, "Ingredients", materialName, e);
					}
				}

				Material itemMaterial;
				String itemName = conf.getString("AdvancedCrafting.Result.Item");

				try {
					Objects.requireNonNull(itemName);
					itemMaterial = Material.valueOf(itemName.toUpperCase());
				} catch(NullPointerException e) {
					throw new MissingArgumentException(recipe, "Result.Item", e);
				} catch(IllegalArgumentException e) {
					throw new ArgumentParseException(recipe, "Result.Item", itemName, e);
				}

				ItemStack itemStack = new ItemStack(itemMaterial, conf.getInt("AdvancedCrafting.Result.Count", 1));
				ItemMeta meta = itemStack.getItemMeta();
				//noinspection ConstantConditions
				meta.setDisplayName(conf.getString("AdvancedCrafting.Result.Name"));
				meta.setUnbreakable(conf.getBoolean("AdvancedCrafting.Result.Unbreakable"));
				meta.setLore(conf.getStringList("AdvancedCrafting.Result.Lore"));

				if(meta instanceof PotionMeta) {
					PotionMeta potionMeta = (PotionMeta) meta;
					potionMeta.setColor(Color.GREEN);

					for(Map<?, ?> map : conf.getMapList("AdvancedCrafting.Result.Effects")) {
						String effectName = (String) map.get("Key");
						if(effectName == null)
							continue;

						PotionEffectType potionEffectType = PotionEffectType.getByName(effectName);
						if(potionEffectType == null)
							continue;

						Integer duration = (Integer) map.get("Duration"), level = (Integer) map.get("Level");
						duration = (duration == null || duration <= 0) ? 1200 : duration * 20;
						level = (level == null || level <= 0) ? 0 : level - 1;

						potionMeta.addCustomEffect(new PotionEffect(potionEffectType, duration, level), true);
					}
				}

				for(Map<?, ?> map : conf.getMapList("AdvancedCrafting.Result.Enchant")) {
					String enchantName = (String) map.get("Key");
					Enchantment enchantment = Enchantment.getByKey(NamespacedKey.minecraft(enchantName.trim().toLowerCase()));

					if(enchantment == null) {
						AdvancedCrafting.plugin()
								.getLogger()
								.warning("Couldn't parse enchantment [" + enchantName + "] in crafting recipe [" + file.getName() + "]");
						continue;
					}

					Integer level = (Integer) map.get("Level");
					meta.addEnchant(enchantment, level != null ? level : 1, true);
				}

				itemStack.setItemMeta(meta);
				recipe.result = itemStack;
			}
		}

		return recipe;
	}

	public CraftingRecipe(Type type, NamespacedKey key) {
		this.type = type;
		this.key = key;
		enchantments = new HashMap<>();
		materials = new HashMap<>();

		shape = null;
		result = null;
	}

	public void register() {

		Recipe recipe;

		if(type.equals(Type.SHAPED)) {

			ShapedRecipe shapedRecipe = new ShapedRecipe(key, result);
			shapedRecipe.shape(shape);

			for(Map.Entry<String, Material> material : materials.entrySet())
				shapedRecipe.setIngredient(material.getKey().charAt(0), material.getValue());

			recipe = shapedRecipe;
		} else {

			ShapelessRecipe shapelessRecipe = new ShapelessRecipe(key, result);
			for(Material value : materials.values())
				shapelessRecipe.addIngredient(value);

			recipe = shapelessRecipe;
		}

		Bukkit.addRecipe(recipe);
	}

	public String getKey() {
		return key.getKey();
	}

	public enum Type {
		SHAPED,
		SHAPELESS
	}

}