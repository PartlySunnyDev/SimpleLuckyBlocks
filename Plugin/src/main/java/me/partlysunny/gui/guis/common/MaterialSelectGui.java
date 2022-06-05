package me.partlysunny.gui.guis.common;

import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.gui.type.util.Gui;
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane;
import me.partlysunny.util.Util;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

public class MaterialSelectGui extends ValueReturnGui<Material> {
    @Override
    public Gui getGui(HumanEntity p) {
        if (!(p instanceof Player player)) return new ChestGui(3, "");
        ChestGui gui = new ChestGui(5, ChatColor.GRAY + "Select Material");
        PaginatedPane pane = new PaginatedPane(0, 0, 9, 5);
        String[] entityList = new String[Material.values().length];
        int count = 0;
        for (Material m : Material.values()) {
            entityList[count] = m.toString();
            count++;
        }
        Util.addListPages(pane, player, this, 1, 1, 7, 3, Util.getAlphabetSorted(entityList), gui);
        gui.addPane(pane);
        Util.setClickSoundTo(Sound.BLOCK_METAL_PRESSURE_PLATE_CLICK_OFF, gui);
        return gui;
    }

    @Override
    protected Material getValueFromString(String s) {
        return Material.getMaterial(s.toUpperCase());
    }
}
