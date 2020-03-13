package com.steffbeard.totalwar.core.commands;

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.DecimalFormat;
import java.time.Month;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import com.steffbeard.totalwar.core.Main;
import com.steffbeard.totalwar.core.Messages;
import com.steffbeard.totalwar.core.calander.CalendarFiles;

public class CalendarCommand implements CommandExecutor {
	
	private Messages message;
	private Main main;
	private CalendarFiles calendarFiles;
	
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {

        switch (cmd.getName()) {
            case "cal":
            case "calendar":

                Permission calendarAdminPerm = new Permission("core.calendar.admin");
                Permission standardPerm = new Permission("core.calendar");
                Permission listPerm = new Permission("core.calendar.list");

                if (!sender.hasPermission(calendarAdminPerm) && !sender.hasPermission(standardPerm)) {
                    main.sendMessage(sender, message.messagePermission);

                    return false;
                }

                if (args.length == 0) {
                    showCalendar(sender);
                    return true;
                }


                if (args[0].equalsIgnoreCase("admin") || args[0].equalsIgnoreCase("a")) {

                    if (!sender.hasPermission(calendarAdminPerm)) {
                    	main.sendMessage(sender, message.messagePermission);

                        return false;
                    }

                    if (args.length == 1) {
                        showAdminHelp(sender);
                        return true;
                    }

                    if (args.length > 1) {

                        if (args[1].equalsIgnoreCase("timetravel") || args[1].equalsIgnoreCase("tt")) {

                            String calendartttop = dashes.substring(0, dashes.length() - 4) + ChatColor.GOLD + "[" + phIdentity + ChatColor.GOLD + " : "
                                    + ChatColor.GREEN + "Calendar - Time Travel" + ChatColor.GOLD + "]" + dashes;

                            if (args.length == 2) {
                                sender.sendMessage(calendartttop);
                                showTimeTravelHelp(sender);
                                return true;
                            }

                            if (args[2].equalsIgnoreCase("set")) {

                                if (args.length == 3) {
                                    sender.sendMessage(calendartttop);
                                    showTimeTravelHelp(sender);
                                    return true;
                                } else if (args.length > 8) {
                                    sender.sendMessage(phprefix + errorcolor + errormsg + identitycolor + "/cal admin tt set" + errorcolor + " for more help.");
                                    return true;
                                }


                                String month_string;
                                String officialtime;


                                GregorianCalendar gc = getCalendarFromArguments(sender, args);
                                String time = getTimeFromArguments(sender, args);

                                if (gc == null) {
                                    return false;
                                }

                                if (!(time.isEmpty())) {
                                    officialtime = time;

                                } else {
                                    officialtime = pCalendar.get12HrTime(pCalendar.getWorld().getTime());
                                }


                                month_string = getMonthInString(gc);


                                sender.sendMessage(phprefix + successcolor + " We're time traveling to: " + identitycolor + gc.get(Calendar.DAY_OF_MONTH) + " " +
                                        month_string + " " + gc.get(Calendar.YEAR) + successcolor + ". The time is now " + identitycolor + officialtime);

                                pCalendar.setCalendar(gc);
                                pCalendar.setTicks(pCalendar.getTicksOf12HrTime(officialtime));
                                calendarFiles.save();

                                return true;
                            }

                            sender.sendMessage(phprefix + errorcolor + errormsg + identitycolor + "/cal admin tt" + errorcolor + " for more help.");
                            return true;
                        } else if (args[1].equalsIgnoreCase("event") || args[1].equalsIgnoreCase("e")) {

                            String calendaretop = dashes.substring(0, dashes.length() - 4) + ChatColor.GOLD + "[" + phIdentity + ChatColor.GOLD + " : "
                                    + ChatColor.GREEN + "Calendar - Event" + ChatColor.GOLD + "]" + dashes;

                            if (args.length == 2) {
                                sender.sendMessage(calendaretop);
                                showEventHelp(sender);
                                return true;
                            }

                            if (args[2].equalsIgnoreCase("add")) {

                                if (args.length == 3) {
                                    sender.sendMessage(calendaretop);
                                    showEventHelp(sender);
                                    return true;
                                }

                                if (args.length > 8 || args.length < 6) {
                                    sender.sendMessage(phprefix + errorcolor + errormsg + identitycolor + "/cal admin e" + errorcolor + " for more help.");
                                    return true;
                                }


                                if (senderEventTitle.containsKey(sender.getName()) || senderEventDesc.containsKey(sender.getName())) {
                                    sender.sendMessage(phprefix + errorcolor + " You are in the process of making an event. You can cancel with " + identitycolor + "/cal admin e cancel" + errorcolor + ".");
                                    return true;
                                }


                                String month_string;
                                String officialtime;


                                GregorianCalendar gc = getCalendarFromArguments(sender, args);

                                if (gc == null) {
                                    return false;
                                }

                                String time = getTimeFromArguments(sender, args);


                                if (!(time.isEmpty())) {
                                    officialtime = time;

                                } else {
                                    officialtime = "12:00 A.M.";
                                }

                                // success, the arguments are all valid, for program to proceed
                                // what is the event title : /cal admin e title <title>
                                // what is the event desc : / cal admin e desc <desc>


                                sender.sendMessage(phprefix + standardcolor + " Enter the title of the event: " + identitycolor + "/cal admin e title <title>" + standardcolor + ".");
                                sender.sendMessage(standardcolor + " You have 15 seconds to enter an event name. To cancel the event: " + identitycolor + "/cal admin e cancel");


                                PEvent pEvent = new PEvent(getCalendarFromArguments(sender, args), officialtime);
                                senderEventTitle.put(sender.getName(), pEvent);

                                createEventTitleThread(sender, 15);


                                return true;

                            } else if (args[2].equalsIgnoreCase("cancel") || args[2].equalsIgnoreCase("c")) {


                                if (args.length != 3) {
                                    sender.sendMessage(calendaretop);
                                    showEventHelp(sender);
                                    return true;
                                }

                                if (senderEventTitle.containsKey(sender.getName()) || senderEventDesc.containsKey(sender.getName())) {
                                    sender.sendMessage(phprefix + standardcolor + " You have canceled the event creation process.");
                                    senderEventDesc.remove(sender.getName());
                                    senderEventTitle.remove(sender.getName());
                                    return true;
                                } else {
                                    sender.sendMessage(phprefix + standardcolor + " You must create an event in order to cancel one. For more help: " + identitycolor + "/cal admin e" + standardcolor + ".");
                                    return false;
                                }
                            } else if (args[2].equalsIgnoreCase("remove") || args[2].equalsIgnoreCase("r")) {

                                if (args.length == 3) {
                                    sender.sendMessage(calendaretop);
                                    showEventHelp(sender);
                                    return true;
                                }

                                String name = "";

                                for (int i = 3; i < args.length; i++) {

                                    if (i == 3) {
                                        name = args[i];
                                        continue;
                                    }
                                    name += " " + args[i];
                                }

                                if (calendarFiles.isEvent(name)) {
                                    PEvent event = calendarFiles.getEvent(name);
                                    sender.sendMessage(phprefix + successcolor + " You have successfully removed the event: " + identitycolor + event.getName() + standardcolor + ".");
                                    calendarFiles.removeEvent(event.getName());
                                    return true;
                                }


                                sender.sendMessage(phprefix + errorcolor + " There is no event with that name. No event was removed.");
                                return false;

                            } else if (args[2].equalsIgnoreCase("title") || args[2].equalsIgnoreCase("t")) {

                                if (!senderEventTitle.containsKey(sender.getName())) {
                                    sender.sendMessage(phprefix + standardcolor + " You must create an event in order to name one. For more help: " + identitycolor + "/cal admin e" + standardcolor + ".");
                                    return true;
                                }
                                if (args.length == 3) {
                                    sender.sendMessage(calendaretop);
                                    showEventHelp(sender);
                                    return true;
                                }

                                String name = "";

                                for (int i = 3; i < args.length; i++) {

                                    if (i == 3) {
                                        name = args[i];
                                        continue;
                                    }

                                    name += " " + args[i];

                                }

                                if (calendarFiles.isEvent(name)) {
                                    sender.sendMessage(phprefix + errorcolor + " There's already an event with this name.");
                                    return true;
                                }

                                PEvent event = senderEventTitle.get(sender.getName());
                                event.setName(name);

                                senderEventDesc.put(sender.getName(), event);

                                sender.sendMessage(phprefix + standardcolor + " Enter the description of the event: " + identitycolor + "/cal admin e desc <desc>" + standardcolor + ".");
                                sender.sendMessage(standardcolor + " You have 1 minute to enter an event name. To cancel the event: " + identitycolor + "/cal admin e cancel");

                                createEventDescThread(sender, 60);

                                senderEventTitle.remove(sender.getName());

                                return true;

                            } else if (args[2].equalsIgnoreCase("desc") || args[2].equalsIgnoreCase("d") || args[2].equalsIgnoreCase("description")) {

                                if (!senderEventDesc.containsKey(sender.getName())) {
                                    sender.sendMessage(phprefix + standardcolor + " You must create an event title in order to describe one. For more help: " + identitycolor + "/cal admin e" + standardcolor + ".");
                                    return true;
                                }
                                if (args.length == 3) {
                                    sender.sendMessage(calendaretop);
                                    showEventHelp(sender);
                                    return true;
                                }


                                String desc = "";

                                for (int i = 3; i < args.length; i++) {

                                    if (i == 3) {
                                        desc = args[i];
                                        continue;
                                    }

                                    desc += " " + args[i];

                                }

                                PEvent event = senderEventDesc.get(sender.getName());
                                event.setDescription(desc);

                                GregorianCalendar cal = event.getCalendar();
                                String time = event.getTime();

                                sender.sendMessage(phprefix + successcolor + " You have successfully created an event at: ");
                                sender.sendMessage(phprefix + identitycolor + " " + cal.get(Calendar.DAY_OF_MONTH) + " " + getMonthInString(cal) + " " +
                                        cal.get(Calendar.YEAR) + standardcolor + " at " + identitycolor + time + standardcolor);
                                sender.sendMessage(phprefix + standardcolor + " I hope that players attend " + identitycolor + event.getName() + standardcolor + "! Check all events with " + identitycolor + "/cal list" + standardcolor + ".");

                                //save to file
                                calendarFiles.saveEvent(event);
                                senderEventDesc.remove(sender.getName());
                                return true;
                            }

                            sender.sendMessage(phprefix + errorcolor + errormsg + identitycolor + "/cal admin e" + errorcolor + " for more help.");
                            return true;

                        } else if (args[1].equalsIgnoreCase("toggle") || args[1].equalsIgnoreCase("t")) {

                            if (args.length == 2) {
                                showToggleHelp(sender, 1);
                                return true;
                            } else if (args.length <= 4) {
                                if (StringUtils.isNumeric(args[2])) {
                                    int page = Integer.valueOf(args[2]);
                                    showToggleHelp(sender, page);
                                    return true;
                                } else {

                                    boolean value = false;
                                    boolean valueAssigned = false;

                                    if (args.length == 4) {
                                        switch (args[3].toLowerCase()) {
                                            case "t":
                                            case "true":
                                            case "1":
                                                value = true;
                                                valueAssigned = true;
                                            case "f":
                                            case "false":
                                            case "0":
                                                value = false;
                                                valueAssigned = true;
                                            default:
                                                sender.sendMessage(phprefix + standardcolor + "Provide a value of true or false when setting configuration.");
                                                return true;
                                        }
                                    }

                                    switch (args[2].toLowerCase()) {
                                        case "pause":
                                        case "p":
                                            if (!valueAssigned) {
                                                value = !calendarFiles.getSetting("pause");
                                            }
                                            calendarFiles.setSetting("pause", value);
                                            sender.sendMessage(phprefix + standardcolor + " You've set " + identitycolor + "PAUSE" + standardcolor + " to the value of " + identitycolor + value);
                                            break;
                                        case "autosave":
                                        case "auto-save":
                                            if (!valueAssigned) {
                                                value = !calendarFiles.getSetting("auto-save");
                                            }
                                            calendarFiles.setSetting("auto-save", value);
                                            sender.sendMessage(phprefix + standardcolor + " You've set " + identitycolor + "AUTO=SAVE" + standardcolor + " to the value of " + identitycolor + value);
                                            break;
                                        case "autoguess":
                                        case "ag":
                                        case "auto-guess":
                                            if (!valueAssigned) {
                                                value = !calendarFiles.getSetting("auto-guess");
                                            }
                                            //calendarFiles.setSetting("auto-guess", value);
                                            //sender.sendMessage(phprefix + standardcolor + " You've set " + identitycolor + "AUTO-GUESS" + standardcolor + " to the value of " + identitycolor +  value);
                                            sender.sendMessage(phprefix + errorcolor + " Sorry! This configuration setting hasn't been tested enough! It is still under maintenance!");
                                            break;
                                        case "autoshow":
                                        case "auto-show":
                                            if (!valueAssigned) {
                                                value = !calendarFiles.getSetting("auto-show");
                                            }
                                            calendarFiles.setSetting("auto-show", value);
                                            sender.sendMessage(phprefix + standardcolor + " You've set " + identitycolor + "AUTO=SHOW" + standardcolor + " to the value of " + identitycolor + value);
                                            break;

                                        case "newdayalert":
                                        case "dayalert":
                                        case "nda":
                                        case "da":
                                        case "newdayalerts":
                                        case "dayalerts":
                                        case "new-day-alert":
                                            if (!valueAssigned) {
                                                value = !calendarFiles.getSetting("new-day-alert");
                                            }
                                            calendarFiles.setSetting("new-day-alert", value);
                                            sender.sendMessage(phprefix + standardcolor + " You've set " + identitycolor + "NEW-DAY-ALERT" + standardcolor + " to the value of " + identitycolor + value);
                                            break;
                                        case "eventalert":
                                        case "ea":
                                        case "eventalerts":
                                        case "event-alert":
                                            if (!valueAssigned) {
                                                value = !calendarFiles.getSetting("event-alert");
                                            }
                                            calendarFiles.setSetting("event-alert", value);
                                            sender.sendMessage(phprefix + standardcolor + " You've set " + identitycolor + "EVENT-ALERT" + standardcolor + " to the value of " + identitycolor + value);
                                            break;
                                        case "list":
                                        case "l":
                                            sender.sendMessage(ChatColor.DARK_GRAY + "" + ChatColor.STRIKETHROUGH + "" + "------------" + ChatColor.DARK_GRAY + "[" + identitycolor + " Page 1" + ChatColor.DARK_GRAY + " ]" + ChatColor.STRIKETHROUGH + "------------");
                                            sender.sendMessage(String.format("%-22s %s", datesColor + "Setting", identitycolor + "Value"));
                                            sender.sendMessage("");

                                            for (String setting : calendarFiles.getKeySettings()) { //Gather all events

                                                int addition = 0;

                                                if (setting.length() < 20) {
                                                    addition = 20 - setting.length();
                                                }

                                                int width = setting.length() + addition;

                                                sender.sendMessage(String.format("%-" + width + "s %s", ChatColor.BLUE + setting, identitycolor + "" + calendarFiles.getSetting(setting)));

                                            }
                                            break;
                                        default:
                                            sender.sendMessage(phprefix + standardcolor + " The page number or configuration you requested is not valid.");
                                            return true;
                                    }


                                }
                            } else {
                                sender.sendMessage(phprefix + standardcolor + " The page number or configuration you requested is not valid.");
                                return true;
                            }
                        } else {
                            sender.sendMessage(phprefix + errorcolor + errormsg + identitycolor + "/cal admin" + errorcolor + " for more help.");
                            //fail to find paramter. Ie : /calendar admin chicken
                        }

                    }


                    return false;


                } else if (args[0].equalsIgnoreCase("list") || args[0].equalsIgnoreCase("l")) {

                    if (!sender.hasPermission(calendarAdminPerm) && !sender.hasPermission(listPerm)) {
                        sender.sendMessage(phprefix + nopermscolor + " You have no permission " + nopermsparenthesis + "(" + nopermsidentity +
                                listPerm.getName() + nopermsparenthesis + ")" + nopermscolor + " to access this command.");

                        return false;
                    }

                    if (args.length > 2) {
                        sender.sendMessage(phprefix + standardcolor + " Did you mean to execute: " + identitycolor + "/cal list");
                        return true;
                    }


                    if (calendarFiles.getAllEvents().isEmpty()) {
                        sender.sendMessage(phprefix + standardcolor + " There are no events registered in this server.");
                        return true;
                    }

                    int page;

                    //todo check if ur already creating an event and title si already completed

                    if (args.length == 1) {
                        page = 1;
                    } else {
                        if (StringUtils.isNumeric(args[1])) {
                            page = Integer.valueOf(args[1]);
                        } else {
                            sender.sendMessage(phprefix + standardcolor + " The page number you requested is not valid.");
                            return true;
                        }
                    }

                    List<PEvent> events = calendarFiles.getAllEventsFrom((4 * page) - 4, 4 * page);

                    if (events.isEmpty()) {
                        sender.sendMessage(phprefix + standardcolor + " There are no events registered on that page.");
                        return true;
                    }

                    sender.sendMessage(ChatColor.DARK_GRAY + "" + ChatColor.STRIKETHROUGH + "" + "------------" + ChatColor.DARK_GRAY + "[" + identitycolor + " Page " + page + ChatColor.DARK_GRAY + " ]" + ChatColor.STRIKETHROUGH + "------------");
                    sender.sendMessage(String.format("%-22s %s", datesColor + "Date (mm/dd/yy)", identitycolor + "Event Name")); //Header 17
                    sender.sendMessage("");

                    for (PEvent event : events) { //Gather all events

                        DecimalFormat nf3 = new DecimalFormat("#00"); //Format date

                        String name = event.getName(); //event name

                        GregorianCalendar gc = event.getCalendar(); //getting our calendar obj
                        String month = nf3.format(gc.get(Calendar.MONTH) + 1); //getting month
                        int year = gc.get(Calendar.YEAR); //getting year
                        String day = nf3.format(gc.get(Calendar.DAY_OF_MONTH)); //getting day of month

                        String time = event.getTime(); //time
                        String month_in_string = getMonthInString(event.getCalendar()); //month as January


                        String formatted_date = datesColor + month + datesSptr + "-" + datesColor + day + datesSptr + "-" + datesColor + year; //20
                        String full_formatted_date = datesColor + day + " " + month_in_string + " " + year + " | " + time;
                        String formatted_name = identitycolor + name;
                        String formatted_desc = formatted_name + ": " + identitycolor + event.getDescription();

                        new FancyMessage(String.format("%-32s", formatted_date)).tooltip(full_formatted_date).then(formatted_name).tooltip(formatted_desc).send(sender);

                    }
                    sender.sendMessage(phprefix + standardcolor + " Hover over dates and event names for more information.");

                    return true;

                } else if (args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("h")) {

                    if (args.length != 1) {
                        sender.sendMessage(phprefix + standardcolor + " Did you mean to execute: " + identitycolor + "/cal help");
                        return true;
                    } else {
                        showHelp(sender);
                        return true;
                    }

                } else {

                    sender.sendMessage(phprefix + errorcolor + errormsg + identitycolor + "/cal help" + errorcolor + " for more help.");
                }
        }

        return false;
    }

