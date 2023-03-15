package com.technicjelle.bluemapplayercontrol.commands;

import de.bluecolored.bluemap.api.BlueMapAPI;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public class BMPC implements CommandExecutor, TabCompleter {

	private List<String> playerNames;
	private long cacheAge;

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
			//Suppressing deprecation warnings for Server::getOfflinePlayer(String), because we are only using the Name to look up the UUID, not as a unique identifier
			@SuppressWarnings("deprecation")
			OfflinePlayer targetPlayer = sender.getServer().getOfflinePlayer(args[args.length - 1]); //if the last argument is a player name
			if (!targetPlayer.hasPlayedBefore()) { //targetPlayer can't be null so this is the easiest way to verify that a player with the given name actually exists
				if(othersAllowed(sender)) {
					sender.sendMessage(ChatColor.YELLOW + "Player not found");
				} else {
					noPermissionWarning(sender);
				}
				return true;
			} else {
				if(othersAllowed(sender)) {
					String displayName = targetPlayer.getName();
					Player onlinePlayer = targetPlayer.getPlayer();
					if(onlinePlayer != null)
						displayName = onlinePlayer.getDisplayName();
					UUID targetUUID = targetPlayer.getUniqueId();
					if (args.length == 1) {
						//TODO: Toggle other
						sender.sendMessage(ChatColor.YELLOW + "This command is not yet implemented. Please specify show/hide");
						return true;
					} else if (args[0].equalsIgnoreCase("show")) {
						api.getWebApp().setPlayerVisibility(targetUUID, true);
						sender.sendMessage(displayName + " is now visible on the map");
						return true;
					} else if (args[0].equalsIgnoreCase("hide")) {
						api.getWebApp().setPlayerVisibility(targetUUID, false);
						sender.sendMessage(displayName + " is now invisible on the map");
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
					if(playerNames == null|| cacheAge < System.currentTimeMillis() - 60000){
						playerNames = Arrays.stream(sender.getServer().getOfflinePlayers())
								.map(OfflinePlayer::getName)
								.collect(Collectors.toList());
						cacheAge = System.currentTimeMillis();
					}
					completions.addAll(playerNames.stream()
							.filter(name -> name.toLowerCase().startsWith(args[args.length - 1].toLowerCase()))
							.collect(Collectors.toList()));
				}
			}
		}
		return completions;
	}

	private boolean othersAllowed(CommandSender sender) {
		return sender.isOp() || sender.hasPermission("bmpc.others");
	}
}
