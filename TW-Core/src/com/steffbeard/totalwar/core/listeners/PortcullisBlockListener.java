package com.steffbeard.totalwar.core.listeners;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import static org.bukkit.block.BlockFace.*;
import org.bukkit.event.block.BlockRedstoneEvent;

import com.steffbeard.totalwar.core.Config;
import com.steffbeard.totalwar.core.Main;
import com.steffbeard.totalwar.core.Portcullis;
import com.steffbeard.totalwar.core.PortcullisMover;
import com.steffbeard.totalwar.core.Directions;

@SuppressWarnings("deprecation")
public class PortcullisBlockListener implements Listener {
    
	private Main plugin;
	private Config config;
	private static final Logger logger = Main.logger;
	
    public PortcullisBlockListener(Main plugin) {
        this.plugin = plugin;
        wallMaterials.addAll(Config.additionalWallMaterials);
    }

	@EventHandler(priority= EventPriority.MONITOR)
    public void onBlockRedstoneChange(BlockRedstoneEvent event) {
        try {
            if (logger.isLoggable(Level.FINEST)) {
                logger.log(Level.FINEST, "[PorteCoulissante] PortcullisBlockListener.onBlockRedstoneChange() (thread: " + Thread.currentThread() + ")", new Throwable());
            }
            Block block = event.getBlock();
            Location location = block.getLocation();
            if (logger.isLoggable(Level.FINE)) {
                logger.fine("[PorteCoulissante] Redstone event on block @ " + location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ() + ", type: " + block.getType() + "; " + event.getOldCurrent() + " -> " + event.getNewCurrent());
                if (logger.isLoggable(Level.FINEST)) {
                    logger.finest("[PorteCoulissante] Type according to World.getBlockAt(): " + block.getWorld().getBlockAt(location).getType());
                    logger.finest("[PorteCoulissante] Type according to World.getBlockTypeIdAt(): " + Material.getMaterial(block.getWorld().getBlockTypeIdAt(location)));
                }
            }
            if (! ((event.getOldCurrent() == 0) || (event.getNewCurrent() == 0))) {
                // Not a power on or off event
                return;
            }
            if (! CONDUCTIVE.contains(block.getTypeId())) {
                logger.fine("[PorteCoulissante] Block @ " + location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ() + ", type: " + block.getType() + " not conductive; ignoring");
                return;
            }
            boolean powerOn = event.getOldCurrent() == 0;
            if (logger.isLoggable(Level.FINE)) {
                logger.fine("[PorteCoulissante] Block powered " + (powerOn ? "on" : "off"));
            }
            for (BlockFace direction: CARDINAL_DIRECTIONS) {
                Portcullis portCullis = findPortcullisInDirection(block, direction);
                if (portCullis != null) {
                    portCullis = normalisePortcullis(portCullis);
                    if (logger.isLoggable(Level.FINE)) {
                        logger.fine("[PorteCoulissante] Portcullis found! (x: " + portCullis.getX() + ", z: " + portCullis.getZ() + ", y: " + portCullis.getY() + ", width: " + portCullis.getWidth() + ", height: " + portCullis.getHeight() + ", direction: " + portCullis.getDirection() + ")");
                        if (logger.isLoggable(Level.FINEST)) {
                            logger.finest("[PorteCoulissante] According to Bukkit cache:");
                            World world = block.getWorld();
                            for (int y = portCullis.getY() + portCullis.getHeight() + 4; y >= portCullis.getY() - 5; y--) {
                                StringBuilder sb = new StringBuilder("[PorteCoulissante] ");
                                sb.append(y);
                                for (int i = -5; i <= portCullis.getWidth() + 4; i++) {
                                    sb.append('|');
                                    sb.append(world.getBlockAt(portCullis.getX() + i * portCullis.getDirection().getModX(), y, portCullis.getZ() + i * portCullis.getDirection().getModZ()).getType().name().substring(0, 2));
                                }
                                logger.finest(sb.toString());
                            }
                            logger.finest("[PorteCoulissante] According to Minecraft:");
                            for (int y = portCullis.getY() + portCullis.getHeight() + 4; y >= portCullis.getY() - 5; y--) {
                                StringBuilder sb = new StringBuilder("[PorteCoulissante] ");
                                sb.append(y);
                                for (int i = -5; i <= portCullis.getWidth() + 4; i++) {
                                    sb.append('|');
                                    sb.append(Material.getMaterial(world.getBlockTypeIdAt(portCullis.getX() + i * portCullis.getDirection().getModX(), y, portCullis.getZ() + i * portCullis.getDirection().getModZ())).name().substring(0, 2));
                                }
                                logger.finest(sb.toString());
                            }
                        }
                    }
                    if (powerOn) {
                        hoistPortcullis(portCullis);
                    } else {
                        dropPortcullis(portCullis);
                    }
                }
            }
        } catch (Throwable t) {
            logger.log(Level.SEVERE, "[PorteCoulissante] Exception thrown while handling redstone event!", t);
        }
    }

