package me.partlysunny.gui.guis.loot.table;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.gui.type.util.Gui;
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import me.partlysunny.SimpleLuckyBlocksCore;
import me.partlysunny.blocks.loot.LootTableManager;
import me.partlysunny.gui.GuiInstance;
import me.partlysunny.gui.GuiManager;
import me.partlysunny.util.Util;
import me.partlysunny.util.classes.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

import static me.partlysunny.util.Util.addPageNav;
import static me.partlysunny.util.Util.deleteFile;

public class TableManagementGui implements GuiInstance {
    @Override
    public Gui getGui(HumanEntity p) {
        if (!(p instanceof Player player)) return new ChestGui(3, "");
        SimpleLuckyBlocksCore.reload();
        String[] values = LootTableManager.getEntryKeys();
        JavaPlugin plugin = JavaPlugin.getPlugin(SimpleLuckyBlocksCore.class);
        ChestGui gui = new ChestGui(5, "Manage Loot Tables");
        PaginatedPane pane = new PaginatedPane(0, 0, 9, 5);
        int numPages = (int) Math.ceil(values.length / 21f);
        if (numPages == 0) {
            numPages = 1;
        }
        int count = 0;
        for (int i = 0; i < numPages; i++) {
            StaticPane border = new StaticPane(0, 0, 9, 5);
            StaticPane items = new StaticPane(1, 1, 7, 3);
            addPageNav(pane, numPages, i, border, gui);
            border.addItem(new GuiItem(ItemBuilder.builder(Material.GREEN_CONCRETE).setName(ChatColor.GREEN + "Create new table").build(), item -> GuiManager.openInventory(player, "tableCreation")), 1, 0);
            border.addItem(new GuiItem(ItemBuilder.builder(Material.YELLOW_CONCRETE).setName(ChatColor.GOLD + "Reload").build(), item -> GuiManager.openInventory(player, "tableManagement")), 2, 0);
            items.fillWith(ItemBuilder.builder(Material.GRAY_STAINED_GLASS_PANE).setName("").build());
            for (int j = count; j < count + 27; j++) {
                if (j > values.length - 1) {
                    break;
                }
                String name = values[j];
                ItemStack build = ItemBuilder.builder(Material.PAPER).setName(ChatColor.GRAY + name).build();
                Util.addLoreLine(build, ChatColor.GREEN + "Click to open with this value!");
                Util.addLoreLine(build, ChatColor.RED + "Right click to delete!");
                items.addItem(new GuiItem(build, event -> {
                    if (event.isLeftClick()) {
                        TableCreationGui.openWithValue(player, new TableSaveWrapper(name, LootTableManager.getTable(name)));
                    } else if (event.isRightClick()) {
                        deleteFile(new File(plugin.getDataFolder() + "/lootTables", name + ".yml"));
                        GuiManager.openInventory(player, "tableManagement");
                    }
                }), (j - count) % 7, (j - count) / 7);
            }
            count += 27;
            Util.addReturnButton(border, player, "lootMenu", 0, 4);
            pane.addPane(i, border);
            pane.addPane(i, items);
        }
        gui.addPane(pane);
        Util.setClickSoundTo(Sound.BLOCK_METAL_PRESSURE_PLATE_CLICK_OFF, gui);
        return gui;
    }
}
