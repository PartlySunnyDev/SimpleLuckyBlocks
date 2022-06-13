package me.partlysunny.gui.guis.loot.entry.creation.command;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.gui.type.util.Gui;
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import me.partlysunny.blocks.loot.entry.command.CommandEntry;
import me.partlysunny.gui.GuiManager;
import me.partlysunny.gui.guis.loot.entry.creation.EntryCreateGui;
import me.partlysunny.gui.guis.loot.entry.creation.EntrySaveWrapper;
import me.partlysunny.gui.textInput.ChatListener;
import me.partlysunny.util.Util;
import me.partlysunny.util.classes.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class CommandEntryCreateGui extends EntryCreateGui<CommandEntry> {

    private void addNewCommandFromInput(Player pl) {
        boolean hasValue = saves.containsKey(pl.getUniqueId());
        String currentInput = ChatListener.getCurrentInput(pl);
        if (currentInput == null) {
            Util.invalid("Invalid value!", pl);
            return;
        }
        if (hasValue) {
            saves.get(pl.getUniqueId()).entry().addCommand(currentInput);
        } else {
            saves.put(pl.getUniqueId(), new EntrySaveWrapper<>(null, new CommandEntry(new ArrayList<>(List.of(currentInput)))));
        }
    }

    @Override
    public Gui getGui(HumanEntity p) {
        if (!(p instanceof Player player)) return new ChestGui(3, "");
        EntrySaveWrapper<CommandEntry> commandEntry = saves.getOrDefault(player.getUniqueId(), new EntrySaveWrapper<>(null, new CommandEntry(new ArrayList<>())));
        ChestGui gui = new ChestGui(5, ChatColor.RED + "Command Entry Creator");
        Util.setClickSoundTo(Sound.BLOCK_METAL_PRESSURE_PLATE_CLICK_OFF, gui);
        PaginatedPane pane = new PaginatedPane(0, 0, 9, 5);
        int displaySize = 21;
        String[] a = commandEntry.entry().getCommands();
        int numPages = (int) Math.ceil(a.length / (displaySize * 1f));
        if (numPages == 0) {
            numPages = 1;
        }
        int count = 0;
        for (int i = 0; i < numPages; i++) {
            StaticPane border = new StaticPane(0, 0, 9, 5);
            StaticPane items = new StaticPane(1, 1, 7, 3);
            border.fillWith(new ItemStack(Material.BLACK_STAINED_GLASS_PANE));
            Util.addTextInputLink(border, player, "commandEntryCreate", ChatColor.RED + "Enter command", ItemBuilder.builder(Material.GREEN_CONCRETE).setName(ChatColor.GREEN + "Add new command").build(), 1, 0, this::addNewCommandFromInput);
            border.addItem(new GuiItem(ItemBuilder.builder(Material.YELLOW_CONCRETE).setName(ChatColor.GOLD + "Reload").build(), item -> GuiManager.openInventory(player, "commandEntryCreate")), 2, 0);
            Util.addRenameButton(border, player, saves, new EntrySaveWrapper<>(null, new CommandEntry(new ArrayList<>(List.of()))), "commandEntryCreate", 3, 0);
            border.addItem(new GuiItem(ItemBuilder.builder(Material.BLUE_CONCRETE).setName(ChatColor.BLUE + "Create Entry").build(), item -> {
                EntrySaveWrapper<CommandEntry> save = saves.get(player.getUniqueId());
                if (Util.saveInfo(player, save == null || save.entry().commands().size() < 1, save.name(), save.entry().getSave(), "lootEntries"))
                    return;
                player.sendMessage(ChatColor.GREEN + "Successfully created command entry with name " + save.name() + "!");
                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
                saves.remove(player.getUniqueId());
                GuiManager.openInventory(player, "entryManagement");
            }), 8, 2);
            items.fillWith(ItemBuilder.builder(Material.GRAY_STAINED_GLASS_PANE).setName("").build());
            for (int j = count; j < count + displaySize; j++) {
                if (j > a.length - 1) {
                    break;
                }
                String command = a[j];
                ItemStack commandItem = ItemBuilder.builder(Material.PAPER).setName(ChatColor.GRAY + "" + (j + 1)).setLore(Util.splitLoreForLine(command).toArray(new String[0])).build();
                Util.addLoreLine(commandItem, ChatColor.RED + "Right click to delete!");
                Util.addEditable(commandItem);
                items.addItem(new GuiItem(commandItem, item -> {
                    if (item.isRightClick()) {
                        commandEntry.entry().removeCommand(command);
                        GuiManager.openInventory(player, "commandEntryCreate");
                    }
                    if (item.isLeftClick()) {
                        commandEntry.entry().removeCommand(command);
                        player.closeInventory();
                        ChatListener.startChatListen(player, "commandEntryCreate", ChatColor.RED + "Enter command", this::addNewCommandFromInput);
                    }
                }), (j - count) % 7, (j - count) / 7);
            }
            count += displaySize;
            Util.addReturnButton(border, player, "entryCreation", 0, 4);
            pane.addPane(i, border);
            pane.addPane(i, items);
        }
        gui.addPane(pane);
        return gui;
    }
}
