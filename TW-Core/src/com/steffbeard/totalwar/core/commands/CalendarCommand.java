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
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import com.steffbeard.totalwar.core.Main;
import com.steffbeard.totalwar.core.Messages;
import com.steffbeard.totalwar.core.calander.CalendarFiles;
import com.steffbeard.totalwar.core.calander.PCalendar;
import com.steffbeard.totalwar.core.calander.PEvent;
import com.steffbeard.totalwar.core.utils.fanciful.FancyMessage;

public class CalendarCommand implements CommandExecutor {
	
	private Messages message;
	private Main plugin;
	private CalendarFiles calendarFiles;
	private static PCalendar pCalendar;
	
	 public final static String dashes = ChatColor.DARK_RED + "" + ChatColor.STRIKETHROUGH + "" + "-----------";
	 public final static String calendarbot = ChatColor.DARK_RED + "" + ChatColor.STRIKETHROUGH + "" + "----------------------------------------";
	 public final static String dashescalendar = ChatColor.YELLOW + "" + ChatColor.STRIKETHROUGH + "" + "------";
	 public final static String calendartop = dashes + ChatColor.GOLD + "[" + "TW" + ChatColor.GOLD + " : " + ChatColor.GREEN + "Calendar" + ChatColor.GOLD + "]" + dashes;
	 public final static String errormsg = " There was an error processing your command. Use: ";
	 public final static HashMap<String, PEvent> senderEventTitle = new HashMap<>();
	 public final static HashMap<String, PEvent> senderEventDesc = new HashMap<>();
	
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {

        switch (cmd.getName()) {
            case "cal":
            case "calendar":

                Permission calendarAdminPerm = new Permission("core.calendar.admin");
                Permission standardPerm = new Permission("core.calendar");
                Permission listPerm = new Permission("core.calendar.list");

                if (!sender.hasPermission(calendarAdminPerm) && !sender.hasPermission(standardPerm)) {
                    plugin.sendMessage(sender, message.messagePermission);

                    return false;
                }

                if (args.length == 0) {
                    showCalendar(sender);
                    return true;
                }


                if (args[0].equalsIgnoreCase("admin") || args[0].equalsIgnoreCase("a")) {

                    if (!sender.hasPermission(calendarAdminPerm)) {
                    	plugin.sendMessage(sender, message.messagePermission);

                        return false;
                    }

                    if (args.length == 1) {
                        showAdminHelp(sender);
                        return true;
                    }

                    if (args.length > 1) {

                        if (args[1].equalsIgnoreCase("timetravel") || args[1].equalsIgnoreCase("tt")) {

                            String calendartttop = dashes.substring(0, dashes.length() - 4) + ChatColor.GOLD + "[" +  ChatColor.GOLD + " : "
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
                                    sender.sendMessage("/cal admin tt set for more help.");
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
                                    officialtime = PCalendar.get12HrTime(pCalendar.getWorld().getTime());
                                }


                                month_string = getMonthInString(gc);


                                sender.sendMessage("We're time traveling to: " + gc.get(Calendar.DAY_OF_MONTH) + " " +
                                        month_string + " " + gc.get(Calendar.YEAR) + ChatColor.GREEN + ". The time is now " + ChatColor.GOLD + officialtime);

                                pCalendar.setCalendar(gc);
                                pCalendar.setTicks(PCalendar.getTicksOf12HrTime(officialtime));
                                calendarFiles.save();

                                return true;
                            }

                            sender.sendMessage("/cal admin tt for more help.");
                            return true;
                        } else if (args[1].equalsIgnoreCase("event") || args[1].equalsIgnoreCase("e")) {

                            String calendaretop = dashes.substring(0, dashes.length() - 4) + ChatColor.GOLD + "[" +  ChatColor.GOLD + " : "
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
                                    sender.sendMessage("/cal admin e for more help.");
                                    return true;
                                }


                                if (senderEventTitle.containsKey(sender.getName()) || senderEventDesc.containsKey(sender.getName())) {
                                    sender.sendMessage("You are in the process of making an event. You can cancel with " + ChatColor.RED + "/cal admin e cancel");
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


                                sender.sendMessage("Enter the title of the event: /cal admin e title <title>");
                                sender.sendMessage("You have 15 seconds to enter an event name. To cancel the event: /cal admin e cancel");


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
                                    sender.sendMessage(ChatColor.RESET + " You have canceled the event creation process.");
                                    senderEventDesc.remove(sender.getName());
                                    senderEventTitle.remove(sender.getName());
                                    return true;
                                } else {
                                    sender.sendMessage("You must create an event in order to cancel one. For more help: /cal admin e");
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
                                    sender.sendMessage(ChatColor.DARK_GREEN + "You have successfully removed the event: " + ChatColor.GOLD + event.getName());
                                    calendarFiles.removeEvent(event.getName());
                                    return true;
                                }


                                sender.sendMessage(ChatColor.RED + "There is no event with that name. No event was removed.");
                                return false;

                            } else if (args[2].equalsIgnoreCase("title") || args[2].equalsIgnoreCase("t")) {

                                if (!senderEventTitle.containsKey(sender.getName())) {
                                    sender.sendMessage("You must create an event in order to name one. For more help: /cal admin e");
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
                                    sender.sendMessage(ChatColor.DARK_RED + "There's already an event with this name.");
                                    return true;
                                }

                                PEvent event = senderEventTitle.get(sender.getName());
                                event.setName(name);

                                senderEventDesc.put(sender.getName(), event);

                                sender.sendMessage("Enter the description of the event: /cal admin e desc <desc>");
                                sender.sendMessage("You have 1 minute to enter an event name. To cancel the event: /cal admin e cancel");

                                createEventDescThread(sender, 60);

                                senderEventTitle.remove(sender.getName());

                                return true;

                            } else if (args[2].equalsIgnoreCase("desc") || args[2].equalsIgnoreCase("d") || args[2].equalsIgnoreCase("description")) {

                                if (!senderEventDesc.containsKey(sender.getName())) {
                                    sender.sendMessage("You must create an event title in order to describe one. For more help: /cal admin e");
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

                                sender.sendMessage("You have successfully created an event at: ");
                                sender.sendMessage(" " + cal.get(Calendar.DAY_OF_MONTH) + " " + getMonthInString(cal) + " " +
                                        cal.get(Calendar.YEAR) + " at " + time);
                                sender.sendMessage("Check all events with /cal list");

                                //save to file
                                calendarFiles.saveEvent(event);
                                senderEventDesc.remove(sender.getName());
                                return true;
                            }

                            sender.sendMessage("/cal admin e for more help.");
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
                                                sender.sendMessage("Provide a value of true or false when setting configuration.");
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
                                            sender.sendMessage(ChatColor.RESET + "You've set " + ChatColor.GOLD + "PAUSE" + ChatColor.RESET + " to the value of " + ChatColor.GOLD + value);
                                            break;
                                        case "autosave":
                                        case "auto-save":
                                            if (!valueAssigned) {
                                                value = !calendarFiles.getSetting("auto-save");
                                            }
                                            calendarFiles.setSetting("auto-save", value);
                                            sender.sendMessage(ChatColor.RESET + "You've set " + ChatColor.GOLD + "AUTO=SAVE" + ChatColor.RESET + " to the value of " + ChatColor.GOLD + value);
                                            break;
                                        case "autoguess":
                                        case "ag":
                                        case "auto-guess":
                                            if (!valueAssigned) {
                                                value = !calendarFiles.getSetting("auto-guess");
                                            }
                                            //calendarFiles.setSetting("auto-guess", value);
                                            //sender.sendMessage(ChatColor.RESET + " You've set " + ChatColor.GOLD + "AUTO-GUESS" + ChatColor.RESET + " to the value of " + ChatColor.GOLD +  value);
                                            sender.sendMessage(ChatColor.YELLOW + "Sorry! This configuration setting hasn't been tested enough! It is still under maintenance!");
                                            break;
                                        case "autoshow":
                                        case "auto-show":
                                            if (!valueAssigned) {
                                                value = !calendarFiles.getSetting("auto-show");
                                            }
                                            calendarFiles.setSetting("auto-show", value);
                                            sender.sendMessage(ChatColor.RESET + " You've set " + ChatColor.GOLD + "AUTO=SHOW" + ChatColor.RESET + " to the value of " + ChatColor.GOLD + value);
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
                                            sender.sendMessage(ChatColor.RESET + " You've set " + ChatColor.GOLD + "NEW-DAY-ALERT" + ChatColor.RESET + " to the value of " + ChatColor.GOLD + value);
                                            break;
                                        case "eventalert":
                                        case "ea":
                                        case "eventalerts":
                                        case "event-alert":
                                            if (!valueAssigned) {
                                                value = !calendarFiles.getSetting("event-alert");
                                            }
                                            calendarFiles.setSetting("event-alert", value);
                                            sender.sendMessage(ChatColor.RESET + " You've set " + ChatColor.GOLD + "EVENT-ALERT" + ChatColor.RESET + " to the value of " + ChatColor.GOLD + value);
                                            break;
                                        case "list":
                                        case "l":
                                            sender.sendMessage(ChatColor.DARK_GRAY + "" + ChatColor.STRIKETHROUGH + "" + "------------" + ChatColor.DARK_GRAY + "[" + ChatColor.GOLD + " Page 1" + ChatColor.DARK_GRAY + " ]" + ChatColor.STRIKETHROUGH + "------------");
                                            sender.sendMessage(String.format("%-22s %s", ChatColor.AQUA + "Setting", ChatColor.GOLD + "Value"));
                                            sender.sendMessage("");

                                            for (String setting : calendarFiles.getKeySettings()) { //Gather all events

                                                int addition = 0;

                                                if (setting.length() < 20) {
                                                    addition = 20 - setting.length();
                                                }

                                                int width = setting.length() + addition;

                                                sender.sendMessage(String.format("%-" + width + "s %s", ChatColor.BLUE + setting, ChatColor.GOLD + "" + calendarFiles.getSetting(setting)));

                                            }
                                            break;
                                        default:
                                            sender.sendMessage(ChatColor.RESET + " The page number or configuration you requested is not valid.");
                                            return true;
                                    }


                                }
                            } else {
                                sender.sendMessage(ChatColor.RESET + " The page number or configuration you requested is not valid.");
                                return true;
                            }
                        } else {
                            sender.sendMessage(ChatColor.DARK_RED + errormsg + ChatColor.GOLD + "/cal admin" + ChatColor.DARK_RED + " for more help.");
                            //fail to find paramter. Ie : /calendar admin chicken
                        }

                    }


                    return false;


                } else if (args[0].equalsIgnoreCase("list") || args[0].equalsIgnoreCase("l")) {

                    if (!sender.hasPermission(calendarAdminPerm) && !sender.hasPermission(listPerm)) {
                        plugin.sendMessage(sender, message.messagePermission);                   

                        return false;
                    }

                    if (args.length > 2) {
                        sender.sendMessage(ChatColor.RESET + " Did you mean to execute: " + ChatColor.GOLD + "/cal list");
                        return true;
                    }


                    if (calendarFiles.getAllEvents().isEmpty()) {
                        sender.sendMessage(ChatColor.RESET + " There are no events registered in this server.");
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
                            sender.sendMessage(ChatColor.RESET + " The page number you requested is not valid.");
                            return true;
                        }
                    }

                    List<PEvent> events = calendarFiles.getAllEventsFrom((4 * page) - 4, 4 * page);

                    if (events.isEmpty()) {
                        sender.sendMessage(ChatColor.RESET + " There are no events registered on that page.");
                        return true;
                    }

                    sender.sendMessage(ChatColor.DARK_GRAY + "" + ChatColor.STRIKETHROUGH + "" + "------------" + ChatColor.DARK_GRAY + "[" + ChatColor.GOLD + " Page " + page + ChatColor.DARK_GRAY + " ]" + ChatColor.STRIKETHROUGH + "------------");
                    sender.sendMessage(String.format("%-22s %s", ChatColor.AQUA + "Date (mm/dd/yy)", ChatColor.GOLD + "Event Name")); //Header 17
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


                        String formatted_date = ChatColor.AQUA + month + ChatColor.DARK_GRAY + "-" + ChatColor.AQUA + day + ChatColor.DARK_GRAY + "-" + ChatColor.AQUA + year; //20
                        String full_formatted_date = ChatColor.AQUA + day + " " + month_in_string + " " + year + " | " + time;
                        String formatted_name = ChatColor.GOLD + name;
                        String formatted_desc = formatted_name + ": " + ChatColor.GOLD + event.getDescription();

                        new FancyMessage(String.format("%-32s", formatted_date)).tooltip(full_formatted_date).then(formatted_name).tooltip(formatted_desc).send(sender);

                    }
                    sender.sendMessage(ChatColor.RESET + " Hover over dates and event names for more information.");

                    return true;

                } else if (args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("h")) {

                    if (args.length != 1) {
                        sender.sendMessage(ChatColor.RESET + " Did you mean to execute: " + ChatColor.GOLD + "/cal help");
                        return true;
                    } else {
                        showHelp(sender);
                        return true;
                    }

                } else {

                    sender.sendMessage(ChatColor.DARK_RED + errormsg + ChatColor.GOLD + "/cal help" + ChatColor.DARK_RED + " for more help.");
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
                    sender.sendMessage(ChatColor.RESET + " The time to title the event has expired.");
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
                    sender.sendMessage(ChatColor.RESET + " The time to give an event a description has expired.");
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
                sender.sendMessage(ChatColor.DARK_RED + " The value of time is not formatted as: " + ChatColor.GOLD + "x:xx" + ChatColor.DARK_RED + ".");
                return "";
            }

            if (args[7].equalsIgnoreCase("P.M.") || args[7].equalsIgnoreCase("PM")
                    || args[7].equalsIgnoreCase("A.M.") || args[7].equalsIgnoreCase("AM")) {
                time_period = args[7].toUpperCase();
            } else {
                sender.sendMessage(ChatColor.DARK_RED + " The value of am/pm is not defined as: " + ChatColor.GOLD + "A.M. or P.M." + ChatColor.DARK_RED + ".");
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
            sender.sendMessage(ChatColor.DARK_RED + " The value of the day is not numerical.");
            return null;
        }

        if (StringUtils.isNumeric(args[4])) {
            month = Integer.parseInt(args[4].replace(" ", ""));
        } else {
            sender.sendMessage(ChatColor.DARK_RED + " The value of the month is not numerical.");
            return null;
        }
        if (StringUtils.isNumeric(args[5].replace(" ", ""))) {
            year = Integer.parseInt(args[5]);
        } else {
            sender.sendMessage(ChatColor.DARK_RED + " The value of the year is not numerical.");
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
        sender.sendMessage(dashes.substring(0, dashes.length() - 4) + ChatColor.GOLD + "[" +  ChatColor.GOLD + " : "
                + ChatColor.GREEN + "Calendar - Help (Page " + 1 + ")" + ChatColor.GOLD + "]" + dashes);

        sender.sendMessage(ChatColor.RESET + "To view the calendar, do the following (hover for more info): ");
        new FancyMessage("/calendar")
                .color(ChatColor.YELLOW)
                .tooltip(ChatColor.RESET + "The following command shows the date and time.")
                .send(sender);
        sender.sendMessage("");
        sender.sendMessage(ChatColor.RESET + "To view admin panel, do the following (hover for more info): ");
        new FancyMessage("/calendar admin ")
                .color(ChatColor.YELLOW)
                .tooltip(ChatColor.RESET + "The following command shows the admin panel.")
                .send(sender);
        sender.sendMessage("");
        sender.sendMessage(ChatColor.RESET + "To view the event list, do the following (hover for more info): ");
        new FancyMessage("/calendar list")
                .color(ChatColor.YELLOW)
                .tooltip(ChatColor.RESET + "The following command shows the list of events.")
                .send(sender);
    }

    public static void showCalendar(CommandSender sender) {

        GregorianCalendar gc = pCalendar.getCalendar();

        String time = PCalendar.get12HrTime(pCalendar.getTicks());

        String month = Month.of(gc.get(Calendar.MONTH) + 1).toString().toLowerCase();
        String firstletter = month.substring(0, 1).toUpperCase();
        String rest = month.substring(1);
        month = firstletter + rest;

        int day = gc.get(Calendar.DAY_OF_MONTH);
        int year = gc.get(Calendar.YEAR);
        String dayName = gc.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());//Locale.US);


        sender.sendMessage(dashescalendar + ChatColor.GOLD + "[" + ChatColor.GRAY + "Calendar" + ChatColor.GOLD
                + "]" + dashescalendar);
        sender.sendMessage(ChatColor.RESET + "Date: " + ChatColor.GOLD + day + " " + month + " " + year);
        sender.sendMessage(ChatColor.RESET + "Day: " + ChatColor.GOLD + dayName);
        sender.sendMessage(ChatColor.RESET + "Time: " + ChatColor.GOLD + time);

    }

    public void showAdminHelp(CommandSender sender) {


        sender.sendMessage(calendartop);
        sender.sendMessage("/calendar admin event" + " | " + "View event help page");
        sender.sendMessage("/calendar admin timetravel" + " | " + "View time-traveling help page");
        sender.sendMessage("/calendar admin toggle" + " | " + "View toggle settings help page");
        sender.sendMessage(calendarbot);


    }

    public void showTimeTravelHelp(CommandSender sender) {
        sender.sendMessage(ChatColor.RESET + "To change the date, do the following (hover for example): ");
        new FancyMessage("/calendar admin tt set <day> <month> <year> [optional: time]")
                .color(ChatColor.YELLOW)
                .tooltip(ChatColor.RESET + "Example: " + ChatColor.YELLOW + "/cal admin tt set 13 2 1970 5:45 P.M.\n" + ChatColor.RESET + "The following example would change the date to " + ChatColor.GOLD + "13 February 1970 5:45 P.M.")
                .send(sender);
    }

    public void showToggleHelp(CommandSender sender, int page) {

        switch (page) {
            case 1:
                sender.sendMessage(dashes.substring(0, dashes.length() - 4) + ChatColor.GOLD + "[" +  ChatColor.GOLD + " : "
                        + ChatColor.GREEN + "Calendar - Toggle (Page " + page + ")" + ChatColor.GOLD + "]" + dashes);
                sender.sendMessage(ChatColor.RESET + "To view all the values of the configuration use: /cal admin t list");
                sender.sendMessage("");
                sender.sendMessage(ChatColor.RESET + "To pause/resume the calendar, do the following (hover for more info): ");
                new FancyMessage("/calendar admin toggle pause [#optional true/false]")
                        .color(ChatColor.YELLOW)
                        .tooltip(ChatColor.RESET + "The following command pauses/resumes time entirely. The sun will not move at all which means days cannot advance, neither will time.")
                        .send(sender);
                sender.sendMessage("");
                sender.sendMessage(ChatColor.RESET + "To toggle auto-saving, do the following (hover for more info): ");
                new FancyMessage("/calendar admin toggle autosave [#optional true/false]")
                        .color(ChatColor.YELLOW)
                        .tooltip(ChatColor.RESET + "The following command automatically saves the calendar into a file so in case of a crash it is able to boot up properly.\n\nRecommended: TRUE.")
                        .send(sender);
                break;
            case 2:
                sender.sendMessage(dashes.substring(0, dashes.length() - 4) + ChatColor.GOLD + "[" +  ChatColor.GOLD + " : "
                        + ChatColor.GREEN + "Calendar - Toggle (Page " + page + ")" + ChatColor.GOLD + "]" + dashes);
                sender.sendMessage(ChatColor.RESET + "To toggle auto-guess, do the following (hover for more info): ");
                new FancyMessage("/calendar admin toggle autoguess [#optional true/false]")
                        .color(ChatColor.YELLOW)
                        .tooltip(ChatColor.RESET + "The following command automatically accurately guesses the the calendar's date while the server is offline. \nFor instance, if the server is to be offline for a day. Would you like the calendar to remain the same date/time before it wnet offline or \nto accurately estimate the date/time it missed on.")
                        .send(sender);
                sender.sendMessage("");
                sender.sendMessage(ChatColor.RESET + "To toggle new day alerts, do the following (hover for more info): ");
                new FancyMessage("/calendar admin toggle newDayAlerts [#optional true/false]")
                        .color(ChatColor.YELLOW)
                        .tooltip(ChatColor.RESET + "The following command toggles the lovely note block tune that plays at midnight, the start of a new day.")
                        .send(sender);
                sender.sendMessage("");
                sender.sendMessage(ChatColor.RESET + "To toggle event alerts, do the following (hover for more info): ");
                new FancyMessage("/calendar admin toggle eventAlerts [#optional true/false]")
                        .color(ChatColor.YELLOW)
                        .tooltip(ChatColor.RESET + "The following command toggles the melodic note block tune that plays at the time of an event.")
                        .send(sender);
                break;
            case 3:
                sender.sendMessage(dashes.substring(0, dashes.length() - 4) + ChatColor.GOLD + "[" +  ChatColor.GOLD + " : "
                        + ChatColor.GREEN + "Calendar - Toggle (Page " + page + ")" + ChatColor.GOLD + "]" + dashes);
                sender.sendMessage(ChatColor.RESET + "To toggle auto-show, do the following (hover for more info): ");
                new FancyMessage("/calendar admin toggle autoshow [#optional true/false]")
                        .color(ChatColor.YELLOW)
                        .tooltip(ChatColor.RESET + "The following command decides whether the calendar should automatically display to the user as a join message.")
                        .send(sender);
                break;
            default:
                sender.sendMessage(ChatColor.RESET + " There are no toggle options registered in this page.");
                break;
        }

    }

    public void showEventHelp(CommandSender sender) {

        sender.sendMessage(ChatColor.RESET + "To add an event, do the following (hover for example): ");
        new FancyMessage("/cal admin e add <day> <month> <year> [optional: time]")
                .color(ChatColor.YELLOW)
                .tooltip(ChatColor.RESET + "Example: " + ChatColor.YELLOW + "/cal admin e add 13 2 1970 5:45 P.M.\n" + ChatColor.RESET + "The following example would add an event in " + ChatColor.GOLD + "13 February 1970 5:45 P.M.")
                .send(sender);
        sender.sendMessage("");
        sender.sendMessage(ChatColor.RESET + "To remove an event, do the following (hover for example): ");
        new FancyMessage("/cal admin e remove <title-of-event>")
                .color(ChatColor.YELLOW)
                .tooltip(ChatColor.RESET + "Example: " + ChatColor.YELLOW + "/cal admin e remove myTitle\n" + ChatColor.RESET + "The following example would remove the event with the name of" + ChatColor.GOLD + " \"myTitle\"")
                .send(sender);

    }
}
