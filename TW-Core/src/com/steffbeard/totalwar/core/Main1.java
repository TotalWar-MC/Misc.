package net.toxiic.multiblock;

import java.util.HashMap;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.command.CommandExecutor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;

import net.toxiic.multiblock.structure.Structure;
import net.toxiic.multiblock.structure.StructureData;

public class Main extends JavaPlugin implements Listener {

	public static MultiblockManager mm = new MultiblockManager();
	public static Plugin plugin = null;
	public static YamlConfiguration config = null;
	static WorldEditPlugin worldEditPlugin = null;
	static boolean debug = false;
	FileConfiguration data = getConfig();

	public void onEnable() {
		plugin = this;
		config = (YamlConfiguration) getConfig();
		if (!Bukkit.getServer().getPluginManager().isPluginEnabled("ItemNBTAPI")) {
			Bukkit.getConsoleSender().sendMessage(
					"ItemNBTAPI is not installed! It must be installed for this plugin to function properly.");
			Bukkit.getPluginManager().disablePlugin(this);
		}
		if (Bukkit.getServer().getPluginManager().isPluginEnabled("WorldEdit")) {
			worldEditPlugin = (WorldEditPlugin) Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");
		} else {
			Bukkit.getConsoleSender().sendMessage(
					"WorldEdit is not installed! It must be installed for this plugin to function properly.");
			Bukkit.getPluginManager().disablePlugin(this);
		}
		init();
		this.getConfig().options().copyDefaults(true);
		this.saveDefaultConfig();
	}

	public void onDisable() {
		this.saveDefaultConfig();
		this.saveConfig();
	}

	@EventHandler
	public void onMbClick(StructureInteractEvent e) {
		Structure s = e.getData().getStructure();
		StructureData data = e.getData();
		Location size = s.getSize();
		Location top = data.getTopPoint();
		Location bottom = data.getBottomPoint();
		Location sh = e.getData().getWhatBlockShould();
		e.getPlayer().sendMessage(ChatColor.AQUA + "Structure name : " + ChatColor.GOLD + s.getName());
		e.getPlayer().sendMessage(
				ChatColor.AQUA + "Size : " + ChatColor.GOLD + size.getX() + ", " + size.getY() + ", " + size.getZ());
		e.getPlayer().sendMessage(
				ChatColor.AQUA + "Top point : " + ChatColor.GOLD + top.getX() + ", " + top.getY() + ", " + top.getZ());
		e.getPlayer().sendMessage(ChatColor.AQUA + "Bottom point : " + ChatColor.GOLD + bottom.getX() + ", "
				+ bottom.getY() + ", " + bottom.getZ());
		e.getPlayer().sendMessage(ChatColor.AQUA + "Block : " + ChatColor.GOLD + sh.getBlockX() + ", " + sh.getBlockY()
				+ ", " + sh.getBlockZ());
	}

	public void init() {
		Bukkit.getPluginManager().registerEvents(this, this);
		Bukkit.getPluginManager().registerEvents(mm, this);
		Bukkit.getPluginCommand("multiblock").setExecutor(new Commands());
		Bukkit.getPluginCommand("multiblock").setTabCompleter(new CommandTabCompleter());
		this.loadStructuresFromConfig();
	}

	public void loadStructuresFromConfig() {
		Object[] s = config.getConfigurationSection("structures").getKeys(false).toArray();
		for (Object n : s) {
			String name = (String) n;
			ConfigurationSection b = config.getConfigurationSection("structures." + n + ".blocks");
			HashMap<Location, BlockData> blocks = new HashMap<Location, BlockData>();
			for (Object o : b.getKeys(false).toArray()) {
				String[] coords = ((String) o).split(",");
				Location loc = new Location(Bukkit.getWorld("world"), Integer.parseInt(coords[0]),
						Integer.parseInt(coords[1]), Integer.parseInt(coords[2]));
				String value = ((String) b.get((String) o));
				blocks.put(loc, Util.getBlockDataFromString(loc, value));
			}
			new Structure(name, blocks);
		}
	}

