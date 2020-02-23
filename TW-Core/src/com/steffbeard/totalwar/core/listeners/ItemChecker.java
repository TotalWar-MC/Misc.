package com.steffbeard.totalwar.core.listeners;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.steffbeard.totalwar.core.Config;
import com.steffbeard.totalwar.core.FoodItem;

import org.bukkit.inventory.Inventory;

public class ItemChecker {
    
	public long now;
	private Config config;
    
    public ItemChecker() {
        this.now = System.currentTimeMillis();
    }
    
    public void checkInventory(final Inventory inventory, final boolean isChest) {
        if (config.enableFreezing && isChest) {
            this.checkForSnow(inventory);
        }
        ItemStack[] contents;
        for (int length = (contents = inventory.getContents()).length, j = 0; j < length; ++j) {
            final ItemStack i = contents[j];
            if (i != null) {
                if (FoodItem.isFoodItem(i.getType())) {
                    final FoodItem fi = new FoodItem(i);
                    fi.updateSpoilTime(this.now);
                    if (!fi.phase.equals(FoodItem.FoodPhase.ROTTEN)) {
                        i.setItemMeta(fi.toItem().getItemMeta());
                    }
                }
            }
        }
    }
    
    private void checkForSnow(final Inventory inv) {
        final int balls = this.countSnowballs(inv);
        if (balls == 0) {
            return;
        }
        ItemStack[] contents;
        for (int length = (contents = inv.getContents()).length, j = 0; j < length; ++j) {
            final ItemStack i = contents[j];
            if (i != null && FoodItem.isFoodItem(i.getType()) && FoodItem.hasFoodItemLore(i)) {
                final FoodItem food = new FoodItem(i);
                if (food.freezeTime <= 0L) {
                    if (balls >= i.getAmount()) {
                        food.freeze();
                        inv.removeItem(new ItemStack[] { new ItemStack(Material.SNOW_BALL, i.getAmount()) });
                    }
                }
            }
        }
    }
    
    private int countSnowballs(final Inventory inv) {
        int balls = 0;
        ItemStack[] contents;
        for (int length = (contents = inv.getContents()).length, j = 0; j < length; ++j) {
            final ItemStack i = contents[j];
            if (i != null && i.getType().equals((Object)Material.SNOW_BALL)) {
                balls += i.getAmount();
            }
        }
        return balls;
    }
    
    @SuppressWarnings("unused")
	private int getEmptySlots(final Inventory inv) {
        int slots = 0;
        ItemStack[] contents;
        for (int length = (contents = inv.getContents()).length, j = 0; j < length; ++j) {
            final ItemStack i = contents[j];
            if (i == null) {
                ++slots;
            }
        }
        return slots;
    }
    
    void checkItem(final ItemStack i) {
        final FoodItem fi = new FoodItem(i);
        fi.updateSpoilTime(this.now);
        if (!fi.phase.equals(FoodItem.FoodPhase.ROTTEN)) {
            i.setItemMeta(fi.toItem().getItemMeta());
        }
    }
    
    public static int foodValue(final Material mat, final int dmg) {
        switch (mat) {
            case APPLE: {
                return 4;
            }
            case MUSHROOM_SOUP: {
                return 6;
            }
            case BREAD: {
                return 5;
            }
            case PORK: {
                return 3;
            }
            case GRILLED_PORK: {
                return 8;
            }
            case GOLDEN_APPLE: {
                return 4;
            }
            case RAW_FISH: {
                if (dmg <= 1) {
                    return 2;
                }
                return 1;
            }
            case COOKED_FISH: {
                if (dmg == 0) {
                    return 5;
                }
                return 6;
            }
            case COOKIE: {
                return 2;
            }
            case MELON: {
                return 2;
            }
            case RAW_BEEF: {
                return 3;
            }
            case COOKED_BEEF: {
                return 8;
            }
            case RAW_CHICKEN: {
                return 2;
            }
            case COOKED_CHICKEN: {
                return 6;
            }
            case ROTTEN_FLESH: {
                return 4;
            }
            case SPIDER_EYE: {
                return 2;
            }
            case CARROT_ITEM: {
                return 3;
            }
            case POTATO_ITEM: {
                return 1;
            }
            case BAKED_POTATO: {
                return 5;
            }
            case POISONOUS_POTATO: {
                return 2;
            }
            case GOLDEN_CARROT: {
                return 6;
            }
            case PUMPKIN_PIE: {
                return 8;
            }
            case RABBIT: {
                return 3;
            }
            case COOKED_RABBIT: {
                return 5;
            }
            case RABBIT_STEW: {
                return 10;
            }
            case MUTTON: {
                return 2;
            }
            case COOKED_MUTTON: {
                return 6;
            }
            case CHORUS_FRUIT: {
                return 4;
            }
            case BEETROOT: {
                return 1;
            }
            case BEETROOT_SOUP: {
                return 6;
            }
            default: {
                return 4;
            }
        }
    }
}
