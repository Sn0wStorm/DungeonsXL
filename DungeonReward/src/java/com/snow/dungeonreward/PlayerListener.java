package java.com.snow.dungeonreward;

import java.util.ListIterator;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerListener implements Listener {

	@EventHandler(ignoreCancelled = true)
	public void onPlayerJoin(PlayerJoinEvent event) {
		final Player player = event.getPlayer();
		P.p.getServer().getScheduler().scheduleSyncDelayedTask(P.p, new AskRunnable(player.getName()), 10);
	}

	public class AskRunnable implements Runnable {
		private final String name;

		public AskRunnable(String name) {
			this.name = name;
		}

		@Override
		public void run() {
			Reward reward = Reward.get(name);
			if (reward != null) {
				Player p = P.p.getServer().getPlayerExact(name);
				if (p != null) {
					if (reward.isDone()) {
						reward.show(p);
					} else {
						if (!reward.hasInv()) {
							if (reward.isTut()) {
								// Do some after tutorial stuff
							}
							reward.askForInv(name);
						}
						if (reward.dec()) {
							P.p.getServer().getScheduler().scheduleSyncDelayedTask(P.p, new AskRunnable(name), 40);
						} else {
							Reward.remove(name);
							P.p.errorLog("Could not receive Inventory");
						}
					}
				}
			}
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onInventoryClose(InventoryCloseEvent event) {
		if (event.getInventory().getHolder() instanceof Reward) {
			if (event.getPlayer() instanceof Player) {
				final Reward reward = (Reward) event.getInventory().getHolder();
				final Player player = (Player) event.getPlayer();
				if (System.currentTimeMillis() - reward.time < 8000) {
					P.p.getServer().getScheduler().scheduleSyncDelayedTask(P.p, new Runnable() {
						@Override
						public void run() {
							player.openInventory(reward.getInventory());
						}
					}, 2);
				} else {
					Reward.remove(player.getName());
					ListIterator<ItemStack> iter = event.getInventory().iterator();
					Location loc = player.getLocation();
					World world = loc.getWorld();
					while (iter.hasNext()) {
						ItemStack stack = iter.next();
						if (stack != null && !stack.getType().equals(Material.AIR)) {
							world.dropItem(loc, stack);
						}
					}
				}
			}
		}
	}

}
