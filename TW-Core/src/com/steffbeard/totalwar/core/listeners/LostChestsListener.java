package com.steffbeard.totalwar.core.listeners;

import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;
import org.bukkit.Location;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.command.CommandSender;
import org.bukkit.block.Chest;
import org.bukkit.event.inventory.InventoryClickEvent;
import com.steffbeard.totalwar.core.*;

public class LostChestsListener extends KeyListener
{
    public LostChestsListener(final Main plugin) {
        super(plugin);
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    private void onInventoryClick(final InventoryClickEvent event) {
        if (!KeyAPI.isUsedKey(event.getCurrentItem())) {
            return;
        }
        final InventoryHolder holder = event.getInventory().getHolder();
        if (!(holder instanceof Chest)) {
            return;
        }
        final Location location = ((Chest)holder).getLocation();
        if (KeyAPI.hasPadlock(location) && KeyAPI.extractLocation(event.getCurrentItem()).equals((Object)location)) {
            event.setCancelled(true);
            this.plugin.sendMessage((CommandSender)event.getWhoClicked(), this.plugin.getMessages().message6);
        }
    }
}
