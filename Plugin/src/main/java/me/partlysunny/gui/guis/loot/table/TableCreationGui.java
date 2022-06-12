package me.partlysunny.gui.guis.loot.table;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.gui.type.util.Gui;
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import me.partlysunny.blocks.loot.CustomLootTable;
import me.partlysunny.blocks.loot.TableEntryWrapper;
import me.partlysunny.gui.GuiInstance;
import me.partlysunny.gui.GuiManager;
import me.partlysunny.gui.SelectGui;
import me.partlysunny.gui.SelectGuiManager;
import me.partlysunny.gui.textInput.ChatListener;
import me.partlysunny.util.Util;
import me.partlysunny.util.classes.ItemBuilder;
import me.partlysunny.util.classes.RandomList;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

import static me.partlysunny.util.Util.addPageNav;

public class TableCreationGui implements GuiInstance {

    private static final Map<UUID, TableSaveWrapper> tableSaves = new HashMap<>();

    public static void openWithValue(Player p, TableSaveWrapper value) {
        tableSaves.put(p.getUniqueId(), value);
        GuiManager.openInventory(p, "tableCreation");
    }

    @Override
    public Gui getGui(HumanEntity p) {
        if (!(p instanceof Player player)) return new ChestGui(3, "");
        UUID pId = player.getUniqueId();
        boolean a = tableSaves.containsKey(pId);
        TableEntryWrapper b = (TableEntryWrapper) SelectGuiManager.getValueGui("tableEntry").getValue(player.getUniqueId());
        if (b != null) {
            if (a) {
                TableSaveWrapper plValue = tableSaves.get(player.getUniqueId());
                plValue.table().getEntries().add(b, b.weight());
            } else {
                tableSaves.put(player.getUniqueId(), new TableSaveWrapper(null, new CustomLootTable(0, new ArrayList<>(List.of(b)))));
            }
            SelectGuiManager.getValueGui("tableEntry").resetValue(player.getUniqueId());
        }
        TableSaveWrapper tableInfo = tableSaves.getOrDefault(pId, new TableSaveWrapper(null, new CustomLootTable(0, new ArrayList<>(List.of()))));
        RandomList<TableEntryWrapper> values = tableInfo.table().getEntries();
        ChestGui gui = new ChestGui(5, "Create Loot Table");
        PaginatedPane pane = new PaginatedPane(0, 0, 9, 5);
        int numPages = (int) Math.ceil(values.size() / 21f);
        if (numPages == 0) {
            numPages = 1;
        }
        int count = 0;
        for (int i = 0; i < numPages; i++) {
            StaticPane border = new StaticPane(0, 0, 9, 5);
            StaticPane items = new StaticPane(1, 1, 7, 3);
            addPageNav(pane, numPages, i, border, gui);
            border.addItem(new GuiItem(ItemBuilder.builder(Material.GREEN_CONCRETE).setName(ChatColor.GREEN + "Add new").build(), item -> GuiManager.openInventory(player, "tableEntrySelect")), 1, 0);
            border.addItem(new GuiItem(ItemBuilder.builder(Material.YELLOW_CONCRETE).setName(ChatColor.GOLD + "Reload").build(), item -> GuiManager.openInventory(player, "tableCreation")), 2, 0);
            border.addItem(new GuiItem(ItemBuilder.builder(Material.ACACIA_SIGN).setName(ChatColor.RED + "Rename").setLore(ChatColor.GRAY + "Current name: " + tableInfo.name()).build(), event -> {
                ChatListener.startChatListen(player, "tableCreation", ChatColor.RED + "Enter new name!", pl -> {
                    String input = ChatListener.getCurrentInput(pl);
                    if (input.length() < 2 || input.length() > 30) {
                        Util.invalid("Characters must be at least 2 and at most 29!", pl);
                        return;
                    }
                    if (!Util.isValidFilePath(input)) {
                        Util.invalid("Invalid File Name!", pl);
                        return;
                    }
                    if (!tableSaves.containsKey(pl.getUniqueId())) {
                        tableSaves.put(pl.getUniqueId(), new TableSaveWrapper(null, new CustomLootTable(0, new ArrayList<>())));
                    }
                    tableSaves.get(pl.getUniqueId()).setName(input);
                });
                player.closeInventory();
            }), 3, 0);
            Util.addTextInputLink(border, player, "tableCreation", ChatColor.RED + "Enter rolls amount or \"cancel\" to cancel", Util.getInfoItem("Rolls", String.valueOf(tableInfo.table().rolls())), 4, 0, pl -> {
                boolean hasValue = tableSaves.containsKey(pl.getUniqueId());
                Integer currentInput = Util.getTextInputAsInt(pl);
                if (currentInput == null) {
                    Util.invalid("Invalid value!", pl);
                    return;
                }
                if (hasValue) {
                    tableSaves.get(pl.getUniqueId()).table().setRolls(currentInput);
                } else {
                    TableSaveWrapper value = new TableSaveWrapper(null, new CustomLootTable(currentInput, new ArrayList<>()));
                    tableSaves.put(pl.getUniqueId(), value);
                }
            });
            items.fillWith(ItemBuilder.builder(Material.GRAY_STAINED_GLASS_PANE).setName("").build());
            for (int j = count; j < count + 27; j++) {
                if (j > values.size() - 1) {
                    break;
                }
                TableEntryWrapper entry = values.get(j).getObject();
                ItemStack build = ItemBuilder.builder(Material.PAPER).setName(ChatColor.GRAY + entry.entry()).setLore(ChatColor.GRAY + "Message: " + Util.processText(entry.message()), ChatColor.GRAY + "Weight: " + entry.weight()).build();
                Util.addLoreLine(build, ChatColor.GREEN + "Click to open with this value!");
                Util.addLoreLine(build, ChatColor.RED + "Right click to delete!");
                items.addItem(new GuiItem(build, event -> {
                    if (event.isLeftClick()) {
                        SelectGui<TableEntryWrapper> tableEntry = (SelectGui<TableEntryWrapper>) SelectGuiManager.getValueGui("tableEntry");
                        tableEntry.setReturnTo(pId, "tableCreation");
                        tableEntry.openWithValue(player, entry, "tableEntrySelect");
                    } else if (event.isRightClick()) {
                        tableInfo.table().removeEntry(entry);
                        GuiManager.openInventory(player, "tableCreation");
                    }
                }), (j - count) % 7, (j - count) / 7);
            }
            border.addItem(new GuiItem(ItemBuilder.builder(Material.BLUE_CONCRETE).setName(ChatColor.BLUE + "Create Loot Table").build(), item -> {
                TableSaveWrapper save = tableSaves.get(player.getUniqueId());
                if (Util.saveInfo(player, save == null || save.name() == null, save.name(), save.table().getSave(), "lootTables"))
                    return;
                player.sendMessage(ChatColor.GREEN + "Successfully created loot table with name " + save.name() + "!");
                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
                GuiManager.openInventory(player, "tableManagement");
            }), 8, 2);
            count += 27;
            Util.addReturnButton(border, player, "tableManagement", 0, 4);
            pane.addPane(i, border);
            pane.addPane(i, items);
        }
        gui.addPane(pane);
        Util.setClickSoundTo(Sound.BLOCK_METAL_PRESSURE_PLATE_CLICK_OFF, gui);
        return gui;
    }
}
