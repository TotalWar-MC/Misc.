package com.steffbeard.totalwar.core.events;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class StructureInteractEvent extends Event implements Cancellable {
	private StructureData data = null;
	private boolean cancelled = false;
	private Block clicked = null;
	private static final HandlerList handlers = new HandlerList();
	private Player player = null;

	public StructureInteractEvent(StructureData data, Block clicked, Player player) {
		this.data = data;
		this.clicked = clicked;
		this.player = player;
	}

	public StructureData getData() {
		return this.data;
	}

	public Block getBlock() {
		return this.clicked;
	}

	public Player getPlayer() {
		return this.player;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean arg0) {
		cancelled = true;
	}
}
