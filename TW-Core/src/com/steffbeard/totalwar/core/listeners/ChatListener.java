package com.steffbeard.totalwar.core.listeners;

import org.bukkit.event.EventHandler;
import java.util.List;
import org.bukkit.configuration.ConfigurationSection;
import java.util.Iterator;
import java.util.Set;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import java.util.Random;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.event.Listener;

public class ChatListener implements Listener {
    
	private JavaPlugin plugin;
    private Random rand;
    
    public ChatListener(final JavaPlugin p) {
        this.plugin = p;
        this.rand = new Random();
    }
    
    @EventHandler
    public void onChatEvent(final AsyncPlayerChatEvent e) {
        if (!e.getPlayer().hasPermission("textreplace.replace")) {
            return;
        }
        final FileConfiguration cfg = this.plugin.getConfig();
        final Set<String> groups = (Set<String>)cfg.getKeys(false);
        for (final String hog : groups) {
            final ConfigurationSection hogcs = cfg.getConfigurationSection(hog);
            final boolean enabled = hogcs.getBoolean("enable", true);
            if (enabled) {
                final List<String> plys = (List<String>)hogcs.getStringList("players");
                if (!plys.isEmpty() && !plys.contains(e.getPlayer().getName().toLowerCase())) {
                    continue;
                }
                final ConfigurationSection wordblock = hogcs.getConfigurationSection("blocks");
                final Set<String> blocks = (Set<String>)wordblock.getKeys(false);
                for (String word : blocks) {
                    final ConfigurationSection blockcs = wordblock.getConfigurationSection(word);
                    word = word.toLowerCase().substring(1);
                    final String rep = blockcs.getString("replace", "");
                    final double chance = blockcs.getDouble("chance", 100.0);
                    if (e.getMessage().toLowerCase().contains(word) && this.rand.nextDouble() < chance / 100.0) {
                        e.setMessage(e.getMessage().replaceAll("(?i)" + word, rep));
                    }
                }
            }
        }
    }
}