    public void createEventTitleThread(final CommandSender sender, final int expiration) {
        new BukkitRunnable() {
            int i = 0;

            @Override
            public void run() {


                if (!senderEventTitle.containsKey(sender.getName())) {
                    cancel();
                    return;
                }

                if (sender instanceof Player) {
                    if (expiration - i <= 10) {
                        ((Player) sender).playSound(((Player) sender).getLocation(), Sound.BLOCK_NOTE_BASEDRUM, 5, 11 - (expiration - i));
                    }
                }

                if (i == expiration) {
                    senderEventTitle.remove(sender.getName());
                    sender.sendMessage(phprefix + standardcolor + " The time to title the event has expired.");
                    cancel();
                    return;
                } else {
                    i++;
                }

            }
        }.runTaskTimerAsynchronously(plugin, 0, 20); //every sec
    }

    public void createEventDescThread(CommandSender sender, int expiration) {
        new BukkitRunnable() {
            int i = 0;

            @Override
            public void run() {


                if (!senderEventDesc.containsKey(sender.getName())) {
                    cancel();
                    return;
                }

                if (sender instanceof Player) {
                    if (expiration - i <= 10) {
                        ((Player) sender).playSound(((Player) sender).getLocation(), Sound.BLOCK_NOTE_BASEDRUM, 5, 11 - (expiration - i));
                    }
                }

                if (i == expiration) {
                    senderEventDesc.remove(sender.getName());
                    sender.sendMessage(phprefix + standardcolor + " The time to give an event a description has expired.");
                    cancel();
                    return;
                } else {
                    i++;
                }

            }
        }.runTaskTimerAsynchronously(plugin, 0, 20); //every sec
    }

