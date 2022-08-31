package com.technicjelle.bluemapplayercontrol.commands;

import de.bluecolored.bluemap.api.BlueMapAPI;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class BMPC implements CommandExecutor, TabCompleter {

	public BMPC() {
	}

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
		if (BlueMapAPI.getInstance().isPresent()) {
			BlueMapAPI api = BlueMapAPI.getInstance().get();

			// === SELF ===
			if(sender instanceof Player) { // only players can self
				UUID senderUUID = sender.getServer().getPlayerUniqueId(sender.getName());
				if (args.length == 0) {
					//TODO: Toggle self
					sender.sendMessage(ChatColor.YELLOW + "This command is not yet implemented. Please specify show/hide");
					return true;
				}
				if (args.length == 1) {
					if (args[0].equalsIgnoreCase("show")) {
						api.getWebApp().setPlayerVisibility(senderUUID, true);
						sender.sendMessage("You are now visible on the map");
						return true;
					} else if (args[0].equalsIgnoreCase("hide")) {
						api.getWebApp().setPlayerVisibility(senderUUID, false);
						sender.sendMessage("You are now invisible on the map");
						return true;
					}
				}
			}

			// === OTHER ===
			Player targetPlayer = sender.getServer().getPlayer(args[args.length - 1]); //if the last argument is a player name
			if (targetPlayer == null) {
				if(othersAllowed(sender)) {
					sender.sendMessage(ChatColor.YELLOW + "Player not found");
				} else {
					noPermissionWarning(sender);
				}
				return true;
			} else {
				if(othersAllowed(sender)) {
					UUID targetUUID = targetPlayer.getUniqueId();
					if (args.length == 1) {
						//TODO: Toggle other
						sender.sendMessage(ChatColor.YELLOW + "This command is not yet implemented. Please specify show/hide");
						return true;
					} else if (args[0].equalsIgnoreCase("show")) {
						api.getWebApp().setPlayerVisibility(targetUUID, true);
						sender.sendMessage(targetPlayer.getDisplayName() + " is now visible on the map");
						return true;
					} else if (args[0].equalsIgnoreCase("hide")) {
						api.getWebApp().setPlayerVisibility(targetUUID, false);
						sender.sendMessage(targetPlayer.getDisplayName() + " is now invisible on the map");
						return true;
					}
				} else {
					noPermissionWarning(sender);
					return true;
				}
			}
		}

		return false;
	}

	private void noPermissionWarning(CommandSender sender) {
		sender.sendMessage(ChatColor.RED + "You are don't have permission to change the visibility of others");
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		List<String> completions = new ArrayList<>();
		if (args.length == 1) {
			completions.add("show");
			completions.add("hide");
		}
		if (othersAllowed(sender)) {
			if (sender.getServer().getPlayer(args[0]) == null
					|| args[0].equalsIgnoreCase("show")
					|| args[0].equalsIgnoreCase("hide")
					|| args[0].isBlank()) {
				if(args.length <= 2) {
					for (Player player : sender.getServer().getOnlinePlayers()) {
						completions.add(player.getName());
					}
				}
			}
		}
		return completions;
	}

	private boolean othersAllowed(CommandSender sender) {
		return sender.isOp() || sender.hasPermission("bmpc.others");
	}
}
