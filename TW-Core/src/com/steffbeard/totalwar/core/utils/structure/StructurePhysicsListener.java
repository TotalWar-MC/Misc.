package com.steffbeard.totalwar.core.utils.structure;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import moe.kira.structure.StructureAPI.Clickable;
import moe.kira.structure.StructureAPI.Coord;
import moe.kira.structure.StructureAPI.LeftOnly;
import moe.kira.structure.StructureAPI.LinkStage;
import moe.kira.structure.StructureAPI.MainHandOnly;
import moe.kira.structure.StructureAPI.ReactAtBlock;
import moe.kira.structure.StructureAPI.RightOnly;
import moe.kira.structure.StructureAPI.Trigger;

import moe.kira.structure.event.CoordInteractEvent;
import moe.kira.structure.misc.Reference;

public class StructurePhysicsListener implements Listener {
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlace(BlockPlaceEvent evt) {
        StructureAPI.match(evt.getBlock(), LinkStage.CREATE, tree -> {
            if (tree.isPresent())
                tree.getStructureTree().structure.onCreate(evt, tree);
        });
    }
    
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBreak(BlockBreakEvent evt) {
        StructureAPI.match(BlockBreakEvent.class, evt.getBlock(), LinkStage.DESTROY, tree -> {
            if (tree.isPresent())
                ((ReactAtBlock) tree.getStructureTree().structure).onBlockEvent(evt, tree);
        });
    }
    
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = false)
    public void handle(PlayerInteractEvent event) {
        Action action = event.getAction();
        boolean leftClick = action == Action.LEFT_CLICK_BLOCK;
        boolean rightClick = action == Action.RIGHT_CLICK_BLOCK;
        if (!leftClick && !rightClick) return; // no a click with block
        
        Block originBlock = event.getClickedBlock();
        boolean offHand = event.getHand() == EquipmentSlot.OFF_HAND;
        
        Reference<Coord> originCoord = Reference.empty();
        StructureAPI.match(originBlock, LinkStage.INTERACT, matched -> {
            if (matched.isEmpty())
                return;
            
            CoordInteractEvent coordEvent = new CoordInteractEvent(event, matched.getStructureTree(), originCoord.get());
            coordEvent.setCancelled(event.isCancelled()); // sync cancellation
            Bukkit.getPluginManager().callEvent(coordEvent);
            
            event.setCancelled(coordEvent.isCancelled()); // sync cancellation
            if (!event.isCancelled())
                ((Clickable) matched.getStructureTree().getStructure()).onInteract(event, matched, originCoord.get());
        }, tree -> {
            if (!(tree.structure instanceof Clickable)) return false;
            if (tree.structure instanceof LeftOnly && rightClick) return false;
            if (tree.structure instanceof RightOnly && leftClick) return false;
            if (tree.structure instanceof MainHandOnly && offHand) return false;
            return true;
        }, coord -> {
            if (originCoord.isEmpty() /* reduce instructions */ && coord.isOrigin()) {
                if (coord.trigger == null) coord.trigger = new Trigger();
                if ((rightClick && !coord.trigger.rightClickable) || (leftClick && !coord.trigger.leftClickable) || (offHand && !coord.trigger.offHandClickable)) return false;
                originCoord.set(coord);
            }
            return true;
        });
    }
}
