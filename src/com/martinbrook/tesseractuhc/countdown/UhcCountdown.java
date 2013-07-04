package com.martinbrook.tesseractuhc.countdown;

import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;

import com.martinbrook.tesseractuhc.UhcMatch;

public abstract class UhcCountdown {

	protected int remainingSeconds = 0;
	protected int task = -1;
	private Plugin plugin;
	protected UhcMatch match;
	private Boolean active;

	
	public UhcCountdown(int countdownLength, Plugin plugin, UhcMatch match) {
		this.remainingSeconds = countdownLength;
		this.plugin = plugin;
		this.match = match;
		this.active = true;
		this.task = plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			public void run() {
				tick();
			}
		});
	}
	
	protected abstract void preWarn();
	protected abstract void nearing();
	protected abstract void complete();
	protected abstract String getDescription();
	
	public Boolean cancel() {
		if (!this.active) return false; 
		plugin.getServer().getScheduler().cancelTask(task);
		remainingSeconds = -1;
		this.active=false;
		return true;
	}
	
	private void tick() {
		if (remainingSeconds < 0) return;
		
		if (remainingSeconds == 0) {
			this.active=false;
			this.complete();
			return;
		}
		
		if (remainingSeconds == 120)
			this.preWarn();
		
		if (remainingSeconds == 90)
			this.nearing();
		
		if (remainingSeconds >= 60) {
			if (remainingSeconds % 60 == 0) {
				int minutes = remainingSeconds / 60;
				broadcast(ChatColor.LIGHT_PURPLE + this.getDescription() + " in " + minutes + " minute" + (minutes == 1? "":"s"));
			}
		} else if (remainingSeconds % 15 == 0) {
			broadcast(ChatColor.LIGHT_PURPLE + this.getDescription()  + " in " + remainingSeconds + " seconds");
		} else if (remainingSeconds <= 5) { 
			broadcast(ChatColor.LIGHT_PURPLE + "" + remainingSeconds + "...");
		}
		
		remainingSeconds--;
		this.task = plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			public void run() {
				tick();
			}
		}, 20L);
	}
	
	private void broadcast(String message) {
		plugin.getServer().broadcastMessage(message);
	}
	
	public Boolean isActive() {
		return this.active;
	}


}
