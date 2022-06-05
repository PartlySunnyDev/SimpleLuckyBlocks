package me.partlysunny.util.classes;

import jline.internal.Nullable;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionType;

import java.util.Arrays;

public class PotionBuilder {

    private final ItemStack toReturn;
    private final PotionMeta meta;

    public PotionBuilder(PotionFormat f) {
        Material m = Material.POTION;
        switch (f) {
            case SPLASH -> m = Material.SPLASH_POTION;
            case LINGERING -> m = Material.LINGERING_POTION;
        }
        this.toReturn = new ItemStack(m);
        this.meta = (PotionMeta) toReturn.getItemMeta();
    }

    public static PotionBuilder builder(PotionFormat f) {
        return new PotionBuilder(f);
    }

    public PotionBuilder setName(String name) {
        meta.setDisplayName(name);
        return this;
    }

    public PotionBuilder setLore(String... lore) {
        meta.setLore(Arrays.asList(lore));
        return this;
    }

    public PotionBuilder setPotionData(PotionType t, @Nullable Color color) {
        if (t != null) {
            meta.setBasePotionData(new PotionData(t));
        }
        if (color != null) {
            meta.setColor(color);
        }
        return this;
    }

    public PotionBuilder addCustomEffect(PotionEffect e) {
        meta.addCustomEffect(e, true);
        return this;
    }

    public ItemStack build() {
        toReturn.setItemMeta(meta);
        return toReturn;
    }


    public enum PotionFormat {
        SPLASH,
        POTION,
        LINGERING
    }

}
