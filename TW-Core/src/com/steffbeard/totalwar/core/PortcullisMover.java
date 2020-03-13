package com.steffbeard.totalwar.core;

import java.awt.Point;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitScheduler;

import com.steffbeard.totalwar.core.Directions;

@SuppressWarnings("deprecation")
public class PortcullisMover implements Runnable {
	
	private Main main;
    private Logger logger;
	private Config config;
    private Portcullis portcullis;
    
    private int taskId;
    
    private final Set<Integer> wallMaterials;
    
    private Status status;
    public static enum Status {IDLE, HOISTING, DROPPING};
    private Status IDLE = null;
    private Status HOISTING = Status.HOISTING;
    private Status DROPPING = Status.DROPPING;
    
	
    public PortcullisMover(Main plugin, Portcullis portcullis, Set<Integer> wallMaterials) {
        this.portcullis = portcullis;
        this.wallMaterials = wallMaterials;
    }

    public Portcullis getPortcullis() {
        return portcullis;
    }

    public void setPortcullis(Portcullis portcullis) {
        if (! portcullis.equals(this.portcullis)) {
            throw new IllegalArgumentException();
        }
        this.portcullis = portcullis;
    }

    public void hoist() {
        if (status == HOISTING) {
            if (logger.isLoggable(Level.FINE)) {
                logger.fine("Portcullis already hoisting; ignoring request");
            }
            return;
        } else if (logger.isLoggable(Level.FINE)) {
            logger.fine("Hoisting portcullis");
        }
        BukkitScheduler scheduler = main.getServer().getScheduler();
        if (status == DROPPING) {
            scheduler.cancelTask(taskId);
        }
        int hoistingDelay = config.hoistingDelay;
        taskId = scheduler.scheduleSyncRepeatingTask(main, this, hoistingDelay / 2, hoistingDelay);
        
        status = HOISTING;
    }

    public void drop() {
        if (status == DROPPING) {
            if (logger.isLoggable(Level.FINE)) {
                logger.fine("Portcullis already dropping; ignoring request");
            }
            return;
        } else if (logger.isLoggable(Level.FINE)) {
            logger.fine("Dropping portcullis");
        }
        BukkitScheduler scheduler = main.getServer().getScheduler();
        if (status == HOISTING) {
            scheduler.cancelTask(taskId);
        }
        int droppingDelay = config.droppingDelay;
        taskId = scheduler.scheduleSyncRepeatingTask(main, this, droppingDelay, droppingDelay);
        
        status = DROPPING;
    }

    @Override
    public void run() {
        try {
            if (logger.isLoggable(Level.FINEST)) {
                logger.log(Level.FINEST, "PortCullisMover.run() (thread: " + Thread.currentThread() + ")", new Throwable());
            }
            if ((status == HOISTING) && (! movePortcullisUp(portcullis))) {
                main.getServer().getScheduler().cancelTask(taskId);
                taskId = 0;
                status = IDLE;
                if (logger.isLoggable(Level.FINE)) {
                    logger.fine("Portcullis hoisted!");
                }
            } else if ((status == DROPPING) && (! movePortcullisDown(portcullis))) {
                main.getServer().getScheduler().cancelTask(taskId);
                taskId = 0;
                status = IDLE;
                if (logger.isLoggable(Level.FINE)) {
                    logger.fine("Portcullis dropped!");
                }
            }
        } catch (Throwable t) {
            logger.log(Level.SEVERE, "Exception thrown while moving portcullis!", t);
        }
    }

