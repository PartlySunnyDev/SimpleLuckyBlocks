package me.partlysunny.blocks.loot.entry.structure;

import me.partlysunny.blocks.loot.entry.EntryType;
import me.partlysunny.blocks.loot.entry.IEntry;
import me.partlysunny.worldedit.WorldEditHook;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class StructureEntry implements IEntry {

    private final String structure;
    private final double offsetX;
    private final double offsetY;
    private final double offsetZ;

    public StructureEntry(String structure, double offsetX, double offsetY, double offsetZ) {
        this.structure = structure;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.offsetZ = offsetZ;
    }

    @Override
    public void execute(Location l, Player p) {
        if (p == null) {
            return;
        }
        WorldEditHook.paste(structure, l.add(offsetX, offsetY, offsetZ));
    }

    @Override
    public YamlConfiguration getSave() {
        YamlConfiguration config = new YamlConfiguration();
        return config;
    }

    @Override
    public EntryType getEntryType() {
        return EntryType.STRUCTURE;
    }
}
