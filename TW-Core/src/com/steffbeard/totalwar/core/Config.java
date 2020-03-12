package com.steffbeard.totalwar.core;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.potion.PotionEffect;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.io.File;
import java.util.LinkedHashMap;
import java.util.List;

import com.steffbeard.totalwar.core.utils.ConfigManager;

public class Config extends ConfigManager {
    
    //public boolean enableInventoryWeight;
    @ConfigOptions(name = "options.enable-fire-arrows")
    public boolean entityFireTrail;
    @ConfigOptions(name = "options.speed-multiplier")
    public static double speedMultiplier; 
    @ConfigOptions(name = "options.enable-torch-use")
    public boolean enableTorches;
    @ConfigOptions(name = "options.enable-entity-moving")
    public boolean entityMoving;
    @ConfigOptions(name = "options.allow-floating")
    public boolean allowFloating;
    @ConfigOptions(name = "int.hoisting-delay")
    public int hoistingDelay;
    @ConfigOptions(name = "int.dropping-delay")
    public int droppingDelay;
    @ConfigOptions(name = "portcullis-materials.list")
    public HashSet<Integer> portcullisMaterials;
    @ConfigOptions(name = "portcullis-power-blocks.list")
    public HashSet<Integer> powerBlocks;
    @ConfigOptions(name = "additional-wall-materials.list")
    public HashSet<Integer> additionalWallMaterials;
    @ConfigOptions(name = "options.state-change-color")
    public boolean stateChangeColor;
    @ConfigOptions(name = "options.enable-freezing")
    public boolean enableFreezing;
    @ConfigOptions(name = "fully-spoiled.damage")
    public int rottenDamage;
    @ConfigOptions(name = "stale.damage")
    public int staleDamage;
    @ConfigOptions(name = "spoiled.damage")
    public int spoiledDamage;
    @ConfigOptions(name = "options.enable-rotten-debuffs")
    public boolean enableRottenDebuffs;
    @ConfigOptions(name = "options.enable-stale-debuffs")
    public boolean enableStaleDebuffs;
    @ConfigOptions(name = "rotten-food-debuffs.list")
    public Collection<PotionEffect> rottenDebuffs;
    @ConfigOptions(name = "stale-food-debuffs.list")
    public Collection<PotionEffect> staleDebuffs;
    @ConfigOptions(name = "spoiled-food-debuffs.list")
    public Collection<PotionEffect> spoiledDebuffs;
    @ConfigOptions(name = "item-refresh.rate")
    public int itemRefreshRate;
    @ConfigOptions(name = "rotten-food.level")
    public int rottenLevel;
    @ConfigOptions(name = "spoiled-food.level")
    public int spoiledLevel;
    @ConfigOptions(name = "stale-food.level")
    public int staleLevel;
    @ConfigOptions(name = "fresh.prefix")
    public String freshPrefix;
    @ConfigOptions(name = "frozen.prefix")
    public String frozenPrefix;
    @ConfigOptions(name = "stale.prefix")
    public String stalePrefix;
    @ConfigOptions(name = "spoiled.prefix")
    public String spoiledPrefix;
    @ConfigOptions(name = "rotten.prefix")
    public String rottenPrefix;
    @ConfigOptions(name = "salt-restore.ammount")
    public int saltRestore;
    @ConfigOptions(name = "cook-restore.ammount")
    public int cookRestore;
    @ConfigOptions(name = "progress-bar.size")
    public int progressBarSize;
    @ConfigOptions(name = "special-spoilage.time")
    public long specialSpoilageTime;
    @ConfigOptions(name = "default-spoilage.time")
    public long defaultSpoilageTime;
    @ConfigOptions(name = "freeze.time")
    public int freezeTime;
    @ConfigOptions(name = "spoil-duration.slow")
    public int spoilDurationSlow;
    //@ConfigOptions(name = "non-perishible.list")
    //public List<String> nonPerishible;
    @ConfigOptions(name = "options.reusable-keys")
    public boolean reusableKeys;
    @ConfigOptions(name = "options.enable-spoiled-debuffs")
    public boolean enableSpoiledDebuffs;
    @ConfigOptions(name = "options.disable-hoppers")
    public boolean disableHoppers;
    @ConfigOptions(name = "options.encrypt-lore")
    public boolean encryptLore;
    @ConfigOptions(name = "options.can-rename-items")
    public boolean canRenameItems;
    @ConfigOptions(name = "key.material")
    public Material keyMaterial;
    @ConfigOptions(name = "key.name")
    public String keyName;
    @ConfigOptions(name = "key.shape")
    public List<String> keyShape;
    @ConfigOptions(name = "masterkey.material")
    public Material masterKeyMaterial;
    @ConfigOptions(name = "masterkey.name")
    public String masterKeyName;
    @ConfigOptions(name = "masterkey.shape")
    public List<String> masterKeyShape;
    @ConfigOptions(name = "bunch-of-keys.material")
    public Material bunchOfKeysMaterial;
    @ConfigOptions(name = "bunch-of-keys.name")
    public String bunchOfKeysName;
    @ConfigOptions(name = "bunch-of-keys.shape")
    public List<String> bunchOfKeysShape;
    @ConfigOptions(name = "shape-materials-v1")
    public LinkedHashMap<String, String> shapeMaterials;
    @ConfigOptions(name = "options.enable-enchanting")
    public boolean enableEnchanting;
    @ConfigOptions(name = "options.bow-buff")
    public double bowBuff;
    
