package com.steffbeard.totalwar.core.multiblock.structure;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class StructureData {
	private Structure structure = null;
	private Location top = null;
	private Location bottom = null;
	private Location should = null;
	private Inventory inv = null;

	public StructureData(Structure structure, Location top, Location bottom, Location should) {
		this.structure = structure;
		this.top = top;
		this.bottom = bottom;
		this.should = should;
	}

	/*
	 * public Inventory getInventory() { return this.inv; }
	 */

	public Location getTopPoint() {
		return this.top;
	}

	public Location getBottomPoint() {
		return this.bottom;
	}

	public Structure getStructure() {
		return this.structure;
	}

	public Location getWhatBlockShould() {
		return this.should;
	}
}
