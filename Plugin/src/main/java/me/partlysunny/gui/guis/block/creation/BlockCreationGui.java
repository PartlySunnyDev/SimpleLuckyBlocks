package me.partlysunny.gui.guis.block.creation;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.gui.type.util.Gui;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import me.partlysunny.blocks.LuckyBlockType;
import me.partlysunny.gui.GuiInstance;
import me.partlysunny.gui.GuiManager;
import me.partlysunny.gui.SelectGui;
import me.partlysunny.gui.SelectGuiManager;
import me.partlysunny.gui.guis.block.creation.recipe.ShapedRecipeWrapper;
import me.partlysunny.gui.guis.common.material.MaterialSelectGui;
import me.partlysunny.gui.textInput.ChatListener;
import me.partlysunny.util.Util;
import me.partlysunny.util.classes.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BlockCreationGui implements GuiInstance {

    private static final Map<UUID, BlockSaveWrapper> blockSaves = new HashMap<>();

    public static void openWithValue(Player p, BlockSaveWrapper value) {
        blockSaves.put(p.getUniqueId(), value);
        GuiManager.openInventory(p, "blockCreation");
    }

    @Override
    public Gui getGui(HumanEntity p) {
        if (!(p instanceof Player player)) return new ChestGui(3, "");
        UUID pId = player.getUniqueId();
        Util.handleSelectInput("material", player, blockSaves, new BlockSaveWrapper(null, new LuckyBlockType()), Material.class, (blockSaveWrapper, material) -> blockSaveWrapper.type().setBlockType(material));
        Util.handleSelectInput("tableType", player, blockSaves, new BlockSaveWrapper(null, new LuckyBlockType()), String.class, (blockSaveWrapper, string) -> blockSaveWrapper.type().setLootTable(string));
        BlockSaveWrapper blockInfo = blockSaves.getOrDefault(pId, new BlockSaveWrapper(null, new LuckyBlockType()));
        ChestGui gui = new ChestGui(4, "Create Lucky Block");
        StaticPane mainPane = new StaticPane(0, 0, 9, 5);
        Util.addRenameButton(mainPane, player, blockSaves, new BlockSaveWrapper(null, new LuckyBlockType()), "blockCreation", 1, 1);
        mainPane.addItem(new GuiItem(ItemBuilder.builder(blockInfo.type().blockType()).setName(ChatColor.GRAY + "Current Block Type").build(), x -> {
            SelectGuiManager.getSelectGui("materialSelect").setReturnTo(p.getUniqueId(), "blockCreation");
            MaterialSelectGui.setFilters(pId, "meta", "block");
            p.closeInventory();
            GuiManager.openInventory(player, "materialSelect");
        }), 1, 2);
        Util.addTextInputLink(mainPane, player, "blockCreation", ChatColor.RED + "Enter block display name or \"cancel\" to cancel", Util.getInfoItem("Display Name", blockInfo.type().displayName()), 3, 1, pl -> {
            boolean hasValue = blockSaves.containsKey(pl.getUniqueId());
            String currentInput = ChatListener.getCurrentInput(pl);
            if (currentInput == null) {
                Util.invalid("Invalid value!", pl);
                return;
            }
            if (hasValue) {
                blockSaves.get(pl.getUniqueId()).type().setDisplayName(currentInput);
            } else {
                BlockSaveWrapper value = new BlockSaveWrapper(null, new LuckyBlockType());
                value.type().setDisplayName(currentInput);
                blockSaves.put(pl.getUniqueId(), value);
            }
        });
        Util.addTextInputLink(mainPane, player, "blockCreation", ChatColor.RED + "Enter new inner item head value (use a website like https://minecraft-heads.com and copy the 'value' section) or \"cancel\" to cancel", ItemBuilder.builder(blockInfo.type().innerItem()).setName(ChatColor.BLUE + "Inner item").setLore(Util.splitLoreForLine("If the skull is not showing make sure you check you copied the value correctly!").toArray(new String[0])).build(), 2, 2, pl -> {
            boolean hasValue = blockSaves.containsKey(pl.getUniqueId());
            String currentInput = ChatListener.getCurrentInput(pl);
            if (currentInput == null) {
                Util.invalid("Invalid value!", pl);
                return;
            }
            if (hasValue) {
                blockSaves.get(pl.getUniqueId()).type().setInnerItem(Util.convert(Util.HeadType.BASE64, currentInput));
            } else {
                BlockSaveWrapper value = new BlockSaveWrapper(null, new LuckyBlockType());
                value.type().setInnerItem(Util.convert(Util.HeadType.BASE64, currentInput));
                blockSaves.put(pl.getUniqueId(), value);
            }
        });
        mainPane.addItem(new GuiItem(ItemBuilder.builder(Material.BOOK).setName(ChatColor.BLUE + "Change Recipe!").build(), x -> {
            SelectGuiManager.getSelectGui("recipe").setReturnTo(p.getUniqueId(), "blockCreation");
            p.closeInventory();
            ((SelectGui<ShapedRecipeWrapper>) SelectGuiManager.getSelectGui("recipe")).openWithValue(player, new ShapedRecipeWrapper(blockInfo.name()), "recipeSelect");
            GuiManager.openInventory(player, "materialSelect");
        }), 1, 2);
        Util.addSelectionLink(mainPane, player, "blockCreation", "entryTypeSelect", Util.getInfoItem("Loot Table", blockInfo.type().lootTable()), 3, 2);
        Util.addReturnButton(mainPane, player, "blockManagement", 0, 3);
        gui.addPane(mainPane);
        Util.setClickSoundTo(Sound.BLOCK_METAL_PRESSURE_PLATE_CLICK_OFF, gui);
        return null;
    }
}
