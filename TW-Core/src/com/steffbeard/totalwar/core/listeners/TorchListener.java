/*
 * 
package com.steffbeard.totalwar.core.listeners;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

@SuppressWarnings({"unused", "unlikely-arg-type"})
public class TorchListener implements Listener {

	 *
	 * Makes torchs work like flint and steals for the aesthetic
	 *
	
	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		Action action = event.getAction();
		Player player = event.getPlayer();
		ItemStack mainhand = ((PlayerInventory) event).getItemInMainHand();
		
		if(mainhand.equals(Material.TORCH)) {
			if(action.equals(Action.LEFT_CLICK_BLOCK)) {
				
			}
			
		}
	}
}
*/