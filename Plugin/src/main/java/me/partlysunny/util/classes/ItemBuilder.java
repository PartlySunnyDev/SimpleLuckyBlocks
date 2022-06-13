package me.partlysunny.util.classes;

import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ItemBuilder {

    private final ItemMeta meta;
    private final ItemStack s;
    private final Map<Enchantment, Integer> enchants = new HashMap<>();
    private final NBTItem nbti;

    public ItemBuilder(Material m) {
        this.s = new ItemStack(m);
        this.nbti = new NBTItem(s);
        this.meta = s.getItemMeta();
    }

    public ItemBuilder(ItemStack s) {
        this.s = s.clone();
        ItemMeta itemMeta = s.getItemMeta();
        if (itemMeta != null) {
            this.meta = itemMeta.clone();
        } else {
            this.meta = null;
        }
        this.nbti = new NBTItem(this.s);
    }

    public static ItemBuilder builder(Material m) {
        return new ItemBuilder(m);
    }

    public static ItemBuilder builder(ItemStack i) {
        return new ItemBuilder(i);
    }

    public ItemBuilder setNbtTag(String key, Object value) {
        nbti.setObject(key, value);
        return this;
    }

    public ItemBuilder setName(String name) {
        if (meta != null) meta.setDisplayName(name);
        return this;
    }

    public ItemBuilder setLore(String... lore) {
        if (meta != null) meta.setLore(Arrays.asList(lore));
        return this;
    }

    public ItemBuilder addEnchantment(Enchantment e, int level) {
        enchants.put(e, level);
        return this;
    }

    public ItemBuilder setUnbreakable(boolean u) {
        if (meta != null) meta.setUnbreakable(u);
        return this;
    }

    public ItemStack build() {
        if (meta != null) s.setItemMeta(meta);
        for (Enchantment m : enchants.keySet()) {
            s.addUnsafeEnchantment(m, enchants.get(m));
        }
        nbti.mergeCustomNBT(s);
        return s;
    }

    public ItemBuilder setAmount(int amount) {
        s.setAmount(amount);
        return this;
    }
}
