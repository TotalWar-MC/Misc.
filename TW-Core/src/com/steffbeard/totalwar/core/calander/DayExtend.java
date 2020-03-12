package com.steffbeard.totalwar.core.calander;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

import com.steffbeard.totalwar.core.events.NewDayEvent;
import com.steffbeard.totalwar.core.events.NewEventEvent;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

/*

Extend a minecraft day (24,000 ticks | 20 minutes) to 3 times its size which should result to 72,000 ticks and an hour of time.

 */

public class DayExtend extends BukkitRunnable {
	
	private PCalendar pCalendar;
	private CalendarFiles calendarFiles;

    long currentTime;

    World world = pCalendar.getWorld();

    @Override
    public void run() {

        if(calendarFiles.getSetting("pause") == true){
            return;
        }

        GregorianCalendar gc = pCalendar.getCalendar();

        List<PEvent> events = calendarFiles.getAllEvents();

        for (PEvent event : events) {
            GregorianCalendar eventCalendar = event.getCalendar();
            long eventTicks = PCalendar.getTicksOf12HrTime(event.getTime());

            if (eventCalendar.equals(gc)) {
                if (PCalendar.get12HrTime(currentTime).equalsIgnoreCase(PCalendar.get12HrTime(eventTicks))){
                    NewEventEvent e = new NewEventEvent(event);
                    Bukkit.getPluginManager().callEvent(e);
                    if (e.isCancelled()) {

                    } else {
                        calendarFiles.removeEvent(event.getName());
                    }
                }

            }


        }


        currentTime = world.getTime() + 1; //Adjust to realtime

        pCalendar.setTicks(currentTime); //Have object replicate real time as it also saves time in world

        if(calendarFiles.getSetting("auto-save") == true) {
            calendarFiles.save();
        }

        if (currentTime == 18000) {


            NewDayEvent e = new NewDayEvent();

            Bukkit.getPluginManager().callEvent(e);

            if (e.isCancelled()) {

            } else {

                gc.add(Calendar.DAY_OF_MONTH, 1);
                pCalendar.setCalendar(gc);
            }


        }

    }


}
