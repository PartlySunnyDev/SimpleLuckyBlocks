package me.partlysunny.gui.guis.common.item.enchant;

import org.bukkit.enchantments.Enchantment;

public class EnchantContainer {

    private Enchantment enchant;
    private int lvl;

    public EnchantContainer(Enchantment enchant, int lvl) {
        this.enchant = enchant;
        this.lvl = lvl;
    }

    public Enchantment enchant() {
        return enchant;
    }

    public void setEnchant(Enchantment enchant) {
        this.enchant = enchant;
    }

    public int lvl() {
        return lvl;
    }

    public void setLvl(int lvl) {
        this.lvl = lvl;
    }
}
