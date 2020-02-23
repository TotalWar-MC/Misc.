package com.steffbeard.totalwar.core;

import org.bukkit.configuration.ConfigurationSection;
import java.util.HashMap;

public class ExpireTime
{
    private HashMap<String, Long> expireTimes;
    
    public ExpireTime(final Main main) {
        this.expireTimes = new HashMap<String, Long>();
        final ConfigurationSection config = main.getConfig().getConfigurationSection("expire-times");
        for (final String material : config.getKeys(false)) {
            this.expireTimes.put(material, config.getLong(material) * 1000L);
        }
    }
    
    public Long getExpireTime(final String material) {
        return (this.expireTimes.get(material) == null) ? 10000L : this.expireTimes.get(material);
    }
}
