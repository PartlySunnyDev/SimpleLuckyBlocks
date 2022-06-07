package me.partlysunny.gui.guis.loot.entry.creation;

import me.partlysunny.blocks.loot.entry.IEntry;

public class EntrySaveWrapper<T extends IEntry> {

    private final T entry;
    private String name;

    public EntrySaveWrapper(String name, T entry) {
        this.name = name;
        this.entry = entry;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String name() {
        return name;
    }

    public T entry() {
        return entry;
    }
}
