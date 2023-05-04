package com.technicjelle.bluemapplayercontrol.commands;

import de.bluecolored.bluemap.api.BlueMapAPI;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BMPC implements CommandExecutor, TabCompleter {

	public BMPC() {
	}

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
		if (BlueMapAPI.getInstance().isPresent()) {
			BlueMapAPI api = BlueMapAPI.getInstance().get();

			// === SELF ===
			if (sender instanceof Player) { // only players can self
				UUID senderUUID = sender.getServer().getPlayerUniqueId(sender.getName());
				if (args.length == 0) {
					//toggle
					if (api.getWebApp().getPlayerVisibility(senderUUID)) {
						hideSelf(api, sender, senderUUID);
					} else {
						showSelf(api, sender, senderUUID);
					}
					return true;
				}
				if (args.length == 1) {
					if (args[0].equalsIgnoreCase("show")) {
						showSelf(api, sender, senderUUID);
						return true;
					} else if (args[0].equalsIgnoreCase("hide")) {
						hideSelf(api, sender, senderUUID);
						return true;
					}
				}
			} else {
				if (args.length == 0) {
					sender.sendMessage(ChatColor.RED + "You must be a player to hide yourself");
					return true;
				}
			}

			// === OTHER ===
			Player targetPlayer = sender.getServer().getPlayer(args[args.length - 1]); //if the last argument is a player name
			if (targetPlayer == null) {
				if (othersAllowed(sender)) {
					sender.sendMessage(ChatColor.YELLOW + "Player not found");
				} else {
					noPermissionWarning(sender);
				}
				return true;
			} else {
				if (othersAllowed(sender)) {
					if (args.length == 1) {
						//toggle
						if (api.getWebApp().getPlayerVisibility(targetPlayer.getUniqueId())) {
							hideOther(api, sender, targetPlayer);
						} else {
							showOther(api, sender, targetPlayer);
						}
						return true;
					} else if (args[0].equalsIgnoreCase("show")) {
						showOther(api, sender, targetPlayer);
						return true;
					} else if (args[0].equalsIgnoreCase("hide")) {
						hideOther(api, sender, targetPlayer);
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

	private static void showSelf(BlueMapAPI blueMapAPI, CommandSender sender, UUID senderUUID) {
		blueMapAPI.getWebApp().setPlayerVisibility(senderUUID, true);
		sender.sendMessage("You are now " + ChatColor.AQUA + "visible" + ChatColor.RESET + " on the map");
	}

	private static void hideSelf(BlueMapAPI blueMapAPI, CommandSender sender, UUID senderUUID) {
		blueMapAPI.getWebApp().setPlayerVisibility(senderUUID, false);
		sender.sendMessage("You are now " + ChatColor.GOLD + "invisible" + ChatColor.RESET + " on the map");
	}

	private static void showOther(BlueMapAPI api, @NotNull CommandSender sender, Player targetPlayer) {
		api.getWebApp().setPlayerVisibility(targetPlayer.getUniqueId(), true);
		sender.sendMessage(targetPlayer.getDisplayName() + " is now " + ChatColor.AQUA + "visible" + ChatColor.RESET + " on the map");
	}

	private static void hideOther(BlueMapAPI api, @NotNull CommandSender sender, Player targetPlayer) {
		api.getWebApp().setPlayerVisibility(targetPlayer.getUniqueId(), false);
		sender.sendMessage(targetPlayer.getDisplayName() + " is now " + ChatColor.GOLD + "invisible" + ChatColor.RESET + " on the map");
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
			if (othersAllowed(sender)) {
				for (Player player : sender.getServer().getOnlinePlayers()) {
					completions.add(player.getName());
				}
			}
		}
		if (othersAllowed(sender)) {
			if (sender.getServer().getPlayer(args[0]) == null
					|| args[0].equalsIgnoreCase("show")
					|| args[0].equalsIgnoreCase("hide")
					|| args[0].isBlank()) {
				if (args.length <= 2) {
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
