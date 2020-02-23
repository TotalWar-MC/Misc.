package com.steffbeard.totalwar.core.listeners;

import org.bukkit.Particle;
import org.bukkit.entity.Arrow;
import org.bukkit.block.Block;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;

import com.steffbeard.totalwar.core.Config;

import org.bukkit.event.EventHandler;
import org.bukkit.entity.LivingEntity;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.entity.Entity;
import java.util.HashSet;
import org.bukkit.event.Listener;

public class ArrowListener implements Listener {
	
	private Config config;
    private HashSet<Entity> track_arrow;
    
    public ArrowListener() {
        this.track_arrow = new HashSet<Entity>();
    }
    
    @EventHandler
    public void onProjectileShotEvent(final EntityShootBowEvent event) {
        final Entity entity = (Entity)event.getEntity();
        if (entity instanceof Player) {
            final Player player = (Player)event.getEntity();
            if (player.hasPermission("flame.bow") && event.getBow().containsEnchantment(Enchantment.ARROW_FIRE)) {
                this.track_arrow.add(event.getProjectile());
            }
        }
        else if (entity instanceof LivingEntity && event.getBow().containsEnchantment(Enchantment.ARROW_FIRE)) {
            this.track_arrow.add(event.getProjectile());
        }
    }
    
    public boolean getBlockSide(final ProjectileHitEvent event) {
        final Location hLoc = event.getHitBlock().getLocation();
        BlockFace[] values;
        for (int length = (values = BlockFace.values()).length, i = 0; i < length; ++i) {
            final BlockFace face = values[i];
            final Block block = hLoc.getWorld().getBlockAt(hLoc).getRelative(face);
            if (block.getType() == Material.AIR) {
                block.setType(Material.FIRE);
                if (block.getType() == Material.FIRE) {
                    return true;
                }
            }
        }
        return false;
    }
    
    @EventHandler
    public void onProjectileHitEvent(final ProjectileHitEvent event) {
        final Entity entity = (Entity)event.getEntity();
        if (entity instanceof Arrow) {
            final Arrow arrow = (Arrow)entity;
            if (this.track_arrow.contains(arrow)) {
                this.track_arrow.remove(arrow);
                if (!(event.getHitEntity() instanceof Entity) && !this.getBlockSide(event)) {
                    arrow.getLocation();
                    arrow.getWorld().spawnParticle(Particle.SMOKE_NORMAL, arrow.getLocation(), 10, 0.1, 0.1, 0.1, 0.1);
                    arrow.remove();
                }
            }
        }
    }
    
    // buffing bows
    
    @EventHandler
    public void onEntityShootBowEventAlreadyIntializedSoIMadeThisUniqueName(EntityShootBowEvent event) {
      Integer power = event.getBow().getEnchantmentLevel(Enchantment.ARROW_DAMAGE);
      MetadataValue metadata = new FixedMetadataValue((Plugin) this, power);
      event.getProjectile().setMetadata("power", metadata);
    }

    @EventHandler
    public void onArrowHitEntity(EntityDamageByEntityEvent event) {
      Double multiplier = config.bowBuff;
      if(multiplier <= 1.000001 && multiplier >= 0.999999) {
        return;
      }

      if (event.getEntity() instanceof LivingEntity) {
        Entity damager = event.getDamager();
        if (damager instanceof Arrow) {
          Arrow arrow = (Arrow) event.getDamager();
          Double damage = event.getDamage() * config.bowBuff;
          Integer power = 0;
          if(arrow.hasMetadata("power")) {
            power = arrow.getMetadata("power").get(0).asInt();
          }
          damage *= Math.pow(1.25, power - 5); // f(x) = 1.25^(x - 5)
          event.setDamage(damage);
        }
      }
    }
}
