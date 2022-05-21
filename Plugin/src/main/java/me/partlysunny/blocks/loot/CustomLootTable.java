package me.partlysunny.blocks.loot;

import me.partlysunny.util.classes.Pair;
import me.partlysunny.util.classes.RandomList;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class CustomLootTable {

    private final String id;
    private final RandomList<LootTableEntry> entries;
    private final int rolls;

    @SafeVarargs
    public CustomLootTable(String id, int rolls, Pair<LootTableEntry, Integer>... entries) {
        this.id = id;
        RandomList<LootTableEntry> l = new RandomList<>();
        for (Pair<LootTableEntry, Integer> entry : entries) {
            l.add(entry.a(), entry.b());
        }
        this.entries = l;
        this.rolls = rolls;
    }

    public void dropTableAt(Location l) {
        List<ItemStack> drops = new ArrayList<>();
        for (int i = 0; i < rolls; i++) {
            LootTableEntry e = entries.raffle();
            drops.add(e.getRoll());
        }
        for (ItemStack i : drops) {
            World world = l.getWorld();
            if (world == null) {
                return;
            }
            world.dropItemNaturally(l, i);
        }
    }

    public String id() {
        return id;
    }
}
