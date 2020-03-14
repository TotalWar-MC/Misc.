package com.steffbeard.totalwar.core.listeners;

import com.steffbeard.totalwar.core.Main;
import com.steffbeard.totalwar.core.calander.CalendarFiles;
import com.steffbeard.totalwar.core.events.NewEventEvent;
import com.xxmicloxx.NoteBlockAPI.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

@SuppressWarnings("deprecation")
public class NewEventListener implements Listener {

	@EventHandler
    public void onNewEvent(NewEventEvent e) {

        if (Main.calendarFiles.getSetting("event-alert") == true) {
            for (Player players : Bukkit.getOnlinePlayers()) {
                if (!NoteBlockPlayerMain.isReceivingSong(players))
                    playNewEventAlert(players);
            }
        }

    }


    public void playNewEventAlert(Player p) {

        Song s = NBSDecoder.parse(CalendarFiles.eventAlert);

        SongPlayer sp = new RadioSongPlayer(s);

        sp.addPlayer(p);
        sp.setPlaying(true);
        sp.setAutoDestroy(true);


    }

}