    private boolean movePortcullisUp(Portcullis portcullis) {
        World world = main.getServer().getWorld(portcullis.getWorldName());
        if (world == null) {
            // The world is gone!!!
            if (logger.isLoggable(Level.FINE)) {
                logger.fine("World not loaded; cancelling the hoist!");
            }
            return false;
        }
        int x = portcullis.getX();
        int z = portcullis.getZ();
        int y = portcullis.getY();
        int width = portcullis.getWidth();
        int height = portcullis.getHeight();
        BlockFace direction = portcullis.getDirection();

        // Check whether the relevant chunks (the portcullis might straddle two
        // chunks) are loaded. In theory someone might build a huge portcullis
        // which straddles multiple chunks, but they're on their own... ;-)
        Set<Point> chunkCoords = getChunkCoords(x, z, direction, width);
        if (! areChunksLoaded(world, chunkCoords)) {
            if (logger.isLoggable(Level.FINE)) {
                logger.fine("Some or all chunks not loaded; cancelling the hoist!");
            }
            return false;
        }
        
        // Check whether the portcullis is still intact
        if (! isPortcullisWhole(world)) {
            if (logger.isLoggable(Level.FINE)) {
                logger.fine("Portcullis no longer intact; cancelling the hoist!");
            }
            return false;
        }

        // Check whether there is room above the portcullis
        if (y + height >= world.getMaxHeight()) {
            if (logger.isLoggable(Level.FINE)) {
                logger.fine("World ceiling reached; no more room; destroying portcullis!");
            }
            explodePortcullis(world);
            return false;
        }
        
        BlockFace actualDirection = Directions.actual(direction);
        int dx = actualDirection.getModX(), dz = actualDirection.getModZ();
        if (! config.allowFloating) {
            // Check that the portcullis would not be floating, if that is not
            // allowed
            boolean solidBlockFound = false;
            for (int yy = y + 1; yy <= y + height; yy++) {
				int blockID = world.getBlockTypeIdAt(x - dx, yy, z - dz);
                if (wallMaterials.contains(blockID) || SUPPORTING_MATERIALS.contains(blockID)) {
                    solidBlockFound = true;
                    break;
                }
                blockID = world.getBlockTypeIdAt(x + width * dx, yy, z + width * dz);
                if (wallMaterials.contains(blockID) || SUPPORTING_MATERIALS.contains(blockID)) {
                    solidBlockFound = true;
                    break;
                }
            }
            if (! solidBlockFound) {
                if (logger.isLoggable(Level.FINE)) {
                    logger.fine("Portcullis would be floating, which is not allowed; cancelling the hoist!");
                }
                return false;
            }
        }
        
        for (int i = 0; i < width; i++) {
            Block block = world.getBlockAt(x + i * dx, y + height, z + i * dz);
            if (! AIR_MATERIALS.contains(block.getTypeId())) {
                // No room to move up, we're done here
                if (logger.isLoggable(Level.FINE)) {
                    logger.fine("Not enough room above portcullis (block of type " + block.getType() + " found @ " + (x + i * dx) + ", " + (y + height) + ", " + (z + i * dz) + ")");
                }
                return false;
            }
        }

        // There is room. Move the portcullis up one block
        if (logger.isLoggable(Level.FINE)) {
            logger.fine("Moving portcullis up one row.");
        }
        int portcullisType = portcullis.getType();
        byte portcullisData = portcullis.getData();
        for (int i = 0; i < width; i++) {
            // Set the block above the portcullis to "fence"
            if (logger.isLoggable(Level.FINEST)) {
                logger.finest("Setting block @ " + (x + i * dx) + ", " + (y + height) + ", " + (z + i * dz) + " to type " + portcullisType + ", data " + portcullisData + ".");
            }
            Block block = world.getBlockAt(x + i * dx, y + height, z + i * dz);
            block.setTypeIdAndData(portcullisType, portcullisData, true);
            // Set the block below to "air"
            if (logger.isLoggable(Level.FINEST)) {
                logger.finest("Setting block @ " + (x + i * dx) + ", " + y + ", " + (z + i * dz) + " to \"air\".");
            }
            block = world.getBlockAt(x + i * dx, y, z + i * dz);
            block.setTypeIdAndData(BLK_AIR, (byte) 0, true);
        }

        // Move any entities and items on top of the portcullis up (but only if
        // enabled)
        if (config.entityMoving) {
            moveEntitiesUp(world, chunkCoords, portcullis);
        }
        
        portcullis.setY(y + 1);
        return true;
    }

