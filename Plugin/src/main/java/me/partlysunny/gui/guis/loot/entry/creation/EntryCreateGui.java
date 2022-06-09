package me.partlysunny.gui.guis.loot.entry.creation;

import me.partlysunny.blocks.loot.entry.IEntry;
import me.partlysunny.gui.GuiInstance;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public abstract class EntryCreateGui<T extends IEntry> implements GuiInstance {

    protected final Map<UUID, EntrySaveWrapper<T>> saves = new HashMap<>();

    public EntrySaveWrapper<T> getSave(UUID player) {
        return saves.get(player);
    }

    public void setSave(UUID player, EntrySaveWrapper<T> value) {
        saves.put(player, value);
    }

}
