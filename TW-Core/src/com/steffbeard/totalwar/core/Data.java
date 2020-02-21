package com.steffbeard.totalwar.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.io.File;
import org.bukkit.Location;
import java.util.List;

import com.steffbeard.totalwar.core.utils.ConfigManager;

public class Data extends ConfigManager {
	
    @ConfigOptions(name = "padlocks")
    public List<Location> padlocks;
    
    protected Data(final File dataFolder) {
        super(new File(dataFolder, "data.yml"), Arrays.asList("Key Data"));
        this.padlocks = new ArrayList<Location>();
    }
}