    private boolean movePortcullisDown(Portcullis portcullis) {
        World world = main.getServer().getWorld(portcullis.getWorldName());
        if (world == null) {
            // The world is gone!!!
            if (logger.isLoggable(Level.FINE)) {
                logger.fine("World not loaded; cancelling the move!");
            }
            return false;
        }
        int x = portcullis.getX();
        int z = portcullis.getZ();
        int y = portcullis.getY();
        int width = portcullis.getWidth();
        int height = portcullis.getHeight();
        BlockFace direction = portcullis.getDirection();

        // Check whether the relevant chunks (the portcullis might straddle two
        // chunks) are loaded. In theory someone might build a huge portcullis
        // which straddles multiple chunks, but they're on their own... ;-)
        if (! areChunksLoaded(world, getChunkCoords(x, z, direction, width))) {
            if (logger.isLoggable(Level.FINE)) {
                logger.fine("Some or all chunks not loaded; cancelling the drop!");
            }
            return false;
        }

        // Check whether the portcullis is still intact
        if (! isPortcullisWhole(world)) {
            if (logger.isLoggable(Level.FINE)) {
                logger.fine("Portcullis no longer intact; cancelling the drop!");
            }
            return false;
        }
        
        // Check whether there is room below the portcullis
        if (y <= 0) {
            if (logger.isLoggable(Level.FINE)) {
                logger.fine("World floor reached; no more room.");
            }
            return false;
        }
        BlockFace actualDirection = Directions.actual(direction);
        int dx = actualDirection.getModX(), dz = actualDirection.getModZ();
        for (int i = 0; i < width; i++) {
            Block block = world.getBlockAt(x + i * dx, y - 1, z + i * dz);
            if (! AIR_MATERIALS.contains(block.getTypeId())) {
                // No room to move down, we're done here
                if (logger.isLoggable(Level.FINE)) {
                    logger.fine("Not enough room below portcullis (block of type " + block.getType() + " found @ " + (x + i * dx) + ", " + (y - 1) + ", " + (z + i * dz) + ")");
                }
                return false;
            }
        }

        // There is room. Move the portcullis down one block
        if (logger.isLoggable(Level.FINE)) {
            logger.fine("Moving portcullis down one row.");
        }
        int portcullisType = portcullis.getType();
        byte portcullisData = portcullis.getData();
        y--;
        for (int i = 0; i < width; i++) {
            // Set the block above the portcullis to "air"
            if (logger.isLoggable(Level.FINEST)) {
                logger.finest("Setting block @ " + (x + i * dx) + ", " + (y + height) + ", " + (z + i * dz) + " to \"air\".");
            }
            Block block = world.getBlockAt(x + i * dx, y + height, z + i * dz);
            block.setTypeIdAndData(BLK_AIR, (byte) 0, true);
            // Set the block below to "fence"
            if (logger.isLoggable(Level.FINEST)) {
                logger.finest("Setting block @ " + (x + i * dx) + ", " + y + ", " + (z + i * dz) + " to type " + portcullisType + ", data " + portcullisData + ".");
            }
            block = world.getBlockAt(x + i * dx, y, z + i * dz);
            block.setTypeIdAndData(portcullisType, portcullisData, true);
        }

        portcullis.setY(y);

        return true;
    }

