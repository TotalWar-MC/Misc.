package com.steffbeard.totalwar.core;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
//import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import com.steffbeard.totalwar.core.calander.CalendarFiles;
import com.steffbeard.totalwar.core.calander.DayExtend;
import com.steffbeard.totalwar.core.calander.PCalendar;
import com.steffbeard.totalwar.core.commands.CalendarCommand;
import com.steffbeard.totalwar.core.commands.PlaytimeCommand;
import com.steffbeard.totalwar.core.listeners.ArrowListener;
import com.steffbeard.totalwar.core.listeners.BlocksListener;
import com.steffbeard.totalwar.core.listeners.BunchOfKeysListener;
import com.steffbeard.totalwar.core.listeners.GlobalListener;
import com.steffbeard.totalwar.core.listeners.HopperListener;
import com.steffbeard.totalwar.core.listeners.ItemChecker;
import com.steffbeard.totalwar.core.listeners.MinecartListener;
import com.steffbeard.totalwar.core.listeners.SpoiledFoodListener;
import com.steffbeard.totalwar.core.listeners.TorchListener;
import com.steffbeard.totalwar.core.utils.KeyUtils;

public class Main extends JavaPlugin
{
    public static Main instance;
    public static Logger logger;
    public static CalendarFiles calendarFiles;
    public static PCalendar pCalendar;
    private int timer;
    protected ItemStack key;
    protected ItemStack masterKey;
    protected ItemStack keyClone;
    protected ItemStack bunchOfKeys;
    protected ItemStack padlockFinder;
    protected Config config;
    protected Messages messages;
    protected Data data;
	private KeyAPI api;
	
	SpoiledFoodListener listener;
	ItemChecker checker;
	int taskID;
    
