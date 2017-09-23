package com.wesley27.headshoteffects;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginDescriptionFile;

public class Cmd implements CommandExecutor {
	private HeadshotEffects plugin;
	private PluginDescriptionFile pdf;
	private FileConfiguration config;
	
	private final String msgBorder = ChatColor.GOLD + "-" + ChatColor.YELLOW + "-" + ChatColor.GOLD + "-" + ChatColor.YELLOW + "-" + 
			ChatColor.GOLD + "-" + ChatColor.YELLOW + "-" + ChatColor.GOLD + "-" + ChatColor.YELLOW + "-" + 
			ChatColor.GOLD + "-" + ChatColor.YELLOW + "-" + ChatColor.GOLD + "-" + ChatColor.YELLOW + "-" + 
			ChatColor.GOLD + "-" + ChatColor.YELLOW + "-" + ChatColor.GOLD + "-" + ChatColor.YELLOW + "-" + 
			ChatColor.GOLD + "-" + ChatColor.YELLOW + "-" + ChatColor.GOLD + "-" + ChatColor.YELLOW + "-" + 
			ChatColor.GOLD + "-" + ChatColor.YELLOW + "-" + ChatColor.GOLD + "-" + ChatColor.YELLOW + "-" + 
			ChatColor.GOLD + "-" + ChatColor.YELLOW + "-" + ChatColor.GOLD + "-" + ChatColor.YELLOW + "-" + 
			ChatColor.GOLD + "-" + ChatColor.YELLOW + "-" + ChatColor.GOLD + "-";

	Cmd() {
		this.plugin = HeadshotEffects.getInstance();
		this.pdf = plugin.getDescription();
		this.config = plugin.getConfig();
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(args.length == 0) {
			sender.sendMessage(msgBorder);
			sender.sendMessage(ChatColor.YELLOW + "HeadshotEffects v" + pdf.getVersion() + " by wesley27");
			sender.sendMessage(msgBorder);
			if(plugin.configNotifier) {
				sender.sendMessage(ChatColor.YELLOW + "Config Version - " + ChatColor.RED + config.getInt("configVersion") + " (out of date, please delete and reload)");
			} else {
				sender.sendMessage(ChatColor.YELLOW + "Config Version - " + ChatColor.WHITE + config.getInt("configVersion"));
			}
			sender.sendMessage("");
			sender.sendMessage(ChatColor.YELLOW + "/headshoteffects reload " + ChatColor.WHITE + "- Reloads the configuration (currently disabled)");
			sender.sendMessage(msgBorder);
			
			return true;
		} else {
			if(args[0].equals("reload")) {
				sender.sendMessage(ChatColor.RED + "[HeadshotEffects] This command is currently disabled as a bug is worked out.");
				return false;
				
				/*String cmd = command.getName();

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
				return false;*/
			} else {
				sender.sendMessage(ChatColor.RED + "[HeadshotEffects] Unknown command. Try /headshoteffects");
				return false;
			}
		}
	}
}