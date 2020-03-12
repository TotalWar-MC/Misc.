package us.persistenthistory;

import com.xxmicloxx.NoteBlockAPI.NoteBlockPlayerMain;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import us.persistenthistory.calendar.*;

import java.util.logging.Logger;

public class PersistentHistory extends JavaPlugin {

    public static Plugin plugin;
    public static Logger logger;
    public static PluginDescriptionFile pdFile;
    public static CalendarFiles calendarFiles;
    public static PCalendar pCalendar;

    @Override
    public void onLoad() {
        //pre-enable
    }

    @Override
    public void onEnable() {

        logger = Logger.getLogger("Minecraft");
        plugin = this;
        pdFile = this.getDescription();

        CommandSender console = Bukkit.getConsoleSender();
        Bukkit.dispatchCommand(console, "gamerule doDaylightCycle false");

        calendarFiles = new CalendarFiles();
        calendarFiles.buildCalendar();

        pCalendar = new PCalendar(calendarFiles.getCalendar(), calendarFiles.getTicks());




        new DayExtend().runTaskTimerAsynchronously(this, 0, 3);

        registerCommands();
        registerEvents();

        logger.info(pdFile.getName() + " has been successfully enabled on the server.");
    }

    @Override
    public void onDisable() {

        plugin = null;

        calendarFiles.save();

        logger.info(pdFile.getName() + " has been successfully disabled on the server.");
    }


    public void registerCommands() {

        getCommand("calendar").setExecutor(new CalendarCommand());
        getCommand("cal").setExecutor(new CalendarCommand());

    }

    public void registerEvents() {
        PluginManager pm = this.getServer().getPluginManager();

        pm.registerEvents(new NewDayListener(), this);
        pm.registerEvents(new NewEventListener(), this);
        pm.registerEvents(new SongEndListener(), this);
        pm.registerEvents(new CalendarJoinListener(), this);


    }

    public static NoteBlockPlayerMain getNoteBlockAPI() {
        Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("NoteBlockAPI");

        if (plugin == null || !(plugin instanceof NoteBlockPlayerMain)) {
            return null;
        }

        return (NoteBlockPlayerMain) plugin;
    }
}
