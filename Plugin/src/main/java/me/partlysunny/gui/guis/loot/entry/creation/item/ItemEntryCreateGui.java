package me.partlysunny.gui.guis.loot.entry.creation.item;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.gui.type.util.Gui;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import me.partlysunny.SimpleLuckyBlocksCore;
import me.partlysunny.gui.GuiInstance;
import me.partlysunny.gui.GuiManager;
import me.partlysunny.gui.guis.common.ValueGuiManager;
import me.partlysunny.gui.guis.common.ValueReturnGui;
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
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ItemEntryCreateGui implements GuiInstance {

    private static final Map<UUID, ItemInfo> itemSaves = new HashMap<>();

    @Override
    public Gui getGui(HumanEntity p) {
        if (!(p instanceof Player player)) return new ChestGui(3, "");
        UUID pId = player.getUniqueId();
        ChestGui gui = new ChestGui(3, ChatColor.RED + "Item Entry Creator");
        Util.setClickSoundTo(Sound.BLOCK_METAL_PRESSURE_PLATE_CLICK_OFF, gui);
        ItemInfo itemInfo;
        ItemStack createdItem = (ItemStack) ValueGuiManager.getValueGui("itemMaker").getValue(pId);
        if (itemSaves.containsKey(p.getUniqueId())) {
            if (createdItem != null) {
                itemSaves.get(p.getUniqueId()).setItem(createdItem);
                ValueGuiManager.getValueGui("itemMaker").resetValue(player.getUniqueId());
            }
            itemInfo = itemSaves.get(p.getUniqueId());
        } else {
            ItemInfo value = new ItemInfo(new ItemStack(Material.WOODEN_AXE), 0, 0);
            if (createdItem != null) {
                value.setItem(createdItem);
                ValueGuiManager.getValueGui("itemMaker").resetValue(player.getUniqueId());
            }
            itemSaves.put(player.getUniqueId(), value);
            itemInfo = value;
        }
        StaticPane mainPane = new StaticPane(0, 0, 9, 3);
        mainPane.fillWith(new ItemStack(Material.GRAY_STAINED_GLASS_PANE));
        ItemStack item = itemInfo.item().clone();
        Util.addLoreLine(item, ChatColor.GREEN + "Click to edit!");
        mainPane.addItem(new GuiItem(item, x -> {
            ValueGuiManager.getValueGui("itemMaker").setReturnTo(p.getUniqueId(), "itemEntryCreate");
            p.closeInventory();
            ((ValueReturnGui<ItemStack>) ValueGuiManager.getValueGui("itemMaker")).openWithValue(player, itemInfo.item(), "itemMakerSelect");
        }), 1, 1);
        ItemStack minItem = ItemBuilder.builder(Material.PAPER).setName(ChatColor.BLUE + "Minimum amount").setLore(ChatColor.GRAY + "" + itemInfo.min()).build();
        Util.addTextInputLink(mainPane, player, "itemEntryCreate", ChatColor.RED + "Enter minimum value or \"cancel\" to cancel", minItem, 2, 1, pl -> {
            boolean hasValue = itemSaves.containsKey(pl.getUniqueId());
            Integer currentInput = Util.getTextInputAsInt(pl);
            if (currentInput == null) {
                Util.invalid("Invalid value!", pl);
                return;
            }
            if (hasValue) {
                itemInfo.setMin(currentInput);
            } else {
                itemSaves.put(pl.getUniqueId(), new ItemInfo(new ItemStack(Material.WOODEN_AXE), currentInput, 0));
            }
        });
        ItemStack maxItem = ItemBuilder.builder(Material.PAPER).setName(ChatColor.BLUE + "Maximum amount").setLore(ChatColor.GRAY + "" + itemInfo.max()).build();
        Util.addTextInputLink(mainPane, player, "itemEntryCreate", ChatColor.RED + "Enter maximum value or \"cancel\" to cancel", maxItem, 3, 1, pl -> {
            boolean hasValue = itemSaves.containsKey(pl.getUniqueId());
            Integer currentInput = Util.getTextInputAsInt(pl);
            if (currentInput == null) {
                Util.invalid("Invalid value!", pl);
                return;
            }
            if (hasValue) {
                itemInfo.setMax(currentInput);
            } else {
                itemSaves.put(pl.getUniqueId(), new ItemInfo(new ItemStack(Material.WOODEN_AXE), 0, currentInput));
            }
        });
        mainPane.addItem(new GuiItem(ItemBuilder.builder(Material.ACACIA_SIGN).setName(ChatColor.RED + "Rename").setLore(ChatColor.GRAY + "Current name: " + itemInfo.name()).build(), event -> {
            ChatListener.startChatListen(player, "itemEntryCreate", ChatColor.RED + "Enter new name!", pl -> {
                String input = ChatListener.getCurrentInput(pl);
                if (input.length() < 2 || input.length() > 30) {
                    Util.invalid("Characters must be at least 2 and at most 29!", pl);
                    return;
                }
                if (!itemSaves.containsKey(pl.getUniqueId())) {
                    itemSaves.put(pl.getUniqueId(), new ItemInfo(new ItemStack(Material.WOODEN_AXE), 0, 0));
                }
                itemSaves.get(pl.getUniqueId()).setName(input);
            });
            player.closeInventory();
        }), 4, 1);
        mainPane.addItem(new GuiItem(ItemBuilder.builder(Material.BLUE_CONCRETE).setName(ChatColor.BLUE + "Create Item Entry").build(), event -> {
            ItemInfo save = itemSaves.get(player.getUniqueId());
            if (save == null || save.min() > save.max()) {
                Util.invalid("Invalid info!", player);
                return;
            }
            YamlConfiguration config = save.getSave();
            try {
                config.save(new File(JavaPlugin.getPlugin(SimpleLuckyBlocksCore.class).getDataFolder() + "/lootEntries", save.name() + ".yml"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            player.sendMessage(ChatColor.GREEN + "Successfully created item entry with name " + save.name() + "!");
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
            GuiManager.openInventory(player, "entryManagement");
        }), 7, 1);
        Util.addReturnButton(mainPane, player, "entryManagement", 0, 2);
        gui.addPane(mainPane);
        return gui;
    }
}
