package com.steffbeard.totalwar.core.multiblock.structure;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.steffbeard.totalwar.core.multiblock.MultiblockManager;

public class Structure {
	MultiblockManager mm = new MultiblockManager();
	private boolean loaded = true;
	private String name = "";
	private Map<Location, BlockData> multiblock = new HashMap<Location, BlockData>();

	public Structure(String name, HashMap<Location, BlockData> blocks) {
		this.name = name;
		HashMap<Location, BlockData> b = new HashMap<Location, BlockData>();
		for (Entry<Location, BlockData> e : blocks.entrySet()) {
			if (!e.getValue().getMaterial().toString().equalsIgnoreCase("air")) {
				b.put(e.getKey(), e.getValue());
			}
		}
		this.multiblock = b;
		Main.mm.loadStructure(this);
	}

	public void delete() {
		mm.deleteStructure(this);
	}

	public boolean isLoaded() {
		return loaded;
	}

	public void setLoaded(boolean loaded) {
		this.loaded = loaded;
	}

	public Map<Location, BlockData> getBlocks() {
		return this.multiblock;
	}

	public Map<Location, BlockData> getBlocks(boolean useConfig) {
		Map<Location, BlockData> ret = new HashMap<Location, BlockData>();
		if (!useConfig) {
			ConfigurationSection blocks = Main.config
					.getConfigurationSection("structures." + this.getName() + ".blocks");
			for (String str : blocks.getKeys(false)) {
				String[] coords = str.split(",");
				int x = Integer.parseInt(coords[0]);
				int y = Integer.parseInt(coords[1]);
				int z = Integer.parseInt(coords[2]);
				Location loc = new Location(Bukkit.getWorld("world"), x, y, z);
				BlockData bd = Util.getBlockDataFromString(loc, blocks.getString(str));
				ret.put(loc, bd);
			}
		} else {
			ret = this.multiblock;
		}
		return ret;
	}

	public String getName() {
		return this.name;
	}

	public Location getSize() {
		int minX = 0, maxX = 0, minY = 0, maxY = 0, minZ = 0, maxZ = 0;
		Location top = new Location(null, maxX, maxY, maxZ);
		Location bottom = new Location(null, minX, minY, minZ);
		for (Entry<Location, BlockData> entry : this.getBlocks(false).entrySet()) {
			Location l = entry.getKey();
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
		}
		if (top.getBlockX() <= 0) {
			top.setX((top.getX() - 1) * -1);
		}
		if (top.getBlockY() <= 0) {
			top.setY((top.getY() - 1) * -1);
		}
		if (top.getBlockZ() <= 0) {
			top.setZ((top.getZ() - 1) * -1);
		}
		if (bottom.getBlockX() <= 0) {
			bottom.setX((bottom.getX() - 1) * -1);
		}
		if (bottom.getBlockY() <= 0) {
			bottom.setY((bottom.getY() - 1) * -1);
		}
		if (bottom.getBlockZ() <= 0) {
			bottom.setZ((bottom.getZ() - 1) * -1);
		}
		Location size = new Location(null, bottom.getX() + top.getX(), bottom.getY() + top.getY(),
				bottom.getZ() + top.getZ());
		return size;
	}
}
