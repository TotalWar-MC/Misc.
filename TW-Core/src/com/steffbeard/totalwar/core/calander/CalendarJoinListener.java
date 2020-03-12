package com.steffbeard.totalwar.core.calander;


import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import com.steffbeard.totalwar.core.commands.CalendarCommand;

public class CalendarJoinListener implements Listener {

	private CalendarFiles calendarFiles;
	
    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        if (calendarFiles.getSetting("auto-show")) {
            Player p = e.getPlayer();
            CalendarCommand.showCalendar(p);
        }
    }

}
