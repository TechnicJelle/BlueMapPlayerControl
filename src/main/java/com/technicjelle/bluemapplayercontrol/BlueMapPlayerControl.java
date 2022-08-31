package com.technicjelle.bluemapplayercontrol;

import com.technicjelle.bluemapplayercontrol.commands.BMPC;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

@SuppressWarnings("unused")
public final class BlueMapPlayerControl extends JavaPlugin {
	BMPC executor;

	@Override
	public void onEnable() {
		getLogger().info("BlueMapPlayerControl enabled");

		PluginCommand bmpc = Bukkit.getPluginCommand("bmpc");
		executor = new BMPC();
		if(bmpc != null) {
			bmpc.setExecutor(executor);
			bmpc.setTabCompleter(executor);
		} else {
			getLogger().warning("bmpc is null. This is not good");
		}

	}

	@Override
	public void onDisable() {
		getLogger().info("BlueMapPlayerControl disabled");
	}
}