    private void moveEntitiesUp(World world, Set<Point> chunkCoords, Portcullis portcullis) {
        for (Point chunkCoord: chunkCoords) {
            Chunk chunk = world.getChunkAt(chunkCoord.x, chunkCoord.y);
            for (Entity entity: chunk.getEntities()) {
                Location location = entity.getLocation();
                if (logger.isLoggable(Level.FINE)) {
                    logger.fine("Considering entity " + entity + "@" + entity.getEntityId() + ": " + location.getX() + ", " + location.getY() + ", " + location.getZ());
                }
                if (isOnPortcullis(location, portcullis)) {
                    if (logger.isLoggable(Level.FINE)) {
                        logger.fine("Entity is on portcullis; moving it up");
                    }
                    location.setY(location.getY() + 1);
                    entity.teleport(location);
                }
            }
        }
    }

    private boolean isOnPortcullis(Location location, Portcullis portcullis) {
        int x = portcullis.getX(), y = portcullis.getY(), z = portcullis.getZ(), width = portcullis.getWidth(), height = portcullis.getHeight();
        BlockFace actualDirection = Directions.actual(portcullis.getDirection());
        int x2 = x + actualDirection.getModX() * width;
        int z2 = z + actualDirection.getModZ() * width;
        if (x > x2) {
            int tmp = x;
            x = x2;
            x2 = tmp;
        }
        if (z > z2) {
            int tmp = z;
            z = z2;
            z2 = tmp;
        }
        int locX = location.getBlockX(), locY = location.getBlockY(), locZ = location.getBlockZ();
        if (logger.isLoggable(Level.FINE)) {
            logger.fine("Portcullis coordinates: " + x + " -> " + x2 + ", " + z + " -> " + z2 + ", " + (y + height));
            logger.fine("Location: " + locX + ", " + locZ + ", " + locY);
        }
        return (locX >= x) && (locX <= x2) && (locZ >= z) && (locZ <= z2) && (locY == (y + height));
    }

    private boolean areChunksLoaded(World world, Set<Point> chunkCoords) {
        for (Point point: chunkCoords) {
            if (! world.isChunkLoaded(point.x, point.y)) {
                return false;
            }
        }
        return true;
    }

    private Set<Point> getChunkCoords(int x, int z, BlockFace direction, int width) {
        Set<Point> chunkCoords = new HashSet<Point>();
        int firstChunkX = x >> 4;
        int firstChunkZ = z >> 4;
        chunkCoords.add(new Point(firstChunkX, firstChunkZ));
        BlockFace actualDirection = Directions.actual(direction);
        int secondChunkX = (x + actualDirection.getModX() * (width - 1)) >> 4;
        int secondChunkZ = (z + actualDirection.getModZ() * (width - 1)) >> 4;
        if ((secondChunkX != firstChunkX) || (secondChunkZ != firstChunkZ)) {
            chunkCoords.add(new Point(secondChunkX, secondChunkZ));
        }
        return chunkCoords;
    }
    
    private boolean isPortcullisWhole(World world) {
        int portcullisX = portcullis.getX();
        int portcullisY1 = portcullis.getY(), portcullisY2 = portcullisY1 + portcullis.getHeight();
        int portcullisZ = portcullis.getZ();
        int portcullisWidth = portcullis.getWidth();
        BlockFace actualPortcullisDirection = Directions.actual(portcullis.getDirection());
        int dx = actualPortcullisDirection.getModX(), dz = actualPortcullisDirection.getModZ();
        int portcullisType = portcullis.getType();
        byte portcullisData = portcullis.getData();
        for (int y = portcullisY1; y < portcullisY2; y++) {
            int x = portcullisX;
            int z = portcullisZ;
            for (int i = 0; i < portcullisWidth; i++) {
                Block block = world.getBlockAt(x, y, z);
                if ((block.getTypeId() != portcullisType) || (block.getData() != portcullisData)) {
                    return false;
                }
                x += dx;
                z += dz;
            }
        }
        return true;
    }
    
