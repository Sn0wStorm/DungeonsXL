package java.com.snow.dungeonreward;


import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import de.mickare.xserver.Message;
import de.mickare.xserver.XServerPlugin;
import de.mickare.xserver.net.XServer;

public class P extends JavaPlugin {

	final static String DXL = "dxl";

	public static P p;
	public XServerPlugin xServer;
	public XListener xListener;
	public PlayerListener playerListener;
	//public int id = 0;

	@Override
	public void onEnable() {
		p = this;

		if (!getServer().getPluginManager().isPluginEnabled("XServer")) {
			p = null;
			log("XServer not installed! Disabling");
			return;
		}

		xServer = (XServerPlugin) getServer().getPluginManager().getPlugin("XServer");
		// Listeners
		xListener = new XListener();
		xServer.getManager().getEventHandler().registerListenerUnsafe(this, xListener);

		playerListener = new PlayerListener();
		getServer().getPluginManager().registerEvents(playerListener, this);

		//getCommand("DungeonReward").setExecutor(new CommandListener());

		readConfig();
		loadData();

		// Heartbeat


		this.log(this.getDescription().getName() + " enabled!");
	}

	@Override
	public void onDisable() {
		if (p == null) {
			return;
		}
		clearData();

		// Stop shedulers
		p.getServer().getScheduler().cancelTasks(this);

		this.log(this.getDescription().getName() + " disabled!");
	}

	public void reload() {

	}

	public void clearData() {
		Reward.clear();
		HandlerList.unregisterAll(p);
		getServer().getScheduler().cancelTasks(P.p);
		xServer.getManager().getEventHandler().unregisterListener(xListener);
	}

	public void loadData() {
	}

	public void readConfig() {

	}

	public boolean sendDxlMessage(Message msg) {
		XServer target = P.p.xServer.getManager().getServer(P.DXL);
		if (target == null) {
			P.p.log("Could not find Server: " + P.DXL);
			return false;
		}
		try {
			target.sendMessage(msg);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public void msg(CommandSender sender, String msg) {
		sender.sendMessage(color(msg));
	}

	public void log(String msg) {
		this.msg(Bukkit.getConsoleSender(), "[DungeonReward] " + msg);
	}

	public void errorLog(String msg) {
		this.msg(Bukkit.getConsoleSender(), "&4[ERROR][DungeonReward] " + msg);
	}

	public String color(String msg) {
		if (msg != null) {
			msg = ChatColor.translateAlternateColorCodes('&', msg);
		}

		return msg;
	}

	public String removeColor(String string) {
		return ChatColor.stripColor(string);
	}

}

