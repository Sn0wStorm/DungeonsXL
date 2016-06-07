package io.github.dre2n.dungeonsxl.xserver;


import de.mickare.xserver.Message;
import de.mickare.xserver.XServerPlugin;
import de.mickare.xserver.net.XServer;
import io.github.dre2n.commons.util.messageutil.MessageUtil;
import io.github.dre2n.dungeonsxl.DungeonsXL;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class XMan {

	public static final String TARGET = "main";
	public static XServerPlugin x;

	public static void init() {
		x = (de.mickare.xserver.XServerPlugin) Bukkit.getServer().getPluginManager().getPlugin("XServer");
		x.getManager().getEventHandler().registerListenerUnsafe(DungeonsXL.getInstance(), new XListener());
	}

	public static void sendData(Message msg) {
		XServer target = x.getManager().getServer(TARGET);
		if (target == null) {
			MessageUtil.log(DungeonsXL.getInstance(), "Could not find Server: " + TARGET);
			return;
		}
		try {
			target.sendMessage(msg);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
