package me.partlysunny.blocks.loot.entry.structure;

import me.partlysunny.blocks.loot.entry.EntryType;
import me.partlysunny.blocks.loot.entry.IEntry;
import me.partlysunny.worldedit.WorldEditHook;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class StructureEntry implements IEntry {

    private String structure;
    private int offsetX;
    private int offsetY;
    private int offsetZ;

    public StructureEntry() {
        this("", 0, 0, 0);
    }

    public StructureEntry(String structure, int offsetX, int offsetY, int offsetZ) {
        this.structure = structure;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.offsetZ = offsetZ;
    }

    public String structure() {
        return structure;
    }

    public void setStructure(String structure) {
        this.structure = structure;
    }

    public int offsetX() {
        return offsetX;
    }

    public void setOffsetX(int offsetX) {
        this.offsetX = offsetX;
    }

    public int offsetY() {
        return offsetY;
    }

    public void setOffsetY(int offsetY) {
        this.offsetY = offsetY;
    }

    public int offsetZ() {
        return offsetZ;
    }

    public void setOffsetZ(int offsetZ) {
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
        config.set("entryType", "structure");
        config.set("offsetX", offsetX);
        config.set("offsetY", offsetY);
        config.set("offsetZ", offsetZ);
        config.set("structure", structure);
        return config;
    }

    @Override
    public EntryType getEntryType() {
        return EntryType.STRUCTURE;
    }
}
