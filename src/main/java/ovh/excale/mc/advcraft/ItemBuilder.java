package ovh.excale.mc.advcraft;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Objects;

public class ItemBuilder {

	private final ItemStack stack;
	private final ItemMeta meta;
	private final HashMap<Enchantment, Integer> enchants;

	public ItemBuilder(Material material) {
		Material material1 = Objects.requireNonNull(material);
		stack = new ItemStack(material);
		meta = stack.getItemMeta();
		enchants = new HashMap<>();
	}

	public ItemBuilder addEnchant(Enchantment enchantment, @Nullable Integer level) {

		if(enchantment != null)
			enchants.put(enchantment, level != null ? level : 1);

		return this;
	}

	public ItemBuilder setName(String name) {

		if(name != null)
			meta.setDisplayName(name);

		return this;
	}

	public ItemBuilder setLore(String[] lore) {

		if(lore != null)
			meta.setLore(Arrays.asList(lore.clone()));

		return this;
	}

	public ItemBuilder setCount(int count) {

		stack.setAmount(count);

		return this;
	}

	public ItemBuilder setUnbreakable(boolean unbreakable) {

		meta.setUnbreakable(unbreakable);

		return this;
	}

	public ItemStack build() {

		for(Entry<Enchantment, Integer> entry : enchants.entrySet())
			stack.addEnchantment(entry.getKey(), entry.getValue());

		return stack;
	}

}
