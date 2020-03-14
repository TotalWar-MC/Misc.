package com.steffbeard.totalwar.core.listeners;

import com.steffbeard.totalwar.core.Main;
import com.steffbeard.totalwar.core.calander.CalendarFiles;
import com.steffbeard.totalwar.core.events.NewDayEvent;

import com.xxmicloxx.NoteBlockAPI.*;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

@SuppressWarnings("deprecation")
public class NewDayListener implements Listener {

    @EventHandler
    public void onNewDay(NewDayEvent e) {

        if (Main.calendarFiles.getSetting("new-day-alert") == true) {

            for (Player players : Bukkit.getOnlinePlayers()) {

                if(!NoteBlockPlayerMain.isReceivingSong(players))
                playNewDayAlert(players);
            }
        }

    }



    public void playNewDayAlert(Player p) {

        Song s = NBSDecoder.parse(CalendarFiles.newDayAlert);

        SongPlayer sp = new RadioSongPlayer(s);

        sp.addPlayer(p);
        sp.setPlaying(true);
        sp.setAutoDestroy(true);


    }

}
