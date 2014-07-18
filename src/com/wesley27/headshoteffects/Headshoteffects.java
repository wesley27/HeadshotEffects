package com.wesley27.headshoteffects;

import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.plugin.java.JavaPlugin;

public class Headshoteffects extends JavaPlugin implements Listener {

	Logger logger = Logger.getLogger("Minecraft");

	public void onEnable() {
		logger.info("HeadshotSounds Enabled");
		getServer().getPluginManager().registerEvents(this, this);
		
		loadConfiguration();
	}

	public void onDisable() {
		logger.info("HeadshotSounds Disabled");
	}
	
	public void loadConfiguration() {
		getConfig().options().copyDefaults(true);
		saveDefaultConfig();
	}

	@EventHandler(ignoreCancelled = true)
	public void onHeadShot(EntityDamageByEntityEvent event) {
		if(event.getCause() != DamageCause.PROJECTILE) {
			return;
		}
		
		Projectile proj = (Projectile)event.getDamager();
		
		if(!(proj.getShooter() instanceof Player)) {
			return;
		}
		
		Entity victim = event.getEntity();
		EntityType victimType = victim.getType();
		
		double projy = proj.getLocation().getY();
		double victimy = victim.getLocation().getY();
		boolean headshot = projy - victimy > getBodyheight(victimType);
		
		if(headshot) {
			Player shooter = (Player)proj.getShooter();
			Player victimn = (Player)victim;
			World w = shooter.getWorld();
			
			String vnamevar = victimn.getName();
			String snamevar = shooter.getName();
			
			if(!(getConfig().getString("ShooterMessage")).equalsIgnoreCase("none")) {
				String shootermsg = ChatColor.translateAlternateColorCodes('&', getConfig().getString("ShooterMessage"));
				
				shooter.sendMessage(shootermsg.replace("%victim", vnamevar));
			}
			
			if(!(getConfig().getString("HeadshotSound")).equalsIgnoreCase("none")) {
				try {
					Sound hssound = Sound.valueOf(getConfig().getString("HeadshotSound"));
					w.playSound(shooter.getLocation(), hssound, 2, 1);
				}
				catch(IllegalArgumentException invalidsound) {
					shooter.sendMessage(ChatColor.RED + "[HeadshotSounds] An invalid sound name was entered in the config, notify an admin!");
				}
			}
			
			if(!(getConfig().getString("RunCommand")).equalsIgnoreCase("none")) {
				String runcmd = ChatColor.translateAlternateColorCodes('&', getConfig().getString("RunCommand"));
				
				getServer().dispatchCommand(getServer().getConsoleSender(), runcmd
						.replace("%shooter", snamevar)
						.replace("%victim", vnamevar)
						);
			}
			
			else {
				return;
			}
		}		
	}
	
	private double getBodyheight(EntityType type) {
		switch(type) {
		case CREEPER:
		case ZOMBIE:
		case SKELETON:
		case PLAYER:
		case PIG_ZOMBIE:
		case VILLAGER:
			return 1.35d;
		default:
			return Float.POSITIVE_INFINITY;
		}
	}
}