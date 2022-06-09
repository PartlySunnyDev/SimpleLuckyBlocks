package me.partlysunny.gui.guis.common.material;

import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.gui.type.util.Gui;
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane;
import me.partlysunny.gui.SelectGui;
import me.partlysunny.gui.guis.common.material.filters.FilterManager;
import me.partlysunny.gui.guis.common.material.filters.MaterialFilter;
import me.partlysunny.util.Util;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

import java.util.*;

public class MaterialSelectGui extends SelectGui<Material> {

    private static final Map<UUID, List<MaterialFilter>> filters = new HashMap<>();

    public static void setFilters(UUID player, String... newFilters) {
        List<MaterialFilter> f = new ArrayList<>();
        for (String s : newFilters) {
            f.add(FilterManager.getFilter(s));
        }
        filters.put(player, f);
    }

    @Override
    public Gui getGui(HumanEntity p) {
        if (!(p instanceof Player player)) return new ChestGui(3, "");
        ChestGui gui = new ChestGui(5, ChatColor.GRAY + "Select Material");
        PaginatedPane pane = new PaginatedPane(0, 0, 9, 5);
        List<String> list = new ArrayList<>();
        for (Material m : Material.values()) {
            if (!filters.containsKey(player.getUniqueId())) {
                list.add(m.toString());
            } else {
                boolean q = true;
                for (MaterialFilter f : filters.get(player.getUniqueId())) {
                    if (!f.doesQualify(m)) {
                        q = false;
                    }
                }
                if (!q) {
                    continue;
                }
                list.add(m.toString());
            }
        }
        String[] entityList = list.toArray(new String[0]);
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