	public void registerExampleMultiblocks() {
		HashMap<Location, ItemStack> multiblock1 = new HashMap<Location, ItemStack>();
		multiblock1.put(new Location(null, 0, 0, 0), new ItemStack(Material.COBBLESTONE));
		multiblock1.put(new Location(null, 0, 0, 1), new ItemStack(Material.COBBLESTONE));
		multiblock1.put(new Location(null, 0, 0, 2), new ItemStack(Material.COBBLESTONE));
		multiblock1.put(new Location(null, 0, 1, 0), new ItemStack(Material.STONE_BRICKS));
		multiblock1.put(new Location(null, 0, 1, 1), new ItemStack(Material.FURNACE));
		multiblock1.put(new Location(null, 0, 1, 2), new ItemStack(Material.STONE_BRICKS));
		multiblock1.put(new Location(null, 0, 2, 0), new ItemStack(Material.STONE_BRICKS));
		multiblock1.put(new Location(null, 0, 2, 1), new ItemStack(Material.COBBLESTONE_WALL));
		multiblock1.put(new Location(null, 0, 2, 2), new ItemStack(Material.STONE_BRICKS));
		multiblock1.put(new Location(null, 1, 0, 0), new ItemStack(Material.COBBLESTONE));
		multiblock1.put(new Location(null, 1, 0, 1), new ItemStack(Material.COBBLESTONE));
		multiblock1.put(new Location(null, 1, 0, 2), new ItemStack(Material.COBBLESTONE));
		multiblock1.put(new Location(null, 1, 1, 0), new ItemStack(Material.FURNACE));
		multiblock1.put(new Location(null, 1, 1, 1), new ItemStack(Material.LAVA));
		multiblock1.put(new Location(null, 1, 1, 2), new ItemStack(Material.FURNACE));
		multiblock1.put(new Location(null, 1, 2, 0), new ItemStack(Material.COBBLESTONE_WALL));
		multiblock1.put(new Location(null, 1, 2, 1), new ItemStack(Material.AIR));
		multiblock1.put(new Location(null, 1, 2, 2), new ItemStack(Material.COBBLESTONE_WALL));
		multiblock1.put(new Location(null, 2, 0, 0), new ItemStack(Material.COBBLESTONE));
		multiblock1.put(new Location(null, 2, 0, 1), new ItemStack(Material.COBBLESTONE));
		multiblock1.put(new Location(null, 2, 0, 2), new ItemStack(Material.COBBLESTONE));
		multiblock1.put(new Location(null, 2, 1, 0), new ItemStack(Material.STONE_BRICKS));
		multiblock1.put(new Location(null, 2, 1, 1), new ItemStack(Material.FURNACE));
		multiblock1.put(new Location(null, 2, 1, 2), new ItemStack(Material.STONE_BRICKS));
		multiblock1.put(new Location(null, 2, 2, 0), new ItemStack(Material.STONE_BRICKS));
		multiblock1.put(new Location(null, 2, 2, 1), new ItemStack(Material.COBBLESTONE_WALL));
		multiblock1.put(new Location(null, 2, 2, 2), new ItemStack(Material.STONE_BRICKS));

		HashMap<Location, ItemStack> multiblock2 = new HashMap<Location, ItemStack>();
		multiblock2.put(new Location(null, 0, 0, 0), new ItemStack(Material.NETHER_BRICKS));
		multiblock2.put(new Location(null, 0, 0, 1), new ItemStack(Material.NETHER_BRICKS));
		multiblock2.put(new Location(null, 0, 0, 2), new ItemStack(Material.STONE_BRICKS));
		multiblock2.put(new Location(null, 0, 0, 3), new ItemStack(Material.NETHER_BRICKS));
		multiblock2.put(new Location(null, 0, 0, 4), new ItemStack(Material.NETHER_BRICKS));
		multiblock2.put(new Location(null, 0, 1, 0), new ItemStack(Material.NETHER_BRICKS));
		multiblock2.put(new Location(null, 0, 1, 1), new ItemStack(Material.NETHER_BRICK_FENCE));
		multiblock2.put(new Location(null, 0, 1, 2), new ItemStack(Material.STONE_BRICKS));
		multiblock2.put(new Location(null, 0, 1, 3), new ItemStack(Material.NETHER_BRICK_FENCE));
		multiblock2.put(new Location(null, 0, 1, 4), new ItemStack(Material.NETHER_BRICKS));
		multiblock2.put(new Location(null, 0, 2, 0), new ItemStack(Material.NETHER_BRICKS));
		multiblock2.put(new Location(null, 0, 2, 1), new ItemStack(Material.NETHER_BRICK_FENCE));
		multiblock2.put(new Location(null, 0, 2, 2), new ItemStack(Material.STONE_BRICKS));
		multiblock2.put(new Location(null, 0, 2, 3), new ItemStack(Material.NETHER_BRICK_FENCE));
		multiblock2.put(new Location(null, 0, 2, 4), new ItemStack(Material.NETHER_BRICKS));
		multiblock2.put(new Location(null, 0, 3, 0), new ItemStack(Material.NETHER_BRICKS));
		multiblock2.put(new Location(null, 0, 3, 1), new ItemStack(Material.NETHER_BRICKS));
		multiblock2.put(new Location(null, 0, 3, 2), new ItemStack(Material.STONE_BRICKS));
		multiblock2.put(new Location(null, 0, 3, 3), new ItemStack(Material.NETHER_BRICKS));
		multiblock2.put(new Location(null, 0, 3, 4), new ItemStack(Material.NETHER_BRICKS));
		multiblock2.put(new Location(null, 0, 4, 0), new ItemStack(Material.NETHER_BRICKS));
		multiblock2.put(new Location(null, 0, 4, 1), new ItemStack(Material.NETHER_BRICKS));
		multiblock2.put(new Location(null, 0, 4, 2), new ItemStack(Material.STONE_BRICKS));
		multiblock2.put(new Location(null, 0, 4, 3), new ItemStack(Material.NETHER_BRICKS));
		multiblock2.put(new Location(null, 0, 4, 4), new ItemStack(Material.NETHER_BRICKS));
		multiblock2.put(new Location(null, 1, 0, 0), new ItemStack(Material.NETHER_BRICKS));
		multiblock2.put(new Location(null, 1, 0, 1), new ItemStack(Material.NETHER_BRICKS));
		multiblock2.put(new Location(null, 1, 0, 2), new ItemStack(Material.NETHER_BRICKS));
		multiblock2.put(new Location(null, 1, 0, 3), new ItemStack(Material.NETHER_BRICKS));
		multiblock2.put(new Location(null, 1, 0, 4), new ItemStack(Material.NETHER_BRICKS));
		multiblock2.put(new Location(null, 1, 1, 0), new ItemStack(Material.NETHER_BRICK_FENCE));
		multiblock2.put(new Location(null, 1, 1, 1), new ItemStack(Material.LAVA));
		multiblock2.put(new Location(null, 1, 1, 2), new ItemStack(Material.LAVA));
		multiblock2.put(new Location(null, 1, 1, 3), new ItemStack(Material.LAVA));
		multiblock2.put(new Location(null, 1, 1, 4), new ItemStack(Material.NETHER_BRICK_FENCE));
		multiblock2.put(new Location(null, 1, 2, 0), new ItemStack(Material.NETHER_BRICK_FENCE));
		multiblock2.put(new Location(null, 1, 2, 1), new ItemStack(Material.LAVA));
		multiblock2.put(new Location(null, 1, 2, 2), new ItemStack(Material.LAVA));
		multiblock2.put(new Location(null, 1, 2, 3), new ItemStack(Material.LAVA));
		multiblock2.put(new Location(null, 1, 2, 4), new ItemStack(Material.NETHER_BRICK_FENCE));
		multiblock2.put(new Location(null, 1, 3, 0), new ItemStack(Material.NETHER_BRICKS));
		multiblock2.put(new Location(null, 1, 3, 1), new ItemStack(Material.LAVA));
		multiblock2.put(new Location(null, 1, 3, 2), new ItemStack(Material.LAVA));
		multiblock2.put(new Location(null, 1, 3, 3), new ItemStack(Material.LAVA));
		multiblock2.put(new Location(null, 1, 3, 4), new ItemStack(Material.NETHER_BRICKS));
		multiblock2.put(new Location(null, 1, 4, 0), new ItemStack(Material.NETHER_BRICKS));
		multiblock2.put(new Location(null, 1, 4, 1), new ItemStack(Material.NETHER_BRICKS));
		multiblock2.put(new Location(null, 1, 4, 2), new ItemStack(Material.STONE_BRICKS));
		multiblock2.put(new Location(null, 1, 4, 3), new ItemStack(Material.NETHER_BRICKS));
		multiblock2.put(new Location(null, 1, 4, 4), new ItemStack(Material.NETHER_BRICKS));
		multiblock2.put(new Location(null, 2, 0, 0), new ItemStack(Material.STONE_BRICKS));
		multiblock2.put(new Location(null, 2, 0, 1), new ItemStack(Material.NETHER_BRICKS));
		multiblock2.put(new Location(null, 2, 0, 2), new ItemStack(Material.NETHER_BRICKS));
		multiblock2.put(new Location(null, 2, 0, 3), new ItemStack(Material.NETHER_BRICKS));
		multiblock2.put(new Location(null, 2, 0, 4), new ItemStack(Material.STONE_BRICKS));
		multiblock2.put(new Location(null, 2, 1, 0), new ItemStack(Material.STONE_BRICKS));
		multiblock2.put(new Location(null, 2, 1, 1), new ItemStack(Material.LAVA));
		multiblock2.put(new Location(null, 2, 1, 2), new ItemStack(Material.LAVA));
		multiblock2.put(new Location(null, 2, 1, 3), new ItemStack(Material.LAVA));
		multiblock2.put(new Location(null, 2, 1, 4), new ItemStack(Material.STONE_BRICKS));
		multiblock2.put(new Location(null, 2, 2, 0), new ItemStack(Material.STONE_BRICKS));
		multiblock2.put(new Location(null, 2, 2, 1), new ItemStack(Material.LAVA));
		multiblock2.put(new Location(null, 2, 2, 2), new ItemStack(Material.LAVA));
		multiblock2.put(new Location(null, 2, 2, 3), new ItemStack(Material.LAVA));
		multiblock2.put(new Location(null, 2, 2, 4), new ItemStack(Material.STONE_BRICKS));
		multiblock2.put(new Location(null, 2, 3, 0), new ItemStack(Material.STONE_BRICKS));
		multiblock2.put(new Location(null, 2, 3, 1), new ItemStack(Material.LAVA));
		multiblock2.put(new Location(null, 2, 3, 2), new ItemStack(Material.LAVA));
		multiblock2.put(new Location(null, 2, 3, 3), new ItemStack(Material.LAVA));
		multiblock2.put(new Location(null, 2, 3, 4), new ItemStack(Material.STONE_BRICKS));
		multiblock2.put(new Location(null, 2, 4, 0), new ItemStack(Material.STONE_BRICKS));
		multiblock2.put(new Location(null, 2, 4, 1), new ItemStack(Material.STONE_BRICKS));
		multiblock2.put(new Location(null, 2, 4, 2), new ItemStack(Material.STONE_BRICKS));
		multiblock2.put(new Location(null, 2, 4, 3), new ItemStack(Material.STONE_BRICKS));
		multiblock2.put(new Location(null, 2, 4, 4), new ItemStack(Material.STONE_BRICKS));
		multiblock2.put(new Location(null, 3, 0, 0), new ItemStack(Material.NETHER_BRICKS));
		multiblock2.put(new Location(null, 3, 0, 1), new ItemStack(Material.NETHER_BRICKS));
		multiblock2.put(new Location(null, 3, 0, 2), new ItemStack(Material.NETHER_BRICKS));
		multiblock2.put(new Location(null, 3, 0, 3), new ItemStack(Material.NETHER_BRICKS));
		multiblock2.put(new Location(null, 3, 0, 4), new ItemStack(Material.NETHER_BRICKS));
		multiblock2.put(new Location(null, 3, 1, 0), new ItemStack(Material.NETHER_BRICK_FENCE));
		multiblock2.put(new Location(null, 3, 1, 1), new ItemStack(Material.LAVA));
		multiblock2.put(new Location(null, 3, 1, 2), new ItemStack(Material.LAVA));
		multiblock2.put(new Location(null, 3, 1, 3), new ItemStack(Material.LAVA));
		multiblock2.put(new Location(null, 3, 1, 4), new ItemStack(Material.NETHER_BRICK_FENCE));
		multiblock2.put(new Location(null, 3, 2, 0), new ItemStack(Material.NETHER_BRICK_FENCE));
		multiblock2.put(new Location(null, 3, 2, 1), new ItemStack(Material.LAVA));
		multiblock2.put(new Location(null, 3, 2, 2), new ItemStack(Material.LAVA));
		multiblock2.put(new Location(null, 3, 2, 3), new ItemStack(Material.LAVA));
		multiblock2.put(new Location(null, 3, 2, 4), new ItemStack(Material.NETHER_BRICK_FENCE));
		multiblock2.put(new Location(null, 3, 3, 0), new ItemStack(Material.NETHER_BRICKS));
		multiblock2.put(new Location(null, 3, 3, 1), new ItemStack(Material.LAVA));
		multiblock2.put(new Location(null, 3, 3, 2), new ItemStack(Material.LAVA));
		multiblock2.put(new Location(null, 3, 3, 3), new ItemStack(Material.LAVA));
		multiblock2.put(new Location(null, 3, 3, 4), new ItemStack(Material.NETHER_BRICKS));
		multiblock2.put(new Location(null, 3, 4, 0), new ItemStack(Material.NETHER_BRICKS));
		multiblock2.put(new Location(null, 3, 4, 1), new ItemStack(Material.NETHER_BRICKS));
		multiblock2.put(new Location(null, 3, 4, 2), new ItemStack(Material.STONE_BRICKS));
		multiblock2.put(new Location(null, 3, 4, 3), new ItemStack(Material.NETHER_BRICKS));
		multiblock2.put(new Location(null, 3, 4, 4), new ItemStack(Material.NETHER_BRICKS));
		multiblock2.put(new Location(null, 4, 0, 0), new ItemStack(Material.NETHER_BRICKS));
		multiblock2.put(new Location(null, 4, 0, 1), new ItemStack(Material.NETHER_BRICKS));
		multiblock2.put(new Location(null, 4, 0, 2), new ItemStack(Material.STONE_BRICKS));
		multiblock2.put(new Location(null, 4, 0, 3), new ItemStack(Material.NETHER_BRICKS));
		multiblock2.put(new Location(null, 4, 0, 4), new ItemStack(Material.NETHER_BRICKS));
		multiblock2.put(new Location(null, 4, 1, 0), new ItemStack(Material.NETHER_BRICKS));
		multiblock2.put(new Location(null, 4, 1, 1), new ItemStack(Material.NETHER_BRICK_FENCE));
		multiblock2.put(new Location(null, 4, 1, 2), new ItemStack(Material.STONE_BRICKS));
		multiblock2.put(new Location(null, 4, 1, 3), new ItemStack(Material.NETHER_BRICK_FENCE));
		multiblock2.put(new Location(null, 4, 1, 4), new ItemStack(Material.NETHER_BRICKS));
		multiblock2.put(new Location(null, 4, 2, 0), new ItemStack(Material.NETHER_BRICKS));
		multiblock2.put(new Location(null, 4, 2, 1), new ItemStack(Material.NETHER_BRICK_FENCE));
		multiblock2.put(new Location(null, 4, 2, 2), new ItemStack(Material.STONE_BRICKS));
		multiblock2.put(new Location(null, 4, 2, 3), new ItemStack(Material.NETHER_BRICK_FENCE));
		multiblock2.put(new Location(null, 4, 2, 4), new ItemStack(Material.NETHER_BRICKS));
		multiblock2.put(new Location(null, 4, 3, 0), new ItemStack(Material.NETHER_BRICKS));
		multiblock2.put(new Location(null, 4, 3, 1), new ItemStack(Material.NETHER_BRICKS));
		multiblock2.put(new Location(null, 4, 3, 2), new ItemStack(Material.STONE_BRICKS));
		multiblock2.put(new Location(null, 4, 3, 3), new ItemStack(Material.NETHER_BRICKS));
		multiblock2.put(new Location(null, 4, 3, 4), new ItemStack(Material.NETHER_BRICKS));
		multiblock2.put(new Location(null, 4, 4, 0), new ItemStack(Material.NETHER_BRICKS));
		multiblock2.put(new Location(null, 4, 4, 1), new ItemStack(Material.NETHER_BRICKS));
		multiblock2.put(new Location(null, 4, 4, 2), new ItemStack(Material.STONE_BRICKS));
		multiblock2.put(new Location(null, 4, 4, 3), new ItemStack(Material.NETHER_BRICKS));
		multiblock2.put(new Location(null, 4, 4, 4), new ItemStack(Material.NETHER_BRICKS));

		// new Structure("medium_furnace", multiblock1);
		// new Structure("large_nether_furnace", multiblock2);
	}
}
