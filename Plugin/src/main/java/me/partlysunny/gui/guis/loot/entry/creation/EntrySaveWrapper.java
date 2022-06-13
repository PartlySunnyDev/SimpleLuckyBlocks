package me.partlysunny.gui.guis.loot.entry.creation;

import me.partlysunny.blocks.loot.entry.IEntry;
import me.partlysunny.gui.guis.Renamable;

public class EntrySaveWrapper<T extends IEntry> implements Renamable {

    private final T entry;
    private String name;

    public EntrySaveWrapper(String name, T entry) {
        this.name = name;
        this.entry = entry;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }


    @Override
    public String name() {
        return name;
    }

    public T entry() {
        return entry;
    }
}
