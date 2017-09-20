package com.wesley27.headshoteffects;

import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class PlayerHeadshot {
	
	private HeadshotEffects plugin;
	private FileConfiguration config;
	
	public PlayerHeadshot() {
		plugin = HeadshotEffects.getInstance();
		config = plugin.getConfig();
	}
	
	public void checkConfig(Player shooter, Player victima, World w, EntityDamageByEntityEvent event) {
		
		String vnamevar = victima.getName();
		String snamevar = shooter.getName();
		String shooterm = config.getString("ShooterMessage");
		
		if(!(shooterm.equalsIgnoreCase("none"))) {
			String shootermsg = ChatColor.translateAlternateColorCodes('&', config.getString("ShooterMessage"));

			shooter.sendMessage(shootermsg.replace("%victim", vnamevar));
		} 
		
		if(!victima.hasPermission("headshoteffects.bypassextra")) {
			if(config.getDouble("DamageOptions.Players.ExtraDamage") != 0) {
				if(event.getDamager() instanceof Arrow) {
					if(config.getBoolean("DamageOptions.Players.NoHelmOnly")) {
						if(victima.getInventory().getHelmet() == null) {
							double dmg = event.getDamage() + config.getDouble("DamageOptions.Players.ExtraDamage");
							event.setDamage(dmg);
						}
					} else {
						double dmg = event.getDamage() + config.getDouble("DamageOptions.Players.ExtraDamage");
						event.setDamage(dmg);
					}				
				}
			}
			
			if(config.getBoolean("DamageOptions.Players.RandomExtraDamage")) {
				if(event.getDamager() instanceof Arrow) {
					if(config.getBoolean("DamageOptions.Players.NoHelmOnly")) {
						if(victima.getInventory().getHelmet() == null) {
							Random r = new Random();
							double dmg = event.getDamage() + r.nextInt(11);
							event.setDamage(dmg);
						}
					} else {
						Random r = new Random();
						double dmg = event.getDamage() + r.nextInt(11);
						event.setDamage(dmg);
					}
				}
			}
			
			if(config.getBoolean("DamageOptions.Players.InstaKill")) {
				if(event.getDamager() instanceof Arrow) {
					if(config.getBoolean("DamageOptions.Players.NoHelmOnly")) {
						if(victima.getInventory().getHelmet() == null) {
							event.setDamage(50);
						}
					} else {
						event.setDamage(50);
					}
				}
			}
		}
		
		if(!config.getString("ParticleEffect").equals("none")) {
			if(event.getDamager() instanceof Arrow) {
				w.playEffect(event.getDamager().getLocation(), Effect.valueOf(config.getString("ParticleEffect")), 4);
			}
		}

		if(!(config.getString("HeadshotSound")).equalsIgnoreCase("none")) {
			try {
				Sound hssound = Sound.valueOf(config.getString("HeadshotSound"));
				w.playSound(shooter.getLocation(), hssound, 2, 1);
			} catch (IllegalArgumentException invalidsound) {
				shooter.sendMessage(ChatColor.RED + "[HeadshotSounds] An invalid sound name was entered in the config, notify an admin!");
			}
		}

		if(!(config.getString("RunCommand")).equalsIgnoreCase("none")) {
			String runcmd = ChatColor.translateAlternateColorCodes('&', config.getString("RunCommand"));

			plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), runcmd.replace("%shooter", snamevar).replace("%victim", vnamevar));
		}else {
			return;
		}
	}
}
