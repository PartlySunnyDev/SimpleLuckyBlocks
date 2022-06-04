package me.partlysunny.gui.guis.loot.entry;

import me.partlysunny.blocks.loot.entry.EntryType;
import me.partlysunny.gui.SaveInfo;

public interface EntrySaveInfo extends SaveInfo {
    EntryType getEntryType();
}