    private void explodePortcullis(World world) {
        int portcullisX = portcullis.getX();
        int portcullisY1 = portcullis.getY(), portcullisY2 = portcullisY1 + portcullis.getHeight();
        int portcullisZ = portcullis.getZ();
        BlockFace actualPortcullisDirection = Directions.actual(portcullis.getDirection());
        int dx = actualPortcullisDirection.getModX(), dz = actualPortcullisDirection.getModZ();
        int portcullisType = portcullis.getType();
        byte portcullisData = portcullis.getData();
        ItemStack itemStack = new ItemStack(portcullisType, 1, (short) 0, portcullisData);
        for (int y = portcullisY1; y < portcullisY2; y++) {
            int x = portcullisX;
            int z = portcullisZ;
            for (int i = 0; i < portcullis.getWidth(); i++) {
                Block block = world.getBlockAt(x, y, z);
                block.setTypeIdAndData(BLK_AIR, (byte) 0, true);
                world.dropItemNaturally(block.getLocation(), itemStack);
                x += dx;
                z += dz;
            }
        }
    }
    
    public static final int BLK_AIR                   =   0;
    public static final int BLK_STONE                 =   1;
    public static final int BLK_GRASS                 =   2;
    public static final int BLK_DIRT                  =   3;
    public static final int BLK_COBBLESTONE           =   4;
    public static final int BLK_WOODEN_PLANK          =   5;
    public static final int BLK_SAPLING               =   6;
    public static final int BLK_BEDROCK               =   7;
    public static final int BLK_WATER                 =   8;
    public static final int BLK_STATIONARY_WATER      =   9;
    public static final int BLK_LAVA                  =  10;
    public static final int BLK_STATIONARY_LAVA       =  11;
    public static final int BLK_SAND                  =  12;
    public static final int BLK_GRAVEL                =  13;
    public static final int BLK_GOLD_ORE              =  14;
    public static final int BLK_IRON_ORE              =  15;
    public static final int BLK_COAL                  =  16;
    public static final int BLK_WOOD                  =  17;
    public static final int BLK_LEAVES                =  18;
    public static final int BLK_SPONGE                =  19;
    public static final int BLK_GLASS                 =  20;
    public static final int BLK_LAPIS_LAZULI_ORE      =  21;
    public static final int BLK_LAPIS_LAZULI_BLOCK    =  22;
    public static final int BLK_DISPENSER             =  23;
    public static final int BLK_SANDSTONE             =  24;
    public static final int BLK_NOTE_BLOCK            =  25;
    public static final int BLK_BED                   =  26;
    public static final int BLK_POWERED_RAILS         =  27;
    public static final int BLK_DETECTOR_RAILS        =  28;
    public static final int BLK_STICKY_PISTON         =  29;
    public static final int BLK_COBWEB                =  30;
    public static final int BLK_TALL_GRASS            =  31;
    public static final int BLK_DEAD_SHRUBS           =  32;
    public static final int BLK_PISTON                =  33;
    public static final int BLK_PISTON_EXTENSION      =  34;
    public static final int BLK_WOOL                  =  35;
    