    private Portcullis findPortcullisInDirection(Block block, BlockFace direction) {
        BlockFace actualDirection = Directions.actual(direction);
        Block powerBlock = block.getRelative(actualDirection);
        int powerBlockType = powerBlock.getTypeId();
        if (isPotentialPowerBlock(powerBlockType)) {
            byte powerBlockData = powerBlock.getData();
            if (logger.isLoggable(Level.FINE)) {
                logger.fine("[PorteCoulissante] Potential power block found (type: " + powerBlockType + ", data: " + powerBlockData + ")");
            }
            Block firstPortcullisBlock = powerBlock.getRelative(actualDirection);
            if (isPotentialPortcullisBlock(firstPortcullisBlock)) {
                int portcullisType = firstPortcullisBlock.getTypeId();
                byte portcullisData = firstPortcullisBlock.getData();
                if (logger.isLoggable(Level.FINE)) {
                    logger.fine("[PorteCoulissante] Potential portcullis block found (type: " + portcullisType + ", data: " + portcullisData + ")");
                }
                if ((portcullisType == powerBlockType) && (portcullisData == powerBlockData)) {
                    // The portcullis can't be made of the same blocks as its frame
                    if (logger.isLoggable(Level.FINE)) {
                        logger.fine("[PorteCoulissante] Potential portcullis block is same type as wall; aborting");
                    }
                    return null;
                }
                Block lastPortCullisBlock = firstPortcullisBlock.getRelative(actualDirection);
                if (isPortcullisBlock(portcullisType, portcullisData, lastPortCullisBlock)) {
                    int width = 2;
                    Block nextBlock = lastPortCullisBlock.getRelative(actualDirection);
                    while (isPortcullisBlock(portcullisType, portcullisData, nextBlock)) {
                        width++;
                        lastPortCullisBlock = nextBlock;
                        nextBlock = lastPortCullisBlock.getRelative(actualDirection);
                    }
                    // At least two fences found in a row. Now search up and down
                    int highestY = firstPortcullisBlock.getLocation().getBlockY();
                    Block nextBlockUp = firstPortcullisBlock.getRelative(UP);
                    while (isPortcullisBlock(portcullisType, portcullisData, nextBlockUp)) {
                        highestY++;
                        nextBlockUp = nextBlockUp.getRelative(UP);
                    }
                    int lowestY = firstPortcullisBlock.getLocation().getBlockY();
                    Block nextBlockDown = firstPortcullisBlock.getRelative(DOWN);
                    while (isPortcullisBlock(portcullisType, portcullisData, nextBlockDown)) {
                        lowestY--;
                        nextBlockDown = nextBlockDown.getRelative(DOWN);
                    }
                    int height = highestY - lowestY + 1;
                    if (height >= 2) {
                        if (logger.isLoggable(Level.FINE)) {
                            logger.fine("[PorteCoulissante] Found potential portcullis of width " + width + " and height " + height);
                        }
                        int x = firstPortcullisBlock.getX();
                        int y = lowestY;
                        int z = firstPortcullisBlock.getZ();
                        World world = firstPortcullisBlock.getWorld();
                        // Check the integrity of the portcullis
                        for (int i = -1; i <= width; i++) {
                            for (int dy = -1; dy <= height; dy++) {
                                if ((((i == -1) || (i == width)) && (dy != -1) && (dy != height))
                                        || (((dy == -1) || (dy == height)) && (i != -1) && (i != width))) {
                                    // This is one of the blocks to the sides or above or below of the portcullis
                                    Block frameBlock = world.getBlockAt(x + i * actualDirection.getModX(), y + dy, z + i * actualDirection.getModZ());
                                    if (isPortcullisBlock(portcullisType, portcullisData, frameBlock)) {
                                        if (logger.isLoggable(Level.FINE)) {
                                            logger.fine("[PorteCoulissante] Block of same type as potential portcullis found in frame; aborting");
                                        }
                                        return null;
                                    }
                                } else if ((i >= 0) && (i < width) && (dy >= 0) && (dy < height)) {
                                    // This is a portcullis block
                                    Block portcullisBlock = world.getBlockAt(x + i * actualDirection.getModX(), y + dy, z + i * actualDirection.getModZ());
                                    if (! isPortcullisBlock(portcullisType, portcullisData, portcullisBlock)) {
                                        if (logger.isLoggable(Level.FINE)) {
                                            logger.fine("[PorteCoulissante] Block of wrong type (" + portcullisBlock.getTypeId() + ") found inside potential portcullis; aborting");
                                        }
                                        return null;
                                    }
                                }
                            }
                        }
                        if (logger.isLoggable(Level.FINE)) {
                            logger.fine("[PorteCoulissante] Portcullis found! Location: " + x + ", " + y + ", " + z + ", width: " + width + ", height: " + height + ", direction: " + direction + ", type: " + portcullisType + ", data: " + portcullisData);
                        }
                        return new Portcullis(world.getName(), x, z, y, width, height, direction, portcullisType, portcullisData);
                    }
                }
            }
        }
        return null;
    }