    private String getTimeFromArguments(CommandSender sender, String[] args) {
        String time = "";
        String time_period = "";

        if (args.length > 6) {
            if (args[6].matches("\\d{2}:\\d{2}") || args[6].matches("\\d{1}:\\d{2}")) {
                time = args[6];
            } else {
                sender.sendMessage(phprefix + errorcolor + " The value of time is not formatted as: " + identitycolor + "x:xx" + errorcolor + ".");
                return "";
            }

            if (args[7].equalsIgnoreCase("P.M.") || args[7].equalsIgnoreCase("PM")
                    || args[7].equalsIgnoreCase("A.M.") || args[7].equalsIgnoreCase("AM")) {
                time_period = args[7].toUpperCase();
            } else {
                sender.sendMessage(phprefix + errorcolor + " The value of am/pm is not defined as: " + identitycolor + "A.M. or P.M." + errorcolor + ".");
                return "";
            }

            return time + " " + time_period;

        }

        return "";
    }

    private GregorianCalendar getCalendarFromArguments(CommandSender sender, String[] args) {
        int day;
        int month;
        int year;


        if (StringUtils.isNumeric(args[3])) {
            day = Integer.parseInt(args[3].replace(" ", ""));
        } else {
            sender.sendMessage(phprefix + errorcolor + " The value of the day is not numerical.");
            return null;
        }

        if (StringUtils.isNumeric(args[4])) {
            month = Integer.parseInt(args[4].replace(" ", ""));
        } else {
            sender.sendMessage(phprefix + errorcolor + " The value of the month is not numerical.");
            return null;
        }
        if (StringUtils.isNumeric(args[5].replace(" ", ""))) {
            year = Integer.parseInt(args[5]);
        } else {
            sender.sendMessage(phprefix + errorcolor + " The value of the year is not numerical.");
            return null;
        }

        GregorianCalendar gc = new GregorianCalendar(year, month - 1, day);
        return gc;

    }

