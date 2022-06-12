package me.partlysunny.gui.guis.loot;

import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.gui.type.util.Gui;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import me.partlysunny.gui.GuiInstance;
import me.partlysunny.util.Util;
import me.partlysunny.util.classes.ItemBuilder;
import me.partlysunny.util.classes.Pair;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

public class LootMenuGui implements GuiInstance {
    @Override
    public Gui getGui(HumanEntity p) {
        if (!(p instanceof Player player)) return new ChestGui(3, "");
        ChestGui generalSelectionMenu = Util.getGeneralSelectionMenu(ChatColor.GOLD + "Loot", player,
                new Pair<>("entryManagement", ItemBuilder.builder(Material.BOOK).setName(net.md_5.bungee.api.ChatColor.GOLD + "Loot Entries").setLore(net.md_5.bungee.api.ChatColor.GRAY + "Manage your loot entries!").build()),
                new Pair<>("tableManagement", ItemBuilder.builder(Material.BOOKSHELF).setName(net.md_5.bungee.api.ChatColor.YELLOW + "Loot Tables").setLore(net.md_5.bungee.api.ChatColor.GRAY + "Manage your loot tables!").build())
        );
        Util.addReturnButton((StaticPane) generalSelectionMenu.getPanes().get(0), player, "mainPage", 0, 2);
        return generalSelectionMenu;
    }
}
