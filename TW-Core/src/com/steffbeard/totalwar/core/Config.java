package com.steffbeard.totalwar.core;

import org.bukkit.ChatColor;
import java.util.Arrays;
import java.io.File;
import java.util.LinkedHashMap;
import java.util.List;
import org.bukkit.Material;
import com.steffbeard.totalwar.core.utils.Skyoconfig;

public class Config extends Skyoconfig {

    public boolean enableInventoryWeight;
    public boolean entityFireTrail;
    @ConfigOptions(name = "options.reusable-keys")
    public boolean reusableKeys;
    @ConfigOptions(name = "options.disable-hoppers")
    public boolean disableHoppers;
    @ConfigOptions(name = "options.encrypt-lore")
    public boolean encryptLore;
    @ConfigOptions(name = "options.can-rename-items")
    public boolean canRenameItems;
    @ConfigOptions(name = "key.material")
    public Material keyMaterial;
    @ConfigOptions(name = "key.name")
    public String keyName;
    @ConfigOptions(name = "key.shape")
    public List<String> keyShape;
    @ConfigOptions(name = "masterkey.material")
    public Material masterKeyMaterial;
    @ConfigOptions(name = "masterkey.name")
    public String masterKeyName;
    @ConfigOptions(name = "masterkey.shape")
    public List<String> masterKeyShape;
    @ConfigOptions(name = "bunch-of-keys.material")
    public Material bunchOfKeysMaterial;
    @ConfigOptions(name = "bunch-of-keys.name")
    public String bunchOfKeysName;
    @ConfigOptions(name = "bunch-of-keys.shape")
    public List<String> bunchOfKeysShape;
    @ConfigOptions(name = "shape-materials-v1")
    public LinkedHashMap<String, String> shapeMaterials;
    @ConfigOptions(name = "options.enable-enchanting")
    public boolean enableEnchanting;
    
    protected Config(final File dataFolder) {
        super(new File(dataFolder, "config.yml"), Arrays.asList("Key configuration"));
        this.enableInventoryWeight = true;
        this.entityFireTrail = true;
        this.reusableKeys = true;
        this.disableHoppers = true;
        this.encryptLore = false;
        this.canRenameItems = false;
        this.keyMaterial = Material.TRIPWIRE_HOOK;
        this.keyName = ChatColor.GOLD + "Key";
        this.keyShape = Arrays.asList("I", "L");
        this.masterKeyMaterial = Material.NAME_TAG;
        this.masterKeyName = ChatColor.DARK_PURPLE + "Master Key";
        this.masterKeyShape = Arrays.asList("C", "L");
        this.bunchOfKeysMaterial = Material.NAME_TAG;
        this.bunchOfKeysName = ChatColor.BLUE + "Bunch of keys";
        this.bunchOfKeysShape = Arrays.asList(" S ", "SLS", " S ");
        (this.shapeMaterials = new LinkedHashMap<String, String>()).put("I", Material.IRON_INGOT.name());
        this.shapeMaterials.put("L", Material.LEVER.name());
        this.shapeMaterials.put("C", Material.COMMAND.name());
        this.shapeMaterials.put("S", Material.STRING.name());
	}
}
