package com.steffbeard.totalwar.core.listeners;

import com.xxmicloxx.NoteBlockAPI.SongEndEvent;
import com.xxmicloxx.NoteBlockAPI.SongPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class SongEndListener implements Listener {

    @EventHandler
    public void onSongEnd(SongEndEvent e) {
        SongPlayer sp = e.getSongPlayer();

        for (String player_string : sp.getPlayerList()) {
            Player p = Bukkit.getPlayer(player_string);
            sp.removePlayer(p);
        }

    }

}