    private String getMonthInString(GregorianCalendar gc) {

        String month_string;

        month_string = Month.of(gc.get(Calendar.MONTH) + 1).toString().toLowerCase();
        String firstletter = month_string.substring(0, 1).toUpperCase();
        String rest = month_string.substring(1);
        month_string = firstletter + rest;
        return month_string;
    }

    public void showHelp(CommandSender sender) {
        sender.sendMessage(dashes.substring(0, dashes.length() - 4) + ChatColor.GOLD + "[" + phIdentity + ChatColor.GOLD + " : "
                + ChatColor.GREEN + "Calendar - Help (Page " + 1 + ")" + ChatColor.GOLD + "]" + dashes);

        sender.sendMessage(standardcolor + "To view the calendar, do the following (hover for more info): ");
        new FancyMessage("/calendar")
                .color(adminidentity)
                .tooltip(standardcolor + "The following command shows the date and time.")
                .send(sender);
        sender.sendMessage("");
        sender.sendMessage(standardcolor + "To view admin panel, do the following (hover for more info): ");
        new FancyMessage("/calendar admin ")
                .color(adminidentity)
                .tooltip(standardcolor + "The following command shows the admin panel.")
                .send(sender);
        sender.sendMessage("");
        sender.sendMessage(standardcolor + "To view the event list, do the following (hover for more info): ");
        new FancyMessage("/calendar list")
                .color(adminidentity)
                .tooltip(standardcolor + "The following command shows the list of events.")
                .send(sender);
    }