    public static final int BLK_DANDELION             =  37;
    public static final int BLK_ROSE                  =  38;
    public static final int BLK_BROWN_MUSHROOM        =  39;
    public static final int BLK_RED_MUSHROOM          =  40;
    public static final int BLK_GOLD_BLOCK            =  41;
    public static final int BLK_IRON_BLOCK            =  42;
    public static final int BLK_DOUBLE_SLAB           =  43;
    public static final int BLK_SLAB                  =  44;
    public static final int BLK_BRICK_BLOCK           =  45;
    public static final int BLK_TNT                   =  46;
    public static final int BLK_BOOKSHELF             =  47;
    public static final int BLK_MOSSY_COBBLESTONE     =  48;
    public static final int BLK_OBSIDIAN              =  49;
    public static final int BLK_TORCH                 =  50;
    public static final int BLK_FIRE                  =  51;
    public static final int BLK_MONSTER_SPAWNER       =  52;
    public static final int BLK_WOODEN_STAIRS         =  53;
    public static final int BLK_CHEST                 =  54;
    public static final int BLK_REDSTONE_WIRE         =  55;
    public static final int BLK_DIAMOND_ORE           =  56;
    public static final int BLK_DIAMOND_BLOCK         =  57;
    public static final int BLK_CRAFTING_TABLE        =  58;
    public static final int BLK_WHEAT                 =  59;
    public static final int BLK_TILLED_DIRT           =  60;
    public static final int BLK_FURNACE               =  61;
    public static final int BLK_BURNING_FURNACE       =  62;
    public static final int BLK_SIGN                  =  63;
    public static final int BLK_WOODEN_DOOR           =  64;
    public static final int BLK_LADDER                =  65;
    public static final int BLK_RAILS                 =  66;
    public static final int BLK_COBBLESTONE_STAIRS    =  67;
    public static final int BLK_WALL_SIGN             =  68;
    public static final int BLK_LEVER                 =  69;
    public static final int BLK_STONE_PRESSURE_PLATE  =  70;
    public static final int BLK_IRON_DOOR             =  71;
    public static final int BLK_WOODEN_PRESSURE_PLATE =  72;
    public static final int BLK_REDSTONE_ORE          =  73;
    public static final int BLK_GLOWING_REDSTONE_ORE  =  74;
    public static final int BLK_REDSTONE_TORCH_OFF    =  75;
    public static final int BLK_REDSTONE_TORCH_ON     =  76;
    public static final int BLK_STONE_BUTTON          =  77;
    public static final int BLK_SNOW                  =  78;
    public static final int BLK_ICE                   =  79;
    public static final int BLK_SNOW_BLOCK            =  80;
    public static final int BLK_CACTUS                =  81;
    public static final int BLK_CLAY                  =  82;
    public static final int BLK_SUGAR_CANE            =  83;
    public static final int BLK_JUKEBOX               =  84;
    public static final int BLK_FENCE                 =  85;
    public static final int BLK_PUMPKIN               =  86;
    public static final int BLK_NETHERRACK            =  87;
    public static final int BLK_SOUL_SAND             =  88;
    public static final int BLK_GLOWSTONE             =  89;
    public static final int BLK_PORTAL                =  90;
    public static final int BLK_JACK_O_LANTERN        =  91;
    public static final int BLK_CAKE                  =  92;
    public static final int BLK_REDSTONE_REPEATER_OFF =  93;
    public static final int BLK_REDSTONE_REPEATER_ON  =  94;
    public static final int BLK_LOCKED_CHEST          =  95;
    public static final int BLK_TRAPDOOR              =  96;
    public static final int BLK_HIDDEN_SILVERFISH     =  97;
    public static final int BLK_STONE_BRICKS          =  98;
    public static final int BLK_HUGE_BROWN_MUSHROOM   =  99;
    public static final int BLK_HUGE_RED_MUSHROOM     = 100;
    public static final int BLK_IRON_BARS             = 101;
    public static final int BLK_GLASS_PANE            = 102;
    public static final int BLK_MELON                 = 103;
    public static final int BLK_PUMPKIN_STEM          = 104;
    public static final int BLK_MELON_STEM            = 105;
    public static final int BLK_VINES                 = 106;
    public static final int BLK_FENCE_GATE            = 107;
    public static final int BLK_BRICK_STAIRS          = 108;
    public static final int BLK_STONE_BRICK_STAIRS    = 109;
    public static final int BLK_MYCELIUM              = 110;
    public static final int BLK_LILY_PAD              = 111;
    public static final int BLK_NETHER_BRICK          = 112;
    public static final int BLK_NETHER_BRICK_FENCE    = 113;
    public static final int BLK_NETHER_BRICK_STAIRS   = 114;
    public static final int BLK_NETHER_WART           = 115;
    public static final int BLK_ENCHANTMENT_TABLE     = 116;
    public static final int BLK_BREWING_STAND         = 117;
    public static final int BLK_CAULDRON              = 118;
    public static final int BLK_END_PORTAL            = 119;
    public static final int BLK_END_PORTAL_FRAME      = 120;
    public static final int BLK_END_STONE             = 121;
    public static final int BLK_DRAGON_EGG            = 122;
    public static final int BLK_REDSTONE_LAMP_OFF     = 123;
    public static final int BLK_REDSTONE_LAMP_ON      = 124;
    public static final int BLK_WOODEN_DOUBLE_SLAB    = 125;
    public static final int BLK_WOODEN_SLAB           = 126;
    public static final int BLK_COCOA_PLANT           = 127;
    public static final int BLK_SANDSTONE_STAIRS      = 128;
    public static final int BLK_EMERALD_ORE           = 129;
    public static final int BLK_ENDER_CHEST           = 130;
    public static final int BLK_TRIPWIRE_HOOK         = 131;

