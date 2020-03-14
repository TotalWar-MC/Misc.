package com.steffbeard.totalwar.core.calander;

import org.bukkit.configuration.file.YamlConfiguration;

import com.steffbeard.totalwar.core.Main;

import java.io.*;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

@SuppressWarnings("unused")
public class CalendarFiles {
	
	private Main plugin;
	private PCalendar pCalendar;
    public static File calendar_folder, calendar_file, events_file,
            calendar_audio_folder, newDayAlert, eventAlert, calendar_settings_file;

    public CalendarFiles() {

        File dataFolder = plugin.getDataFolder();
        dataFolder.mkdir();

        calendar_folder = new File(plugin.getDataFolder() + "/calendar");
        calendar_audio_folder = new File(plugin.getDataFolder() + "/calendar/audio");

        calendar_file = new File(calendar_folder, "calendar.yml");
        events_file = new File(calendar_folder, "events.yml");

        newDayAlert = new File(calendar_audio_folder, "newDayAlert.nbs");
        eventAlert = new File(calendar_audio_folder, "eventAlert.nbs");

        calendar_settings_file = new File(calendar_folder, "settings.yml");

        buildCalendar();

    }

    public GregorianCalendar getCalendar() {
        if (!calendar_folder.exists() || !calendar_file.exists()) {

            return null;
        }

        YamlConfiguration calendar_yml = YamlConfiguration.loadConfiguration(calendar_file);

        long calendarInMillis = Long.valueOf(calendar_yml.get("CalendarTimeInMillis").toString());

        GregorianCalendar gc = new GregorianCalendar();
        gc.setTimeInMillis(calendarInMillis);

        return gc;

    }

    public long getTicks() {
        if (!calendar_folder.exists() || !calendar_file.exists()) {

            return 0; //reset day
        }

        YamlConfiguration calendar_yml = YamlConfiguration.loadConfiguration(calendar_file);

        long ticks = Long.valueOf(calendar_yml.get("TicksInADay").toString());

        return ticks;

    }

    public boolean getSetting(String setting) {

        if (!calendar_folder.exists() || !calendar_settings_file.exists()) {
            return false;
        }

        YamlConfiguration settings_yml = YamlConfiguration.loadConfiguration(calendar_settings_file);

        switch (setting.toLowerCase()) {
            case "auto-guess":
                return settings_yml.getBoolean("auto-guess");
            case "auto-save":
                return settings_yml.getBoolean("auto-save");
            case "new-day-alert":
                return settings_yml.getBoolean("new-day-alert");
            case "event-alert":
                return settings_yml.getBoolean("event-alert");
            case "pause":
                return settings_yml.getBoolean("pause");
            case "auto-show":
                return settings_yml.getBoolean("auto-show");
            default:
                return false;
        }
    }

    public Set<String> getKeySettings(){
        if (!calendar_folder.exists() || !calendar_settings_file.exists()) {
            return null;
        }

        YamlConfiguration settings_yml = YamlConfiguration.loadConfiguration(calendar_settings_file);

        return settings_yml.getKeys(false);
    }

    public void setSetting(String setting, boolean value) {

        if (!calendar_folder.exists() || !calendar_settings_file.exists()) {
            return;
        }

        YamlConfiguration settings_yml = YamlConfiguration.loadConfiguration(calendar_settings_file);

        switch (setting.toLowerCase()) {
            case "auto-guess":
                settings_yml.set("auto-guess", value);
                break;
            case "auto-save":
                settings_yml.set("auto-save", value);
                break;
            case "new-day-alert":
                settings_yml.set("new-day-alert", value);
                break;
            case "event-alert":
                settings_yml.set("event-alert", value);
                break;
            case "pause":
                settings_yml.set("pause", value);
                break;
            case "auto-show":
                settings_yml.set("auto-show", value);
            default:
                return;
        }

        try{
            settings_yml.save(calendar_settings_file);
        } catch (IOException e){
            e.printStackTrace();
        }
    }


