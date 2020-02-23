package com.steffbeard.totalwar.core.listeners;

import org.bukkit.inventory.FurnaceInventory;
import org.bukkit.block.Furnace;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.Material;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.ChatColor;
import java.util.Collection;
import org.bukkit.potion.PotionEffect;

import com.steffbeard.totalwar.core.Config;
import com.steffbeard.totalwar.core.FoodItem;
import com.steffbeard.totalwar.core.Main;

import java.util.ArrayList;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.Listener;

@SuppressWarnings("deprecation")
public class SpoiledFoodListener implements Listener {
    
	/*
	 * Handles food spoilage and rot
	 */
	
	private Config config;
	
	@EventHandler
    void invClick(final InventoryClickEvent e) {
        if (e.getCurrentItem() == null) {
            return;
        }
        if (!FoodItem.isFoodItem(e.getCurrentItem().getType())) {
            return;
        }
        new ItemChecker().checkItem(e.getCurrentItem());
    }
    
    @EventHandler
    void invOpen(final InventoryOpenEvent e) {
        final ItemChecker checker = new ItemChecker();
        checker.checkInventory(e.getInventory(), true);
    }
    
    @EventHandler
    void pickup(final PlayerPickupItemEvent e) {
        if (FoodItem.isFoodItem(e.getItem().getItemStack().getType())) {
            new ItemChecker().checkItem(e.getItem().getItemStack());
        }
    }
    
    @SuppressWarnings("incomplete-switch")
	@EventHandler
    void eat(final PlayerItemConsumeEvent e) {
        final ItemStack i = e.getItem();
        if (!FoodItem.isFoodItem(i.getType()) || !FoodItem.hasFoodItemLore(i)) {
            return;
        }
        final Player p = e.getPlayer();
        int removeFood = 0;
        final FoodItem food = new FoodItem(i);
        food.updateSpoilTime(System.currentTimeMillis());
        String msg = "";
        int damage = 0;
        final ArrayList<PotionEffect> eff = new ArrayList<PotionEffect>();
        if (food.getPercent() <= 0) {
            removeFood = ItemChecker.foodValue(i.getType(), i.getDurability()) * config.rottenLevel / 100;
            damage = config.rottenDamage;
            if (config.enableRottenDebuffs) {
                eff.addAll((Collection<? extends PotionEffect>) config.rottenDebuffs);
            }
        }
        else {
            switch (food.phase) {
                case FRESH: {
                    return;
                }
                case STALE: {
                    removeFood = ItemChecker.foodValue(i.getType(), i.getDurability()) * config.staleLevel / 100;
                    damage = config.staleDamage;
                    if (!config.enableStaleDebuffs) {
                        eff.addAll((Collection<? extends PotionEffect>) config.staleDebuffs);
                        break;
                    }
                    break;
                }
                case SPOILED: {
                    removeFood = ItemChecker.foodValue(i.getType(), i.getDurability()) * config.spoiledLevel / 100;
                    damage = config.spoiledDamage;
                    if (!config.enableSpoiledDebuffs) {
                        eff.addAll((Collection<? extends PotionEffect>) config.spoiledDebuffs);
                        break;
                    }
                    break;
                }
            }
        }
        if (!msg.equals("")) {
            p.sendMessage(ChatColor.translateAlternateColorCodes('$', msg));
        }
        if (damage > 0) {
            p.damage((double)damage);
        }
        p.setFoodLevel(p.getFoodLevel() - removeFood);
        if (!eff.isEmpty()) {
            for (final PotionEffect effect : eff) {
                p.addPotionEffect(effect);
            }
        }
    }
    
    public String colorize(final String msg) {
        String coloredMsg = "";
        for (int i = 0; i < msg.length(); ++i) {
            if (msg.charAt(i) == '&') {
                coloredMsg = String.valueOf(coloredMsg) + '§';
            }
            else {
                coloredMsg = String.valueOf(coloredMsg) + msg.charAt(i);
            }
        }
        return coloredMsg;
    }
    
    @EventHandler
    void craftItem(final CraftItemEvent e) {
        int filledSlots = 0;
        ItemStack food = null;
        ItemStack salt = null;
        boolean sugar = false;
        ItemStack[] matrix;
        for (int length = (matrix = e.getInventory().getMatrix()).length, j = 0; j < length; ++j) {
            final ItemStack i = matrix[j];
            if (i != null && !i.getType().equals((Object)Material.AIR)) {
                ++filledSlots;
                if (i.getType().isEdible()) {
                    food = i;
                }
                else if (i.hasItemMeta() && i.getItemMeta().hasDisplayName() && i.getItemMeta().getDisplayName().equals(ChatColor.AQUA + "Salt")) {
                    salt = i;
                }
                else if (salt == null && i.getType().equals((Object)Material.SUGAR)) {
                    sugar = true;
                }
            }
        }
        if (filledSlots != 2) {
            return;
        }
        if (sugar && food != null) {
            e.setCancelled(true);
            return;
        }
        if (e.getClick().equals((Object)ClickType.SHIFT_RIGHT)) {
            e.setCancelled(true);
            return;
        }
        final ItemStack foodClone = food.clone();
        final FoodItem foodItem = new FoodItem(foodClone);
        foodItem.addDurability(config.saltRestore, false, true);
        if (e.getClick().equals((Object)ClickType.SHIFT_LEFT)) {
            if (salt.getAmount() > food.getAmount()) {
                foodClone.setAmount(food.getAmount());
                salt.setAmount(salt.getAmount() - food.getAmount());
                e.getInventory().remove(food);
            }
            else if (salt.getAmount() < food.getAmount()) {
                foodClone.setAmount(salt.getAmount());
                food.setAmount(food.getAmount() - salt.getAmount());
                e.getInventory().remove(salt);
            }
            else {
                foodClone.setAmount(food.getAmount());
                e.getInventory().remove(salt);
                e.getInventory().remove(food);
            }
            final Player p = (Player)e.getWhoClicked();
            p.getInventory().addItem(new ItemStack[] { foodClone });
            p.updateInventory();
            return;
        }
        foodClone.setAmount(1);
        e.setCurrentItem(foodClone);
    }
    
    @EventHandler
    void furnace(final FurnaceSmeltEvent e) {
        if (e.getSource().getType().equals((Object)Material.POTION)) {
            e.getBlock().getWorld().dropItem(e.getBlock().getLocation(), Main.salt());
        }
        if (!FoodItem.isFoodItem(e.getResult().getType())) {
            return;
        }
        if (!FoodItem.hasFoodItemLore(e.getSource())) {
            return;
        }
        final FurnaceInventory furnaceInv = ((Furnace)e.getBlock().getState()).getInventory();
        final ItemStack occupied = furnaceInv.getResult();
        final ItemStack result = e.getResult();
        result.setItemMeta(e.getSource().getItemMeta());
        final FoodItem food = new FoodItem(result);
        if (!food.phase.equals(FoodItem.FoodPhase.ROTTEN)) {
            food.addDurability(config.cookRestore, false, false);
        }
        if (occupied != null && occupied.getType().name().contains(e.getResult().getType().name())) {
            occupied.setItemMeta(result.getItemMeta());
            ItemStack source = e.getSource();
            if (source.getAmount() > 1) {
                source.setAmount(source.getAmount() - 1);
            }
            else {
                source = null;
            }
        }
    }
}
