package me.partlysunny.gui.guis.loot.entry;

import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.gui.type.util.Gui;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import me.partlysunny.blocks.loot.entry.EntryType;
import me.partlysunny.gui.GuiInstance;
import me.partlysunny.util.Util;
import me.partlysunny.util.classes.Pair;
import org.bukkit.ChatColor;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class EntryCreationGui implements GuiInstance {
    @Override
    public Gui getGui(HumanEntity p) {
        if (!(p instanceof Player player)) return new ChestGui(3, "");
        Pair<String, ItemStack>[] a = new Pair[EntryType.values().length];
        int count = 0;
        for (EntryType t : EntryType.values()) {
            a[count] = new Pair<>(t.id() + "EntryCreate", t.displayItem());
            count++;
        }
        ChestGui generalSelectionMenu = Util.getGeneralSelectionMenu(ChatColor.GRAY + "Select entry type!", player, a);
        Util.addReturnButton((StaticPane) generalSelectionMenu.getPanes().get(0), player, "lootEntriesPage", 0, 2);
        return generalSelectionMenu;
    }
}