    public boolean save() {
        if (!calendar_folder.exists() || !calendar_file.exists()) {
            return false;
        }

        YamlConfiguration calendar_yml = YamlConfiguration.loadConfiguration(calendar_file);

        calendar_yml.set("CalendarTimeInMillis", pCalendar.getCalendar().getTimeInMillis());
        calendar_yml.set("TicksInADay", pCalendar.getWorld().getTime());

        try {
            calendar_yml.save(calendar_file);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean saveEvent(PEvent event) {

        if (!calendar_folder.exists() || !events_file.exists()) {
            return false;
        }

        YamlConfiguration events_yml = YamlConfiguration.loadConfiguration(events_file);
        String name = event.getName();


        events_yml.createSection(name);

        events_yml.createSection(name + "." + "Description");
        events_yml.createSection(name + "." + "CalendarTimeInMillis");
        events_yml.createSection(name + "." + "Time");

        events_yml.set(name + "." + "Description", event.getDescription());
        events_yml.set(name + "." + "CalendarTimeInMillis", event.getCalendar().getTimeInMillis());
        events_yml.set(name + "." + "Time", event.getTime());

        try {
            events_yml.save(events_file);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void saveResource(String resourcePath, String name, File out_to_folder, boolean replace) {
        if (resourcePath != null && !resourcePath.equals("")) {
            resourcePath = resourcePath.replace('\\', '/');
            InputStream in = plugin.getResource(resourcePath);
            if (in == null) {
                throw new IllegalArgumentException("The embedded resource '" + resourcePath + "' cannot be found in " + plugin.getName());
            } else {
                File outFile = new File(out_to_folder, name);
				int lastIndex = resourcePath.lastIndexOf(47);

                try {
                    if (outFile.exists() && !replace) {
                        plugin.getLogger().log(Level.WARNING, "Could not save " + outFile.getName() + " to " + outFile + " because " + outFile.getName() + " already exists.");
                    } else {

                        if (outFile.exists() && replace) {
                            outFile.delete();
                        }

                        OutputStream out = new FileOutputStream(outFile);
                        byte[] buf = new byte[4096];

                        int len;
                        while ((len = in.read(buf)) > 0) {
                            out.write(buf, 0, len);
                        }

                        out.close();
                        in.close();
                    }
                } catch (IOException var10) {
                    plugin.getLogger().log(Level.SEVERE, "Could not save " + outFile.getName() + " to " + outFile, var10);
                }

            }
        } else {
            throw new IllegalArgumentException("ResourcePath cannot be null or empty");
        }
    }

    public PEvent getEvent(String name) {
        if (!calendar_folder.exists() || !events_file.exists()) {
            return null;
        }

        YamlConfiguration events_yml = YamlConfiguration.loadConfiguration(events_file);

        Set<String> set = events_yml.getKeys(false);


        for (String id : set) {

            if (name.equalsIgnoreCase(id)) {


                String description = events_yml.get(id + "." + "Description").toString();
                String calendarTime = events_yml.get(id + "." + "CalendarTimeInMillis").toString();
                String time = events_yml.get(id + "." + "Time").toString();

                GregorianCalendar gc = new GregorianCalendar();
                gc.setTimeInMillis(Long.valueOf(calendarTime));

                PEvent event = new PEvent(gc, time, id, description);

                return event;
            }

        }

        return null;
    }

    public boolean isEvent(String name) {
        if (!calendar_folder.exists() || !events_file.exists()) {
            return false;
        }

        YamlConfiguration events_yml = YamlConfiguration.loadConfiguration(events_file);

        Set<String> set = events_yml.getKeys(false);


        for (String id : set) {

            if (name.equalsIgnoreCase(id)) {


                String description = events_yml.get(id + "." + "Description").toString();
                String calendarTime = events_yml.get(id + "." + "CalendarTimeInMillis").toString();
                String time = events_yml.get(id + "." + "Time").toString();

                GregorianCalendar gc = new GregorianCalendar();
                gc.setTimeInMillis(Long.valueOf(calendarTime));

                PEvent event = new PEvent(gc, time, id, description);

                return true;
            }

        }

        return false;
    }

    public boolean removeEvent(String name) {
        if (!calendar_folder.exists() || !events_file.exists()) {
            return false;
        }

        YamlConfiguration events_yml = YamlConfiguration.loadConfiguration(events_file);

        Set<String> set = events_yml.getKeys(false);

        for (String id : set) {

            if (name.equalsIgnoreCase(id)) {

                events_yml.set(id, null);
                try {
                    events_yml.save(events_file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return true;
            }
        }
        return false;


    }

    public List<PEvent> getAllEvents() {
        if (!calendar_folder.exists() || !events_file.exists()) {
            return null;
        }

        YamlConfiguration events_yml = YamlConfiguration.loadConfiguration(events_file);

        Set<String> set = events_yml.getKeys(false);


        List<PEvent> list = new ArrayList<>();

        for (String id : set) {

            PEvent e = getEvent(id);
            list.add(e);
        }


        return list;
    }

    public List<PEvent> getAllEventsFrom(int beginIndex, int endIndex) {

        List<PEvent> list = getAllEvents();


        if (list.isEmpty()) {
            return list;
        }

        List<PEvent> newlist = new ArrayList<>();

        for (int i = beginIndex; i < endIndex; i++) {

            if (i + 1 > list.size()) {
                continue;
            }

            PEvent element = list.get(i);
            newlist.add(element);
        }

        return newlist;

    }

    public void buildCalendar() {
        calendar_folder.mkdir();
        calendar_audio_folder.mkdir();

        if (!calendar_file.exists()) {
            try {
                calendar_file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

            YamlConfiguration calendar_yml = YamlConfiguration.loadConfiguration(calendar_file);
            GregorianCalendar gc = new GregorianCalendar(1300, 6, 5); //0 is january
            calendar_yml.createSection("CalendarTimeInMillis");
            calendar_yml.createSection("TicksInADay");

            try {
                calendar_yml.save(calendar_file);
            } catch (Exception e) {
                e.printStackTrace();
            }

            calendar_yml.set("CalendarTimeInMillis", gc.getTimeInMillis());
            calendar_yml.set("TicksInADay", 12000);


            try {
                calendar_yml.save(calendar_file);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (!events_file.exists()) {
            try {
                events_file.createNewFile();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (!newDayAlert.exists()) {
            saveResource("calendar\\newDayAlert.nbs", "newDayAlert.nbs", calendar_audio_folder, false);
        }

        if (!eventAlert.exists()) {
            saveResource("calendar\\eventAlert.nbs", "eventAlert.nbs", calendar_audio_folder, false);
        }

        if (!calendar_settings_file.exists()) {
            saveResource("calendar\\settings.yml", "settings.yml", calendar_folder, false);
        }


    }

}
