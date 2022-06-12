package me.partlysunny.gui.guis.common;

import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.gui.type.util.Gui;
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane;
import me.partlysunny.blocks.loot.entry.LootEntryManager;
import me.partlysunny.gui.SelectGui;
import me.partlysunny.util.Util;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

public class EntryTypeSelectGui extends SelectGui<String> {
    @Override
    public Gui getGui(HumanEntity p) {
        if (!(p instanceof Player player)) return new ChestGui(3, "");
        ChestGui gui = new ChestGui(5, ChatColor.GRAY + "Select Wand Type");
        PaginatedPane pane = new PaginatedPane(0, 0, 9, 5);
        String[] entryKeys = LootEntryManager.getEntryKeys();
        Util.addListPages(pane, player, this, 1, 1, 7, 3, Util.getAlphabetSorted(entryKeys), gui);
        gui.addPane(pane);
        Util.setClickSoundTo(Sound.BLOCK_METAL_PRESSURE_PLATE_CLICK_OFF, gui);
        return gui;
    }

    @Override
    protected String getValueFromString(String s) {
        return s;
    }
}
