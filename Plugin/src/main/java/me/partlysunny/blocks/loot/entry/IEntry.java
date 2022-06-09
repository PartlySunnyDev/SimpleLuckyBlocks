package me.partlysunny.blocks.loot.entry;

import me.partlysunny.gui.SaveInfo;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public interface IEntry extends SaveInfo {

    void execute(Location l, Player p);

    EntryType getEntryType();

}
