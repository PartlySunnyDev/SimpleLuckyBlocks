package me.partlysunny.blocks.loot;

import me.partlysunny.util.Util;
import org.bukkit.inventory.ItemStack;

public class LootTableEntry {

    private final int min;
    private final int max;
    private final ItemStack itemToDrop;

    public LootTableEntry(ItemStack itemToDrop, int min, int max) {
        this.itemToDrop = itemToDrop;
        this.min = min;
        this.max = max;
    }

    public ItemStack getRoll() {
        ItemStack newItem = itemToDrop.clone();
        newItem.setAmount(Util.getRandomBetween(min, max));
        return newItem;
    }

    public int min() {
        return min;
    }

    public int max() {
        return max;
    }

    public ItemStack itemToDrop() {
        return itemToDrop;
    }
}
