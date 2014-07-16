package com.wesley27.headshotsounds;

import java.util.logging.Logger;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
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
	}
	
	public void onDisable() {
		logger.info("HeadshotSounds Disabled");
	}
	
	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		if(event.getCause() != DamageCause.ENTITY_ATTACK) {
			return;
		}
		
		Player shooter = (Player) event.getDamager();
		
		if(!(shooter.getLastDamageCause() instanceof Player)) {
			return;
		}
		
		Entity arw = event.getEntity();
		
		Location l = shooter.getLocation();
		
		double y = shooter.getLocation().getY();
		double arwy = arw.getLocation().getY();
		boolean headshot = y - arwy > 1.40D;
		
		if(headshot) {
			World world = shooter.getWorld();
			world.playEffect(l,Effect.ZOMBIE_DESTROY_DOOR,0);
		}
	}

}
