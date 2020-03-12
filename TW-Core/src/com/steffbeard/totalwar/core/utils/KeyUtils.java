package com.steffbeard.totalwar.core.utils;

import org.bukkit.Material;
import org.bukkit.util.BlockIterator;
import org.bukkit.entity.LivingEntity;
import org.bukkit.material.MaterialData;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.block.BlockState;
import org.bukkit.block.Block;
import org.bukkit.material.TrapDoor;
import org.bukkit.block.DoubleChest;
import org.bukkit.block.Chest;
import org.bukkit.Location;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.ItemStack;
import org.bukkit.ChatColor;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Collection;
import java.util.Map;
import java.util.List;
import java.util.Random;
import java.util.Arrays;

import com.steffbeard.totalwar.core.KeyAPI;
import com.steffbeard.totalwar.core.utils.DoorUtils;

public class KeyUtils
{
    public static final ChatColor randomChatColor(final ChatColor... exclude) {
        final List<ChatColor> excludeList = Arrays.asList(exclude);
        final ChatColor[] values = ChatColor.values();
        final Random random = new Random();
        ChatColor randomColor;
        do {
            randomColor = values[random.nextInt(values.length)];
        } while (excludeList.contains(randomColor));
        return randomColor;
    }
    
    public static final <V> Map<String, V> keepAll(final Map<String, V> map, final Collection<String> objects) {
        final Map<String, V> result = new HashMap<String, V>();
        for (final String object : objects) {
            final char[] chars = new char[object.length()];
            object.getChars(0, (object.length() > 3) ? 3 : object.length(), chars, 0);
            char[] array;
            for (int length = (array = chars).length, i = 0; i < length; ++i) {
                final char c = array[i];
                if (c != ' ') {
                    final String character = String.valueOf(c);
                    if (map.containsKey(character)) {
                        result.put(character, map.get(character));
                    }
                }
            }
        }
        return result;
    }
    
    public static final boolean isValidItem(final ItemStack item) {
        if (item != null && item.hasItemMeta()) {
            final ItemMeta meta = item.getItemMeta();
            return meta.hasDisplayName();
        }
        return false;
    }
    
    public static final boolean correctLocation(final Location location) {
        if (KeyAPI.hasPadlock(location, false)) {
            return false;
        }
        final Block block = location.getBlock();
        final BlockState state = block.getState();
        if (state instanceof Chest) {
            final InventoryHolder holder = ((Chest)state).getInventory().getHolder();
            if (holder instanceof DoubleChest) {
                final Location left = ((Chest)((DoubleChest)holder).getLeftSide()).getLocation();
                if (KeyAPI.hasPadlock(left, false)) {
                    location.setX(left.getX());
                    location.setZ(left.getZ());
                    return true;
                }
                final Location right = ((Chest)((DoubleChest)holder).getRightSide()).getLocation();
                if (KeyAPI.hasPadlock(right, false)) {
                    location.setX(right.getX());
                    location.setZ(right.getZ());
                    return true;
                }
            }
            return false;
        }
        final MaterialData data = state.getData();
        if (DoorUtils.instanceOf(data)) {
            location.setY((double)DoorUtils.getBlockBelow(block).getY());
            if (KeyAPI.hasPadlock(location, false)) {
                return true;
            }
            final Block doubleDoor = DoorUtils.getDoubleDoor(block);
            if (doubleDoor != null) {
                final Location doubleDoorLocation = doubleDoor.getLocation();
                doubleDoorLocation.setY(location.getY());
                if (KeyAPI.hasPadlock(doubleDoorLocation, false)) {
                    location.setX(doubleDoorLocation.getX());
                    location.setZ(doubleDoorLocation.getZ());
                }
            }
            return true;
        }
        else {
            if (data instanceof TrapDoor) {
                final Block attached = block.getRelative(((TrapDoor)data).getAttachedFace());
                location.setX((double)attached.getX());
                location.setZ((double)attached.getZ());
                return true;
            }
            return false;
        }
    }
    
    public static final Block getTargetBlock(final LivingEntity entity, final int range) {
        final BlockIterator bit = new BlockIterator(entity, range);
        while (bit.hasNext()) {
            final Block next = bit.next();
            if (next != null && next.getType() != Material.AIR) {
                return next;
            }
        }
        return null;
    }
    
    public static final <T> void clearFields(final T instance) throws IllegalArgumentException, IllegalAccessException {
        Field[] declaredFields;
        for (int length = (declaredFields = instance.getClass().getDeclaredFields()).length, i = 0; i < length; ++i) {
            final Field field = declaredFields[i];
            field.setAccessible(true);
            field.set(Modifier.isStatic(field.getModifiers()) ? null : instance, null);
        }
    }
    
    public static final <V, K> Map<K, V> createMap(final K[] keys, final V[] values) {
        if (keys.length != values.length) {
            return null;
        }
        final Map<K, V> map = new HashMap<K, V>();
        for (int i = 0; i != keys.length; ++i) {
            map.put(keys[i], values[i]);
        }
        return map;
    }
    
    public static final ItemStack createItem(final String name, final Material material) {
        final ItemStack item = new ItemStack(material);
        final ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        item.setItemMeta(meta);
        return item;
    }
}
