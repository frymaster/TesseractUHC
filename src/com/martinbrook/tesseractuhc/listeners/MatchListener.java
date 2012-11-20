package com.martinbrook.tesseractuhc.listeners;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

import org.bukkit.ChatColor;

import com.martinbrook.tesseractuhc.MatchPhase;
import com.martinbrook.tesseractuhc.UhcMatch;
import com.martinbrook.tesseractuhc.UhcPlayer;
import com.martinbrook.tesseractuhc.notification.DamageNotification;

public class MatchListener implements Listener {
	private UhcMatch m;
	public MatchListener(UhcMatch m) { this.m = m; }
	

		
	/**
	 * Handle death events; add bonus items, if any.
	 * 
	 * @param pDeath
	 */
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e){
		Player p = e.getEntity();
		
		// If it's a pvp kill, drop bonus items
		if (p.getKiller() != null) {
			ItemStack bonus = m.getKillerBonus();
			if (bonus != null)
				e.getDrops().add(bonus);
		}
		
		// Make death message red
		String msg = e.getDeathMessage();
		e.setDeathMessage(ChatColor.RED + msg);
		
		// Save death point
		if (msg.indexOf("fell out of the world") == -1)
			m.setLastDeathLocation(p.getLocation());
		
		// Handle the death
		UhcPlayer up = m.getUhcPlayer(p);
		if (up != null && up.isLaunched() && !up.isDead() && m.getMatchPhase() == MatchPhase.MATCH)
			m.handlePlayerDeath(up);

	}

	@EventHandler
	public void onRespawn(PlayerRespawnEvent e) {
		// Only do anything if match is in progress
		if (m.getMatchPhase() != MatchPhase.MATCH) return;
		
		Player p = e.getPlayer();
		
		// If they're a dead UHC player, put them into adventure mode and make sure they respawn at overworld spawn
		UhcPlayer up = m.getUhcPlayer(p);
		
		if (up != null) {
			if (up.isDead()) {
				p.setGameMode(GameMode.ADVENTURE);
				e.setRespawnLocation(m.getStartingWorld().getSpawnLocation());
			}
		}
	}
	


	
	@EventHandler(ignoreCancelled = true)
	public void onEntityDamage(EntityDamageEvent e) {
		// Only interested in players taking damage
		if (e.getEntityType() != EntityType.PLAYER) return;
		
		// Only interested if match is in progress. Cancel damage if not.
		if (m.getMatchPhase() != MatchPhase.MATCH) {
			e.setCancelled(true);
			return;
		}
		
		// Only interested in registered players
		UhcPlayer up = m.getUhcPlayer((Player) e.getEntity());
		if (up == null) return;
		
		m.sendNotification(new DamageNotification(up, e.getDamage(), e.getCause()), e.getEntity().getLocation());
		
	}
	
	
	@EventHandler(ignoreCancelled = true)
	public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
		// Only interested in players taking damage
		if (e.getEntityType() != EntityType.PLAYER) return;
		
		// Only interested if match is in progress. Cancel damage if not.
		if (m.getMatchPhase() != MatchPhase.MATCH) {
			e.setCancelled(true);
			return;
		}
		
		// Only interested in registered players
		UhcPlayer up = m.getUhcPlayer((Player) e.getEntity());
		if (up == null) return;
		
		m.sendNotification(new DamageNotification(up, e.getDamage(), e.getCause(), e.getDamager()), e.getEntity().getLocation());
		
	}

	@EventHandler(ignoreCancelled = true)
	public void onBlockBreak(BlockBreakEvent e) {
		// Mining fatigue
		if (e.getBlock().getType() == Material.STONE) {
			m.doMiningFatigue(e.getPlayer(), e.getBlock().getLocation().getBlockY());
		}
	}
	
	

	
	
}