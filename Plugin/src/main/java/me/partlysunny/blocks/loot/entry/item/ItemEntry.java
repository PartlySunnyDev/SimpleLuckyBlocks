package me.partlysunny.blocks.loot.entry.item;

import me.partlysunny.blocks.loot.entry.IEntry;
import me.partlysunny.util.Util;
import org.bukkit.Location;
import org.bukkit.entity.Player;
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
    public void execute(Location l, Player p) {
        if (l.getWorld() == null) {
            return;
        }
        l.getWorld().dropItemNaturally(l, getRoll());
    }

}
