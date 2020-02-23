package com.steffbeard.totalwar.core;

import org.bukkit.block.BlockFace;

/**
 * Special directional constants class which provides compatibility with older
 * versions of Bukkit in which the BlockFace directions were incorrect due to
 * historical reasons.
 */

public final class Directions {
    private Directions() {
        // Prevent instantiation
    }
    
    public static BlockFace actual(BlockFace direction) {
        if (legacy) {
            switch (direction) {
                case WEST:
                    return BlockFace.NORTH;
                case NORTH:
                    return BlockFace.EAST;
                case EAST:
                    return BlockFace.SOUTH;
                case SOUTH:
                    return BlockFace.WEST;
                default:
                    return direction;
            }
        } else {
            return direction;
        }
    }
    
    public static final BlockFace ACTUAL_NORTH;
    public static final BlockFace ACTUAL_EAST;
    public static final BlockFace ACTUAL_SOUTH;
    public static final BlockFace ACTUAL_WEST;
    
    private static final boolean legacy = BlockFace.NORTH.getModX() == -1;
    
    static {
        if (legacy) {
            ACTUAL_NORTH = BlockFace.EAST;
            ACTUAL_EAST = BlockFace.SOUTH;
            ACTUAL_SOUTH = BlockFace.WEST;
            ACTUAL_WEST = BlockFace.NORTH;
        } else {
            ACTUAL_NORTH = BlockFace.NORTH;
            ACTUAL_EAST = BlockFace.EAST;
            ACTUAL_SOUTH = BlockFace.SOUTH;
            ACTUAL_WEST = BlockFace.WEST;
        }
    }
}
