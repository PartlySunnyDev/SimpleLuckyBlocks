package me.partlysunny.blocks.loot.entry.command;

import me.partlysunny.blocks.loot.entry.IEntry;
import org.bukkit.Bukkit;
import org.bukkit.Location;
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
