package me.partlysunny.gui.guis.loot.entry.creation.command;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.gui.type.util.Gui;
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import me.partlysunny.SimpleLuckyBlocksCore;
import me.partlysunny.gui.GuiInstance;
import me.partlysunny.gui.GuiManager;
import me.partlysunny.gui.textInput.ChatListener;
import me.partlysunny.util.Util;
import me.partlysunny.util.classes.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class CommandEntryCreateGui implements GuiInstance {

    private static final Map<UUID, CommandInfo> commandSaves = new HashMap<>();

    private static void addNewCommandFromInput(Player pl) {
        boolean hasValue = commandSaves.containsKey(pl.getUniqueId());
        String currentInput = ChatListener.getCurrentInput(pl);
        if (currentInput == null) {
            Util.invalid("Invalid value!", pl);
            return;
        }
        if (hasValue) {
            commandSaves.get(pl.getUniqueId()).addCommand(currentInput);
        } else {
            commandSaves.put(pl.getUniqueId(), new CommandInfo(new ArrayList<>(List.of(currentInput))));
        }
    }

    @Override
    public Gui getGui(HumanEntity p) {
        if (!(p instanceof Player player)) return new ChestGui(3, "");
        CommandInfo commandEntry;
        if (commandSaves.containsKey(p.getUniqueId())) {
            commandEntry = commandSaves.get(p.getUniqueId());
        } else {
            CommandInfo value = new CommandInfo(new ArrayList<>());
            commandSaves.put(player.getUniqueId(), value);
            commandEntry = value;
        }
        ChestGui gui = new ChestGui(5, ChatColor.RED + "Command Entry Creator");
        Util.setClickSoundTo(Sound.BLOCK_METAL_PRESSURE_PLATE_CLICK_OFF, gui);
        PaginatedPane pane = new PaginatedPane(0, 0, 9, 5);
        int displaySize = 21;
        String[] a = commandEntry.commands().toArray(new String[0]);
        int numPages = (int) Math.ceil(a.length / (displaySize * 1f));
        if (numPages == 0) {
            numPages = 1;
        }
        int count = 0;
        for (int i = 0; i < numPages; i++) {
            StaticPane border = new StaticPane(0, 0, 9, 5);
            StaticPane items = new StaticPane(1, 1, 7, 3);
            border.fillWith(new ItemStack(Material.BLACK_STAINED_GLASS_PANE));
            Util.addTextInputLink(border, player, "commandEntryCreate", ChatColor.RED + "Enter command", ItemBuilder.builder(Material.GREEN_CONCRETE).setName(ChatColor.GREEN + "Add new command").build(), 1, 0, CommandEntryCreateGui::addNewCommandFromInput);
            border.addItem(new GuiItem(ItemBuilder.builder(Material.YELLOW_CONCRETE).setName(ChatColor.GOLD + "Reload").build(), item -> GuiManager.openInventory(player, "commandEntryCreate")), 2, 0);
            border.addItem(new GuiItem(ItemBuilder.builder(Material.ACACIA_SIGN).setName(ChatColor.RED + "Rename").setLore(ChatColor.GRAY + "Current name: " + commandEntry.name()).build(), item -> {
                ChatListener.startChatListen(player, "commandEntryCreate", ChatColor.RED + "Enter new name!", pl -> {
                    String input = ChatListener.getCurrentInput(pl);
                    if (input.length() < 2 || input.length() > 30) {
                        Util.invalid("Characters must be at least 2 and at most 29!", pl);
                        return;
                    }
                    if (!commandSaves.containsKey(pl.getUniqueId())) {
                        commandSaves.put(pl.getUniqueId(), new CommandInfo(new ArrayList<>()));
                    }
                    commandSaves.get(pl.getUniqueId()).setName(input);
                });
                player.closeInventory();
            }), 3, 0);
            border.addItem(new GuiItem(ItemBuilder.builder(Material.BLUE_CONCRETE).setName(ChatColor.BLUE + "Create Entry").build(), item -> {
                CommandInfo save = commandSaves.get(player.getUniqueId());
                if (save == null || save.commands().size() < 1) {
                    Util.invalid("Invalid info!", player);
                    return;
                }
                YamlConfiguration config = save.getSave();
                try {
                    config.save(new File(JavaPlugin.getPlugin(SimpleLuckyBlocksCore.class).getDataFolder() + "/lootEntries", save.name() + ".yml"));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                player.sendMessage(ChatColor.GREEN + "Successfully created command entry with name " + save.name() + "!");
                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
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
                Util.addLoreLine(commandItem, ChatColor.GREEN + "Left click to edit!");
                items.addItem(new GuiItem(commandItem, item -> {
                    if (item.isRightClick()) {
                        commandEntry.removeCommand(command);
                        GuiManager.openInventory(player, "commandEntryCreate");
                    }
                    if (item.isLeftClick()) {
                        commandEntry.removeCommand(command);
                        player.closeInventory();
                        ChatListener.startChatListen(player, "commandEntryCreate", ChatColor.RED + "Enter command", CommandEntryCreateGui::addNewCommandFromInput);
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
