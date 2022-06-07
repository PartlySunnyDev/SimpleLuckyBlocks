package me.partlysunny.blocks.loot.entry.item;

import me.partlysunny.blocks.loot.entry.EntryType;
import me.partlysunny.blocks.loot.entry.IEntry;
import me.partlysunny.util.Util;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class ItemEntry implements IEntry {

    private int min;
    private int max;

    private ItemStack itemToDrop;

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

    public int min() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public int max() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public ItemStack itemToDrop() {
        return itemToDrop;
    }

    public void setItemToDrop(ItemStack itemToDrop) {
        this.itemToDrop = itemToDrop;
    }

    @Override
    public YamlConfiguration getSave() {
        YamlConfiguration config = new YamlConfiguration();
        config.set("entryType", "item");
        config.set("material", itemToDrop.getType().toString());
        config.set("min", min);
        config.set("max", max);
        config.set("name", itemToDrop.getItemMeta().getDisplayName());
        List<String> lore = itemToDrop.getItemMeta().getLore();
        if (lore != null) {
            config.set("lore", lore);
        }
        config.set("enchantments", Util.getEnchantSection(itemToDrop.getItemMeta().getEnchants()));
        return config;
    }

    @Override
    public EntryType getEntryType() {
        return EntryType.ITEM;
    }

    @Override
    public void execute(Location l, Player p) {
        if (l.getWorld() == null) {
            return;
        }
        l.getWorld().dropItemNaturally(l, getRoll());
    }

}
