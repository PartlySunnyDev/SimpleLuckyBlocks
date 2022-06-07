package me.partlysunny.blocks.loot.entry.command;

import me.partlysunny.blocks.loot.entry.EntryType;
import me.partlysunny.blocks.loot.entry.IEntry;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CommandEntry implements IEntry {

    private final List<CommandWrapper> commands = new ArrayList<>();

    public CommandEntry(List<String> commands) {
        for (String s : commands) {
            this.commands.add(new CommandWrapper(s));
        }
    }

    public List<CommandWrapper> commands() {
        return commands;
    }

    public String[] getCommands() {
        String[] r = new String[commands.size()];
        for (int i = 0; i < commands.size(); i++) {
            r[i] = commands.get(i).command();
        }
        return r;
    }

    @Override
    public YamlConfiguration getSave() {
        YamlConfiguration config = new YamlConfiguration();
        config.set("entryType", "command");
        config.set("commands", commands);
        return config;
    }

    @Override
    public EntryType getEntryType() {
        return EntryType.COMMAND;
    }

    public void addCommand(String command) {
        this.commands.add(new CommandWrapper(command));
    }

    public void removeCommand(String command) {
        this.commands.remove(new CommandWrapper(command));
    }

    @Override
    public void execute(Location l, Player p) {
        if (p == null) {
            return;
        }
        for (CommandWrapper wr : commands) {
            wr.execute(Bukkit.getServer(), l);
        }
    }
}
