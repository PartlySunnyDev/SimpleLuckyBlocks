package me.partlysunny.blocks.loot;

import me.partlysunny.blocks.loot.entry.LootEntryManager;
import me.partlysunny.gui.SaveInfo;
import me.partlysunny.util.classes.RandomList;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

import static me.partlysunny.util.Util.processText;

public class CustomLootTable implements SaveInfo {

    private final RandomList<TableEntryWrapper> entries;
    private int rolls;

    public CustomLootTable(int rolls, List<TableEntryWrapper> entries) {
        RandomList<TableEntryWrapper> l = new RandomList<>();
        for (TableEntryWrapper entry : entries) {
            l.add(entry, entry.weight());
        }
        this.entries = l;
        this.rolls = rolls;
    }

    public void removeEntry(TableEntryWrapper w) {
        RandomList<TableEntryWrapper>.RandomCollectionObject<TableEntryWrapper> toRemove = null;
        for (RandomList<TableEntryWrapper>.RandomCollectionObject<TableEntryWrapper> e : entries) {
            if (e.getObject().equals(w)) {
                toRemove = e;
            }
        }
        if (!(toRemove == null)) {
            entries.remove(toRemove);
        }
    }

    public RandomList<TableEntryWrapper> getEntries() {
        return entries;
    }

    public RandomList<TableEntryWrapper> entries() {
        return entries;
    }

    public int rolls() {
        return rolls;
    }

    public void setRolls(int rolls) {
        this.rolls = rolls;
    }

    public void dropTableAt(Location l, Player p) {
        List<String> hasSaid = new ArrayList<>();
        for (int i = 0; i < rolls; i++) {
            TableEntryWrapper raffle = entries.raffle();
            if (raffle.message() == null || raffle.entry() == null) {
                continue;
            }
            LootEntryManager.getEntry(raffle.entry()).execute(l, p);
            if (!hasSaid.contains(raffle.message()) && !raffle.message().equals("") && !(p == null)) {
                hasSaid.add(raffle.message());
                p.sendMessage(ChatColor.GREEN + processText(raffle.message()));
            }
        }
    }

    @Override
    public YamlConfiguration getSave() {
        YamlConfiguration config = new YamlConfiguration();
        config.set("rolls", rolls);
        YamlConfiguration e = new YamlConfiguration();
        for (RandomList<TableEntryWrapper>.RandomCollectionObject<TableEntryWrapper> t : entries) {
            TableEntryWrapper entry = t.getObject();
            YamlConfiguration subSection = new YamlConfiguration();
            subSection.set("weight", entry.weight());
            if (!entry.message().equals("")) {
                subSection.set("message", entry.message());
            }
            e.set(entry.entry(), subSection);
        }
        config.set("entries", e);
        return config;
    }
}
