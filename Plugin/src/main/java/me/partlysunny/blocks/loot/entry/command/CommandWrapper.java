package me.partlysunny.blocks.loot.entry.command;

import org.bukkit.Location;
import org.bukkit.Server;

public class CommandWrapper {

    private final String command;

    public CommandWrapper(String command) {
        this.command = command;
    }

    public void execute(Server s, Location l) {
        s.dispatchCommand(s.getConsoleSender(), command.replace("<pos>", String.format("%d %d %d", (int)l.getX(), (int)l.getY(), (int)l.getZ())));
    }
}
