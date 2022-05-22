package me.partlysunny.util.classes;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ItemBuilder {

    private final Material m;
    private final ItemMeta meta;
    private final ItemStack s;
    private final Map<Enchantment, Integer> enchants = new HashMap<>();

    public ItemBuilder(Material m) {
        this.m = m;
        this.s = new ItemStack(m);
        this.meta = s.getItemMeta();
    }

    public static ItemBuilder builder(Material m) {
        return new ItemBuilder(m);
    }

    public ItemBuilder setName(String name) {
        meta.setDisplayName(name);
        return this;
    }

    public ItemBuilder setLore(String... lore) {
        meta.setLore(Arrays.asList(lore));
        return this;
    }

    public ItemBuilder addEnchantment(Enchantment e, int level) {
        enchants.put(e, level);
        return this;
    }

    public ItemBuilder setUnbreakable(boolean u) {
        meta.setUnbreakable(u);
        return this;
    }

    public ItemStack build() {
        s.setItemMeta(meta);
        for (Enchantment m : enchants.keySet()) {
            s.addUnsafeEnchantment(m, enchants.get(m));
        }
        return s;
    }

}
