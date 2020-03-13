package moe.kira.structure.plugin;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import moe.kira.structure.StructureAPI.Unsafe;
import moe.kira.structure.StructurePhysicsListener;

public class StructurePlugin extends JavaPlugin {
    private static final String DATA_FOLDER = "persistent_structures";
    private static StructurePlugin instance;
    
    public static final JavaPlugin getInstance() {
        return instance;
    }
    
    @Override
    public void onEnable() {
        instance = this;
        Bukkit.getPluginManager().registerEvents(new StructurePhysicsListener(), this);
    }
    
    @Override
    public void onDisable() {
        Unsafe.unregisterAll();
    }
    
    public static File presistentStructuresFolder() {
        instance.getDataFolder().mkdir();
        File folder = new File(instance.getDataFolder(), DATA_FOLDER);
        folder.mkdir();
        return folder;
    }
}
