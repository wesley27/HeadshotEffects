package com.wesley27.headshoteffects;

import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Arrow;
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

public class Headshoteffects extends JavaPlugin implements Listener {

	Logger logger = Logger.getLogger("Minecraft");

	public void onEnable() {
		logger.info("HeadshotEffects Enabled");
		getServer().getPluginManager().registerEvents(this, this);
		
		getCommand("headshoteffects").setExecutor(new ReloadCmd(this));

		loadConfiguration();
	}

	public void onDisable() {
		logger.info("HeadshotEffects Disabled");
	}

	public void loadConfiguration() {
		getConfig().options().copyDefaults(true);
		saveDefaultConfig();
	}

	@EventHandler(ignoreCancelled = true)
	public void onHeadShot(EntityDamageByEntityEvent event) {
		if (event.getCause() != DamageCause.PROJECTILE) {
			return;
		}

		Projectile proj = (Projectile) event.getDamager();

		if (!(proj.getShooter() instanceof Player)) {
			return;
		}

		Entity victim = event.getEntity();
		EntityType victimType = victim.getType();

		double projy = proj.getLocation().getY();
		double victimy = victim.getLocation().getY();
		boolean headshot = projy - victimy > getBodyheight(victimType);

		if (headshot) {
			if (!(victim instanceof Player)) {
				return;
			}
			Player shooter = (Player) proj.getShooter();
			LivingEntity victimn = (LivingEntity) victim;
			Player victima = (Player) victimn;
			World w = shooter.getWorld();

			if (getConfig().getBoolean("UseSpecificWorlds")) {
				if (getConfig().getStringList("Worlds").contains(w.getName())) {
					checkConfig(shooter, victima, w, event);
				}
			} else {
				checkConfig(shooter, victima, w, event);
			}
		}
	}

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
	
	private void checkConfig(Player shooter, Player victima, World w, EntityDamageByEntityEvent event) {
		String vnamevar = victima.getName();
		String snamevar = shooter.getName();
		
		if (!(getConfig().getString("ShooterMessage")).equalsIgnoreCase("none")) {
			String shootermsg = ChatColor.translateAlternateColorCodes('&', getConfig().getString("ShooterMessage"));

			shooter.sendMessage(shootermsg.replace("%victim", vnamevar));
		}
		
		if (getConfig().getDouble("ExtraDamage") != 0) {
			if (event.getDamager() instanceof Arrow) {
				if (getConfig().getBoolean("ExtraDamage.HelmOnly")) {
					if (victima.getInventory().getHelmet() != null) {
						double dmg = event.getDamage() + getConfig().getDouble("ExtraDamage");
						event.setDamage(dmg);
					}
				}
				else {
					double dmg = event.getDamage() + getConfig().getDouble("ExtraDamage");
					event.setDamage(dmg);
				}				
			}
		}

		if (getConfig().getBoolean("InstaKill")) {
			if (event.getDamager() instanceof Arrow) {
				if (getConfig().getBoolean("InstaKill.HelmOnly")) {
					if (victima.getInventory().getHelmet() != null) {
						event.setDamage(20);
					}
				}
				else {
					event.setDamage(20);
				}
			}
		}

		if (!(getConfig().getString("HeadshotSound")).equalsIgnoreCase("none")) {
			try {
				Sound hssound = Sound.valueOf(getConfig().getString("HeadshotSound"));
				w.playSound(shooter.getLocation(), hssound, 2, 1);
			} catch (IllegalArgumentException invalidsound) {
				shooter.sendMessage(ChatColor.RED + "[HeadshotSounds] An invalid sound name was entered in the config, notify an admin!");
			}
		}

		if (!(getConfig().getString("RunCommand")).equalsIgnoreCase("none")) {
			String runcmd = ChatColor.translateAlternateColorCodes('&', getConfig().getString("RunCommand"));

			getServer().dispatchCommand(getServer().getConsoleSender(), runcmd.replace("%shooter", snamevar).replace("%victim", vnamevar));
		}

		else {
			return;
		}
	}
}