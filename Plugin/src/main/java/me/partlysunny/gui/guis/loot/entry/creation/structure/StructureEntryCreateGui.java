package me.partlysunny.gui.guis.loot.entry.creation.structure;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.gui.type.util.Gui;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import me.partlysunny.blocks.loot.entry.structure.StructureEntry;
import me.partlysunny.gui.GuiManager;
import me.partlysunny.gui.SelectGuiManager;
import me.partlysunny.gui.guis.loot.entry.creation.EntryCreateGui;
import me.partlysunny.gui.guis.loot.entry.creation.EntrySaveWrapper;
import me.partlysunny.util.Util;
import me.partlysunny.util.classes.ItemBuilder;
import me.partlysunny.worldedit.StructureManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class StructureEntryCreateGui extends EntryCreateGui<StructureEntry> {
    @Override
    public Gui getGui(HumanEntity p) {
        if (!(p instanceof Player player)) return new ChestGui(3, "");
        boolean a = saves.containsKey(player.getUniqueId());
        String b = (String) SelectGuiManager.getValueGui("structure").getValue(player.getUniqueId());
        if (b != null) {
            if (a) {
                EntrySaveWrapper<StructureEntry> plValue = saves.get(player.getUniqueId());
                plValue.entry().setStructure(b);
            } else {
                StructureEntry structure = new StructureEntry();
                structure.setStructure(b);
                saves.put(player.getUniqueId(), new EntrySaveWrapper<>(null, structure));
            }
            SelectGuiManager.getValueGui("structure").resetValue(player.getUniqueId());
        }
        ChestGui gui = new ChestGui(3, ChatColor.GRAY + "Structure Entry Creator");
        StaticPane mainPane = new StaticPane(0, 0, 9, 3);
        mainPane.fillWith(new ItemStack(Material.GRAY_STAINED_GLASS_PANE));
        EntrySaveWrapper<StructureEntry> structureEntry;
        if (a) {
            structureEntry = saves.get(player.getUniqueId());
        } else {
            structureEntry = new EntrySaveWrapper<>(null, new StructureEntry());
        }
        ItemStack xItem = ItemBuilder.builder(Material.PAPER).setName(ChatColor.BLUE + "xOffset").setLore(ChatColor.GRAY + "" + structureEntry.entry().offsetX()).build();
        ItemStack yItem = ItemBuilder.builder(Material.PAPER).setName(ChatColor.BLUE + "yOffset").setLore(ChatColor.GRAY + "" + structureEntry.entry().offsetY()).build();
        ItemStack zItem = ItemBuilder.builder(Material.PAPER).setName(ChatColor.BLUE + "zOffset").setLore(ChatColor.GRAY + "" + structureEntry.entry().offsetZ()).build();
        Util.addTextInputLink(mainPane, player, "structureEntryCreate", ChatColor.RED + "Enter xOffset or \"cancel\" to cancel", xItem, 1, 1, pl -> {
            boolean hasValue = saves.containsKey(pl.getUniqueId());
            Integer currentInput = Util.getTextInputAsInt(pl);
            if (currentInput == null) {
                Util.invalid("Invalid value!", pl);
                return;
            }
            if (hasValue) {
                structureEntry.entry().setOffsetX(currentInput);
            } else {
                saves.put(pl.getUniqueId(), new EntrySaveWrapper<>(null, new StructureEntry()));
            }
        });
        Util.addTextInputLink(mainPane, player, "structureEntryCreate", ChatColor.RED + "Enter yOffset or \"cancel\" to cancel", yItem, 2, 1, pl -> {
            boolean hasValue = saves.containsKey(pl.getUniqueId());
            Integer currentInput = Util.getTextInputAsInt(pl);
            if (currentInput == null) {
                Util.invalid("Invalid value!", pl);
                return;
            }
            if (hasValue) {
                structureEntry.entry().setOffsetY(currentInput);
            } else {
                saves.put(pl.getUniqueId(), new EntrySaveWrapper<>(null, new StructureEntry()));
            }
        });
        Util.addTextInputLink(mainPane, player, "structureEntryCreate", ChatColor.RED + "Enter zOffset or \"cancel\" to cancel", zItem, 3, 1, pl -> {
            boolean hasValue = saves.containsKey(pl.getUniqueId());
            Integer currentInput = Util.getTextInputAsInt(pl);
            if (currentInput == null) {
                Util.invalid("Invalid value!", pl);
                return;
            }
            if (hasValue) {
                structureEntry.entry().setOffsetZ(currentInput);
            } else {
                saves.put(pl.getUniqueId(), new EntrySaveWrapper<>(null, new StructureEntry()));
            }
        });
        mainPane.addItem(new GuiItem(ItemBuilder.builder(Material.BLUE_CONCRETE).setName(ChatColor.BLUE + "Create Structure Entry").build(), item -> {
            EntrySaveWrapper<StructureEntry> save = saves.get(player.getUniqueId());
            if (Util.saveInfo(player, save == null, save.name(), save.entry().getSave(), "lootEntries") && !(StructureManager.getStructure(save.entry().structure()) == null))
                return;
            player.sendMessage(ChatColor.GREEN + "Successfully created structure entry with name " + save.name() + "!");
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
            GuiManager.openInventory(player, "entryManagement");
        }), 7, 1);
        ItemStack structureItem = ItemBuilder.builder(Material.STICK).setName(ChatColor.RED + structureEntry.entry().structure()).setLore(ChatColor.GRAY + "Current structure!").build();
        Util.addSelectionLink(mainPane, player, "structureEntryCreate", "structureSelect", structureItem, 4, 1);
        Util.addRenameButton(mainPane, player, saves, new StructureEntry(), "structureEntryCreate", 6, 1);
        Util.addReturnButton(mainPane, player, "entryCreation", 0, 2);
        gui.addPane(mainPane);
        Util.setClickSoundTo(Sound.BLOCK_METAL_PRESSURE_PLATE_CLICK_OFF, gui);
        return gui;
    }
}
