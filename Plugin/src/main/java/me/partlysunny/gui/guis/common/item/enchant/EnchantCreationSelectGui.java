package me.partlysunny.gui.guis.common.item.enchant;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.gui.type.util.Gui;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import me.partlysunny.gui.GuiManager;
import me.partlysunny.gui.SelectGui;
import me.partlysunny.gui.SelectGuiManager;
import me.partlysunny.util.Util;
import me.partlysunny.util.classes.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class EnchantCreationSelectGui extends SelectGui<EnchantContainer> {
    @Override
    public Gui getGui(HumanEntity p) {
        if (!(p instanceof Player player)) return new ChestGui(3, "");
        ChestGui gui = new ChestGui(3, ChatColor.AQUA + "Enchant Creator");
        Enchantment b = (Enchantment) SelectGuiManager.getSelectGui("enchantment").getValue(player.getUniqueId());
        boolean a = values.containsKey(player.getUniqueId());
        if (b != null) {
            if (a) {
                EnchantContainer plValue = this.values.get(player.getUniqueId());
                plValue.setEnchant(b);
            } else {
                this.values.put(player.getUniqueId(), new EnchantContainer(b, 0));
            }
            SelectGuiManager.getSelectGui("potionEffectType").resetValue(player.getUniqueId());
        }
        EnchantContainer c = a ? values.get(player.getUniqueId()) : new EnchantContainer(Enchantment.ARROW_DAMAGE, 0);
        values.put(player.getUniqueId(), c);
        StaticPane mainPane = new StaticPane(0, 0, 9, 3);
        mainPane.fillWith(new ItemStack(Material.GRAY_STAINED_GLASS_PANE));
        Util.addSelectionLink(mainPane, player, "enchantCreationSelect", "enchantmentSelect", ItemBuilder.builder(Material.ENCHANTED_BOOK).addEnchantment(c.enchant(), c.lvl()).build(), 3, 1);
        Util.addTextInputLink(mainPane, player, "enchantCreationSelect", ChatColor.RED + "Enter enchant lvl or \"cancel\" to cancel", ItemBuilder.builder(Material.PAPER).setName(ChatColor.GRAY + "Change LVL").setLore(ChatColor.DARK_GRAY + "Current lvl: " + c.lvl()).build(), 5, 1, pl -> {
            boolean hasValue = this.values.containsKey(pl.getUniqueId());
            Integer currentInput = Util.getTextInputAsInt(pl);
            if (currentInput == null) {
                Util.invalid("Invalid value!", pl);
                return;
            }
            if (hasValue) {
                this.values.get(pl.getUniqueId()).setLvl(currentInput);
            } else {
                this.values.put(pl.getUniqueId(), new EnchantContainer(Enchantment.ARROW_DAMAGE, currentInput));
            }
        });
        mainPane.addItem(new GuiItem(ItemBuilder.builder(Material.ARROW).setName(ChatColor.GREEN + "Back").build(), item -> {
            resetValue(player.getUniqueId());
            GuiManager.openInventory(player, getReturnTo(player));
        }), 0, 2);
        mainPane.addItem(new GuiItem(ItemBuilder.builder(Material.GREEN_CONCRETE).setName(ChatColor.GREEN + "Confirm").build(), item -> {
            player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
            ((EnchantModifierSelectGui) SelectGuiManager.getSelectGui("enchantModifier")).addEnchantTo(player.getUniqueId(), getValue(player.getUniqueId()));
            GuiManager.openInventory(player, getReturnTo(player));
        }), 8, 1);
        gui.addPane(mainPane);
        Util.setClickSoundTo(Sound.BLOCK_METAL_PRESSURE_PLATE_CLICK_OFF, gui);
        return gui;
    }

    @Override
    protected EnchantContainer getValueFromString(String s) {
        String[] values = s.split("\s++");
        return new EnchantContainer(Enchantment.getByKey(NamespacedKey.minecraft(values[0])), Integer.parseInt(s));
    }
}
