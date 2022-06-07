package me.partlysunny.gui.guis.loot.entry.creation.potion;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.gui.type.util.Gui;
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import me.partlysunny.SimpleLuckyBlocksCore;
import me.partlysunny.blocks.loot.entry.potion.PotionEntry;
import me.partlysunny.gui.GuiInstance;
import me.partlysunny.gui.GuiManager;
import me.partlysunny.gui.guis.common.ValueGuiManager;
import me.partlysunny.gui.guis.common.ValueReturnGui;
import me.partlysunny.gui.guis.loot.entry.creation.EntrySaveWrapper;
import me.partlysunny.gui.textInput.ChatListener;
import me.partlysunny.util.Util;
import me.partlysunny.util.classes.ItemBuilder;
import me.partlysunny.util.classes.Pair;
import me.partlysunny.util.classes.PotionBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PotionEntryCreateGui implements GuiInstance {

    private static final Map<UUID, EntrySaveWrapper<PotionEntry>> potionSaves = new HashMap<>();

    public static void addPlayerEffect(Player p, Pair<PotionEffectType, Pair<Integer, Integer>> effect) {
        if (potionSaves.containsKey(p.getUniqueId())) {
            potionSaves.get(p.getUniqueId()).entry().addEffect(effect.a(), effect.b().a(), effect.b().b());
        } else {
            potionSaves.put(p.getUniqueId(), new EntrySaveWrapper<>(null, new PotionEntry(List.of(effect))));
        }
    }

    @Override
    public Gui getGui(HumanEntity p) {
        if (!(p instanceof Player player)) return new ChestGui(3, "");
        EntrySaveWrapper<PotionEntry> potionEntry;
        if (potionSaves.containsKey(p.getUniqueId())) {
            potionEntry = potionSaves.get(p.getUniqueId());
        } else {
            EntrySaveWrapper<PotionEntry> value = new EntrySaveWrapper<>(null, new PotionEntry(List.of()));
            potionSaves.put(player.getUniqueId(), value);
            potionEntry = value;
        }
        ChestGui gui = new ChestGui(5, ChatColor.DARK_AQUA + "Potion Entry Creator");
        Util.setClickSoundTo(Sound.BLOCK_METAL_PRESSURE_PLATE_CLICK_OFF, gui);
        PaginatedPane pane = new PaginatedPane(0, 0, 9, 5);
        int displaySize = 21;
        Pair<PotionEffectType, Pair<Integer, Integer>>[] a = potionEntry.entry().getEffects();
        int numPages = (int) Math.ceil(a.length / (displaySize * 1f));
        if (numPages == 0) {
            numPages = 1;
        }
        int count = 0;
        for (int i = 0; i < numPages; i++) {
            StaticPane border = new StaticPane(0, 0, 9, 5);
            StaticPane items = new StaticPane(1, 1, 7, 3);
            Util.addPageNav(pane, numPages, i, border, gui);
            border.addItem(new GuiItem(ItemBuilder.builder(Material.GREEN_CONCRETE).setName(ChatColor.GREEN + "Add new").build(), item -> GuiManager.openInventory(player, "potionEntrySectionSelect")), 1, 0);
            border.addItem(new GuiItem(ItemBuilder.builder(Material.YELLOW_CONCRETE).setName(ChatColor.GOLD + "Reload").build(), item -> GuiManager.openInventory(player, "potionEntryCreate")), 2, 0);
            border.addItem(new GuiItem(ItemBuilder.builder(Material.ACACIA_SIGN).setName(ChatColor.RED + "Rename").setLore(ChatColor.GRAY + "Current name: " + potionEntry.name()).build(), item -> {
                ChatListener.startChatListen(player, "potionEntryCreate", ChatColor.RED + "Enter new name!", pl -> {
                    String input = ChatListener.getCurrentInput(pl);
                    if (input.length() < 2 || input.length() > 30) {
                        Util.invalid("Characters must be at least 2 and at most 29!", pl);
                        return;
                    }
                    if (Util.isValidFilePath(input)) {
                        Util.invalid("Invalid File Name!", pl);
                        return;
                    }
                    if (!potionSaves.containsKey(pl.getUniqueId())) {
                        potionSaves.put(pl.getUniqueId(), new EntrySaveWrapper<>(null, new PotionEntry(List.of())));
                    }
                    potionSaves.get(pl.getUniqueId()).setName(input);
                });
                player.closeInventory();
            }), 3, 0);
            border.addItem(new GuiItem(ItemBuilder.builder(Material.BLUE_CONCRETE).setName(ChatColor.BLUE + "Create Effect").build(), item -> {
                EntrySaveWrapper<PotionEntry> save = potionSaves.get(player.getUniqueId());
                if (save == null || save.entry().getEffects().length < 1) {
                    Util.invalid("Invalid info!", player);
                    return;
                }
                if (save.name() == null) {
                    Util.invalid("Please specify a name!", player);
                    return;
                }
                YamlConfiguration config = save.entry().getSave();
                try {
                    config.save(new File(JavaPlugin.getPlugin(SimpleLuckyBlocksCore.class).getDataFolder() + "/lootEntries", save.name() + ".yml"));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                player.sendMessage(ChatColor.GREEN + "Successfully created potion entry with name " + save.name() + "!");
                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
                GuiManager.openInventory(player, "entryManagement");
            }), 8, 2);
            items.fillWith(ItemBuilder.builder(Material.GRAY_STAINED_GLASS_PANE).setName("").build());
            for (int j = count; j < count + displaySize; j++) {
                if (j > a.length - 1) {
                    break;
                }
                Pair<PotionEffectType, Pair<Integer, Integer>> potionInfo = a[j];
                ItemStack potionAsItem = PotionBuilder.builder(PotionBuilder.PotionFormat.POTION).setName(potionInfo.a().getName()).addCustomEffect(new PotionEffect(potionInfo.a(), potionInfo.b().a(), potionInfo.b().b())).setPotionData(null, potionInfo.a().getColor()).build();
                Util.addLoreLine(potionAsItem, ChatColor.RED + "Right click to delete!");
                Util.addLoreLine(potionAsItem, ChatColor.GREEN + "Left click to edit!");
                items.addItem(new GuiItem(potionAsItem, item -> {
                    if (item.isRightClick()) {
                        potionEntry.entry().removeEffect(potionInfo.a());
                        GuiManager.openInventory(player, "potionEntryCreate");
                    }
                    if (item.isLeftClick()) {
                        ((ValueReturnGui<Pair<PotionEffectType, Pair<Integer, Integer>>>) (ValueGuiManager.getValueGui("potionEntrySection"))).openWithValue(player, potionInfo, "potionEntrySectionSelect");
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
