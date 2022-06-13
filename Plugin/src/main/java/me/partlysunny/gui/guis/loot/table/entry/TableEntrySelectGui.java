package me.partlysunny.gui.guis.loot.table.entry;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.gui.type.util.Gui;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import me.partlysunny.blocks.loot.TableEntryWrapper;
import me.partlysunny.blocks.loot.entry.LootEntryManager;
import me.partlysunny.gui.GuiManager;
import me.partlysunny.gui.SelectGui;
import me.partlysunny.gui.textInput.ChatListener;
import me.partlysunny.util.Util;
import me.partlysunny.util.classes.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class TableEntrySelectGui extends SelectGui<TableEntryWrapper> {
    @Override
    public Gui getGui(HumanEntity p) {
        if (!(p instanceof Player player)) return new ChestGui(3, "");
        Util.handleSelectInput("entryType", player, values, new TableEntryWrapper(), String.class, TableEntryWrapper::setEntry);
        ChestGui gui = new ChestGui(3, ChatColor.GRAY + "Loot Table Entry Creator");
        StaticPane mainPane = new StaticPane(0, 0, 9, 3);
        mainPane.fillWith(new ItemStack(Material.GRAY_STAINED_GLASS_PANE));
        TableEntryWrapper entryWrapper = values.getOrDefault(player.getUniqueId(), new TableEntryWrapper());
        ItemStack entryItem = Util.getInfoItem("Entry Type", entryWrapper.entry());
        ItemStack messageItem = Util.getInfoItem("Message", entryWrapper.message());
        ItemStack weightItem = Util.getInfoItem("Weight", String.valueOf(entryWrapper.weight()));
        Util.addTextInputLink(mainPane, player, "tableEntrySelect", ChatColor.RED + "Enter new message or \"cancel\" to cancel", messageItem, 1, 1, pl -> {
            boolean hasValue = values.containsKey(pl.getUniqueId());
            String currentInput = ChatListener.getCurrentInput(pl);
            if (currentInput == null) {
                Util.invalid("Invalid value!", pl);
                return;
            }
            if (hasValue) {
                entryWrapper.setMessage(currentInput);
            } else {
                TableEntryWrapper value = new TableEntryWrapper();
                value.setMessage(currentInput);
                values.put(pl.getUniqueId(), value);
            }
        });
        Util.addTextInputLink(mainPane, player, "tableEntrySelect", ChatColor.RED + "Enter weight or \"cancel\" to cancel", weightItem, 3, 1, pl -> {
            boolean hasValue = values.containsKey(pl.getUniqueId());
            Integer currentInput = Util.getTextInputAsInt(pl);
            if (currentInput == null) {
                Util.invalid("Invalid value!", pl);
                return;
            }
            if (hasValue) {
                entryWrapper.setWeight(currentInput);
            } else {
                TableEntryWrapper value = new TableEntryWrapper();
                value.setWeight(currentInput);
                values.put(pl.getUniqueId(), value);
            }
        });
        Util.addSelectionLink(mainPane, player, "tableEntrySelect", "entryTypeSelect", entryItem, 5, 1);
        mainPane.addItem(new GuiItem(ItemBuilder.builder(Material.GREEN_CONCRETE).setName(ChatColor.GREEN + "Submit").build(), event -> {
            if (entryWrapper.weight() < 1) {
                Util.invalid("Weight must not be 0!", player);
                return;
            }
            if (LootEntryManager.getEntry(entryWrapper.entry()) == null) {
                Util.invalid("Select an entry type!", player);
                return;
            }
            player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
            GuiManager.openInventory(player, "tableCreation");
        }), 7, 1);
        mainPane.addItem(new GuiItem(ItemBuilder.builder(Material.ARROW).setName(ChatColor.GREEN + "Back").build(), item -> {
            values.remove(player.getUniqueId());
            GuiManager.openInventory(player, "tableCreation");
        }), 0, 2);
        gui.addPane(mainPane);
        Util.setClickSoundTo(Sound.BLOCK_METAL_PRESSURE_PLATE_CLICK_OFF, gui);
        return gui;
    }

    @Override
    protected TableEntryWrapper getValueFromString(String s) {
        return new TableEntryWrapper("", 0, "");
    }
}
