package com.steffbeard.totalwar.core.listeners;

import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import com.steffbeard.totalwar.core.KeyAPI;
import org.bukkit.block.Chest;
import org.bukkit.block.BlockFace;
import org.bukkit.Material;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.Listener;

public class HopperListener implements Listener
{
    @EventHandler(priority = EventPriority.LOWEST)
    private final void onBlockPlace(final BlockPlaceEvent event) {
        if (event.isCancelled()) {
            return;
        }
        if (event.getItemInHand().getType() != Material.HOPPER) {
            return;
        }
        final Block block = event.getBlock();
        final Block up = block.getRelative(BlockFace.UP);
        final Block down = block.getRelative(BlockFace.DOWN);
        if ((up.getState() instanceof Chest && KeyAPI.hasPadlock(up.getLocation(), true)) || (down.getState() instanceof Chest && KeyAPI.hasPadlock(down.getLocation(), true))) {
            KeyAPI.sendMessage((CommandSender)event.getPlayer(), KeyAPI.getMessages().message3);
            event.setCancelled(true);
        }
    }
}
