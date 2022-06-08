package me.partlysunny.gui.guis.loot.entry.creation.potion;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.gui.type.util.Gui;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import me.partlysunny.gui.GuiManager;
import me.partlysunny.gui.ValueGuiManager;
import me.partlysunny.gui.ValueReturnGui;
import me.partlysunny.util.Util;
import me.partlysunny.util.classes.ItemBuilder;
import me.partlysunny.util.classes.Pair;
import me.partlysunny.util.classes.PotionBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

public class PotionEntrySectionSelectGui extends ValueReturnGui<Pair<PotionEffectType, Pair<Integer, Integer>>> {

    @Override
    public Gui getGui(HumanEntity p) {
        if (!(p instanceof Player player)) return new ChestGui(3, "");
        boolean a = this.values.containsKey(player.getUniqueId());
        PotionEffectType b = (PotionEffectType) ValueGuiManager.getValueGui("potionEffectType").getValue(player.getUniqueId());
        if (b != null) {
            if (a) {
                Pair<PotionEffectType, Pair<Integer, Integer>> plValue = this.values.get(player.getUniqueId());
                plValue.setA(b);
            } else {
                this.values.put(player.getUniqueId(), new Pair<>(b, new Pair<>(0, 0)));
            }
            ValueGuiManager.getValueGui("potionEffectType").resetValue(player.getUniqueId());
        }
        ChestGui gui = new ChestGui(3, ChatColor.GRAY + "Potion Effect Creator");
        Util.setClickSoundTo(Sound.BLOCK_METAL_PRESSURE_PLATE_CLICK_OFF, gui);
        StaticPane mainPane = new StaticPane(0, 0, 9, 3);
        mainPane.fillWith(ItemBuilder.builder(Material.GRAY_STAINED_GLASS_PANE).setName("").build());
        Pair<PotionEffectType, Pair<Integer, Integer>> current = new Pair<>(PotionEffectType.ABSORPTION, new Pair<>(0, 0));
        if (this.values.containsKey(player.getUniqueId())) {
            current = this.values.get(player.getUniqueId());
            Util.flushNulls(current, PotionEffectType.ABSORPTION, new Pair<>(0, 0));
            Util.flushNulls(current.b(), 0, 0);
        } else {
            values.put(player.getUniqueId(), current);
        }
        ItemStack potionItem = PotionBuilder.builder(PotionBuilder.PotionFormat.POTION).setPotionData(Util.asType(current.a()), null).setName(ChatColor.DARK_AQUA + current.a().getName()).setLore().build();
        Util.addSelectionLink(mainPane, player, "potionEntrySectionSelect", "potionEffectTypeSelect", potionItem, 1, 1);
        ItemStack durationItem = ItemBuilder.builder(Material.PAPER).setName(ChatColor.BLUE + "Duration").setLore(ChatColor.GRAY + (current.b().a() == null ? "0" : current.b().a().toString())).build();
        Util.addTextInputLink(mainPane, player, "potionEntrySectionSelect", ChatColor.RED + "Enter duration or \"cancel\" to cancel", durationItem, 3, 1, pl -> {
            boolean hasValue = this.values.containsKey(pl.getUniqueId());
            Integer currentInput = Util.getTextInputAsInt(pl);
            if (currentInput == null) {
                Util.invalid("Invalid value!", pl);
                return;
            }
            if (hasValue) {
                Pair<PotionEffectType, Pair<Integer, Integer>> plValue = this.values.get(pl.getUniqueId());
                plValue.b().setA(currentInput);
            } else {
                this.values.put(pl.getUniqueId(), new Pair<>(PotionEffectType.ABSORPTION, new Pair<>(currentInput * 20, 0)));
            }
        });
        ItemStack lvlItem = ItemBuilder.builder(Material.PAPER).setName(ChatColor.BLUE + "Amplifier").setLore(ChatColor.GRAY + (current.b().b() == null ? "0" : String.valueOf(current.b().b() + 1))).build();
        Util.addTextInputLink(mainPane, player, "potionEntrySectionSelect", ChatColor.RED + "Enter amplifier (lvl) or \"cancel\" to cancel", lvlItem, 5, 1, pl -> {
            boolean hasValue = this.values.containsKey(pl.getUniqueId());
            Integer currentInput = Util.getTextInputAsInt(pl);
            if (currentInput == null) {
                Util.invalid("Invalid value!", pl);
                return;
            }
            if (hasValue) {
                Pair<PotionEffectType, Pair<Integer, Integer>> plValue = this.values.get(pl.getUniqueId());
                plValue.b().setB(currentInput - 1);
            } else {
                this.values.put(pl.getUniqueId(), new Pair<>(PotionEffectType.ABSORPTION, new Pair<>(0, currentInput - 1)));
            }
        });
        ItemStack submitButton = ItemBuilder.builder(Material.GREEN_CONCRETE).setName(ChatColor.GREEN + "Create").build();
        mainPane.addItem(new GuiItem(submitButton, event -> {
            if (getValue(player.getUniqueId()) == null) {
                player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
                player.sendMessage(ChatColor.RED + "Invalid values!");
                return;
            }
            PotionEntryCreateGui.addPlayerEffect(player, getValue(player.getUniqueId()));
            GuiManager.openInventory(player, "potionEntryCreate");
        }), 7, 1);
        Util.addReturnButton(mainPane, player, "potionEntryCreate", 0, 2);
        gui.addPane(mainPane);
        return gui;
    }

    @Override
    protected Pair<PotionEffectType, Pair<Integer, Integer>> getValueFromString(String s) {
        String[] info = s.split("\s++");
        return new Pair<>(PotionEffectType.getByKey(NamespacedKey.minecraft(info[0])), new Pair<>(Integer.parseInt(info[1]), Integer.parseInt(info[2])));
    }
}
