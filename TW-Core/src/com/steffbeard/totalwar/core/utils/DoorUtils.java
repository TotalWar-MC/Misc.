package com.steffbeard.totalwar.core.utils;

import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Block;
import org.bukkit.material.Door;
import org.bukkit.material.MaterialData;

public class DoorUtils
{
    public static final boolean instanceOf(final MaterialData data) {
        return data instanceof Door;
    }
    
    public static final Block getBlockBelow(final Block door) {
        final Location location = door.getLocation();
        location.setY((double)(location.getBlockY() - (instanceOf(door.getRelative(BlockFace.DOWN).getState().getData()) ? 2 : 1)));
        return location.getBlock();
    }
    
    public static final Block getDoubleDoor(final Block door) {
        final Block bottomPart = getBlockBelow(door).getRelative(BlockFace.UP);
        BlockFace[] array;
        for (int length = (array = new BlockFace[] { BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST }).length, i = 0; i < length; ++i) {
            final BlockFace face = array[i];
            final Block relative = bottomPart.getRelative(face);
            if (instanceOf(relative.getState().getData()) && areConnected(bottomPart, relative)) {
                return relative;
            }
        }
        return null;
    }
    
    @SuppressWarnings("deprecation")
	public static final boolean areConnected(final Block door1, final Block door2) {
        return door1.getType() == door2.getType() && (door1.getRelative(BlockFace.UP).getData() & 0x1) != (door2.getRelative(BlockFace.UP).getData() & 0x1) && (door1.getData() & 0x3) == (door2.getData() & 0x3);
    }
}
