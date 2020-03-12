package com.steffbeard.totalwar.core.multiblock;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;

import com.steffbeard.totalwar.core.multiblock.structure.Structure;
import com.steffbeard.totalwar.core.multiblock.structure.StructureData;

public class MultiblockInventory {
	
	private Inventory inv = Bukkit.createInventory(null, 54);
	
	public enum Type {
		CRAFTING,
		PROCESSING,
		STORAGE,
		CUSTOM
	}
	
	public MultiblockInventory(Structure structure) {
		
	}
	
	public MultiblockInventory(StructureData data) {
		
	}
	
	public void openInventory(Player player) {
		
	}
	
	@EventHandler
	public void openMbInventory(InventoryOpenEvent e) {
		if(e.getInventory().getTitle().startsWith("edit - ") && e.getInventory().getSize() == 54) {
			
		}
	}

	@EventHandler
	public void closeMbInventory(InventoryCloseEvent e) {
		if(e.getInventory().getTitle().startsWith("edit - ") && e.getInventory().getSize() == 54) {
			
		}
	}
}
