package me.partlysunny.blocks.loot.entry;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public interface IEntry {

    void execute(Location l, Player p);

}
