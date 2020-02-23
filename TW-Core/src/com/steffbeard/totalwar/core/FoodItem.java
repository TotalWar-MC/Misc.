package com.steffbeard.totalwar.core;

import org.bukkit.Material;
import org.bukkit.ChatColor;
import org.bukkit.inventory.meta.ItemMeta;

import com.steffbeard.totalwar.core.utils.HiddenStringUtils;

import java.util.List;
import java.util.ArrayList;
import org.bukkit.inventory.ItemStack;

public class FoodItem {

    private ItemStack i;
    private long lastCheck;
    private long currentSpoilTime;
    private long maxSpoilTime;
    public long freezeTime;
    private String progressBar;
    private boolean goesToRot;
    private boolean unFreeze;
    private int phase1;
    private int phase2;
    public FoodPhase phase;
    private Config config;
    
    public FoodItem(final ItemStack i) {
        this.progressBar = "";
        this.i = i;
        if (hasFoodItemLore(i)) {
            this.parseLore();
        }
        else {
            this.createNew();
        }
        this.phase1 = config.staleLevel;
        this.phase2 = config.spoiledLevel;
    }
    
    public ItemStack toItem() {
        if (this.goesToRot) {
            this.lastCheck = 0L;
            this.freezeTime = 0L;
            this.currentSpoilTime = 0L;
            this.maxSpoilTime = 0L;
        }
        final String data = String.valueOf(this.lastCheck) + ";" + this.currentSpoilTime + ";" + this.maxSpoilTime + ";" + this.goesToRot + ";" + this.freezeTime + ";" + this.unFreeze;
        final ArrayList<String> lore = new ArrayList<String>();
        lore.add(HiddenStringUtils.encodeString(data));
        if (this.progressBar.equals("")) {
            this.progressBar = this.buildProgressBar();
        }
        lore.add(this.progressBar);
        final ItemMeta meta = this.i.getItemMeta();
        meta.setDisplayName(this.buildName());
        meta.setLore((List<String>)lore);
        this.i.setItemMeta(meta);
        return this.i;
    }
    
    public int getPercent() {
        return (int)(this.currentSpoilTime / (float)(this.maxSpoilTime / 100L));
    }
    
    private String buildName() {
        String type = this.i.getType().name();
        ChatColor color = ChatColor.WHITE;
        if (config.stateChangeColor) {
            if (this.freezeTime > 0L || this.unFreeze) {
                color = ChatColor.AQUA;
            }
            else if (this.getPercent() <= 0) {
                color = ChatColor.DARK_RED;
            }
            else if (this.phase.equals(FoodPhase.SPOILED)) {
                color = ChatColor.RED;
            }
            else if (this.phase.equals(FoodPhase.STALE)) {
                color = ChatColor.YELLOW;
            }
            else {
                color = ChatColor.GREEN;
            }
        }
        String prefix;
        if ((config.frozenPrefix != null || this.freezeTime > 0L) || this.unFreeze) {
            prefix = String.valueOf(config.frozenPrefix) + " ";
        }
        else if (config.rottenPrefix != null || this.getPercent() <= 0) {
            prefix = String.valueOf(config.rottenPrefix) + " ";
        }
        else if (config.spoiledPrefix != null || this.phase.equals(FoodPhase.SPOILED)) {
            prefix = String.valueOf(config.spoiledPrefix) + " ";
        }
        else if (config.stalePrefix != null || this.phase.equals(FoodPhase.STALE)) {
            prefix = String.valueOf(config.stalePrefix) + " ";
        }
        else if (config.freshPrefix != null) {
            prefix = String.valueOf(config.freshPrefix) + " ";
        }
        else {
            prefix = "";
        }
        type = type.toLowerCase();
        if (type.contains("_")) {
            final String[] split = type.split("_");
            type = "";
            String[] array;
            for (int length = (array = split).length, i = 0; i < length; ++i) {
                final String s = array[i];
                type = String.valueOf(type) + " " + s.substring(0, 1).toUpperCase() + s.substring(1);
            }
            type = type.substring(1);
        }
        else {
            type = String.valueOf(type.substring(0, 1).toUpperCase()) + type.substring(1);
        }
        type = type.replace(" Item", "");
        return color + prefix + type;
    }
    
    public void updateSpoilTime(final long now) {
        this.currentSpoilTime -= now - this.lastCheck;
        if (this.freezeTime > 0L) {
            this.freezeTime -= now - this.lastCheck;
            if (this.freezeTime <= 0L) {
                this.freezeTime = 0L;
                if (this.unFreeze) {
                    this.resetFreeze();
                }
                else {
                    this.unFreeze = true;
                }
            }
        }
        if (this.currentSpoilTime <= 0L) {
            this.currentSpoilTime = 0L;
            this.goesToRot = true;
        }
        this.lastCheck = now;
        if (this.getPercent() < this.phase2) {
            this.phase = FoodPhase.SPOILED;
        }
        else if (this.getPercent() < this.phase1) {
            this.phase = FoodPhase.STALE;
        }
        else {
            this.phase = FoodPhase.FRESH;
        }
        this.progressBar = this.buildProgressBar();
    }
    
