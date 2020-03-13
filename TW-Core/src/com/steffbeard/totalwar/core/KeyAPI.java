package com.steffbeard.totalwar.core;

import org.bukkit.inventory.InventoryHolder;
import java.util.ArrayList;
import java.util.Arrays;
import org.bukkit.World;
import com.steffbeard.totalwar.core.utils.ROT47;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.List;
import com.steffbeard.totalwar.core.utils.KeyUtils;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Bukkit;

public class KeyAPI
{
    private static final Main plugin;
    
    static {
        plugin = (Main)Bukkit.getPluginManager().getPlugin("Main");
    }
    
    public static final Main getPlugin() {
        return KeyAPI.plugin;
    }
    
    public static final Config getConfig() {
        return KeyAPI.plugin.config;
    }
    
    public static final Messages getMessages() {
        return KeyAPI.plugin.messages;
    }
    
    public static final ItemStack getKeyItem() {
        return KeyAPI.plugin.key.clone();
    }
    
    public static final ItemStack getMasterKeyItem() {
        return KeyAPI.plugin.masterKey.clone();
    }
    
    public static final ItemStack getKeyCloneItem() {
        return KeyAPI.plugin.keyClone.clone();
    }
    
    public static final ItemStack getPadlockFinderItem() {
        return KeyAPI.plugin.padlockFinder.clone();
    }
    
    public static final void sendMessage(final CommandSender sender, final String message) {
        sender.sendMessage(String.valueOf(KeyAPI.plugin.messages.prefix) + " " + message);
    }
    
    public static final void createPadlock(final Location location) {
        createPadlock(location, null);
    }
    
    public static final void createPadlock(final Location location, final ItemStack key) {
        KeyUtils.correctLocation(location);
        KeyAPI.plugin.data.padlocks.add(location);
        if (isBlankKey(key)) {
            formatItem(location, key);
        }
    }
    
    public static final void removePadlock(final Location location) {
        removePadlock(location, null);
    }
    
    public static final void removePadlock(final Location location, final ItemStack key) {
        KeyUtils.correctLocation(location);
        KeyAPI.plugin.data.padlocks.remove(location);
        if (isUsedKey(key)) {
            final ItemMeta meta = key.getItemMeta();
            meta.setLore((List<String>)null);
            key.setItemMeta(meta);
        }
    }
    
    public static final boolean hasPadlock(final Location location) {
        return hasPadlock(location, true);
    }
    
    public static final boolean hasPadlock(final Location location, final boolean correctLocation) {
        if (correctLocation) {
            KeyUtils.correctLocation(location);
        }
        return KeyAPI.plugin.data.padlocks.contains(location);
    }
    
