package me.partlysunny.gui.guis.common;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.gui.type.util.Gui;
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import me.partlysunny.blocks.loot.entry.LootEntryManager;
import me.partlysunny.gui.GuiManager;
import me.partlysunny.gui.SelectGui;
import me.partlysunny.util.Util;
import me.partlysunny.util.classes.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

import static me.partlysunny.util.Util.addPageNav;

public class EntryTypeSelectGui extends SelectGui<String> {
    @Override
    public Gui getGui(HumanEntity p) {
        if (!(p instanceof Player player)) return new ChestGui(3, "");
        ChestGui gui = new ChestGui(5, ChatColor.GRAY + "Select Entry Type");
        PaginatedPane pane = new PaginatedPane(0, 0, 9, 5);
        String[] entryKeys = LootEntryManager.getEntryKeys();
        pane.setOnClick(event -> {
            if (event.getWhoClicked() instanceof Player pp) {
                pp.playSound(pp.getLocation(), Sound.BLOCK_METAL_PRESSURE_PLATE_CLICK_OFF, 1, 1);
            }
            event.setCancelled(true);
        });
        int displaySize = 7 * 3;
        int numPages = (int) Math.ceil(entryKeys.length / (displaySize * 1f));
        if (numPages == 0) {
            numPages = 1;
        }
        int count = 0;
        for (int i = 0; i < numPages; i++) {
            StaticPane border = new StaticPane(0, 0, 9, 5, Pane.Priority.HIGH);
            StaticPane items = new StaticPane(1, 1, 7, 3, Pane.Priority.HIGHEST);
            border.addItem(new GuiItem(ItemBuilder.builder(Material.GREEN_CONCRETE).setName(ChatColor.GREEN + "Manage Entry Types").build(), item -> GuiManager.openInventory(player, "entryManagement")), 1, 0);
            addPageNav(pane, numPages, i, border, gui);
            items.fillWith(ItemBuilder.builder(Material.GRAY_STAINED_GLASS_PANE).setName("").build());
            for (int j = count; j < count + displaySize; j++) {
                if (j > entryKeys.length - 1) {
                    break;
                }
                String itemName = entryKeys[j];
                items.addItem(new GuiItem(ItemBuilder.builder(Material.PAPER).setName(ChatColor.GRAY + itemName).build(), item -> {
                    update(player.getUniqueId(), itemName);
                    returnTo(player);
                }), (j - count) % 7, (j - count) / 7);
            }
            count += displaySize;
            Util.addReturnButton(border, player, getReturnTo(player), 0, 4);
            pane.addPane(i, border);
            pane.addPane(i, items);
        }
        gui.addPane(pane);
        Util.setClickSoundTo(Sound.BLOCK_METAL_PRESSURE_PLATE_CLICK_OFF, gui);
        return gui;
    }

    @Override
    protected String getValueFromString(String s) {
        return s;
    }
}
