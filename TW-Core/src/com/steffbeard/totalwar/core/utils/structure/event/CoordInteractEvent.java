package com.steffbeard.totalwar.core.utils.structure.event;

import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import lombok.Getter;
import lombok.Setter;
import moe.kira.structure.StructureAPI.Coord;
import moe.kira.structure.StructureAPI.StructureTree;

@Getter
public class CoordInteractEvent extends PlayerEvent implements Cancellable {
    private final PlayerInteractEvent sourceEvent;
    private final StructureTree structureTree;
    private final Coord coord;
    @Setter private boolean cancelled;
    
    public CoordInteractEvent(PlayerInteractEvent evt, StructureTree tree, Coord triggeredCoord) {
        super(evt.getPlayer());
        sourceEvent = evt;
        structureTree = tree;
        coord = triggeredCoord;
    }

    private static final HandlerList handlers = new HandlerList();

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
