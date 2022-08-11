package com.technicjelle.bluemapplayercontrol.commands;

import de.bluecolored.bluemap.api.BlueMapAPI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BMPC implements CommandExecutor, TabCompleter {

	public BMPC() {
	}

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
		if (BlueMapAPI.getInstance().isPresent()) {
			BlueMapAPI api = BlueMapAPI.getInstance().get();

			api.getWebApp().setPlayerVisibility(sender.getServer().getPlayerUniqueId(sender.getName()), false);
			return true;
		}
		return false;
	}


	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		return null;
	}
}
