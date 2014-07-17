package com.wesley27.headshotsounds;

import java.util.logging.Logger;

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

public class headshotsounds extends JavaPlugin implements Listener {

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
		saveConfig();
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
			Entity p = victim;
			World w = p.getWorld();
			w.playSound(p.getLocation(), Sound.ZOMBIE_WOODBREAK, 2, 1);
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