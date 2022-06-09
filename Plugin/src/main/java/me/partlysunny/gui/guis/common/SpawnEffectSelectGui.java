package me.partlysunny.gui.guis.common;

import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.gui.type.util.Gui;
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane;
import me.partlysunny.blocks.loot.entry.mob.SpawnEffect;
import me.partlysunny.gui.SelectGui;
import me.partlysunny.util.Util;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

public class SpawnEffectSelectGui extends SelectGui<SpawnEffect> {
    @Override
    public Gui getGui(HumanEntity p) {
        if (!(p instanceof Player player)) return new ChestGui(3, "");
        ChestGui gui = new ChestGui(5, ChatColor.GRAY + "Select Spawn Effect Type");
        PaginatedPane pane = new PaginatedPane(0, 0, 9, 5);
        String[] spawnEffectList = new String[SpawnEffect.values().length];
        int count = 0;
        for (SpawnEffect e : SpawnEffect.values()) {
            spawnEffectList[count] = e.toString().toLowerCase();
            count++;
        }
        Util.addListPages(pane, player, this, 1, 1, 7, 3, Util.getAlphabetSorted(spawnEffectList), gui);
        gui.addPane(pane);
        Util.setClickSoundTo(Sound.BLOCK_METAL_PRESSURE_PLATE_CLICK_OFF, gui);
        return gui;
    }

    @Override
    protected SpawnEffect getValueFromString(String s) {
        return SpawnEffect.valueOf(s.toUpperCase());
    }
}