    private boolean isPotentialPowerBlock(int wallType) {
        return true;
    }
    
    private boolean isPotentialPortcullisBlock(Block block) {
        return config.portcullisMaterials.contains(block.getTypeId());
    }

    private boolean isPortcullisBlock(int portcullisType, byte portcullisData, Block block) {
        return (block.getTypeId() == portcullisType) && (block.getData() == portcullisData);
    }
    
    private Portcullis normalisePortcullis(Portcullis portcullis) {
        if (portcullis.getDirection() == WEST) {
            return new Portcullis(portcullis.getWorldName(), portcullis.getX() - portcullis.getWidth() + 1, portcullis.getZ(), portcullis.getY(), portcullis.getWidth(), portcullis.getHeight(), EAST, portcullis.getType(), portcullis.getData());
        } else if (portcullis.getDirection() == NORTH) {
            return new Portcullis(portcullis.getWorldName(), portcullis.getX(), portcullis.getZ() - portcullis.getWidth() + 1, portcullis.getY(), portcullis.getWidth(), portcullis.getHeight(), SOUTH, portcullis.getType(), portcullis.getData());
        } else {
            return portcullis;
        }
    }

    private void hoistPortcullis(final Portcullis portcullis) {
        // Check whether the portcullis is already known
        for (PortcullisMover mover: portcullisMovers) {
            if (mover.getPortcullis().equals(portcullis)) {
                if (logger.isLoggable(Level.FINE)) {
                    logger.fine("[PorteCoulissante] Reusing existing portcullis mover");
                }
                // Set the portcullis, because the one cached by the portcullis
                // mover may be made from a different material
                mover.setPortcullis(portcullis);
                mover.hoist();
                return;
            }
        }
        // It isn't
        if (logger.isLoggable(Level.FINE)) {
            logger.fine("[PorteCoulissante] Creating new portcullis mover");
        }
        PortcullisMover mover = new PortcullisMover(plugin, portcullis, wallMaterials);
        portcullisMovers.add(mover);
        mover.hoist();
    }

