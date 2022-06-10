package me.partlysunny.gui.guis.common;

import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.gui.type.util.Gui;
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane;
import me.partlysunny.gui.SelectGui;
import me.partlysunny.util.Util;
import me.partlysunny.worldedit.StructureManager;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

public class StructureSelectGui extends SelectGui<String> {

    @Override
    public Gui getGui(HumanEntity p) {
        if (!(p instanceof Player player)) return new ChestGui(3, "");
        ChestGui gui = new ChestGui(5, ChatColor.GRAY + "Select Structure Type");
        PaginatedPane pane = new PaginatedPane(0, 0, 9, 5);
        String[] wandType = StructureManager.structureKeys().toArray(new String[0]);
        Util.addListPages(pane, player, this, 1, 1, 7, 3, Util.getAlphabetSorted(wandType), gui);
        gui.addPane(pane);
        Util.setClickSoundTo(Sound.BLOCK_METAL_PRESSURE_PLATE_CLICK_OFF, gui);
        return gui;
    }

    @Override
    protected String getValueFromString(String s) {
        return s;
    }

}
