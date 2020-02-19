package com.steffbeard.totalwar.core.listeners;

import com.steffbeard.totalwar.core.*;
import org.bukkit.event.Listener;

public abstract class KeyListener implements Listener {
    
	Main plugin;
    KeyAPI api;
    
    KeyListener(final Main plugin) {
        this.setPlugin(plugin);
    }
    
    public Main getPlugin() {
        return this.plugin;
    }
    
    public void setPlugin(final Main plugin) {
        this.plugin = plugin;
        this.api = plugin.getAPI();
    }
}
