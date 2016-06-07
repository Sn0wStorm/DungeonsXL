/*
 * Copyright (C) 2012-2016 Frank Baumann
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package io.github.dre2n.dungeonsxl.reward;

import de.mickare.xserver.Message;
import io.github.dre2n.dungeonsxl.DungeonsXL;
import io.github.dre2n.dungeonsxl.xserver.XMan;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

/**
 * @author Frank Baumann, Daniel Saukel
 */
public class DLootInventory {

    public static final Material[] blackListItems = new Material[] { Material.WEB };
    static DungeonsXL plugin = DungeonsXL.getInstance();

    //private Inventory inventory;
    //private InventoryView inventoryView;
    private final UUID playerId;
    public ItemStack[] items;

    //private long time;

    public DLootInventory(Player player, ItemStack[] itemStacks, final boolean tut) {
        this.playerId = player.getUniqueId();
        if (XMan.x == null) {
            return;
        }
        plugin.getDLootInventories().add(this);

        items = itemStacks;
        if (tut) {
            for (int i = 0; i < items.length; i++) {
                if (items[i] != null) {
                    for (Material mat : blackListItems) {
                        if (items[i].getType().equals(mat)) {
                            items[i] = null;
                            break;
                        }
                    }
                }
            }
        }
        //MessageUtil.log(plugin, "sending has reward");

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                //MessageUtil.log(plugin, "Yep doing it now!");
                markReward(tut);
                //MessageUtil.log(plugin, "done!");
            }
        });

        /*inventory = Bukkit.createInventory(player, 54, ChatColor.translateAlternateColorCodes('&', plugin.getMessageConfig().getMessage(DMessages.PLAYER_TREASURES)));
        for (ItemStack itemStack : itemStacks) {
            if (itemStack != null) {
                inventory.addItem(itemStack);
            }
        }
        this.player = player;*/
    }

    private void markReward(boolean tut) {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(b);
        byte[] data;

        try {
            out.writeLong(playerId.getMostSignificantBits());
            out.writeLong(playerId.getLeastSignificantBits());
            out.writeBoolean(tut);
            data = b.toByteArray();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        if (data == null) {
            return;
        }

        Message msg = XMan.x.getManager().createMessage("DXL_HasReward", data);
        XMan.sendData(msg);
    }

    public void sendInventory() {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(b);
        byte[] data;

        try {
            out.writeLong(playerId.getMostSignificantBits());
            out.writeLong(playerId.getLeastSignificantBits());
            out.writeShort(items.length);

            for (int i = 0; i < items.length; i++) {
                out.writeShort(i);

                if (items[i] == null || items[i].getType().equals(Material.AIR)) {
                    out.writeBoolean(false);
                    continue;
                }

                out.writeBoolean(true);
                YamlConfiguration serializedItem = new YamlConfiguration();
                serializedItem.options().indent(2);
                serializedItem.set("i", items[i]);

                out.writeUTF(serializedItem.saveToString());

				/*Map<String, Object> serializedItem = items[i].serialize();
				if (serializedItem == null || serializedItem.isEmpty()) {
					out.writeShort(0);
					continue;
				}

				out.writeShort(serializedItem.size());
				for (Map.Entry<String, Object> entry : serializedItem.entrySet()) {
					out.writeUTF(entry.getKey());
					out.writeObject(entry.getValue());
				}*/

            }


            data = b.toByteArray();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        if (data == null) {
            return;
        }

        Message msg = XMan.x.getManager().createMessage("DXL_Inv", data);
        XMan.sendData(msg);
    }

    /**
     * @return the inventory
     */
    /*public Inventory getInventory() {
        return inventory;
    }*/

    /**
     * @param inventory
     * the inventory to set
     */
    /*public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }*/

    /**
     * @return the inventoryView
     */
    /*public InventoryView getInventoryView() {
        return inventoryView;
    }*/

    /**
     * @param inventoryView
     * the inventoryView to set
     */
    /*public void setInventoryView(InventoryView inventoryView) {
        this.inventoryView = inventoryView;
    }*/

    /**
     * @return the player
     */
    /*public Player getPlayer() {
        return player;
    }*/

    /**
     * @param player
     * the player to set
     */
/*
    public void setPlayer(Player player) {
        this.player = player;
    }*/

    /**
     * @return the time
     */
   /*public long getTime() {
        return time;
    }*/

    /**
     * @param time
     * the time to set
     */
    /*public void setTime(long time) {
        this.time = time;
    }*/

    // Static
    /**
     * @param id
     * the playerUUID whose DLootIntentory will be returned
     */
    public static DLootInventory getById(UUID id) {
        for (DLootInventory inventory : plugin.getDLootInventories()) {
            if (inventory.playerId.equals(id)) {
                return inventory;
            }
        }

        return null;
    }

}
