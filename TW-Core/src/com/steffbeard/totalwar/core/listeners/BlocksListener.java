package com.steffbeard.totalwar.core.listeners;

import org.bukkit.event.block.BlockRedstoneEvent;
import java.util.List;
import org.bukkit.block.Block;
import java.util.ArrayList;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityBreakDoorEvent;
import org.bukkit.entity.Player;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.Sound;
import org.bukkit.Material;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import com.steffbeard.totalwar.core.KeyAPI;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.Listener;

public class BlocksListener implements Listener
{
    @EventHandler(priority = EventPriority.LOWEST)
    private final void onBlockPlace(final BlockPlaceEvent event) {
        if (event.isCancelled()) {
            return;
        }
        final ItemStack item = event.getItemInHand();
        if (KeyAPI.isKey(item) || KeyAPI.isMasterKey(item) || KeyAPI.isBunchOfKeys(item)) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    private final void onBlockBreak(final BlockBreakEvent event) {
        if (event.isCancelled()) {
            return;
        }
        final Location location = event.getBlock().getLocation();
        if (!KeyAPI.hasPadlock(location)) {
            return;
        }
        final Player player = event.getPlayer();
        final ItemStack inHand = player.getInventory().getItemInMainHand();
        if (KeyAPI.isValidKey(inHand, location)) {
            if (!KeyAPI.isMasterKey(inHand)) {
                if (KeyAPI.getConfig().reusableKeys) {
                    int amount = 0;
                    if (KeyAPI.isUsedKey(inHand)) {
                        amount = inHand.getAmount();
                        player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
                    }
                    else if (KeyAPI.isBunchOfKeys(inHand)) {
                        amount = KeyAPI.removeKey(inHand, KeyAPI.getKey(location));
                    }
                    if (amount == 0) {
                        return;
                    }
                    final ItemStack key = KeyAPI.getKeyItem();
                    key.setAmount(amount);
                    player.getWorld().dropItemNaturally(player.getEyeLocation(), KeyAPI.getKeyItem());
                }
                else {
                    if (KeyAPI.isBunchOfKeys(inHand)) {
                        KeyAPI.removeKey(inHand, KeyAPI.getKey(location));
                    }
                    else {
                        player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
                    }
                    player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 1.0f, 1.0f);
                }
            }
            KeyAPI.removePadlock(location);
            KeyAPI.sendMessage((CommandSender)player, KeyAPI.getMessages().message2);
        }
        else {
            KeyAPI.sendMessage((CommandSender)player, KeyAPI.getMessages().message3);
        }
        event.setCancelled(true);
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    private final void onEntityBreakDoor(final EntityBreakDoorEvent event) {
        if (event.isCancelled()) {
            return;
        }
        if (KeyAPI.hasPadlock(event.getBlock().getLocation())) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    private final void onEntityExplode(final EntityExplodeEvent event) {
        if (event.isCancelled()) {
            return;
        }
        final List<Block> blocks = (List<Block>)event.blockList();
        for (final Block block : new ArrayList<Block>(blocks)) {
            if (KeyAPI.hasPadlock(block.getLocation())) {
                blocks.remove(block);
            }
        }
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    private final void onBlockRedstone(final BlockRedstoneEvent event) {
        if (KeyAPI.hasPadlock(event.getBlock().getLocation())) {
            event.setNewCurrent(0);
        }
    }
}
