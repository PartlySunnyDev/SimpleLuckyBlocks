package me.partlysunny.gui.guis.loot.entry.creation.command;

import me.partlysunny.blocks.loot.entry.EntryType;
import me.partlysunny.gui.guis.loot.entry.EntrySaveInfo;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.List;

public class CommandInfo implements EntrySaveInfo {

    private String name;
    private List<String> commands;

    public CommandInfo(List<String> commands) {
        this.commands = commands;
    }

    public String name() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> commands() {
        return commands;
    }

    public void setCommands(List<String> commands) {
        this.commands = commands;
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
        this.commands.add(command);
    }

    public void removeCommand(String command) {
        this.commands.remove(command);
    }
}