    protected Config(final File dataFolder) {
        super(new File(dataFolder, "config.yml"), Arrays.asList("Core configuration"));
        //this.enableInventoryWeight = true;
        this.entityFireTrail = true;
        Config.speedMultiplier = 4.0;
        this.enableTorches = true;
        this.hoistingDelay = 40;
        this.droppingDelay = 10;
        this.allowFloating = true;
        this.portcullisMaterials = new HashSet<Integer>(Arrays.asList(BLK_FENCE, BLK_IRON_BARS, BLK_NETHER_BRICK_FENCE));
        this.stateChangeColor = true;
        this.reusableKeys = true;
        this.disableHoppers = true;
        this.encryptLore = false;
        this.canRenameItems = false;
        this.enableEnchanting = false;
        this.bowBuff = 1.000000;
        this.enableFreezing = true;
        this.rottenDamage = 3;
        this.staleDamage = 1;
        this.spoiledDamage = 2;
        this.enableRottenDebuffs = true;
        this.enableStaleDebuffs = false;
        this.enableSpoiledDebuffs = true;
        //this.rottenDebuffs = Potion. + 
        this.frozenPrefix = ChatColor.AQUA + "Frozen";
        this.freshPrefix = ChatColor.GREEN + "Fresh";
        this.spoiledPrefix = ChatColor.RED + "Spoiled";
        this.rottenPrefix = ChatColor.DARK_RED + "Rotten";
        this.stalePrefix = ChatColor.YELLOW + "Stale";
        this.progressBarSize = 1;
        this.itemRefreshRate = 100;
        this.rottenLevel = 100;
        this.spoiledLevel = 80;
        this.staleLevel = 20;
        this.saltRestore = 40;
        this.cookRestore = 60;
        this.specialSpoilageTime = 200;
        this.defaultSpoilageTime = 100;
        this.freezeTime = 60;
        this.spoilDurationSlow = 60;
        //this.nonPerishible = Arrays.asList("");
        this.keyMaterial = Material.TRIPWIRE_HOOK;
        this.keyName = ChatColor.GOLD + "Key";
        this.keyShape = Arrays.asList("I", "L");
        this.masterKeyMaterial = Material.NAME_TAG;
        this.masterKeyName = ChatColor.DARK_PURPLE + "Master Key";
        this.masterKeyShape = Arrays.asList("C", "L");
        this.bunchOfKeysMaterial = Material.NAME_TAG;
        this.bunchOfKeysName = ChatColor.BLUE + "Bunch of keys";
        this.bunchOfKeysShape = Arrays.asList(" S ", "SLS", " S ");
        (this.shapeMaterials = new LinkedHashMap<String, String>()).put("I", Material.IRON_INGOT.name());
        this.shapeMaterials.put("L", Material.LEVER.name());
        this.shapeMaterials.put("C", Material.COMMAND.name());
        this.shapeMaterials.put("S", Material.STRING.name());
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
}
