package me.partlysunny.gui.guis.common.material.filters;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class MetaFilter implements MaterialFilter {
    @Override
    public boolean doesQualify(Material m) {
        return new ItemStack(m).getItemMeta() != null;
    }
}
