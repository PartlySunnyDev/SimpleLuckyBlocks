package me.partlysunny.gui.guis.loot.entry.creation.potion;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.gui.type.util.Gui;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import me.partlysunny.gui.GuiManager;
import me.partlysunny.gui.SelectGui;
import me.partlysunny.util.Util;
import me.partlysunny.util.classes.ItemBuilder;
import me.partlysunny.util.classes.PotionBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;

public class PotionEntrySectionSelectGui extends SelectGui<PotionEntryEffectWrapper> {

    @Override
    public Gui getGui(HumanEntity p) {
        if (!(p instanceof Player player)) return new ChestGui(3, "");
        Util.handleSelectInput("potionEffectType", player, values, new PotionEntryEffectWrapper(), PotionEffectType.class, PotionEntryEffectWrapper::setType);
        ChestGui gui = new ChestGui(3, ChatColor.GRAY + "Potion Effect Creator");
        Util.setClickSoundTo(Sound.BLOCK_METAL_PRESSURE_PLATE_CLICK_OFF, gui);
        StaticPane mainPane = new StaticPane(0, 0, 9, 3);
        mainPane.fillWith(ItemBuilder.builder(Material.GRAY_STAINED_GLASS_PANE).setName("").build());
        UUID pId = player.getUniqueId();
        PotionEntryEffectWrapper entryEffectInfo = values.getOrDefault(pId, new PotionEntryEffectWrapper());
        ItemStack potionItem = PotionBuilder.builder(PotionBuilder.PotionFormat.POTION).setPotionData(Util.asType(entryEffectInfo.type()), null).setName(ChatColor.DARK_AQUA + entryEffectInfo.type().getName()).setLore().build();
        Util.addSelectionLink(mainPane, player, "potionEntrySectionSelect", "potionEffectTypeSelect", potionItem, 1, 1);
        ItemStack durationItem = Util.getInfoItem("Duration", ChatColor.GRAY + "" + entryEffectInfo.duration());
        Util.addTextInputLink(mainPane, player, "potionEntrySectionSelect", ChatColor.RED + "Enter duration or \"cancel\" to cancel", durationItem, 3, 1, pl -> {
            boolean hasValue = this.values.containsKey(pl.getUniqueId());
            Integer currentInput = Util.getTextInputAsInt(pl);
            if (currentInput == null) {
                Util.invalid("Invalid value!", pl);
                return;
            }
            if (hasValue) {
                PotionEntryEffectWrapper plValue = this.values.get(pl.getUniqueId());
                plValue.setDuration(currentInput);
            } else {
                PotionEntryEffectWrapper value = new PotionEntryEffectWrapper();
                value.setDuration(currentInput);
                this.values.put(pl.getUniqueId(), value);
            }
        });
        ItemStack lvlItem = ItemBuilder.builder(Material.PAPER).setName(ChatColor.BLUE + "Amplifier").setLore(ChatColor.GRAY + "" + entryEffectInfo.amplifier()).build();
        Util.addTextInputLink(mainPane, player, "potionEntrySectionSelect", ChatColor.RED + "Enter amplifier (lvl) or \"cancel\" to cancel", lvlItem, 5, 1, pl -> {
            boolean hasValue = this.values.containsKey(pl.getUniqueId());
            Integer currentInput = Util.getTextInputAsInt(pl);
            if (currentInput == null) {
                Util.invalid("Invalid value!", pl);
                return;
            }
            if (hasValue) {
                PotionEntryEffectWrapper plValue = this.values.get(pl.getUniqueId());
                plValue.setAmplifier(currentInput);
            } else {
                PotionEntryEffectWrapper value = new PotionEntryEffectWrapper();
                value.setAmplifier(currentInput);
                this.values.put(pl.getUniqueId(), value);
            }
        });
        ItemStack submitButton = ItemBuilder.builder(Material.GREEN_CONCRETE).setName(ChatColor.GREEN + "Create").build();
        mainPane.addItem(new GuiItem(submitButton, event -> {
            if (getValue(pId) == null) {
                player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
                player.sendMessage(ChatColor.RED + "Invalid values!");
                return;
            }
            PotionEntryCreateGui.addPlayerEffect(player, getValue(pId));
            GuiManager.openInventory(player, "potionEntryCreate");
        }), 7, 1);
        Util.addReturnButton(mainPane, player, "potionEntryCreate", 0, 2);
        gui.addPane(mainPane);
        return gui;
    }

    @Override
    protected PotionEntryEffectWrapper getValueFromString(String s) {
        String[] info = s.split("\s++");
        return new PotionEntryEffectWrapper(PotionEffectType.getByKey(NamespacedKey.minecraft(info[0])), Integer.parseInt(info[1]), Integer.parseInt(info[2]));
    }
}
