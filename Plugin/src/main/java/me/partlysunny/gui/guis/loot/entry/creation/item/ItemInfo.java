package me.partlysunny.gui.guis.loot.entry.creation.item;

import me.partlysunny.blocks.loot.entry.EntryType;
import me.partlysunny.gui.guis.loot.entry.EntrySaveInfo;
import me.partlysunny.util.Util;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class ItemInfo implements EntrySaveInfo {

    private ItemStack item;
    private int min;
    private int max;
    private String name;

    public ItemInfo(ItemStack item, int min, int max) {
        this.item = item;
        this.min = min;
        this.max = max;
    }

    public ItemStack item() {
        return item;
    }

    public void setItem(ItemStack item) {
        this.item = item;
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

    @Override
    public YamlConfiguration getSave() {
        YamlConfiguration config = new YamlConfiguration();
        config.set("entryType", "item");
        config.set("material", item.getType().toString());
        config.set("min", min);
        config.set("max", max);
        config.set("name", item.getItemMeta().getDisplayName());
        List<String> lore = item.getItemMeta().getLore();
        if (lore != null) {
            config.set("lore", lore);
        }
        config.set("enchantments", Util.getEnchantSection(item.getItemMeta().getEnchants()));
        return config;
    }

    @Override
    public EntryType getEntryType() {
        return EntryType.ITEM;
    }

    public String name() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
