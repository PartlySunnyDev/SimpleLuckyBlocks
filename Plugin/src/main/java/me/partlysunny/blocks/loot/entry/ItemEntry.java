package me.partlysunny.blocks.loot.entry;

import me.partlysunny.util.Util;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

public class ItemEntry implements IEntry {

    private final int min;
    private final int max;
    private final ItemStack itemToDrop;

    public ItemEntry(ItemStack itemToDrop, int min, int max) {
        this.itemToDrop = itemToDrop;
        this.min = min;
        this.max = max;
    }

    private ItemStack getRoll() {
        ItemStack newItem = itemToDrop.clone();
        newItem.setAmount(Util.getRandomBetween(min, max));
        return newItem;
    }

    @Override
    public void execute(Location l) {
        if (l.getWorld() == null) {
            return;
        }
        l.getWorld().dropItemNaturally(l, getRoll());
    }

}
