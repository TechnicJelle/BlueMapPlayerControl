package com.technicjelle.bluemapplayercontrol;

import com.technicjelle.bluemapplayercontrol.commands.BMPC;
import de.bluecolored.bluemap.api.BlueMapAPI;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

@SuppressWarnings("unused")
public final class BlueMapPlayerControl extends JavaPlugin {

	@Override
	public void onEnable() {
		// Plugin startup logic
		getLogger().info("BlueMapPlayerControl enabled");
		BlueMapAPI.onEnable(blueMapAPI -> {
			getLogger().info("BlueMapAPI enabled");
			getLogger().info("BlueMapAPI version: " + blueMapAPI.getAPIVersion());
		});

		PluginCommand bmpc = Bukkit.getPluginCommand("bmpc");
		BMPC executor = new BMPC();
		if(bmpc != null) {
			bmpc.setExecutor(executor);
			bmpc.setTabCompleter(executor);
		} else {
			getLogger().warning("bmpc is null. This is not good");
		}

	}

	@Override
	public void onDisable() {
		// Plugin shutdown logic
		getLogger().info("BlueMapPlayerControl disabled");
	}
}