    public void removeDurability(final int percent) {
        final long newCurrent = this.currentSpoilTime / (percent / 100 + 1);
        this.maxSpoilTime /= percent / 100 + 1;
        if (newCurrent < 0L) {
            this.currentSpoilTime = 0L;
        }
        else {
            this.currentSpoilTime = newCurrent;
        }
    }
    
    public void addDurability(final int percent, final boolean alsoMax, final boolean fromMax) {
        long multiplier = this.currentSpoilTime;
        if (fromMax) {
            multiplier = this.maxSpoilTime;
        }
        long newCurrent = this.currentSpoilTime + multiplier * percent / 100L;
        final long newMax = this.maxSpoilTime + this.maxSpoilTime * percent / 100L;
        if (alsoMax) {
            this.maxSpoilTime = newMax;
        }
        else if (newCurrent > this.maxSpoilTime) {
            newCurrent = this.maxSpoilTime;
        }
        this.currentSpoilTime = newCurrent;
        this.updateSpoilTime(System.currentTimeMillis());
        this.toItem();
    }
    
    private String buildProgressBar() {
        int size;
        if (config.progressBarSize == 1) {
            size = 4;
        }
        else if (config.progressBarSize == 2) {
            size = 3;
        }
        else {
            if (config.progressBarSize != 3) {
                return "";
            }
            size = 2;
        }
        ChatColor color = ChatColor.GREEN;
        if (this.phase.equals(FoodPhase.SPOILED)) {
            color = ChatColor.RED;
        }
        else if (this.phase.equals(FoodPhase.STALE)) {
            color = ChatColor.YELLOW;
        }
        String bar = ChatColor.GRAY + "[" + color;
        for (int count = 0; count < 100; ++count) {
            if (count % size == 0) {
                if (this.getPercent() > count) {
                    bar = String.valueOf(bar) + ":";
                }
                else {
                    bar = String.valueOf(bar) + ChatColor.BLACK + ":";
                }
            }
        }
        bar = String.valueOf(bar) + ChatColor.GRAY + "] " + color + this.getPercent() + "%";
        return bar;
    }
    
    private void parseLore() {
        final List<String> lore = (List<String>)this.i.getItemMeta().getLore();
        final String[] data = HiddenStringUtils.extractHiddenString(lore.get(0)).split(";");
        this.lastCheck = Long.parseLong(data[0]);
        this.currentSpoilTime = Long.parseLong(data[1]);
        this.maxSpoilTime = Long.parseLong(data[2]);
        this.freezeTime = Long.parseLong(data[4]);
        this.unFreeze = Boolean.parseBoolean(data[5]);
        if (this.getPercent() <= 0) {
            this.phase = FoodPhase.ROTTEN;
        }
    }
    
    @SuppressWarnings("deprecation")
	private void createNew() {
        this.lastCheck = System.currentTimeMillis();
        this.maxSpoilTime = (config.specialSpoilageTime + this.i.getTypeId());
        if (this.maxSpoilTime < 1L) {
            this.maxSpoilTime = config.defaultSpoilageTime;
        }
        this.maxSpoilTime *= 1000L;
        this.currentSpoilTime = this.maxSpoilTime;
    }
    
    public void freeze() {
        this.freezeTime = config.freezeTime * 1000;
        this.addDurability(config.spoilDurationSlow, true, false);
        this.toItem();
    }
    
    public void resetFreeze() {
        this.removeDurability(config.spoilDurationSlow);
        this.unFreeze = false;
        this.toItem();
    }
    
    @Override
    public String toString() {
        return "Current: " + this.currentSpoilTime + " Max: " + this.maxSpoilTime + " Rotten: " + this.phase.equals(FoodPhase.ROTTEN) + " lastcheck: " + this.lastCheck;
    }
    
    public static boolean hasFoodItemLore(final ItemStack item) {
        return item.hasItemMeta() && item.getItemMeta().hasLore() && HiddenStringUtils.hasHiddenString(item.getItemMeta().getLore().get(0));
    }
    
	public static boolean isFoodItem(final Material mat) {
        //return config.nonPerishible == null || !config.nonPerishible.contains(mat.getId()) && mat.isEdible();
        return mat.isEdible();
    }
    
    public enum FoodPhase
    {
        FRESH("FRESH", 0), 
        STALE("STALE", 1), 
        SPOILED("SPOILED", 2), 
        ROTTEN("ROTTEN", 3);
        
        private FoodPhase(final String name, final int ordinal) {
        }
    }
}
