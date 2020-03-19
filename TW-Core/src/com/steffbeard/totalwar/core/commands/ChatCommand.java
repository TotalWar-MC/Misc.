package com.steffbeard.totalwar.core.commands;

import java.util.List;
import java.util.Iterator;
import java.util.Set;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.ChatColor;
import java.util.ArrayList;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import com.steffbeard.totalwar.core.Main;

import org.bukkit.command.CommandExecutor;

public class ChatCommand implements CommandExecutor {
   
	private Main plugin;
    
    public ChatCommand(final Main p) {
        this.plugin = p;
    }
    
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        final FileConfiguration cfg = this.plugin.getConfig();
        if (args.length < 1 || args[0].equalsIgnoreCase("help")) {
            return false;
        }
        if (!this.strCheck(args)) {
            this.sendError(sender, "The command contained invalid characters");
            return true;
        }
        if (args[0].equalsIgnoreCase("new")) {
            final int minargs = 3;
            final int maxargs = 4;
            String word = "";
            String rep = "";
            String group = "default";
            final double chance = 100.0;
            if (!this.argLength(minargs, maxargs, sender, args)) {
                return true;
            }
            word = args[1];
            rep = args[2];
            if (args.length == 4) {
                group = args[3];
            }
            ConfigurationSection hog = cfg.getConfigurationSection(group);
            if (hog == null) {
                hog = cfg.createSection(group);
                hog.set("enable", (Object)true);
                hog.set("players", (Object)new ArrayList());
                hog.createSection("blocks");
            }
            final ConfigurationSection block = hog.getConfigurationSection("blocks");
            final ConfigurationSection wordblock = block.createSection("_" + word);
            wordblock.set("replace", (Object)rep);
            wordblock.set("chance", (Object)chance);
            this.plugin.saveConfig();
            sender.sendMessage(ChatColor.DARK_AQUA + word + " will now be replaced with " + rep + " [" + group + " group]");
            return true;
        }
        else if (args[0].equalsIgnoreCase("remove")) {
            final int minargs = 2;
            final int maxargs = 3;
            String word = "";
            String group2 = "default";
            if (!this.argLength(minargs, maxargs, sender, args)) {
                return true;
            }
            word = args[1];
            if (args.length == 3) {
                group2 = args[2];
            }
            final ConfigurationSection hog2 = cfg.getConfigurationSection(group2);
            if (!this.hogExists(sender, hog2, group2)) {
                return true;
            }
            final ConfigurationSection blocks = hog2.getConfigurationSection("blocks");
            if (blocks.contains("_" + word.toLowerCase())) {
                blocks.set("_" + word.toLowerCase(), (Object)null);
                sender.sendMessage("Removed word " + word + " from group " + group2);
                this.plugin.saveConfig();
            }
            else {
                this.sendError(sender, "Word " + word + " doesn't exist in group " + group2);
            }
            return true;
        }
        else if (args[0].equalsIgnoreCase("player")) {
            final int minargs = 2;
            final int maxargs = 4;
            String group2 = "default";
            if (!this.argLength(minargs, maxargs, sender, args)) {
                return true;
            }
            final String ply = args[1];
            if (args.length == 2) {
                final StringBuilder list = new StringBuilder(" --------\n " + ply + "\n");
                final Set<String> groups = (Set<String>)cfg.getKeys(false);
                for (final String key : groups) {
                    final ConfigurationSection hog3 = cfg.getConfigurationSection(key);
                    final boolean enabled = hog3.getBoolean("enable");
                    final List<String> players = (List<String>)hog3.getStringList("players");
                    if (players.isEmpty() || players.contains(ply.toLowerCase())) {
                        if (enabled) {
                            list.append("[" + ChatColor.GREEN + "+" + ChatColor.WHITE + "] " + key + "\n");
                        }
                        else {
                            list.append("[" + ChatColor.RED + "-" + ChatColor.WHITE + "] " + key + "\n");
                        }
                    }
                }
                list.append("--------");
                sender.sendMessage(list.toString());
                return true;
            }
            if (args.length >= 4) {
                group2 = args[3];
            }
            final ConfigurationSection hog4 = cfg.getConfigurationSection(group2);
            if (!this.hogExists(sender, hog4, group2)) {
                return true;
            }
            final List<String> players2 = (List<String>)hog4.getStringList("players");
            if (args[2].equalsIgnoreCase("add")) {
                if (!players2.contains(ply.toLowerCase())) {
                    players2.add(ply.toLowerCase());
                    hog4.set("players", (Object)players2);
                    this.plugin.saveConfig();
                    sender.sendMessage("added player " + ply + " to group " + group2);
                }
                else {
                    this.sendError(sender, "player " + ply + " already exists in group " + group2);
                }
            }
            else if (args[2].equalsIgnoreCase("remove")) {
                if (players2.contains(ply.toLowerCase())) {
                    players2.remove(ply.toLowerCase());
                    hog4.set("players", (Object)players2);
                    this.plugin.saveConfig();
                    sender.sendMessage("removed player " + ply + " from group " + group2);
                }
                else {
                    this.sendError(sender, "player " + ply + " does not exist in group " + group2);
                }
            }
            else {
                this.sendError(sender, "/tr player <playername> <add|remove> <groupname>");
            }
            return true;
        }
        else if (args[0].equalsIgnoreCase("group")) {
            final int minargs = 1;
            final int maxargs = 3;
            if (!this.argLength(minargs, maxargs, sender, args)) {
                return true;
            }
            if (args.length == 1) {
                final StringBuilder list2 = new StringBuilder(" --------\n Groups\n");
                final Set<String> groups2 = (Set<String>)cfg.getKeys(false);
                for (final String key2 : groups2) {
                    final ConfigurationSection hog5 = cfg.getConfigurationSection(key2);
                    final boolean enabled2 = hog5.getBoolean("enable");
                    list2.append(String.valueOf(enabled2 ? new StringBuilder("[").append(ChatColor.GREEN).append("+").append(ChatColor.WHITE).append("] ").append(key2).toString() : new StringBuilder("[").append(ChatColor.RED).append("-").append(ChatColor.WHITE).append("] ").append(key2).toString()) + "\n");
                }
                list2.append("--------\n");
                sender.sendMessage(list2.toString());
                return true;
            }
            if (args[1].equalsIgnoreCase("toggle")) {
                String group2 = "default";
                if (args.length == 3) {
                    group2 = args[2];
                }
                final ConfigurationSection hog2 = cfg.getConfigurationSection(group2);
                if (!this.hogExists(sender, hog2, group2)) {
                    return true;
                }
                final boolean enabled3 = hog2.getBoolean("enable");
                hog2.set("enable", (Object)!enabled3);
                this.plugin.saveConfig();
                sender.sendMessage("Group " + group2 + " was toggled " + (enabled3 ? "off" : "on"));
            }
            else if (args[1].equalsIgnoreCase("words")) {
                String group2 = "default";
                if (args.length == 3) {
                    group2 = args[2];
                }
                final ConfigurationSection hog2 = cfg.getConfigurationSection(group2);
                if (!this.hogExists(sender, hog2, group2)) {
                    return true;
                }
                final boolean enable = hog2.getBoolean("enable");
                final ConfigurationSection blocks2 = hog2.getConfigurationSection("blocks");
                final Set<String> wordblocks = (Set<String>)blocks2.getKeys(false);
                final StringBuilder wordlist = new StringBuilder("[" + (enable ? (ChatColor.GREEN + "+") : (ChatColor.RED + "-")) + ChatColor.WHITE + "] " + group2 + "\n");
                for (final String key3 : wordblocks) {
                    wordlist.append(String.valueOf(key3.substring(1)) + ", ");
                }
                final String builtwordlist = wordlist.toString();
                sender.sendMessage(builtwordlist.substring(0, wordlist.length() - 2));
            }
            else {
                this.sendError(sender, "/tr group <toggle|words>");
            }
            return true;
        }
        else if (args[0].equalsIgnoreCase("word")) {
            final int minargs = 1;
            final int maxargs = 5;
            final StringBuilder list2 = new StringBuilder("Words: ");
            if (!this.argLength(minargs, maxargs, sender, args)) {
                return true;
            }
            if (args.length == 1) {
                final Set<String> groups2 = (Set<String>)cfg.getKeys(false);
                for (final String key2 : groups2) {
                    final ConfigurationSection hog5 = cfg.getConfigurationSection(key2);
                    final boolean enabled2 = hog5.getBoolean("enable");
                    final ConfigurationSection blocks3 = hog5.getConfigurationSection("blocks");
                    final Set<String> words = (Set<String>)blocks3.getKeys(false);
                    for (final String word2 : words) {
                        list2.append((enabled2 ? ChatColor.GREEN : ChatColor.RED) + word2.substring(1) + ChatColor.WHITE + ", ");
                    }
                }
                final String wordlist2 = list2.toString();
                sender.sendMessage(wordlist2.substring(0, wordlist2.length() - 2));
                return true;
            }
            if (args.length > 3) {
                if (args.length >= 4) {
                    if (args[2].equalsIgnoreCase("chance")) {
                        String word3 = "";
                        String group3 = "default";
                        if (args.length == 5) {
                            group3 = args[4];
                        }
                        final ConfigurationSection hog = cfg.getConfigurationSection(group3);
                        if (!this.hogExists(sender, hog, group3)) {
                            return true;
                        }
                        word3 = args[1];
                        if (!args[3].matches("[0-9]+\\.?[0-9]*")) {
                            this.sendError(sender, "'" + args[3] + "' is not a valid number");
                            return true;
                        }
                        final double chance2 = Double.parseDouble(args[3]);
                        final ConfigurationSection blocks3 = hog.getConfigurationSection("blocks");
                        if (blocks3.contains("_" + word3.toLowerCase())) {
                            final ConfigurationSection wordblock = blocks3.getConfigurationSection("_" + word3.toLowerCase());
                            final String rep2 = wordblock.getString("replace");
                            wordblock.set("chance", (Object)chance2);
                            this.plugin.saveConfig();
                            sender.sendMessage(String.valueOf(word3) + " -> " + rep2 + " set to " + chance2 + "% chance");
                            return true;
                        }
                        this.sendError(sender, "word " + word3 + " does not exist in group " + group3);
                        return true;
                    }
                    else {
                        this.sendError(sender, "/tr word <word> [chance <chance>]");
                    }
                }
                return true;
            }
            String word3 = "";
            String group = "default";
            if (args.length == 3) {
                group = args[2];
            }
            final ConfigurationSection hog4 = cfg.getConfigurationSection(group);
            if (!this.hogExists(sender, hog4, group)) {
                return true;
            }
            word3 = args[1];
            final ConfigurationSection block2 = hog4.getConfigurationSection("blocks");
            final boolean enabled2 = hog4.getBoolean("enable");
            if (block2.contains("_" + word3.toLowerCase())) {
                final ConfigurationSection wordblock2 = block2.getConfigurationSection("_" + word3.toLowerCase());
                final String rep3 = wordblock2.getString("replace");
                final double chance3 = wordblock2.getDouble("chance");
                sender.sendMessage("[" + (enabled2 ? (ChatColor.GREEN + "+") : (ChatColor.RED + "-")) + ChatColor.WHITE + "] " + word3 + " -> " + rep3 + " | chance: " + (enabled2 ? ChatColor.GREEN : ChatColor.RED) + chance3 + ChatColor.WHITE + "%");
                return true;
            }
            this.sendError(sender, "word " + word3 + " does not exist in group " + group);
            return true;
        }
        else if (args[0].equalsIgnoreCase("enableall")) {
            final int minargs = 1;
            final int maxargs = 1;
            if (!this.argLength(minargs, maxargs, sender, args)) {
                return true;
            }
            final Set<String> groups3 = (Set<String>)cfg.getKeys(false);
            for (final String key4 : groups3) {
                final ConfigurationSection hog4 = cfg.getConfigurationSection(key4);
                hog4.set("enable", (Object)true);
            }
            this.plugin.saveConfig();
            sender.sendMessage("Enabled " + groups3.size() + " groups.");
            return true;
        }
        else if (args[0].equalsIgnoreCase("disableall")) {
            final int minargs = 1;
            final int maxargs = 1;
            if (!this.argLength(minargs, maxargs, sender, args)) {
                return true;
            }
            final Set<String> groups3 = (Set<String>)cfg.getKeys(false);
            for (final String key4 : groups3) {
                final ConfigurationSection hog4 = cfg.getConfigurationSection(key4);
                hog4.set("enable", (Object)false);
            }
            this.plugin.saveConfig();
            sender.sendMessage("Disabled " + groups3.size() + " groups.");
            return true;
        }
        else {
            if (args.length >= 1) {
                this.sendError(sender, "Invalid Command | /tr help");
                return true;
            }
            return false;
        }
    }
    
    private boolean strCheck(final String[] args) {
        for (final String s : args) {
            if (!s.matches("[A-Za-z0-9\\_#,-]*")) {
                return false;
            }
        }
        return true;
    }
    
    private void sendError(final CommandSender s, final String err) {
        s.sendMessage(ChatColor.RED + err);
    }
    
    private boolean argLength(final int minargs, final int maxargs, final CommandSender sender, final String[] args) {
        if (args.length < minargs) {
            this.sendError(sender, "Not enough inputs | /tr help");
            return false;
        }
        if (args.length > maxargs) {
            this.sendError(sender, "Too many inputs | /tr help");
            return false;
        }
        return true;
    }
    
    private boolean hogExists(final CommandSender sender, final ConfigurationSection h, final String group) {
        if (h == null) {
            this.sendError(sender, "group " + group + " does not exist");
            return false;
        }
        return true;
    }
}
