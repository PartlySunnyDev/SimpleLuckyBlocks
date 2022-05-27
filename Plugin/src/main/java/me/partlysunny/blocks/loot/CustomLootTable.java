package me.partlysunny.blocks.loot;

import me.partlysunny.blocks.loot.entry.IEntry;
import me.partlysunny.blocks.loot.entry.LootEntryManager;
import me.partlysunny.util.classes.Pair;
import me.partlysunny.util.classes.RandomList;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

import static me.partlysunny.util.Util.processText;

public class CustomLootTable {

    private final RandomList<Pair<String, IEntry>> entries;
    private final int rolls;

    public CustomLootTable(int rolls, List<Pair<String, Pair<String, Integer>>> entries) {
        //List of Pair<Message, Entry>
        RandomList<Pair<String, IEntry>> l = new RandomList<>();
        for (Pair<String, Pair<String, Integer>> entry : entries) {
            l.add(new Pair<>(entry.b().a(), LootEntryManager.getEntry(entry.a())), entry.b().b());
        }
        this.entries = l;
        this.rolls = rolls;
    }

    public void dropTableAt(Location l, Player p) {
        List<String> hasSaid = new ArrayList<>();
        for (int i = 0; i < rolls; i++) {
            Pair<String, IEntry> raffle = entries.raffle();
            if (raffle.a() == null || raffle.b() == null) {
                continue;
            }
            raffle.b().execute(l, p);
            if (!hasSaid.contains(raffle.a()) && !raffle.a().equals("") && !(p == null)) {
                hasSaid.add(raffle.a());
                p.sendMessage(ChatColor.GREEN + processText(raffle.a()));
            }
        }
    }

}
