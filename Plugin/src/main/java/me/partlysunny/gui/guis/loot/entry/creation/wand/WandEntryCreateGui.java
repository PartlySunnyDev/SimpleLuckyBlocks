package me.partlysunny.gui.guis.loot.entry.creation.wand;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.gui.type.util.Gui;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import me.partlysunny.blocks.loot.entry.wand.WandEntry;
import me.partlysunny.gui.GuiManager;
import me.partlysunny.gui.SelectGuiManager;
import me.partlysunny.gui.guis.loot.entry.creation.EntryCreateGui;
import me.partlysunny.gui.guis.loot.entry.creation.EntrySaveWrapper;
import me.partlysunny.gui.textInput.ChatListener;
import me.partlysunny.util.Util;
import me.partlysunny.util.classes.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class WandEntryCreateGui extends EntryCreateGui<WandEntry> {
    @Override
    public Gui getGui(HumanEntity p) {
        if (!(p instanceof Player player)) return new ChestGui(3, "");
        boolean a = saves.containsKey(player.getUniqueId());
        String b = (String) SelectGuiManager.getValueGui("wand").getValue(player.getUniqueId());
        if (b != null) {
            if (a) {
                EntrySaveWrapper<WandEntry> plValue = saves.get(player.getUniqueId());
                plValue.entry().setWandType(b);
            } else {
                WandEntry wandEntry = new WandEntry();
                wandEntry.setWandType(b);
                saves.put(player.getUniqueId(), new EntrySaveWrapper<>(null, wandEntry));
            }
            SelectGuiManager.getValueGui("wand").resetValue(player.getUniqueId());
        }
        ChestGui gui = new ChestGui(3, ChatColor.GRAY + "Wand Entry Creator");
        StaticPane mainPane = new StaticPane(0, 0, 9, 3);
        mainPane.fillWith(new ItemStack(Material.GRAY_STAINED_GLASS_PANE));
        EntrySaveWrapper<WandEntry> wandInfo;
        if (a) {
            wandInfo = saves.get(player.getUniqueId());
        } else {
            wandInfo = new EntrySaveWrapper<>(null, new WandEntry());
        }
        Util.addTextInputLink(mainPane, player, "wandEntryCreate", ChatColor.RED + "Input new item name:", ItemBuilder.builder(Material.PAPER).setName(ChatColor.GRAY + "Change Name").setLore(ChatColor.GRAY + "Current Name: " + wandInfo.entry().displayName()).build(), 1, 1, pl -> {
            boolean hasValue = saves.containsKey(pl.getUniqueId());
            String input = Util.processText(ChatListener.getCurrentInput(pl));
            if (input.length() < 2 || input.length() > 30) {
                Util.invalid("Characters must be at least 2 and at most 29!", pl);
                return;
            }
            if (!hasValue) {
                saves.put(pl.getUniqueId(), new EntrySaveWrapper<>(null, new WandEntry()));
            }
            saves.get(pl.getUniqueId()).entry().setDisplayName(input);
        });
        Util.addTextInputLink(mainPane, player, "wandEntryCreate", ChatColor.RED + "Input new lore (will auto wrap):", ItemBuilder.builder(Material.PAPER).setName(ChatColor.GRAY + "Change Lore").setLore(wandInfo.entry().lore().toArray(new String[0])).build(), 2, 1, pl -> {
            boolean hasValue = saves.containsKey(pl.getUniqueId());
            String input = Util.processText(ChatListener.getCurrentInput(pl));
            if (input.length() < 2) {
                Util.invalid("Characters must be at least 2!", pl);
                return;
            }
            if (!hasValue) {
                saves.put(pl.getUniqueId(), new EntrySaveWrapper<>(null, new WandEntry()));
            }
            saves.get(pl.getUniqueId()).entry().setLore(Util.splitLoreForLine(input));
        });
        ItemStack minItem = ItemBuilder.builder(Material.PAPER).setName(ChatColor.BLUE + "Minimum power").setLore(ChatColor.GRAY + "" + wandInfo.entry().minPower()).build();
        Util.addTextInputLink(mainPane, player, "wandEntryCreate", ChatColor.RED + "Enter minimum power value or \"cancel\" to cancel", minItem, 3, 1, pl -> {
            boolean hasValue = saves.containsKey(pl.getUniqueId());
            Integer currentInput = Util.getTextInputAsInt(pl);
            if (currentInput == null) {
                Util.invalid("Invalid value!", pl);
                return;
            }
            if (hasValue) {
                wandInfo.entry().setMinPower(currentInput);
            } else {
                saves.put(pl.getUniqueId(), new EntrySaveWrapper<>(null, new WandEntry()));
            }
        });
        ItemStack maxItem = ItemBuilder.builder(Material.PAPER).setName(ChatColor.BLUE + "Maximum power").setLore(ChatColor.GRAY + "" + wandInfo.entry().maxPower()).build();
        Util.addTextInputLink(mainPane, player, "wandEntryCreate", ChatColor.RED + "Enter maximum power value or \"cancel\" to cancel", maxItem, 4, 1, pl -> {
            boolean hasValue = saves.containsKey(pl.getUniqueId());
            Integer currentInput = Util.getTextInputAsInt(pl);
            if (currentInput == null) {
                Util.invalid("Invalid value!", pl);
                return;
            }
            if (hasValue) {
                wandInfo.entry().setMaxPower(currentInput);
            } else {
                saves.put(pl.getUniqueId(), new EntrySaveWrapper<>(null, new WandEntry()));
            }
        });
        ItemStack wandItem = ItemBuilder.builder(Material.STICK).setName(ChatColor.RED + wandInfo.entry().wandType()).setLore(ChatColor.GRAY + "Current wand type!").build();
        Util.addSelectionLink(mainPane, player, "wandEntryCreate", "wandSelect", wandItem, 5, 1);
        mainPane.addItem(new GuiItem(ItemBuilder.builder(Material.BLUE_CONCRETE).setName(ChatColor.BLUE + "Create Wand Entry").build(), item -> {
            EntrySaveWrapper<WandEntry> save = saves.get(player.getUniqueId());
            if (Util.saveInfo(player, save == null, save.name(), save.entry().getSave(), "lootEntries")) return;
            player.sendMessage(ChatColor.GREEN + "Successfully created wand entry with name " + save.name() + "!");
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
            GuiManager.openInventory(player, "entryManagement");
        }), 8, 1);
        Util.addRenameButton(mainPane, player, saves, new WandEntry(), "wandEntryCreate", 6, 1);
        Util.addReturnButton(mainPane, player, "entryCreation", 0, 2);
        gui.addPane(mainPane);
        Util.setClickSoundTo(Sound.BLOCK_METAL_PRESSURE_PLATE_CLICK_OFF, gui);
        return gui;
    }
}
