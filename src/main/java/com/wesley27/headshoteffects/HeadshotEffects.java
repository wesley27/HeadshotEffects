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
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import net.md_5.bungee.api.ChatColor;

/**
 * Main class in the HeadshotEffects plugin, for handling headshots on a minecraft server.
 */
public class HeadshotEffects extends JavaPlugin implements Listener {

	private static HeadshotEffects instance;
	private Logger logger = Logger.getLogger("Minecraft");
	private MobHeadshot mh;
	private PlayerHeadshot ph;
	private FileConfiguration config;
	public boolean configNotifier = false;
	private final int CONFIG_VERSION = 7;

	/**
	 * Starts the plugin and everything needed for it to work as expected.
	 */
	public void onEnable() {
		instance = this;
		logger.info("[HeadshotEffects] HeadshotEffects Enabled");
		
		getServer().getPluginManager().registerEvents(this, this);
		getCommand("headshoteffects").setExecutor(new Cmd());

		config = getConfig();
		loadConfiguration();
		checkConfigVersion();
		
		mh = new MobHeadshot();
		ph = new PlayerHeadshot();
	}

	/**
	 * Disables the plugin.
	 */
	public void onDisable() {
		logger.info("[HeadshotEffects] HeadshotEffects Disabled");
	}

	/**
	 * Loads the HeadshotEffects configuration.
	 */
	public void loadConfiguration() {
		config.options().copyDefaults(true);
		saveDefaultConfig();
	}
	
	/**
	 * Determines if the configuration is the latest version.
	 * Warns the console if it is out of date.
	 */
	public void checkConfigVersion() {
		if(config.getInt("configVersion") != CONFIG_VERSION) {
			configNotifier = true;
			logger.warning("[HeadshotEffects] Your HeadshotEffects config.yml is out of date.");
			logger.warning("[HeadshotEffects] Some features may not work properly or at all.");
			logger.warning("[HeadshotEffects] Please delete your old config.yml and restart your server.");
			logger.warning("[HeadshotEffects] If you don't want to lose your config values, backup your outdated config.");
		}
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
	 * Notifies server administrators if the configuration is out of date.
	 * 
	 * @param event
	 */
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player p = event.getPlayer();
		if(configNotifier && p.hasPermission("headshoteffects.reload")) {
			p.sendMessage(ChatColor.RED + "[HeadshotEffects] Your HeadshotEffects config.yml is out of date. Some features may not work properly or at all.");
			p.sendMessage(ChatColor.RED + "[HeadshotEffects] Please delete your old config.yml and restart your server.");
			p.sendMessage(ChatColor.RED + "[HeadshotEffects] If you don't want to lose your config values, backup your outdated config.");
		}
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
		
		if(!config.getBoolean("UseAllProjectiles")) {
			if(!proj.getType().equals(EntityType.ARROW)) {
				return;
			}
		}

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