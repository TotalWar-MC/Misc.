package com.steffbeard.totalwar.core.listeners;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.steffbeard.totalwar.core.events.StructureInteractEvent;
import com.steffbeard.totalwar.core.multiblock.structure.Structure;
import com.steffbeard.totalwar.core.multiblock.structure.StructureData;

public class MultiblockListener implements Listener {

	/*
	 * Temporary Event to test to see if 
	 * MultiBlock API is properly backported
	 */
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
}
