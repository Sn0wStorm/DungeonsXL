package com.snow.dungeonreward;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import de.mickare.xserver.Message;

public class Reward implements InventoryHolder {
	private static Map <UUID, Reward> rewards = new HashMap<UUID, Reward>();

	private Inventory inv = null;
	private short tries = 3;
	private boolean done = false;
	private boolean tut;
	public long time;

	public Reward(boolean tut) {
		this.tut = tut;
	}

	public void readInv(DataInputStream in) {
		try {
			for (short num = in.readShort(); num > 0; num--) {
				short pos = in.readShort();

				if (in.readBoolean()) {
					YamlConfiguration serializedItem = new YamlConfiguration();
					try {
						serializedItem.loadFromString(in.readUTF());
						ItemStack item = serializedItem.getItemStack("i", null);
						if (item != null && !item.getType().equals(Material.AIR)) {
							inv.setItem(pos, item);
						}
					} catch (InvalidConfigurationException e) {
						e.printStackTrace();
					}
				}

				/*Map<String, Object> serializedItem = new LinkedHashMap<String, Object>();

				for (short size = in.readShort(); size > 0; size--) {
					try {
						serializedItem.put(in.readUTF(), in.readObject());
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
				}

				if (!serializedItem.isEmpty()) {
					inv.setItem(pos, ItemStack.deserialize(serializedItem));
				}*/
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		done = true;
	}

	@Override
	public Inventory getInventory() {
		return inv;
	}

	public synchronized boolean createInv() {
		if (inv != null) {
			return false;
		}
		inv = P.p.getServer().createInventory(this, 54, "Belohnungen");
		return true;
	}

	public synchronized boolean hasInv() {
		return inv != null;
	}

	public synchronized void askForInv(UUID player) {
		ByteArrayOutputStream b = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(b);
		byte[] data = null;

		try {
			out.writeLong(player.getMostSignificantBits());
			out.writeLong(player.getLeastSignificantBits());
			data = b.toByteArray();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

		if (data == null) {
			return;
		}

		Message msg = P.p.xServer.getManager().createMessage("DXL_GetInv", data);
		P.p.sendDxlMessage(msg);
	}

	public void sendGotInv(UUID player) {
		ByteArrayOutputStream b = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(b);
		byte[] data = null;

		try {
			out.writeLong(player.getMostSignificantBits());
			out.writeLong(player.getLeastSignificantBits());
			data = b.toByteArray();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

		if (data == null) {
			return;
		}

		Message msg = P.p.xServer.getManager().createMessage("DXL_GotInv", data);
		P.p.sendDxlMessage(msg);
	}

	public synchronized void show(Player player) {
		if (tut) {
			ItemStack[] items = inv.getContents();
			ItemStack[] in = Arrays.copyOf(items, 36);
			ItemStack[] armor = Arrays.copyOfRange(items, 36, 40);
			player.getInventory().setContents(in);
			player.getInventory().setArmorContents(armor);
			player.sendMessage(" ");
			player.sendMessage("§a####     §lTutorial geschafft!§a     ####");
			player.sendMessage("§a#        Besuche auch den /spawn       #");
			player.sendMessage("§a#####            Viel Spaß!            #####");
			player.sendMessage(" ");
			rewards.remove(player.getUniqueId());
		} else {
			time = System.currentTimeMillis();
			player.openInventory(inv);
		}
	}

	public synchronized boolean dec() {
		tries--;
		return tries > 0;
	}

	public synchronized boolean isDone() {
		return done;
	}

	public boolean isTut() {
		return tut;
	}

	public synchronized static Reward get(UUID player) {
		return rewards.get(player);
	}

	public synchronized static void remove(UUID player) {
		rewards.remove(player);
	}

	public static void clear() {
		rewards.clear();
	}

	public synchronized static void markReward(UUID player, boolean tut) {
		if (rewards.containsKey(player)) {
			return;
		}
		rewards.put(player, new Reward(tut));
	}
}
