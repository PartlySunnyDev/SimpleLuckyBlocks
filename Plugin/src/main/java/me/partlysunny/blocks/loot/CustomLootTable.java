package me.partlysunny.blocks.loot;

import me.partlysunny.blocks.loot.entry.IEntry;
import me.partlysunny.blocks.loot.entry.LootEntryManager;
import me.partlysunny.util.classes.Pair;
import me.partlysunny.util.classes.RandomList;
import org.bukkit.Location;

import java.util.List;

public class CustomLootTable {

    private final RandomList<IEntry> entries;
    private final int rolls;

    public CustomLootTable(int rolls, List<Pair<String, Integer>> entries) {
        RandomList<IEntry> l = new RandomList<>();
        for (Pair<String, Integer> entry : entries) {
            l.add(LootEntryManager.getEntry(entry.a()), entry.b());
        }
        this.entries = l;
        this.rolls = rolls;
    }

    public void dropTableAt(Location l) {
        for (int i = 0; i < rolls; i++) {
            entries.raffle().execute(l);
        }
    }

}
