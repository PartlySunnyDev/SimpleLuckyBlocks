package me.partlysunny.gui.guis.common.material.filters;

import org.bukkit.Material;

public interface MaterialFilter {

    static void init() {

    }

    boolean doesQualify(Material m);

}