    public static final int BLK_EMERALD_BLOCK         = 133;
    public static final int BLK_SPRUCE_WOOD_STAIRS    = 134;
    public static final int BLK_BIRCH_WOOD_STAIRS     = 135;
    public static final int BLK_JUNGLE_WOOD_STAIRS    = 136;

    public static final int BLK_CARROTS               = 141;
    public static final int BLK_POTATOES              = 142;
    public static final int BLK_WOODEN_BUTTON         = 143;

    public static final int BLK_TRAPPED_CHEST                 = 146;
   public static final int BLK_WEIGHTED_PRESSURE_PLATE_LIGHT  = 147;
    public static final int BLK_WEIGHTED_PRESSURE_PLATE_HEAVY = 148;
    public static final int BLK_REDSTONE_COMPARATOR_OFF       = 149;
    public static final int BLK_REDSTONE_COMPARATOR_ON        = 150;

    public static final int BLK_REDSTONE_BLOCK        = 152;
    public static final int BLK_QUARTZ_ORE            = 153;

    public static final int BLK_QUARTZ_BLOCK          = 155;

    public static final int BLK_STAINED_CLAY          = 159;

    public static final int BLK_WOOD_2                = 162;

    public static final int BLK_HAY_BALE              = 170;

    public static final int BLK_HARDENED_CLAY         = 172;
    public static final int BLK_COAL_BLOCK            = 173;
    public static final int BLK_PACKED_ICE            = 174;
    public static final int BLK_DOUBLE_PLANT          = 175;
    
    private static final Set<Integer> AIR_MATERIALS = new HashSet<Integer>(Arrays.asList(
        BLK_AIR, BLK_WATER, BLK_STATIONARY_WATER, BLK_LAVA, BLK_STATIONARY_LAVA,
        BLK_SUGAR_CANE, BLK_SNOW, BLK_DANDELION, BLK_ROSE, BLK_BROWN_MUSHROOM, BLK_RED_MUSHROOM,
        BLK_FIRE, BLK_WHEAT, BLK_TALL_GRASS, BLK_DEAD_SHRUBS, BLK_COBWEB, BLK_PUMPKIN_STEM,
        BLK_MELON_STEM, BLK_VINES, BLK_LILY_PAD, BLK_NETHER_WART, BLK_CARROTS, BLK_POTATOES,
        BLK_DOUBLE_PLANT));
    private static final Set<Integer> SUPPORTING_MATERIALS = new HashSet<Integer>(Arrays.asList(
        BLK_FENCE, BLK_FENCE_GATE, BLK_IRON_BARS, BLK_NETHER_BRICK_FENCE, BLK_WOODEN_SLAB,
        BLK_SLAB, BLK_WOODEN_STAIRS, BLK_COBBLESTONE_STAIRS, BLK_NETHER_BRICK_STAIRS,
        BLK_SANDSTONE_STAIRS, BLK_SPRUCE_WOOD_STAIRS, BLK_BIRCH_WOOD_STAIRS, BLK_JUNGLE_WOOD_STAIRS));    
    
}