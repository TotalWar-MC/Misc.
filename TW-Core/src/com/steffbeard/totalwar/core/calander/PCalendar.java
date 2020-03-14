package com.steffbeard.totalwar.core.calander;

import org.bukkit.Bukkit;
import org.bukkit.World;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.GregorianCalendar;

@SuppressWarnings("unused")
public class PCalendar  {

    GregorianCalendar gc;
    long ticks;

    public PCalendar(GregorianCalendar gc, long ticks) {
        this.gc = gc;
        this.ticks = ticks;
    }

    public GregorianCalendar getCalendar() {
        return gc;
    }

    public void setCalendar(GregorianCalendar gc) {
        this.gc = gc;
    }

    public long getTicks() {
        return ticks;
    }

    public void setTicks(long ticks) {
        this.ticks = ticks;
        getWorld().setTime(ticks);
    }

    public World getWorld() {
        return Bukkit.getWorld("world");
    }

    public static String get12HrTime(long currentTimeInTicks) {

        int seconds = (int) Math.floor((currentTimeInTicks % 1000) * 60 * 60 / 1000);
        int minutes = (int) Math.floor((currentTimeInTicks % 1000) * 60 / 1000);
        int hours = (int) Math.floor(currentTimeInTicks / 1000 + 6);

        if(hours >= 24){
            hours = hours - 24;
        }

        String time_period;

        if (hours < 12) {
            time_period = "A.M.";

            if (hours == 0) {
                hours = 12;
            }
        } else {
            time_period = "P.M.";

            if (hours == 12) {
                hours = 12;

            } else {
                hours = hours - 12;
            }
        }

        NumberFormat formatter = new DecimalFormat("00");

        return hours + ":" + formatter.format(minutes) + " " + time_period;

    }

    public static long getTicksOf12HrTime(String time){ //format: 12:45 P.M.

        String[] time_array = time.split(":|\\ ");

        int hours = Integer.parseInt(time_array[0]);
        int minutes = Integer.parseInt(time_array[1]);
        String time_period = time_array[2];

        if (time_period.equalsIgnoreCase("P.M.")) {
            if(hours != 12)
            hours = hours + 12;
        } else {
            if(hours == 12){
                hours = 24;
            }
        }


        long conversion = ((hours-6) * 1000) + (minutes*1000/60);

        return conversion;


    }


}