    private void dropPortcullis(final Portcullis portcullis) {
        // Check whether the portcullis is already known
        for (PortcullisMover mover: portcullisMovers) {
            if (mover.getPortcullis().equals(portcullis)) {
                if (logger.isLoggable(Level.FINE)) {
                    logger.fine("[PorteCoulissante] Reusing existing portcullis mover");
                }
                // Set the portcullis, because the one cached by the portcullis
                // mover may be made from a different material
                mover.setPortcullis(portcullis);
                mover.drop();
                return;
            }
        }
        // It isn't
        if (logger.isLoggable(Level.FINE)) {
            logger.fine("[PorteCoulissante] Creating new portcullis mover");
        }
        PortcullisMover mover = new PortcullisMover(plugin, portcullis, wallMaterials);
        portcullisMovers.add(mover);
        mover.drop();
    }
    
    private final Set<PortcullisMover> portcullisMovers = new HashSet<PortcullisMover>();
    
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
    
    private final Set<Integer> wallMaterials = new HashSet<Integer>(Arrays.asList(
        BLK_STONE, BLK_GRASS, BLK_DIRT, BLK_COBBLESTONE, BLK_WOODEN_PLANK, BLK_BEDROCK,
        BLK_GOLD_ORE, BLK_IRON_ORE, BLK_COAL, BLK_WOOD, BLK_SPONGE, BLK_GLASS, BLK_LAPIS_LAZULI_ORE,
        BLK_LAPIS_LAZULI_BLOCK, BLK_SANDSTONE, BLK_WOOL, BLK_GOLD_BLOCK, BLK_IRON_BLOCK,
        BLK_DOUBLE_SLAB, BLK_BRICK_BLOCK, BLK_BOOKSHELF, BLK_MOSSY_COBBLESTONE,
        BLK_OBSIDIAN, BLK_CHEST, BLK_DIAMOND_ORE, BLK_DIAMOND_BLOCK, BLK_CRAFTING_TABLE,
        BLK_TILLED_DIRT, BLK_FURNACE, BLK_BURNING_FURNACE, BLK_REDSTONE_ORE, BLK_GLOWING_REDSTONE_ORE,
        BLK_ICE, BLK_SNOW_BLOCK, BLK_CLAY, BLK_JUKEBOX, BLK_NETHERRACK, BLK_SOUL_SAND,
        BLK_GLOWSTONE, BLK_HIDDEN_SILVERFISH, BLK_STONE_BRICKS, BLK_MYCELIUM, BLK_NETHER_BRICK,
        BLK_END_PORTAL_FRAME, BLK_END_STONE, BLK_REDSTONE_LAMP_OFF, BLK_REDSTONE_LAMP_ON,
        BLK_WOODEN_DOUBLE_SLAB, BLK_EMERALD_BLOCK, BLK_EMERALD_ORE, BLK_ENDER_CHEST,
        BLK_TRAPPED_CHEST, BLK_REDSTONE_BLOCK, BLK_QUARTZ_ORE, BLK_QUARTZ_BLOCK,
        BLK_STAINED_CLAY, BLK_WOOD_2, BLK_HAY_BALE, BLK_HARDENED_CLAY, BLK_COAL_BLOCK,
        BLK_PACKED_ICE));
    
    private static final BlockFace[] CARDINAL_DIRECTIONS = {NORTH, EAST, SOUTH, WEST};
    private static final Set<Integer> CONDUCTIVE = new HashSet<Integer>(Arrays.asList(
        BLK_REDSTONE_WIRE, BLK_REDSTONE_TORCH_ON, BLK_REDSTONE_TORCH_OFF, BLK_REDSTONE_REPEATER_ON,
        BLK_REDSTONE_REPEATER_OFF, BLK_STONE_BUTTON, BLK_LEVER, BLK_STONE_PRESSURE_PLATE,
        BLK_WOODEN_PRESSURE_PLATE, BLK_TRIPWIRE_HOOK, BLK_WOODEN_BUTTON, BLK_TRAPPED_CHEST,
        BLK_REDSTONE_COMPARATOR_OFF, BLK_REDSTONE_COMPARATOR_ON, BLK_WEIGHTED_PRESSURE_PLATE_LIGHT,
        BLK_WEIGHTED_PRESSURE_PLATE_HEAVY));
}