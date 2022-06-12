package me.partlysunny.gui.guis.common.material.filters;

import org.bukkit.Material;

public class ItemFilter implements MaterialFilter {
    @Override
    public boolean doesQualify(Material m) {
        return m.isItem();
    }
}
