package com.steffbeard.totalwar.core.calander;

import java.util.GregorianCalendar;

public class PEvent {

    GregorianCalendar calendar;
    String time;
    String name;
    String description;

    public PEvent(GregorianCalendar calendar, String time){
    this.calendar = calendar;
    this.time = time;
    }

    public PEvent(GregorianCalendar calendar, String time, String name, String description){
        this.calendar = calendar;
        this.time = time;
        this.name = name;
        this.description = description;

    }

    public GregorianCalendar getCalendar() {
        return calendar;
    }

    public void setCalendar(GregorianCalendar calendar) {
        this.calendar = calendar;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


}
