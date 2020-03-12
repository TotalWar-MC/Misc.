package com.steffbeard.totalwar.core.utils;

import org.bukkit.Axis;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.Orientable;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class MultiBlockUtils {

	public static BlockData getBlockDataFromString(Location loc, String value) {
		String[] d = value.replaceAll("\\[", "|").replaceAll("\\]", "").replace("minecraft:", "").split("([|])");
		String material = "";
		String data = "";
		BlockFace facing = BlockFace.UP;
		Bisected.Half half = Bisected.Half.BOTTOM;
		Rail.Shape shape = Rail.Shape.NORTH_SOUTH;
		Axis axis = Axis.X;
		boolean waterlogged = false;
		try {
			material = d[0];
			data = d[1];
			for (String info : data.split("([,])")) {
				String[] infoz = info.split("([=])");
				String info1 = infoz[0];
				String info2 = infoz[1];
				if (info1.equalsIgnoreCase("facing")) {
					facing = BlockFace.valueOf(info2.toUpperCase());
				}
				if (info1.equalsIgnoreCase("half")) {
					half = Bisected.Half.valueOf(info2.toUpperCase());
				}
				if (info1.equalsIgnoreCase("waterlogged")) {
					waterlogged = Boolean.valueOf(info2.toUpperCase());
				}
				if (info1.equalsIgnoreCase("shape")) {
					shape = Rail.Shape.valueOf(info2.toUpperCase());
				}
				if (info1.equalsIgnoreCase("axis")) {
					axis = Axis.valueOf(info2.toUpperCase());
				}
			}
		} catch (Exception err) {
		}

		loc.getBlock().setType(Material.valueOf(material.toUpperCase()));

		BlockData bd = loc.getBlock().getBlockData();
		if (loc.getBlock().getBlockData() instanceof Directional) {
			Directional direction = (Directional) bd;
			direction.setFacing(facing);
		}
		if (loc.getBlock().getBlockData() instanceof Bisected) {
			Bisected bisected = (Bisected) bd;
			bisected.setHalf(half);
		}
		if (loc.getBlock().getBlockData() instanceof Waterlogged) {
			Waterlogged w = (Waterlogged) bd;
			w.setWaterlogged(waterlogged);
		}
		if (loc.getBlock().getBlockData() instanceof Rail) {
			Rail rail = (Rail) bd;
			rail.setShape(shape);
		}
		if (loc.getBlock().getBlockData() instanceof Orientable) {
			Orientable or = (Orientable) bd;
			or.setAxis(axis);
		}
		return bd;
	}
}
