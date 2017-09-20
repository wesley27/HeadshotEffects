package com.wesley27.headshoteffects;

import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class MobHeadshot {
	
	private HeadshotEffects plugin;
	private FileConfiguration config;
	
	public MobHeadshot() {
		plugin = HeadshotEffects.getInstance();
		config = plugin.getConfig();
	}
	
	public void checkConfig(Player shooter, Entity victima, World w, EntityDamageByEntityEvent event) {
		
		String vnamevar = victima.getName();
		String snamevar = shooter.getName();
		
		if(config.getDouble("DamageOptions.Mobs.ExtraDamage") != 0) {
			if (event.getDamager() instanceof Arrow) {
				double dmg = event.getDamage() + config.getDouble("DamageOptions.ExtraDamage");
				event.setDamage(dmg);
			}
		}
		
		if(config.getBoolean("DamageOptions.Mobs.RandomExtraDamage")) {
			if(event.getDamager() instanceof Arrow) {
				Random r = new Random();
				double dmg = event.getDamage() + r.nextInt(11);
				event.setDamage(dmg);
			}
		}
		
		if(config.getBoolean("DamageOptions.Mobs.InstaKill")) {
			if (event.getDamager() instanceof Arrow) {
				event.setDamage(50);
			}
		}
		
		if(!config.getString("ParticleEffect").equals("none")) {
			if(event.getDamager() instanceof Arrow) {
				w.playEffect(event.getDamager().getLocation(), Effect.valueOf((config.getString("ParticleEffect"))), 1);
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
		}

		else {
			return;
		}
	}
}