    public static final boolean isKey(final ItemStack item) {
        if (KeyUtils.isValidItem(item) && item.getType() == KeyAPI.plugin.config.keyMaterial) {
            if (!KeyAPI.plugin.config.canRenameItems) {
                if (!item.getItemMeta().getDisplayName().equals(KeyAPI.plugin.config.keyName)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
    
    public static final boolean isBlankKey(final ItemStack item) {
        return isKey(item) && !item.getItemMeta().hasLore();
    }
    
    public static final boolean isUsedKey(final ItemStack item) {
        if (!isKey(item)) {
            return false;
        }
        final List<String> lore = (List<String>)item.getItemMeta().getLore();
        return lore != null && lore.size() == 2;
    }
    
    public static final boolean isMasterKey(final ItemStack item) {
        if (KeyUtils.isValidItem(item) && item.getType() == KeyAPI.plugin.config.masterKeyMaterial) {
            if (!KeyAPI.plugin.config.canRenameItems) {
                if (!item.getItemMeta().getDisplayName().equals(KeyAPI.plugin.config.masterKeyName)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
    
    public static final boolean isBunchOfKeys(final ItemStack item) {
        if (KeyUtils.isValidItem(item) && item.getType() == KeyAPI.plugin.config.bunchOfKeysMaterial) {
            if (!KeyAPI.plugin.config.canRenameItems) {
                if (!item.getItemMeta().getDisplayName().equals(KeyAPI.plugin.config.bunchOfKeysName)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
    
    public static final boolean isBunchOfKeys(final Inventory inventory) {
        return inventory.getName().equals(KeyAPI.plugin.config.bunchOfKeysName) && inventory.getSize() == 9;
    }
    
    public static final boolean isBlankBunchOfKeys(final ItemStack item) {
        return isBunchOfKeys(item) && !item.getItemMeta().hasLore();
    }
    
    public static final boolean isUsedBunchOfKeys(final ItemStack item) {
        return isBunchOfKeys(item) && item.getItemMeta().hasLore();
    }
    
    public static final boolean isValidKey(final ItemStack key, final Location location) {
        return isValidKey(key, location, null);
    }
    
    public static final boolean isValidKey(final ItemStack key, final Location location, final Player player) {
        if (isMasterKey(key)) {
            if (player != null && !player.hasPermission("Key.use.masterkey")) {
                sendMessage((CommandSender)player, KeyAPI.plugin.messages.messagePermission);
            }
            return true;
        }
        KeyUtils.correctLocation(location);
        try {
            final Location keyLocation = extractLocation(key);
            if (keyLocation != null && keyLocation.equals((Object)location)) {
                if (player != null && !player.hasPermission("Key.use.key")) {
                    sendMessage((CommandSender)player, KeyAPI.plugin.messages.messagePermission);
                }
                return true;
            }
            final ItemStack[] extractedKeys = extractKeys(key);
            if (extractedKeys != null) {
                if (player != null && !player.hasPermission("Key.use.bunchofkeys")) {
                    sendMessage((CommandSender)player, KeyAPI.plugin.messages.messagePermission);
                    return true;
                }
                ItemStack[] array;
                for (int length = (array = extractedKeys).length, i = 0; i < length; ++i) {
                    final ItemStack extractedKey = array[i];
                    if (isValidKey(extractedKey, location, null)) {
                        return true;
                    }
                }
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }
    
    public static final Location extractLocation(final ItemStack item) {
        final boolean isKey = isUsedKey(item);
        final List<String> lore = (List<String>)item.getItemMeta().getLore();
        String loreWorld = ChatColor.stripColor((String)lore.get(0));
        String loreLocation = ChatColor.stripColor((String)lore.get(1));
        if (KeyAPI.plugin.config.encryptLore) {
            try {
                loreWorld = ROT47.rotate(loreWorld);
                loreLocation = ROT47.rotate(loreLocation);
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        final World world = Bukkit.getWorld(loreWorld);
        if (world == null) {
            return null;
        }
        final String[] rawLocation = loreLocation.split(", ");
        if (rawLocation.length != 3) {
            return null;
        }
        final Location itemLocation = new Location(world, (double)Integer.parseInt(rawLocation[0]), (double)Integer.parseInt(rawLocation[1]), (double)Integer.parseInt(rawLocation[2]));
        if (isKey && KeyUtils.correctLocation(itemLocation)) {
            formatItem(itemLocation, item);
        }
        return itemLocation;
    }
    
    public static final ItemStack getKey(final Location location) {
        final ItemStack key = getKeyItem();
        formatItem(location, key);
        return key;
    }
    
    public static final ItemStack getPadlockFinder(final Location location) {
        final ItemStack padlockFinder = getPadlockFinderItem();
        formatItem(location, padlockFinder);
        return padlockFinder;
    }
    
    public static final void formatItem(final Location location, final ItemStack item) {
        final ChatColor color = KeyUtils.randomChatColor(ChatColor.BOLD, ChatColor.ITALIC, ChatColor.UNDERLINE, ChatColor.STRIKETHROUGH, ChatColor.MAGIC);
        final ItemMeta meta = item.getItemMeta();
        String loreWorld = location.getWorld().getName();
        String loreLocation = String.valueOf(location.getBlockX()) + ", " + location.getBlockY() + ", " + location.getBlockZ();
        if (KeyAPI.plugin.config.encryptLore) {
            try {
                loreWorld = ROT47.rotate(loreWorld);
                loreLocation = ROT47.rotate(loreLocation);
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        meta.setLore((List<String>)Arrays.asList(color + loreWorld, color + loreLocation));
        item.setItemMeta(meta);
    }
    
    public static final void addKey(final ItemStack bunchOfKeys, final ItemStack key) {
        if (!isBunchOfKeys(bunchOfKeys) || !isUsedKey(key)) {
            return;
        }
        final ItemMeta meta = bunchOfKeys.getItemMeta();
        final List<String> lore = meta.hasLore() ? new ArrayList<String>(meta.getLore()) : new ArrayList<String>();
        lore.addAll(key.getItemMeta().getLore());
        meta.setLore((List<String>)lore);
        bunchOfKeys.setItemMeta(meta);
    }
    
    public static final short removeKey(final ItemStack bunchOfKeys, final ItemStack key) {
        if (!isUsedBunchOfKeys(bunchOfKeys) || !isUsedKey(key)) {
            return 0;
        }
        final ItemMeta meta = bunchOfKeys.getItemMeta();
        final List<String> lore = (List<String>)meta.getLore();
        final List<String> keyLore = (List<String>)key.getItemMeta().getLore();
        for (final String line : new ArrayList<String>(keyLore)) {
            keyLore.remove(line);
            keyLore.add(ChatColor.stripColor(line));
        }
        short deleted = 0;
        for (int i = 0; i != lore.size(); ++i) {
            final String world = lore.get(i);
            final String location = lore.get(++i);
            if (keyLore.equals(Arrays.asList(ChatColor.stripColor(world), ChatColor.stripColor(location))) && lore.removeAll(Arrays.asList(world, location))) {
                ++deleted;
            }
        }
        meta.setLore((List<String>)((lore.size() == 0) ? null : lore));
        bunchOfKeys.setItemMeta(meta);
        return deleted;
    }
    
    public static final void clearKeys(final ItemStack bunchOfKeys) {
        if (!isUsedBunchOfKeys(bunchOfKeys)) {
            return;
        }
        final ItemMeta meta = bunchOfKeys.getItemMeta();
        meta.setLore((List<String>)null);
        bunchOfKeys.setItemMeta(meta);
    }
    
    public static final ItemStack[] extractKeys(final ItemStack bunchOfKeys) {
        if (!isUsedBunchOfKeys(bunchOfKeys)) {
            return null;
        }
        final List<String> lore = (List<String>)bunchOfKeys.getItemMeta().getLore();
        final List<ItemStack> keys = new ArrayList<ItemStack>();
        for (int i = 0; i != lore.size(); ++i) {
            final ItemStack blankKey = getKeyItem();
            final ItemMeta blankMeta = blankKey.getItemMeta();
            blankMeta.setLore((List<String>)Arrays.asList(lore.get(i), lore.get(++i)));
            blankKey.setItemMeta(blankMeta);
            keys.add(blankKey);
        }
        return keys.toArray(new ItemStack[keys.size()]);
    }
    
    public static final Inventory createInventory(final ItemStack bunchOfKeys) {
        return createInventory(bunchOfKeys, new Player[0]);
    }
    
    public static final Inventory createInventory(final ItemStack bunchOfKeys, final Player... players) {
        if (!isBunchOfKeys(bunchOfKeys)) {
            return null;
        }
        final Inventory inventory = Bukkit.createInventory((InventoryHolder)null, 9, getConfig().bunchOfKeysName);
        final ItemStack[] keys = extractKeys(bunchOfKeys);
        if (keys != null && keys.length != 0) {
            inventory.addItem(keys);
        }
        if (players != null && players.length != 0) {
            for (final Player player : players) {
                player.openInventory(inventory);
            }
        }
        return inventory;
    }
}
