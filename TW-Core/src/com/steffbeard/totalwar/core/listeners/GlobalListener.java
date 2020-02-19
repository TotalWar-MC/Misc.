package com.steffbeard.totalwar.core.listeners;

import org.bukkit.Location;
import org.bukkit.material.MaterialData;
import org.bukkit.block.BlockState;
import org.bukkit.block.Block;
import org.bukkit.event.block.Action;
import org.bukkit.material.TrapDoor;
import com.steffbeard.totalwar.core.utils.DoorUtils;
import org.bukkit.block.Chest;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.Inventory;
import org.bukkit.ChatColor;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.meta.ItemMeta;
import com.steffbeard.totalwar.core.Config;
import org.bukkit.inventory.ItemStack;
import org.bukkit.command.CommandSender;
import com.steffbeard.totalwar.core.KeyAPI;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.CraftingInventory;
import java.util.HashSet;
import org.bukkit.event.Listener;

public class GlobalListener implements Listener
{
    private final HashSet<CraftingInventory> padlockFinders;
    
    public GlobalListener() {
        this.padlockFinders = new HashSet<CraftingInventory>();
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    private final void onPrepareItemCraft(final PrepareItemCraftEvent event) {
        final CraftingInventory craftingTable = event.getInventory();
        final ItemStack result = craftingTable.getResult();
        final Player player = (Player)event.getView().getPlayer();
        final boolean isKeyClone = KeyAPI.getKeyCloneItem().equals((Object)result);
        if ((KeyAPI.isBlankKey(result) && !player.hasPermission("Key.craft.key")) || (KeyAPI.isMasterKey(result) && !player.hasPermission("Key.craft.masterkey")) || (isKeyClone && !player.hasPermission("Key.craft.keyclone")) || (KeyAPI.isBlankBunchOfKeys(result) && !player.hasPermission("Key.craft.bunchofkeys"))) {
            KeyAPI.sendMessage((CommandSender)player, KeyAPI.getMessages().messagePermission);
            event.getInventory().setResult((ItemStack)null);
            return;
        }
        final Config config = KeyAPI.getConfig();
        if (isKeyClone) {
            ItemStack key = null;
            ItemStack blankKey = null;
            for (final ItemStack item : craftingTable.all(config.keyMaterial).values()) {
                if (KeyAPI.isKey(item)) {
                    if (item.getAmount() == 2) {
                        continue;
                    }
                    if (KeyAPI.isUsedKey(item)) {
                        key = item;
                    }
                    else {
                        if (!KeyAPI.isBlankKey(item)) {
                            continue;
                        }
                        blankKey = item;
                    }
                }
            }
            if (key == null || blankKey == null) {
                craftingTable.setResult((ItemStack)null);
                return;
            }
            final ItemMeta meta = result.getItemMeta();
            meta.setLore(key.getItemMeta().getLore());
            result.setItemMeta(meta);
        }
    }
    
    @EventHandler
    private final void onInventoryClick(final InventoryClickEvent event) {
        if (event.isCancelled()) {
            return;
        }
        final Inventory inventory = event.getInventory();
        if (this.padlockFinders.contains(inventory)) {
            final ItemStack item = event.getCurrentItem();
            final HumanEntity player = event.getWhoClicked();
            try {
                player.getWorld().dropItemNaturally(player.getEyeLocation(), KeyAPI.getKey(KeyAPI.extractLocation(item)));
            }
            catch (Exception ex) {
                ex.printStackTrace();
                KeyAPI.sendMessage((CommandSender)player, ChatColor.RED + ex.getClass().getName());
            }
        }
    }
    
    @EventHandler
    private final void onInventoryClose(final InventoryCloseEvent event) {
        final Inventory inventory = event.getInventory();
        if (this.padlockFinders.contains(inventory)) {
            this.padlockFinders.remove(inventory);
        }
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    private final void onPlayerInteract(final PlayerInteractEvent event) {
        if (event.isCancelled()) {
            return;
        }
        final Block clicked = event.getClickedBlock();
        if (clicked == null) {
            return;
        }
        final BlockState state = clicked.getState();
        if (!(state instanceof Chest)) {
            final MaterialData data = state.getData();
            if (!DoorUtils.instanceOf(data) && !(data instanceof TrapDoor)) {
                return;
            }
        }
        final Action action = event.getAction();
        final ItemStack item = event.getItem();
        if (action == Action.LEFT_CLICK_BLOCK) {
            final boolean isBlankKey = KeyAPI.isBlankKey(item);
            if (!isBlankKey && !KeyAPI.isMasterKey(item)) {
                return;
            }
            event.setCancelled(true);
            final Player player = event.getPlayer();
            Label_0155: {
                if (isBlankKey) {
                    if (player.hasPermission("Key.use.key")) {
                        break Label_0155;
                    }
                }
                else if (player.hasPermission("Key.use.masterkey")) {
                    break Label_0155;
                }
                KeyAPI.sendMessage((CommandSender)player, KeyAPI.getMessages().messagePermission);
                return;
            }
            final Location location = clicked.getLocation();
            if (KeyAPI.hasPadlock(location)) {
                KeyAPI.sendMessage((CommandSender)player, KeyAPI.getMessages().message3);
                return;
            }
            KeyAPI.createPadlock(location, item);
            KeyAPI.sendMessage((CommandSender)player, KeyAPI.getMessages().message1);
        }
        else if (action == Action.RIGHT_CLICK_BLOCK) {
            final Location location2 = clicked.getLocation();
            if (!KeyAPI.hasPadlock(location2)) {
                return;
            }
            if (!KeyAPI.isValidKey(item, location2)) {
                KeyAPI.sendMessage((CommandSender)event.getPlayer(), KeyAPI.getMessages().message3);
                event.setCancelled(true);
            }
        }
    }
}
