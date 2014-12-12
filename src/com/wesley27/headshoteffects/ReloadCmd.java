package com.wesley27.headshoteffects;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ReloadCmd implements CommandExecutor {
	Headshoteffects plugin;

	ReloadCmd(Headshoteffects plugin) {
		this.plugin = plugin;
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		String cmd = command.getName();

		if (cmd.equals("headshoteffects")) {
			if (args.length >= 1)
				if (args[0].equals("reload")) {
					if (sender instanceof Player) {
						Player player = (Player) sender;

						if (!(player.hasPermission("headshoteffects.reload"))) {
							player.sendMessage(ChatColor.RED + "[HeadshotEffects] You don't have Permissions to do this.");
							return true;
						}
					}

					plugin.reloadConfig();
					sender.sendMessage(ChatColor.RED + "[HeadshotEffects] Config reloaded.");

					return true;
				}
		}
		return false;
	}
}