package me.partlysunny.gui.guis.common.item.enchant;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.gui.type.util.Gui;
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import me.partlysunny.gui.GuiManager;
import me.partlysunny.gui.SelectGui;
import me.partlysunny.gui.SelectGuiManager;
import me.partlysunny.util.Util;
import me.partlysunny.util.classes.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class EnchantModifierSelectGui extends SelectGui<ItemStack> {

    @Override
    public Gui getGui(HumanEntity p) {
        if (!(p instanceof Player player)) return new ChestGui(3, "");
        ChestGui gui = new ChestGui(5, ChatColor.DARK_AQUA + "Enchant Modifier");
        Util.setClickSoundTo(Sound.BLOCK_METAL_PRESSURE_PLATE_CLICK_OFF, gui);
        PaginatedPane pane = new PaginatedPane(0, 0, 9, 5);
        int displaySize = 21;
        List<EnchantContainer> a = new ArrayList<>();
        Map<Enchantment, Integer> enchantments = getValue(player.getUniqueId()).getEnchantments();
        for (Enchantment e : enchantments.keySet()) {
            a.add(new EnchantContainer(e, enchantments.get(e)));
        }
        int numPages = (int) Math.ceil(a.size() / (displaySize * 1f));
        if (numPages == 0) {
            numPages = 1;
        }
        int count = 0;
        for (int i = 0; i < numPages; i++) {
            StaticPane border = new StaticPane(0, 0, 9, 5);
            StaticPane items = new StaticPane(1, 1, 7, 3);
            Util.addPageNav(pane, numPages, i, border, gui);
            border.addItem(new GuiItem(ItemBuilder.builder(Material.GREEN_CONCRETE).setName(ChatColor.GREEN + "Add new").build(), item -> {
                SelectGui<EnchantContainer> enchantCreation = (SelectGui<EnchantContainer>) (SelectGuiManager.getSelectGui("enchantCreation"));
                enchantCreation.setReturnTo(player.getUniqueId(), "enchantModifierSelect");
                GuiManager.openInventory(player, "enchantCreationSelect");
            }), 1, 0);
            border.addItem(new GuiItem(ItemBuilder.builder(Material.YELLOW_CONCRETE).setName(ChatColor.GOLD + "Reload").build(), item -> GuiManager.openInventory(player, "enchantModifierSelect")), 2, 0);
            border.addItem(new GuiItem(ItemBuilder.builder(Material.BLUE_CONCRETE).setName(ChatColor.BLUE + "Update").build(), item -> {
                player.sendMessage(ChatColor.GREEN + "Updated!");
                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
                GuiManager.openInventory(player, getReturnTo(player));
            }), 8, 2);
            items.fillWith(ItemBuilder.builder(Material.GRAY_STAINED_GLASS_PANE).setName("").build());
            for (int j = count; j < count + displaySize; j++) {
                if (j > a.size() - 1) {
                    break;
                }
                EnchantContainer container = a.get(j);
                ItemStack enchantAsItem = ItemBuilder.builder(Material.ENCHANTED_BOOK).addEnchantment(container.enchant(), container.lvl()).build();
                Util.addLoreLine(enchantAsItem, ChatColor.RED + "Right click to delete!");
                Util.addLoreLine(enchantAsItem, ChatColor.GREEN + "Left click to edit!");
                items.addItem(new GuiItem(enchantAsItem, item -> {
                    if (item.isRightClick()) {
                        getValue(player.getUniqueId()).removeEnchantment(container.enchant());
                        GuiManager.openInventory(player, "enchantModifierSelect");
                    }
                    if (item.isLeftClick()) {
                        SelectGui<EnchantContainer> enchantCreation = (SelectGui<EnchantContainer>) (SelectGuiManager.getSelectGui("enchantCreation"));
                        enchantCreation.setReturnTo(player.getUniqueId(), "enchantModifierSelect");
                        enchantCreation.openWithValue(player, container, "enchantCreationSelect");
                    }
                }), (j - count) % 7, (j - count) / 7);
            }
            count += displaySize;
            border.addItem(new GuiItem(ItemBuilder.builder(Material.ARROW).setName(ChatColor.GREEN + "Back").build(), item -> {
                resetValue(player.getUniqueId());
                GuiManager.openInventory(player, getReturnTo(player));
            }), 0, 4);
            pane.addPane(i, border);
            pane.addPane(i, items);
        }
        gui.addPane(pane);
        Util.setClickSoundTo(Sound.BLOCK_METAL_PRESSURE_PLATE_CLICK_OFF, gui);
        return gui;
    }

    public void addEnchantTo(UUID id, EnchantContainer c) {
        getValue(id).addUnsafeEnchantment(c.enchant(), c.lvl());
    }

    @Override
    protected ItemStack getValueFromString(String s) {
        return new ItemStack(Material.AIR);
    }

}