    public static void showCalendar(CommandSender sender) {

        GregorianCalendar gc = pCalendar.getCalendar();

        String time = pCalendar.get12HrTime(pCalendar.getTicks());

        String month = Month.of(gc.get(Calendar.MONTH) + 1).toString().toLowerCase();
        String firstletter = month.substring(0, 1).toUpperCase();
        String rest = month.substring(1);
        month = firstletter + rest;

        int day = gc.get(Calendar.DAY_OF_MONTH);
        int year = gc.get(Calendar.YEAR);
        String dayName = gc.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());//Locale.US);


        sender.sendMessage(dashescalendar + ChatColor.GOLD + "[" + ChatColor.GRAY + "Calendar" + ChatColor.GOLD
                + "]" + dashescalendar);
        sender.sendMessage(standardcolor + "Date: " + identitycolor + day + " " + month + " " + year);
        sender.sendMessage(standardcolor + "Day: " + identitycolor + dayName);
        sender.sendMessage(standardcolor + "Time: " + identitycolor + time);

    }

    public void showAdminHelp(CommandSender sender) {


        sender.sendMessage(calendartop);
        sender.sendMessage(adminidentity + "/calendar admin event" + adminsptr + " | " + admindesc + "View event help page");
        sender.sendMessage(adminidentity + "/calendar admin timetravel" + adminsptr + " | " + admindesc + "View time-traveling help page");
        sender.sendMessage(adminidentity + "/calendar admin toggle" + adminsptr + " | " + admindesc + "View toggle settings help page");
        sender.sendMessage(calendarbot);


    }

    public void showTimeTravelHelp(CommandSender sender) {
        sender.sendMessage(standardcolor + "To change the date, do the following (hover for example): ");
        new FancyMessage("/calendar admin tt set <day> <month> <year> [optional: time]")
                .color(adminidentity)
                .tooltip(standardcolor + "Example: " + adminidentity + "/cal admin tt set 13 2 1970 5:45 P.M.\n" + standardcolor + "The following example would change the date to " + identitycolor + "13 February 1970 5:45 P.M.")
                .send(sender);
    }

    public void showToggleHelp(CommandSender sender, int page) {

        switch (page) {
            case 1:
                sender.sendMessage(dashes.substring(0, dashes.length() - 4) + ChatColor.GOLD + "[" + phIdentity + ChatColor.GOLD + " : "
                        + ChatColor.GREEN + "Calendar - Toggle (Page " + page + ")" + ChatColor.GOLD + "]" + dashes);
                sender.sendMessage(standardcolor + "To view all the values of the configuration use: /cal admin t list");
                sender.sendMessage("");
                sender.sendMessage(standardcolor + "To pause/resume the calendar, do the following (hover for more info): ");
                new FancyMessage("/calendar admin toggle pause [#optional true/false]")
                        .color(adminidentity)
                        .tooltip(standardcolor + "The following command pauses/resumes time entirely. The sun will not move at all which means days cannot advance, neither will time.")
                        .send(sender);
                sender.sendMessage("");
                sender.sendMessage(standardcolor + "To toggle auto-saving, do the following (hover for more info): ");
                new FancyMessage("/calendar admin toggle autosave [#optional true/false]")
                        .color(adminidentity)
                        .tooltip(standardcolor + "The following command automatically saves the calendar into a file so in case of a crash it is able to boot up properly.\n\nRecommended: TRUE.")
                        .send(sender);
                break;
            case 2:
                sender.sendMessage(dashes.substring(0, dashes.length() - 4) + ChatColor.GOLD + "[" + phIdentity + ChatColor.GOLD + " : "
                        + ChatColor.GREEN + "Calendar - Toggle (Page " + page + ")" + ChatColor.GOLD + "]" + dashes);
                sender.sendMessage(standardcolor + "To toggle auto-guess, do the following (hover for more info): ");
                new FancyMessage("/calendar admin toggle autoguess [#optional true/false]")
                        .color(adminidentity)
                        .tooltip(standardcolor + "The following command automatically accurately guesses the the calendar's date while the server is offline. \nFor instance, if the server is to be offline for a day. Would you like the calendar to remain the same date/time before it wnet offline or \nto accurately estimate the date/time it missed on.")
                        .send(sender);
                sender.sendMessage("");
                sender.sendMessage(standardcolor + "To toggle new day alerts, do the following (hover for more info): ");
                new FancyMessage("/calendar admin toggle newDayAlerts [#optional true/false]")
                        .color(adminidentity)
                        .tooltip(standardcolor + "The following command toggles the lovely note block tune that plays at midnight, the start of a new day.")
                        .send(sender);
                sender.sendMessage("");
                sender.sendMessage(standardcolor + "To toggle event alerts, do the following (hover for more info): ");
                new FancyMessage("/calendar admin toggle eventAlerts [#optional true/false]")
                        .color(adminidentity)
                        .tooltip(standardcolor + "The following command toggles the melodic note block tune that plays at the time of an event.")
                        .send(sender);
                break;
            case 3:
                sender.sendMessage(dashes.substring(0, dashes.length() - 4) + ChatColor.GOLD + "[" + phIdentity + ChatColor.GOLD + " : "
                        + ChatColor.GREEN + "Calendar - Toggle (Page " + page + ")" + ChatColor.GOLD + "]" + dashes);
                sender.sendMessage(standardcolor + "To toggle auto-show, do the following (hover for more info): ");
                new FancyMessage("/calendar admin toggle autoshow [#optional true/false]")
                        .color(adminidentity)
                        .tooltip(standardcolor + "The following command decides whether the calendar should automatically display to the user as a join message.")
                        .send(sender);
                break;
            default:
                sender.sendMessage(phprefix + standardcolor + " There are no toggle options registered in this page.");
                break;
        }

    }

    public void showEventHelp(CommandSender sender) {

        sender.sendMessage(standardcolor + "To add an event, do the following (hover for example): ");
        new FancyMessage("/cal admin e add <day> <month> <year> [optional: time]")
                .color(adminidentity)
                .tooltip(standardcolor + "Example: " + adminidentity + "/cal admin e add 13 2 1970 5:45 P.M.\n" + standardcolor + "The following example would add an event in " + identitycolor + "13 February 1970 5:45 P.M.")
                .send(sender);
        sender.sendMessage("");
        sender.sendMessage(standardcolor + "To remove an event, do the following (hover for example): ");
        new FancyMessage("/cal admin e remove <title-of-event>")
                .color(adminidentity)
                .tooltip(standardcolor + "Example: " + adminidentity + "/cal admin e remove myTitle\n" + standardcolor + "The following example would remove the event with the name of" + identitycolor + " \"myTitle\"")
                .send(sender);

    }
}
