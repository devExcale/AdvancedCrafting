package ovh.excale.mc.advcraft;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import ovh.excale.mc.AdvancedCrafting;

import java.util.*;

public class Limiter {

	private static final Map<UUID, Limiter> map = Collections.synchronizedMap(new HashMap<>());
	private static final Map<NamespacedKey, Integer> limitedCrafts = new HashMap<>();

	private final Map<NamespacedKey, Integer> instances;

	private Limiter() {
		instances = Collections.synchronizedMap(new HashMap<>());
	}

	public static Limiter getLimiter(Player player) {
		Limiter limiter = map.get(player.getUniqueId());

		if(limiter == null)
			map.put(player.getUniqueId(), limiter = new Limiter());

		return limiter;
	}

	public static void limit(NamespacedKey key, int max) {
		limitedCrafts.put(key, max);
	}

	public boolean isLimited(NamespacedKey key) {
		Integer i = limitedCrafts.get(key);

		if(i != null && !instances.containsKey(key))
			instances.put(key, i);

		return i != null;
	}

	public int getRemaining(NamespacedKey key) {
		return limitedCrafts.getOrDefault(key, Integer.MAX_VALUE);
	}

	public void reduce(NamespacedKey key, int i) {
		Integer help = instances.get(key);
		if(help != null)
			instances.put(key, help - i);
	}

	public static class Listener implements org.bukkit.event.Listener {

		private static final Listener instance = new Listener();

		public static void start() {
			Bukkit.getPluginManager().registerEvents(instance, AdvancedCrafting.plugin());
		}

		public static void stop() {
			CraftItemEvent.getHandlerList().unregister(instance);
		}

		@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
		private void onCraft(CraftItemEvent event) {

			if(event.getRecipe() instanceof ShapedRecipe || event.getRecipe() instanceof ShapelessRecipe) {
				Player player = (Player) event.getWhoClicked();
				Limiter limiter = Limiter.getLimiter(player);
				NamespacedKey key;
				int amount;

				if(event.getRecipe() instanceof ShapedRecipe)
					key = ((ShapedRecipe) event.getRecipe()).getKey();
				else
					key = ((ShapelessRecipe) event.getRecipe()).getKey();

				if(!limiter.isLimited(key))
					return;

				//noinspection ConstantConditions
				amount = !event.isShiftClick() ? 1 : Arrays.stream(event.getInventory().getMatrix())
						.filter(Objects::nonNull)
						.mapToInt(ItemStack::getAmount)
						.min()
						.orElse(1);

				if(limiter.getRemaining(key) > amount) {
					player.sendMessage("You've maxed this recipe! (Or try not to use SHIFT-click)");
					event.setCancelled(true);
				} else
					limiter.reduce(key, amount);
			}
		}

	}

}
