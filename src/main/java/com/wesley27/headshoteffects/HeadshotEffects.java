package com.wesley27.headshoteffects;

import java.util.logging.Logger;

import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Main class in the HeadshotEffects plugin, for handling headshots on a minecraft server.
 */
public class HeadshotEffects extends JavaPlugin implements Listener {

	private static HeadshotEffects instance;
	private Logger logger = Logger.getLogger("Minecraft");
	private MobHeadshot mh;
	private PlayerHeadshot ph;
	private FileConfiguration config;

	/**
	 * Starts the plugin and everything needed for it to work as expected.
	 */
	public void onEnable() {
		instance = this;
		logger.info("HeadshotEffects Enabled");
		
		getServer().getPluginManager().registerEvents(this, this);
		getCommand("headshoteffects").setExecutor(new ReloadCmd(this));

		config = getConfig();
		loadConfiguration();
		
		mh = new MobHeadshot();
		ph = new PlayerHeadshot();
	}

	/**
	 * Disables the plugin.
	 */
	public void onDisable() {
		logger.info("HeadshotEffects Disabled");
	}

	/**
	 * Loads the HeadshotEffects configuration.
	 */
	public void loadConfiguration() {
		config.options().copyDefaults(true);
		saveDefaultConfig();
	}
	
	/**
	 * Obtains an instance of this plugin for any external class that may need it.
	 * 
	 * @return an instance of HeadshotEffects.
	 */
	public static HeadshotEffects getInstance() {
		return instance;
	}

	/**
	 * Handles any damage to an entity and determines if it is a headshot.
	 */
	@EventHandler(ignoreCancelled = true)
	public void onHeadShot(EntityDamageByEntityEvent event) {
		if(event.getCause() != DamageCause.PROJECTILE) {
			return;
		}

		Projectile proj = (Projectile) event.getDamager();

		if(!(proj.getShooter() instanceof Player)) {
			return;
		}

		Entity victim = event.getEntity();
		EntityType victimType = victim.getType();

		double projy = proj.getLocation().getY();
		double victimy = victim.getLocation().getY();
		boolean headshot = projy - victimy > getBodyheight(victimType);

		if(headshot) {
			Player shooter = (Player) proj.getShooter();
			World w = shooter.getWorld();
			
			if(!(victim instanceof Player)) {
				mh.checkConfig(shooter, victim, w, event);
				return;
			}
			
			LivingEntity victimn = (LivingEntity) victim;
			Player victima = (Player) victimn;
			if(shooter.equals(victima)) return;		//prevent self-shot

			if(!config.getBoolean("RequirePermission") || (config.getBoolean("RequirePermission") && shooter.hasPermission("headshoteffects.effect"))) {
				if(config.getBoolean("UseSpecificWorlds")) {
					if(config.getStringList("Worlds").contains(w.getName())) {
						ph.checkConfig(shooter, victima, w, event);
					}
				} else {
					ph.checkConfig(shooter, victima, w, event);
				}
			}
		}
	}

	/**
	 * Obtains the body height of an entity for calculating a headshot.
	 * 
	 * @param type the type of entity to check for.
	 * @return the body height of the entity. 	
	 */
	private double getBodyheight(EntityType type) {
		switch (type) {
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