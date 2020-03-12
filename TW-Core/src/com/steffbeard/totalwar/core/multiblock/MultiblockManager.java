package com.steffbeard.totalwar.core.multiblock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Axis;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.Orientable;
import org.bukkit.block.data.Rail;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.scheduler.BukkitRunnable;

import com.steffbeard.totalwar.core.events.StructureInteractEvent;
import com.steffbeard.totalwar.core.multiblock.structure.Structure;
import com.steffbeard.totalwar.core.multiblock.structure.StructureData;

import de.tr7zw.itemnbtapi.NBTItem;

public class MultiblockManager implements Listener {
	static Map<String, Structure> structures = new HashMap<String, Structure>();
	List<Player> tmp = new ArrayList<Player>(); // to make sure StructureInteractEvent isnt called twice... idk why but
												// this fixes it.

	@EventHandler
	public void onBlockInteract(PlayerInteractEvent e) {
		try {
			if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK) && e.getHand().equals(EquipmentSlot.HAND)) {
				StructureData data = getMultiblockStructure(e.getClickedBlock().getLocation(), e.getPlayer());
				if (getMultiblockStructure(e.getClickedBlock().getLocation(), e.getPlayer()) != null) {
					if (data.getStructure().isLoaded()) {
						if (tmp.contains(e.getPlayer())) {
							tmp.add(e.getPlayer());
						} else {
							tmp.remove(e.getPlayer());
							StructureInteractEvent event = new StructureInteractEvent(data, e.getClickedBlock(),
									e.getPlayer());
							Bukkit.getPluginManager().callEvent(event);
						}
					}
				}
			}
		} catch (Exception err) {
			err.printStackTrace(System.out);
		}
	}

	@EventHandler
	public void blockPlaceEvent(BlockPlaceEvent e) {
		if (e.getBlockPlaced().getType().equals(Material.REDSTONE_BLOCK)) {
			NBTItem i = new NBTItem(e.getItemInHand());
			if (i.hasKey("multiblock_spawner")) {
				String value = i.getString("multiblock_spawner");
				Location loc = e.getBlockPlaced().getLocation();
				Main.config.createSection("placement." + loc.getWorld().getName() + "," + loc.getBlockX() + ","
						+ loc.getBlockY() + "," + loc.getBlockZ());
				Structure s = getStructureByName(value);
				placeStructureWithDelay(s, loc, 5);
			}
		}
	}

	public void addStructureToConfig(Structure structure) {
		System.out.println("Adding " + structure.getName() + " to config");
		Main.config.createSection("structures." + structure.getName() + ".blocks");
		Main.config.createSection("structures." + structure.getName() + ".inventory");
		ConfigurationSection blocks = Main.config
				.getConfigurationSection("structures." + structure.getName() + ".blocks");
		for (Entry<Location, BlockData> entry : structure.getBlocks().entrySet()) {
			Location l = entry.getKey();
			BlockData bd = entry.getValue();
			if (bd != null) {
				String s1 = l.getBlockX() + "," + l.getBlockY() + "," + l.getBlockZ();
				String s2 = bd.getAsString();
				blocks.set(s1, s2);
			}
		}
		Main.plugin.saveDefaultConfig();
		Main.plugin.saveConfig();
	}

	public List<String> getStructureNames() {
		List<String> ret = new ArrayList<String>();
		for (Entry<String, Structure> entry : structures.entrySet()) {
			Structure s = entry.getValue();
			ret.add(s.getName());
		}
		return ret;
	}

	public List<Structure> getStructures() {
		List<Structure> ret = new ArrayList<Structure>();
		for (Entry<String, Structure> entry : structures.entrySet()) {
			Structure s = entry.getValue();
			ret.add(s);
		}
		return ret;
	}

	@SuppressWarnings("deprecation")
	public void loadStructure(Structure structure) {
		structures.put(structure.getName(), structure);
		addStructureToConfig(structure);
		structure.setLoaded(true);
		System.out.println("Loaded " + structure.getName() + " into memory");
	}

	public void unloadStructure(Structure structure) {
		structure.setLoaded(false);
		System.out.println("Unloaded " + structure.getName() + ".");
	}

	public Structure getStructureByName(String name) throws NullPointerException {
		if (structures.containsKey(name)) {
			return structures.get(name);
		} else {
			throw new NullPointerException();
		}
	}

	public void placeStructureWithDelay(Structure s, Location l, int seconds) {
		new BukkitRunnable() {
			int tmp = 0;

			public void run() {
				tmp++;
				if (tmp == seconds) {
					Main.mm.placeStructure(s, l);
					this.cancel();
				}
			}
		}.runTaskTimer(Main.plugin, 1L, 20L);
	}

	public void placeStructure(Structure s, Location l) {
		Map<Integer, Map<Location, BlockData>> list = new HashMap<Integer, Map<Location, BlockData>>();
		for (Entry<Location, BlockData> blocks : s.getBlocks().entrySet()) {
			Location key = blocks.getKey();
			BlockData value = blocks.getValue();
			Location size = s.getSize();
			int x = key.getBlockX() + l.getBlockX();
			int y = key.getBlockY() + l.getBlockY();
			int z = key.getBlockZ() + l.getBlockZ();
			if (size.getX() % 2 != 0) {
				x = x - (size.getBlockX() / 2);
			} else {
				x = x - (size.getBlockX() / 2) + 1;
			}
			if (size.getZ() % 2 != 0) {
				z = z - (size.getBlockZ() / 2);
			} else {
				z = z - (size.getBlockZ() / 2) + 1;
			}
			Location loc = new Location(l.getWorld(), x, y, z);
			Map<Location, BlockData> tmp = list.get(loc.getBlockY());
			if (tmp == null) {
				tmp = new HashMap<Location, BlockData>();
			}
		}
		for (Entry<Location, BlockData> blocks : s.getBlocks().entrySet()) {
			Location key = blocks.getKey();
			BlockData value = blocks.getValue();
			int y_ = key.getBlockY();
			Map<Location, BlockData> tmp = list.get(y_);
			if (tmp == null) {
				tmp = new HashMap<Location, BlockData>();
			}
			tmp.put(key, value);
			list.put(y_, tmp);
		}
		new BukkitRunnable() {
			int y = 0;

			public void run() {
				try {
					Map<Location, BlockData> map = list.get(y);
					for (Entry<Location, BlockData> blocks : map.entrySet()) {
						Location key = blocks.getKey();
						BlockData value = blocks.getValue();
						Location size = s.getSize();
						int x = key.getBlockX() + l.getBlockX();
						int y = key.getBlockY() + l.getBlockY();
						int z = key.getBlockZ() + l.getBlockZ();
						if (size.getX() % 2 != 0) {
							x = x - (size.getBlockX() / 2);
						} else {
							x = x - (size.getBlockX() / 2) + 1;
						}
						if (size.getZ() % 2 != 0) {
							z = z - (size.getBlockZ() / 2);
						} else {
							z = z - (size.getBlockZ() / 2) + 1;
						}
						Location loc = new Location(l.getWorld(), x, y, z);
						loc.getBlock().setType(value.getMaterial());
						loc.getBlock().setBlockData(value);
						if (this.y == list.size()) {
							this.cancel();
						}
					}
				} catch (Exception err) {
					this.cancel();
				}
				y++;
			}
		}.runTaskTimer(Main.plugin, 1L, 5L);
	}

	public void deleteStructure(Structure structure) {
		System.out.println("deleting Structure " + structure.getName());
		for (Entry<Location, BlockData> e : structure.getBlocks().entrySet()) {
			Location loc = e.getKey();
			BlockData data = e.getValue();
			structure.getBlocks().remove(loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ(),
					data.getAsString());
		}
		this.unloadStructure(structure);
		Main.plugin.getConfig().getConfigurationSection("structures").set(structure.getName(), null);
		Main.plugin.saveDefaultConfig();
		Main.plugin.saveConfig();
		System.out.println("Structure " + structure.getName() + " was deleted.");
	}

	public StructureData getMultiblockStructure(Location loc, Player p) {
		Block b = loc.getBlock();
		for (Structure s : this.getStructures()) {
			boolean tmp = true;
			if (s.isLoaded()) {
				if (tmp) {
					Location l = null;
					Location max = s.getSize();
					int minX = loc.getBlockX() - (max.getBlockX());
					int maxX = loc.getBlockX();
					int minY = loc.getBlockY() - (max.getBlockY());
					int maxY = loc.getBlockY();
					int minZ = loc.getBlockZ() - (max.getBlockZ());
					int maxZ = loc.getBlockZ();
					boolean valid = false;
					Location bottom_ = new Location(b.getWorld(), minX, minY, minZ);
					Location top_ = new Location(b.getWorld(), maxX, maxY, maxZ);
					Location should = null;
					for (int x = minX; x <= maxX; x++) {
						for (int y = minY; y <= maxY; y++) {
							for (int z = minZ; z <= maxZ; z++) {
								int found = 0;
								Location bottom = new Location(b.getWorld(), minX, minY, minZ);
								Location top = new Location(b.getWorld(), maxX, maxY, maxZ);
								for (Entry<Location, BlockData> blocks : s.getBlocks().entrySet()) {
									Location key = blocks.getKey();
									BlockData value = blocks.getValue();
									int x_ = x + key.getBlockX();
									int y_ = y + key.getBlockY();
									int z_ = z + key.getBlockZ();
									l = new Location(b.getWorld(), x_, y_, z_);
									if (l.getBlock().getBlockData().toString().equals(value.toString())) {

										found++;
										if (l.getBlockX() <= top.getBlockX()) {
											top.setX(l.getX());
										}
										if (l.getBlockX() >= bottom.getBlockX()) {
											bottom.setX(l.getX());
										}
										if (l.getBlockY() <= top.getBlockY()) {
											top.setY(l.getY());
										}
										if (l.getBlockY() >= bottom.getBlockY()) {
											bottom.setY(l.getY());
										}
										if (l.getBlockZ() <= top.getBlockZ()) {
											top.setZ(l.getZ());
										}
										if (l.getBlockZ() >= bottom.getBlockZ()) {
											bottom.setZ(l.getZ());
										}
									} else {
										tmp = false;
									}
									if (found == s.getBlocks().size()) {
										valid = true;
										should = new Location(null, x - minX - 1, y - minY - 1, z - minZ - 1);
										bottom_.setX(bottom.getX());
										bottom_.setY(bottom.getY());
										bottom_.setZ(bottom.getZ());
										top_.setX(top.getX());
										top_.setY(top.getY());
										top_.setZ(top.getZ());
									}
								}
							}
						}
					}
					if (valid) {
						if (loc.getBlockX() >= top_.getBlockX() && loc.getBlockX() <= bottom_.getBlockX()) {
							if (loc.getBlockY() >= top_.getBlockY() && loc.getBlockY() <= bottom_.getBlockY()) {
								if (loc.getBlockZ() >= top_.getBlockZ() && loc.getBlockZ() <= bottom_.getBlockZ()) {
									StructureData data = new StructureData(s, top_, bottom_, should);
									return data;
								}
							}
						}
					}
				}
			}
		}
		return null;
	}

}
