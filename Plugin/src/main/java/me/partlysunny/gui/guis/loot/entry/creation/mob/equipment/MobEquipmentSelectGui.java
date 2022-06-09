package me.partlysunny.gui.guis.loot.entry.creation.mob.equipment;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.gui.type.util.Gui;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import me.partlysunny.gui.GuiManager;
import me.partlysunny.gui.SelectGuiManager;
import me.partlysunny.gui.SelectGui;
import me.partlysunny.gui.guis.common.material.MaterialSelectGui;
import me.partlysunny.gui.guis.loot.entry.creation.mob.MobEntryCreateGui;
import me.partlysunny.util.Util;
import me.partlysunny.util.classes.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class MobEquipmentSelectGui extends SelectGui<EquipmentWrapper> {
    @Override
    public Gui getGui(HumanEntity p) {
        if (!(p instanceof Player player)) return new ChestGui(3, "");
        UUID pId = player.getUniqueId();
        ItemStack createdItem = (ItemStack) SelectGuiManager.getValueGui("itemMaker").getValue(pId);
        EquipmentWrapper eqInfo;
        MobSlot slotFor = MobEntryCreateGui.getSlotFor(pId);
        Material validMat = slotFor.getValidMaterial();
        if (values.containsKey(p.getUniqueId())) {
            if (!slotFor.matchesFilter(values.get(pId).item().getType())) {
                values.get(pId).item().setType(validMat);
            }
            if (createdItem != null) {
                values.get(p.getUniqueId()).setItem(createdItem);
                SelectGuiManager.getValueGui("itemMaker").resetValue(player.getUniqueId());
            }
            eqInfo = values.get(p.getUniqueId());
        } else {
            EquipmentWrapper value = new EquipmentWrapper(slotFor, new ItemStack(validMat), 1);
            if (createdItem != null) {
                value.setItem(createdItem);
                SelectGuiManager.getValueGui("itemMaker").resetValue(player.getUniqueId());
            }
            values.put(player.getUniqueId(), value);
            eqInfo = value;
        }
        ChestGui gui = new ChestGui(3, ChatColor.GREEN + "Equipment Creator");
        StaticPane mainPane = new StaticPane(0, 0, 9, 3);
        mainPane.fillWith(new ItemStack(Material.GRAY_STAINED_GLASS_PANE));
        ItemStack item = eqInfo.item().clone();
        Util.addEditable(item);
        EquipmentWrapper finalEqInfo = eqInfo;
        mainPane.addItem(new GuiItem(item, x -> {
            SelectGuiManager.getValueGui("itemMaker").setReturnTo(p.getUniqueId(), "mobEquipmentSelect");
            MaterialSelectGui.setFilters(pId, "meta", slotFor.toString().toLowerCase());
            p.closeInventory();
            ((SelectGui<ItemStack>) SelectGuiManager.getValueGui("itemMaker")).openWithValue(player, finalEqInfo.item(), "itemMakerSelect");
        }), 2, 1);
        ItemStack minItem = ItemBuilder.builder(Material.PAPER).setName(ChatColor.BLUE + "Drop Chance").setLore(ChatColor.GRAY + "" + eqInfo.dropChance()).build();
        Util.addTextInputLink(mainPane, player, "mobEquipmentSelect", ChatColor.RED + "Enter new drop chance or \"cancel\" to cancel", minItem, 4, 1, pl -> {
            boolean hasValue = values.containsKey(pl.getUniqueId());
            Double currentInput = Util.getTextInputAsDouble(pl);
            if (currentInput == null) {
                Util.invalid("Invalid value!", pl);
                return;
            }
            if (hasValue) {
                finalEqInfo.setDropChance(currentInput);
            } else {
                values.put(pl.getUniqueId(), new EquipmentWrapper(MobEntryCreateGui.getSlotFor(pl.getUniqueId()), new ItemStack(validMat), currentInput));
            }
        });
        ItemStack submitButton = ItemBuilder.builder(Material.GREEN_CONCRETE).setName(ChatColor.GREEN + "Create").build();
        mainPane.addItem(new GuiItem(submitButton, event -> {
            if (getValue(player.getUniqueId()) == null) {
                player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
                player.sendMessage(ChatColor.RED + "Invalid values!");
                return;
            }
            player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
            GuiManager.openInventory(player, "mobEntryCreate");
        }), 7, 1);
        Util.addReturnButton(mainPane, player, "mobEntryCreate", 0, 2);
        gui.addPane(mainPane);
        Util.setClickSoundTo(Sound.BLOCK_METAL_PRESSURE_PLATE_CLICK_OFF, gui);
        return gui;
    }

    @Override
    protected EquipmentWrapper getValueFromString(String s) {
        return new EquipmentWrapper(MobSlot.MAIN_HAND, new ItemStack(Material.AIR), 1);
    }
}
