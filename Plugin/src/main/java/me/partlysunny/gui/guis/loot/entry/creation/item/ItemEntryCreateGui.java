package me.partlysunny.gui.guis.loot.entry.creation.item;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.gui.type.util.Gui;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import me.partlysunny.blocks.loot.entry.item.ItemEntry;
import me.partlysunny.gui.GuiManager;
import me.partlysunny.gui.SelectGuiManager;
import me.partlysunny.gui.SelectGui;
import me.partlysunny.gui.guis.common.material.MaterialSelectGui;
import me.partlysunny.gui.guis.loot.entry.creation.EntryCreateGui;
import me.partlysunny.gui.guis.loot.entry.creation.EntrySaveWrapper;
import me.partlysunny.gui.guis.loot.entry.creation.potion.PotionEntryCreateGui;
import me.partlysunny.util.Util;
import me.partlysunny.util.classes.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class ItemEntryCreateGui extends EntryCreateGui<ItemEntry> {

    @Override
    @SuppressWarnings("unchecked")
    public Gui getGui(HumanEntity p) {
        if (!(p instanceof Player player)) return new ChestGui(3, "");
        UUID pId = player.getUniqueId();
        ChestGui gui = new ChestGui(3, ChatColor.RED + "Item Entry Creator");
        Util.setClickSoundTo(Sound.BLOCK_METAL_PRESSURE_PLATE_CLICK_OFF, gui);
        EntrySaveWrapper<ItemEntry> itemInfo;
        ItemStack createdItem = (ItemStack) SelectGuiManager.getValueGui("itemMaker").getValue(pId);
        if (saves.containsKey(p.getUniqueId())) {
            if (createdItem != null) {
                saves.get(p.getUniqueId()).entry().setItemToDrop(createdItem);
                SelectGuiManager.getValueGui("itemMaker").resetValue(player.getUniqueId());
            }
            itemInfo = saves.get(p.getUniqueId());
        } else {
            EntrySaveWrapper<ItemEntry> value = new EntrySaveWrapper<>(null, new ItemEntry(new ItemStack(Material.WOODEN_AXE), 0, 0));
            if (createdItem != null) {
                value.entry().setItemToDrop(createdItem);
                SelectGuiManager.getValueGui("itemMaker").resetValue(player.getUniqueId());
            }
            saves.put(player.getUniqueId(), value);
            itemInfo = value;
        }
        StaticPane mainPane = new StaticPane(0, 0, 9, 3);
        mainPane.fillWith(new ItemStack(Material.GRAY_STAINED_GLASS_PANE));
        ItemStack item = itemInfo.entry().itemToDrop().clone();
        Util.addEditable(item);
        mainPane.addItem(new GuiItem(item, x -> {
            SelectGuiManager.getValueGui("itemMaker").setReturnTo(p.getUniqueId(), "itemEntryCreate");
            MaterialSelectGui.setFilters(player.getUniqueId(), "meta");
            p.closeInventory();
            ((SelectGui<ItemStack>) SelectGuiManager.getValueGui("itemMaker")).openWithValue(player, itemInfo.entry().itemToDrop(), "itemMakerSelect");
        }), 1, 1);
        ItemStack minItem = ItemBuilder.builder(Material.PAPER).setName(ChatColor.BLUE + "Minimum amount").setLore(ChatColor.GRAY + "" + itemInfo.entry().min()).build();
        Util.addTextInputLink(mainPane, player, "itemEntryCreate", ChatColor.RED + "Enter minimum value or \"cancel\" to cancel", minItem, 2, 1, pl -> {
            boolean hasValue = saves.containsKey(pl.getUniqueId());
            Integer currentInput = Util.getTextInputAsInt(pl);
            if (currentInput == null) {
                Util.invalid("Invalid value!", pl);
                return;
            }
            if (hasValue) {
                itemInfo.entry().setMin(currentInput);
            } else {
                saves.put(pl.getUniqueId(), new EntrySaveWrapper<>(null, new ItemEntry(new ItemStack(Material.WOODEN_AXE), currentInput, 0)));
            }
        });
        ItemStack maxItem = ItemBuilder.builder(Material.PAPER).setName(ChatColor.BLUE + "Maximum amount").setLore(ChatColor.GRAY + "" + itemInfo.entry().max()).build();
        Util.addTextInputLink(mainPane, player, "itemEntryCreate", ChatColor.RED + "Enter maximum value or \"cancel\" to cancel", maxItem, 3, 1, pl -> {
            boolean hasValue = saves.containsKey(pl.getUniqueId());
            Integer currentInput = Util.getTextInputAsInt(pl);
            if (currentInput == null) {
                Util.invalid("Invalid value!", pl);
                return;
            }

            if (hasValue) {
                itemInfo.entry().setMax(currentInput);
            } else {
                saves.put(pl.getUniqueId(), new EntrySaveWrapper<>(null, new ItemEntry(new ItemStack(Material.WOODEN_AXE), 0, currentInput)));
            }
        });
        Util.addRenameButton(mainPane, player, saves, new ItemEntry(new ItemStack(Material.WOODEN_AXE), 0, 0), "itemEntryCreate", 4, 1);
        mainPane.addItem(new GuiItem(ItemBuilder.builder(Material.BLUE_CONCRETE).setName(ChatColor.BLUE + "Create Item Entry").build(), event -> {
            EntrySaveWrapper<ItemEntry> save = saves.get(player.getUniqueId());
            if (Util.saveInfo(player, save == null, save.name(), save.entry().getSave())) return;
            player.sendMessage(ChatColor.GREEN + "Successfully created item entry with name " + save.name() + "!");
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
            GuiManager.openInventory(player, "entryManagement");
        }), 7, 1);
        Util.addReturnButton(mainPane, player, "entryManagement", 0, 2);
        gui.addPane(mainPane);
        return gui;
    }
}
