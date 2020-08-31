package ovh.excale.mc.advcraft;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import ovh.excale.mc.AdvancedCrafting;
import ovh.excale.mc.advcraft.exceptions.AdvancedCraftingException;
import ovh.excale.mc.advcraft.exceptions.AttributeParseException;
import ovh.excale.mc.advcraft.exceptions.MissingPathException;

import java.util.HashMap;

public class CraftingParser {

	private final String fileName;

	private final Type type;
	private final NamespacedKey key;
	private final HashMap<String, Material> ingredients;
	private String[] shape;
	private ItemStack result;

	public CraftingParser(String type, String key, String fileName) throws AdvancedCraftingException {

		try {
			this.type = Type.valueOf(type.toUpperCase());
		} catch(IllegalArgumentException e) {
			throw new AttributeParseException("AdvancedCrafting.Type", fileName);
		}

		this.fileName = fileName;

		if(key == null)
			throw new MissingPathException("AdvancedCrafting.Id", fileName);

		this.key = new NamespacedKey(AdvancedCrafting.plugin(), key);
		ingredients = new HashMap<>();
	}

	public CraftingParser setShape(String[] shape) throws MissingPathException {
		if(shape == null)
			throw new MissingPathException("AdvancedCrafting.Recipe.Shape", fileName);
		this.shape = shape;

		return this;
	}

	public CraftingParser addIngredient(String bukkitName) throws AdvancedCraftingException {
		Material material = Material.getMaterial(bukkitName.toUpperCase());

		if(material == null)
			throw new AttributeParseException(bukkitName, fileName);
		ingredients.put(bukkitName, material);

		return this;
	}

	public CraftingParser addIngredient(String charBind, String bukkitName) throws AdvancedCraftingException {
		Material material = Material.getMaterial(bukkitName.toUpperCase());

		if(material == null)
			throw new AttributeParseException(bukkitName, fileName);
		ingredients.put(charBind, material);

		return this;
	}

	public CraftingParser setResult(ItemStack result) {

		this.result = result;

		return this;
	}

	public enum Type {
		SHAPED,
		SHAPELESS
	}

}
