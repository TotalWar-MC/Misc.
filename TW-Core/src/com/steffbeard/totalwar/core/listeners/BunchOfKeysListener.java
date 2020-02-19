package com.steffbeard.totalwar.core.listeners;

import org.bukkit.inventory.Inventory;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;
import com.steffbeard.totalwar.core.KeyAPI;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.Listener;

public class BunchOfKeysListener implements Listener
{
    @EventHandler(priority = EventPriority.HIGHEST)
    private final void onPlayerInteract(final PlayerInteractEvent event) {
        switch (event.getAction()) {
            case RIGHT_CLICK_BLOCK: {
                if (KeyAPI.hasPadlock(event.getClickedBlock().getLocation())) {
                    break;
                }
            }
            case RIGHT_CLICK_AIR: {
                KeyAPI.createInventory(event.getItem(), event.getPlayer());
                break;
            }
		case LEFT_CLICK_AIR:
			break;
		case LEFT_CLICK_BLOCK:
			break;
		case PHYSICAL:
			break;
		default:
			break;
        }
    }
    
    @EventHandler
    private final void onInventoryClick(final InventoryClickEvent event) {
        if (!KeyAPI.isBunchOfKeys(event.getInventory())) {
            return;
        }
        final ItemStack item = event.getCurrentItem();
        if (item.getType() != Material.AIR && !KeyAPI.isUsedKey(item)) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler
    private final void onInventoryClose(final InventoryCloseEvent event) {
        final Inventory inventory = event.getInventory();
        if (!KeyAPI.isBunchOfKeys(inventory)) {
            return;
        }
        final ItemStack bunchOfKeys = event.getPlayer().getInventory().getItemInMainHand();
        KeyAPI.clearKeys(bunchOfKeys);
        for (final ItemStack item : inventory.all(KeyAPI.getConfig().keyMaterial).values()) {
            KeyAPI.addKey(bunchOfKeys, item);
        }
    }
}