    public void onEnable() {
        final File dataFolder = this.getDataFolder();
        this.config = new Config(dataFolder);
        try {
            this.config.load();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        this.messages = new Messages(dataFolder);
        try {
            this.messages.load();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        this.data = new Data(dataFolder);
        try {
            this.data.load();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        if (this.taskID != 0) {
            Bukkit.getScheduler().cancelTask(this.taskID);
        }
        this.startItemCheck();
        this.saveDefaultConfig();
        this.handleLocations();
        final PluginManager manager = Bukkit.getPluginManager();
        manager.registerEvents((Listener)new ArrowListener(), (Plugin)this);
        manager.registerEvents((Listener)new GlobalListener(), (Plugin)this);
        manager.registerEvents((Listener)new BlocksListener(), (Plugin)this);
        manager.registerEvents((Listener)new BunchOfKeysListener(), (Plugin)this);
        manager.registerEvents((Listener)new TorchListener(), (Plugin)this);
        manager.registerEvents((Listener)new MinecartListener(), (Plugin)this);
        if (this.config.disableHoppers) {
            manager.registerEvents((Listener)new HopperListener(), (Plugin)this);
        
        /*
         * Get the stuff for the calendar
         */
        CommandSender console = Bukkit.getConsoleSender();
        Bukkit.dispatchCommand(console, "gamerule doDaylightCycle false");

        calendarFiles = new CalendarFiles();
        calendarFiles.buildCalendar();

        pCalendar = new PCalendar(calendarFiles.getCalendar(), calendarFiles.getTicks());  
        
        new DayExtend().runTaskTimerAsynchronously(this, 0, 3);

        
        /*
         * 
         * Crafting recipes
         * 
         */
            //
            // chainmail helmet
            //
            final ItemStack chelmet = new ItemStack(Material.CHAINMAIL_HELMET);
            final ItemMeta chmeta = chelmet.getItemMeta();
            chmeta.setDisplayName(ChatColor.GRAY + "Mail coif");
           // chmeta.setLore((List<String>)Arrays.asList("'Tis but a scratch.'", "'A scratch!? Your arm's off!'"));
            chelmet.setItemMeta(chmeta);
            chelmet.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
            chelmet.addEnchantment(Enchantment.PROTECTION_PROJECTILE, 4);
            final NamespacedKey chkey = new NamespacedKey((Plugin)this, "chainmail_helmet");
            final ShapedRecipe chrecipe = new ShapedRecipe(chkey, chelmet);
            chrecipe.shape(new String[] { "@@@", "@#@" });
            chrecipe.setIngredient('@', Material.IRON_NUGGET);
            chrecipe.setIngredient('#', Material.AIR);
            Bukkit.addRecipe((Recipe)chrecipe);
            //
            // chainmail chest
            //
            final ItemStack cchestplate = new ItemStack(Material.CHAINMAIL_CHESTPLATE);
            final ItemMeta ccmeta = cchestplate.getItemMeta();
            ccmeta.setDisplayName(ChatColor.GRAY + "Mail Tunic");
          //  ccmeta.setLore((List<String>)Arrays.asList("'Very airy'"));
            cchestplate.setItemMeta(ccmeta);
            cchestplate.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
            final NamespacedKey cckey = new NamespacedKey((Plugin)this, "chainmail_chestplate");
            final ShapedRecipe ccrecipe = new ShapedRecipe(cckey, cchestplate);
            ccrecipe.shape(new String[] { "@#@", "@@@", "@@@" });
            ccrecipe.setIngredient('@', Material.IRON_NUGGET);
            ccrecipe.setIngredient('#', Material.AIR);
            Bukkit.addRecipe((Recipe)ccrecipe);
            //
            // chainmail leggings
            //
            final ItemStack cleggings = new ItemStack(Material.CHAINMAIL_LEGGINGS);
            final ItemMeta clmeta = cleggings.getItemMeta();
            clmeta.setDisplayName(ChatColor.GRAY + "Mail Leggings");
          //  clmeta.setLore((List<String>)Arrays.asList("'Stylish.'"));
            cleggings.setItemMeta(clmeta);
            final NamespacedKey clkey = new NamespacedKey((Plugin)this, "chainmail_leggings");
            final ShapedRecipe clrecipe = new ShapedRecipe(clkey, cleggings);
            clrecipe.shape(new String[] { "@@@", "@#@", "@#@" });
            clrecipe.setIngredient('@', Material.IRON_NUGGET);
            clrecipe.setIngredient('#', Material.AIR);
            Bukkit.addRecipe((Recipe)clrecipe);
            //
            // chain boots
            //
            final ItemStack cboots = new ItemStack(Material.CHAINMAIL_BOOTS);
            final ItemMeta cbmeta = cboots.getItemMeta();
            cbmeta.setDisplayName(ChatColor.GRAY + "Mail Boots");
          //  cbmeta.setLore((List<String>)Arrays.asList("'Stylish.'"));
            cboots.setItemMeta(cbmeta);
            final NamespacedKey cbkey = new NamespacedKey((Plugin)this, "chainmail_boots");
            final ShapedRecipe cbrecipe = new ShapedRecipe(cbkey, cboots);
            cbrecipe.shape(new String[] { "@#@", "@#@" });
            cbrecipe.setIngredient('@', Material.IRON_NUGGET);
            cbrecipe.setIngredient('#', Material.AIR);
            Bukkit.addRecipe((Recipe)cbrecipe);
            //
            // saddle
            //
            final ItemStack saddle = new ItemStack(Material.SADDLE, 1);
            final ItemMeta smeta = saddle.getItemMeta();
            smeta.setDisplayName(ChatColor.GRAY + "Saddle");
            saddle.setItemMeta(smeta);
            final NamespacedKey skey = new NamespacedKey((Plugin)this, "saddle");
            final ShapedRecipe srecipe = new ShapedRecipe(skey, saddle);
            srecipe.shape(new String[] { "LIL", "LLL" });
            srecipe.setIngredient('L', Material.LEATHER);
            srecipe.setIngredient('I', Material.IRON_INGOT);
            Bukkit.addRecipe((Recipe)srecipe);
            //
            // iron horse armor
            //
            final ItemStack ironhorsearmor = new ItemStack(Material.IRON_BARDING);
            final ItemMeta ihameta = ironhorsearmor.getItemMeta();
            ihameta.setDisplayName(ChatColor.GRAY + "Horse Barding");
            ironhorsearmor.setItemMeta(ihameta);
            final NamespacedKey ihakey = new NamespacedKey((Plugin)this, "iron_horse_armor");
            final ShapedRecipe iharecipe = new ShapedRecipe(ihakey, ironhorsearmor);
            iharecipe.shape(new String[] { "I##", "ISI", "III" });
            iharecipe.setIngredient('I', Material.IRON_INGOT);
            iharecipe.setIngredient('S', Material.SADDLE);
            iharecipe.setIngredient('#', Material.AIR);
            Bukkit.addRecipe((Recipe)iharecipe);
            //
            // gold horse armor
            //
            final ItemStack goldhorsearmor = new ItemStack(Material.GOLD_BARDING);
            final ItemMeta ghameta = goldhorsearmor.getItemMeta();
            ghameta.setDisplayName(ChatColor.RED + "Horse Barding");
            goldhorsearmor.setItemMeta(ghameta);
            final NamespacedKey ghakey = new NamespacedKey((Plugin)this, "gold_horse_armor");
            final ShapedRecipe gharecipe = new ShapedRecipe(ghakey, goldhorsearmor);
            gharecipe.shape(new String[] { "I##", "ISI", "III" });
            gharecipe.setIngredient('I', Material.GOLD_INGOT);
            gharecipe.setIngredient('S', Material.SADDLE);
            gharecipe.setIngredient('#', Material.AIR);
            Bukkit.addRecipe((Recipe)gharecipe);
            //
            // Salt DEPRECATED, new way to make this, 
            // keeping as a template for future furnace recipes
            //
            /*
            final ItemStack salt = new ItemStack(Material.SUGAR);
            final ItemMeta saltmeta = salt.getItemMeta();
            saltmeta.setDisplayName(new StringBuilder().append(ChatColor.WHITE).append(ChatColor.BOLD).append("Salt").toString());
            if (config.saltEnchanted) {
            	saltmeta.addEnchant(Enchantment.DURABILITY, 0, true);
            }
            saltmeta.setLore((List<String>)Arrays.asList(ChatColor.WHITE + config.saltDescription));
            salt.setItemMeta(saltmeta);
            Bukkit.addRecipe((Recipe)new FurnaceRecipe(salt, Material.WATER_BUCKET));
            */
            //
            // key
            //
            final ItemStack key = new ItemStack(Material.TRIPWIRE_HOOK);
            final ItemMeta keymeta = key.getItemMeta();
            keymeta.setDisplayName(ChatColor.GOLD + "Key");
            key.setItemMeta(keymeta);
            final NamespacedKey keykey = new NamespacedKey((Plugin)this, "key");
            final ShapedRecipe keyrecipe = new ShapedRecipe(keykey, key);
            keyrecipe.shape(new String[] { "I", "L" });
            keyrecipe.setIngredient('I', Material.IRON_INGOT);
            keyrecipe.setIngredient('L', Material.LEVER);
            Bukkit.addRecipe((Recipe)keyrecipe);
            //
            // masterkey
            // 
            final ItemStack masterKey = new ItemStack(Material.NAME_TAG);
            final ItemMeta masterKeymeta = masterKey.getItemMeta();
            masterKeymeta.setDisplayName(ChatColor.DARK_PURPLE + "Master Key");
            masterKey.setItemMeta(masterKeymeta);
            final NamespacedKey masterKeykey = new NamespacedKey((Plugin)this, "master_key");
            final ShapedRecipe masterKeyrecipe = new ShapedRecipe(masterKeykey, masterKey);
            masterKeyrecipe.shape(new String[] { "C", "L" });
            masterKeyrecipe.setIngredient('C', Material.COMMAND);
            masterKeyrecipe.setIngredient('L', Material.LEVER);
            Bukkit.addRecipe((Recipe)masterKeyrecipe);
            //
            // bunch of keys
            //
            final ItemStack bunchOfKeys = new ItemStack(Material.NAME_TAG);
            final ItemMeta bunchOfKeysmeta = bunchOfKeys.getItemMeta();
            bunchOfKeysmeta.setDisplayName(ChatColor.BLUE + "Bunch of keys");
            bunchOfKeys.setItemMeta(bunchOfKeysmeta);
            final NamespacedKey bunchOfKeyskey = new NamespacedKey((Plugin)this, "bunch_of_keys");
            final ShapedRecipe bunchOfKeysrecipe = new ShapedRecipe(bunchOfKeyskey, bunchOfKeys);
            bunchOfKeysrecipe.shape(new String[] { "#S#", "SLS", "#S#" });
            bunchOfKeysrecipe.setIngredient('S', Material.STRING);
            bunchOfKeysrecipe.setIngredient('L', Material.LEVER);
            bunchOfKeysrecipe.setIngredient('#', Material.AIR);
            Bukkit.addRecipe((Recipe)bunchOfKeysrecipe);
           
            /*
             * Logger
             */
            registerCommands();
            this.getLogger().info("> TOTAL WAR CORE IS ONLINE.");
        }
    }
        public Main() {
        	this.listener = new SpoiledFoodListener();
        	this.taskID = 0;
        }
        
        public static ItemStack salt() {
            final ItemStack saltitem = new ItemStack(Material.SUGAR);
            final ItemMeta saltmeta = saltitem.getItemMeta();
            saltmeta.addEnchant(Enchantment.DURABILITY, 0, true);
            saltmeta.setDisplayName(ChatColor.WHITE + "Salt");
            saltmeta.setLore((List<String>)Arrays.asList(ChatColor.GRAY + "Place in a chest with food to preserve it"));
            saltitem.setItemMeta(saltmeta);
            return saltitem;
        	}
        
        public void registerCommands() {
    		getCommand("playtime").setExecutor(new PlaytimeCommand());
    		getCommand("calendar").setExecutor(new CalendarCommand());
    		getCommand("cal").setExecutor(new CalendarCommand());
    	}
        
        @SuppressWarnings("deprecation")
		public void addRecipe() {
            Material[] values;
            for (int length = (values = Material.values()).length, i = 0; i < length; ++i) {
                final Material mat = values[i];
                if (mat.isEdible()) {
                    final ShapelessRecipe recipe = new ShapelessRecipe(new ItemStack(mat));
                    recipe.addIngredient(mat);
                    recipe.addIngredient(Material.SUGAR);
                    Bukkit.addRecipe((Recipe)recipe);
                }
            }
        }
        
        public void startItemCheck() {
            int delay = config.itemRefreshRate;
            if (delay < 1) {
                delay = 1;
            }
            this.taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask((Plugin)this, (Runnable)new Runnable() {
                @Override
                public void run() {
                    checker.now = System.currentTimeMillis();
                    for (final Player p : Bukkit.getOnlinePlayers()) {
                        checker.checkInventory((Inventory)p.getInventory(), false);
                        checker.checkInventory(p.getOpenInventory().getTopInventory(), true);
                    }
                }
            }, 0L, (long)(delay * 20));
    }
    
    /*
     * 
     *     Calander
     *     
     *   ig time	 ticks	      irl time
		1 second	0.27	    0.0138 seconds
		1 minute	16.6	    0.83 seconds
		1 hour		1,000	    50 seconds
		1 day		24,000	    20 minutes
		7 days  	168,000	    2.3 hours
		30 days	    720,000	    10 hours
	365.2422 days	8,766,000	121.75 hours (5.072916 days)

     */
    
    public void Calender() {
    	
    }
    
    public void createConfig() {
        this.getConfig().options().copyDefaults(true);
        this.saveDefaultConfig();
    }
    
    public void updateTimer() {
        if (!this.getConfig().getBoolean("entityFireTrail")) {
            Bukkit.getScheduler().cancelTask(this.timer);
        }
        else if (!Bukkit.getScheduler().isCurrentlyRunning(this.timer)) {
            this.trackEntity();
        }
    }
    
    // tracks arrows to apply fire when land
    
    private void trackEntity() {
        if (this.getConfig().getBoolean("entityFireTrail")) {
            this.timer = Bukkit.getScheduler().scheduleSyncRepeatingTask((Plugin)this, (Runnable)new Runnable() {
                @Override
                public void run() {
                    for (final Player p : Bukkit.getOnlinePlayers()) {
                        if (Main.this.getConfig().getStringList("EnabledWorlds").contains(p.getWorld().getName())) {
                            for (final Entity e : p.getWorld().getLivingEntities()) {
                                if (!(e instanceof Arrow) && e.getFireTicks() > 0) {
                                    final Block block = e.getLocation().getBlock();
                                    if (block.getType() != Material.AIR) {
                                        continue;
                                    }
                                    block.setType(Material.FIRE);
                                }
                            }
                        }
                    }
                }
            }, 0L, 12L);
        }
    }
    
    // Reduce registered PlayerInteractEvent count. onPlayerInteractAll handles
    //  cancelled events.
    
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent event) {
      if (!event.isCancelled()) {
        onEnchantingTableUse(event);
      }
    }

    @EventHandler(priority = EventPriority.LOWEST) // ignoreCancelled=false
    public void onPlayerInteractAll(PlayerInteractEvent event) {
    }
        
    public void onEnchantingTableUse(PlayerInteractEvent event) {
      if(!config.enableEnchanting) {
        return;
      }
      Action action = event.getAction();
      Material material = event.getClickedBlock().getType();
      boolean enchanting_table = action == Action.RIGHT_CLICK_BLOCK &&
                     material.equals(Material.ENCHANTMENT_TABLE);
      if(enchanting_table) {
        event.setCancelled(true);
      }
    }
    
    private final void handleLocations() {
        for (final Object object : new ArrayList<Object>(this.data.padlocks)) {
            final JSONObject json = (JSONObject)JSONValue.parse(object.toString());
            this.data.padlocks.add(new Location(Bukkit.getWorld(json.get((Object)"world").toString()), Double.parseDouble(json.get((Object)"x").toString()), Double.parseDouble(json.get((Object)"y").toString()), Double.parseDouble(json.get((Object)"z").toString()), Float.parseFloat(json.get((Object)"yaw").toString()), Float.parseFloat(json.get((Object)"pitch").toString())));
            this.data.padlocks.remove(object);
        }
    }
    
    public void onDisable() {
        this.getLogger().info("> SHUTTING DOWN TOTAL WAR CORE.");
        HandlerList.unregisterAll((Plugin)this);
        try {
            this.data.save();
            KeyUtils.clearFields(this);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
	 * 
	 * The built-in version of Bukkit's dropItem() method places the item at the block 
	 * vertex which can make the item jump around. 
	 * This method places the item in the middle of the block location with a slight 
	 * vertical velocity to mimic how normal broken blocks appear.
	 * @param l The location to drop the item
	 * @param is The item to drop
	 * 
	 */
	public void dropItemAtLocation(final Location l, final ItemStack is) {
		
		// Schedule the item to drop 1 tick later
		Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
			@Override
			public void run() {
				l.getWorld().dropItem(l.add(0.5, 0.5, 0.5), is).setVelocity(new Vector(0, 0.05, 0));
			}
		}, 1);
	}
    
    public void reload() {
        this.onDisable();
        this.onEnable();
    }

    public static double getMultiplier() {
        return Config.speedMultiplier;
    }

	public KeyAPI getAPI() {
		return this.api;
	}

	public Messages getMessages() {
		return this.messages;
	}

    public void sendMessage(final CommandSender sender, final String message) {
        sender.sendMessage(this.messages.prefix + " " + message);
	}

}
