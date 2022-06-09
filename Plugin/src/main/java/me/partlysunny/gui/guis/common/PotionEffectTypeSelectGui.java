package me.partlysunny.gui.guis.common;

import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.gui.type.util.Gui;
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane;
import me.partlysunny.gui.SelectGui;
import me.partlysunny.util.Util;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

public class PotionEffectTypeSelectGui extends SelectGui<PotionEffectType> {
    @Override
    public Gui getGui(HumanEntity p) {
        if (!(p instanceof Player player)) return new ChestGui(3, "");
        ChestGui gui = new ChestGui(5, ChatColor.GRAY + "Select Potion Effect Type");
        PaginatedPane pane = new PaginatedPane(0, 0, 9, 5);
        String[] potionEffectList = new String[PotionEffectType.values().length];
        int count = 0;
        for (PotionEffectType e : PotionEffectType.values()) {
            potionEffectList[count] = e.getKey().getKey();
            count++;
        }
        Util.addListPages(pane, player, this, 1, 1, 7, 3, Util.getAlphabetSorted(potionEffectList), gui);
        gui.addPane(pane);
        Util.setClickSoundTo(Sound.BLOCK_METAL_PRESSURE_PLATE_CLICK_OFF, gui);
        return gui;
    }

    @Override
    protected PotionEffectType getValueFromString(String s) {
        return PotionEffectType.getByKey(NamespacedKey.minecraft(s.toLowerCase()));
    }
}
