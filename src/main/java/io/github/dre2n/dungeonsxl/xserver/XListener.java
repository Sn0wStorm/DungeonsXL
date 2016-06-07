package io.github.dre2n.dungeonsxl.xserver;

import de.mickare.xserver.Message;
import de.mickare.xserver.XServerListener;
import de.mickare.xserver.annotations.XEventHandler;
import de.mickare.xserver.events.XServerMessageIncomingEvent;
import io.github.dre2n.commons.util.messageutil.MessageUtil;
import io.github.dre2n.dungeonsxl.DungeonsXL;
import io.github.dre2n.dungeonsxl.reward.DLootInventory;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class XListener implements XServerListener {

	private static DungeonsXL plugin = DungeonsXL.getInstance();

	@XEventHandler(sync = false)
	public void onMessage(XServerMessageIncomingEvent event) {
		Message msg = event.getMessage();

		if (!msg.getSubChannel().startsWith("DXL")) return;

		MessageUtil.log(plugin, "Msg incoming: " + msg.getSubChannel());

		DataInputStream in = new DataInputStream(new ByteArrayInputStream(msg.getContent()));
		String channel = msg.getSubChannel();

		try {
			if (channel.equals("DXL_GetInv")) {
				String name = in.readUTF();
				final DLootInventory loot = DLootInventory.getByName(name);
				if (loot != null) {
					plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
						@Override
						public void run() {
							loot.sendInventory();
						}
					});
				}
			} else if (channel.equals("DXL_GotInv")) {
				String name = in.readUTF();
				final DLootInventory loot = DLootInventory.getByName(name);
				if (loot != null) {
					plugin.getDLootInventories().remove(loot);
				}
			} else {
				MessageUtil.log(plugin, "Ignoring unknown Message!");
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
	}
}
