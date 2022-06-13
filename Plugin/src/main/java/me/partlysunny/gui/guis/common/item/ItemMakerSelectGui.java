package me.partlysunny.gui.guis.common.item;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.gui.type.util.Gui;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import me.partlysunny.gui.GuiManager;
import me.partlysunny.gui.SelectGui;
import me.partlysunny.gui.SelectGuiManager;
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
import java.util.UUID;

public class ItemMakerSelectGui extends SelectGui<ItemStack> {

    @Override
    public Gui getGui(HumanEntity p) {
        if (!(p instanceof Player player)) return new ChestGui(3, "");
        ChestGui gui = new ChestGui(3, ChatColor.GRAY + "Item Maker");
        StaticPane mainPane = new StaticPane(0, 0, 9, 3);
        mainPane.fillWith(new ItemStack(Material.GRAY_STAINED_GLASS_PANE));
        UUID pId = player.getUniqueId();
        boolean hasPlayer = this.values.containsKey(pId);
        ItemStack current = new ItemStack(Material.WOODEN_AXE);
        Material materialValue = (Material) SelectGuiManager.getSelectGui("material").getValue(pId);
        if (hasPlayer) {
            if (materialValue != null) {
                values.get(player.getUniqueId()).setType(materialValue);
                SelectGuiManager.getSelectGui("material").resetValue(pId);
            }
            current = getValue(pId);
        } else {
            if (materialValue != null) {
                values.put(pId, new ItemStack(materialValue));
                SelectGuiManager.getSelectGui("material").resetValue(pId);
            }
        }
        Material mat = current.getType();
        String name = current.getItemMeta().getDisplayName();
        List<String> lore = current.getItemMeta().getLore();
        if (lore == null) {
            lore = new ArrayList<>();
        }
        Util.addSelectionLink(mainPane, player, "itemMakerSelect", "materialSelect", ItemBuilder.builder(mat).setName(mat.name()).build(), 1, 1);
        ItemStack finalCurrent = current;
        mainPane.addItem(new GuiItem(ItemBuilder.builder(Material.ENCHANTED_BOOK).setName(ChatColor.LIGHT_PURPLE + "Modify Enchants").build(), x -> {
            SelectGuiManager.getSelectGui("enchantModifier").setReturnTo(p.getUniqueId(), "itemMakerSelect");
            p.closeInventory();
            ((SelectGui<ItemStack>) SelectGuiManager.getSelectGui("enchantModifier")).openWithValue(player, finalCurrent, "enchantModifierSelect");
        }), 3, 1);
        Util.addTextInputLink(mainPane, player, "itemMakerSelect", ChatColor.RED + "Input new item name:", ItemBuilder.builder(Material.PAPER).setName(ChatColor.GRAY + "Change Name").setLore(ChatColor.GRAY + "Current Name: " + name).build(), 5, 1, pl -> {
            boolean hasValue = this.values.containsKey(pl.getUniqueId());
            String input = Util.processText(ChatListener.getCurrentInput(pl));
            if (input.length() < 2 || input.length() > 30) {
                Util.invalid("Characters must be at least 2 and at most 29!", pl);
                return;
            }
            if (!hasValue) {
                values.put(pl.getUniqueId(), new ItemStack(Material.WOODEN_AXE));
            }
            Util.setName(values.get(pl.getUniqueId()), input);
        });
        Util.addTextInputLink(mainPane, player, "itemMakerSelect", ChatColor.RED + "Input new lore (will auto wrap):", ItemBuilder.builder(Material.PAPER).setName(ChatColor.GRAY + "Change Lore").setLore(lore.toArray(new String[0])).build(), 7, 1, pl -> {
            boolean hasValue = this.values.containsKey(pl.getUniqueId());
            String input = Util.processText(ChatListener.getCurrentInput(pl));
            if (input.length() < 2) {
                Util.invalid("Characters must be at least 2!", pl);
                return;
            }
            if (!hasValue) {
                values.put(pl.getUniqueId(), new ItemStack(Material.WOODEN_AXE));
            }
            Util.setLore(values.get(pl.getUniqueId()), Util.splitLoreForLine(input));
        });
        mainPane.addItem(new GuiItem(ItemBuilder.builder(Material.ARROW).setName(ChatColor.GREEN + "Back").build(), item -> {
            resetValue(player.getUniqueId());
            GuiManager.openInventory(player, getReturnTo(player));
        }), 0, 2);
        mainPane.addItem(new GuiItem(ItemBuilder.builder(Material.GREEN_CONCRETE).setName(ChatColor.GREEN + "Confirm").build(), item -> {
            player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
            GuiManager.openInventory(player, getReturnTo(player));
        }), 8, 1);
        Util.setClickSoundTo(Sound.BLOCK_METAL_PRESSURE_PLATE_CLICK_OFF, gui);
        gui.addPane(mainPane);
        return gui;
    }

    @Override
    protected ItemStack getValueFromString(String s) {
        return new ItemStack(Material.AIR);
    }
}
