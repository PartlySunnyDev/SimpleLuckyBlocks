package me.partlysunny.gui.guis.loot.entry.creation.mob;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.gui.type.util.Gui;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import me.partlysunny.SimpleLuckyBlocksCore;
import me.partlysunny.blocks.loot.entry.mob.MobEntry;
import me.partlysunny.blocks.loot.entry.mob.SpawnEffect;
import me.partlysunny.gui.GuiInstance;
import me.partlysunny.gui.GuiManager;
import me.partlysunny.gui.ValueGuiManager;
import me.partlysunny.gui.guis.loot.entry.creation.EntrySaveWrapper;
import me.partlysunny.gui.guis.loot.entry.creation.mob.equipment.EquipmentWrapper;
import me.partlysunny.gui.guis.loot.entry.creation.mob.equipment.MobSlot;
import me.partlysunny.gui.textInput.ChatListener;
import me.partlysunny.util.Util;
import me.partlysunny.util.classes.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MobEntryCreateGui implements GuiInstance {

    private static final Map<UUID, EntrySaveWrapper<MobEntry>> mobSaves = new HashMap<>();
    private static final Map<UUID, MobSlot> currentSlot = new HashMap<>();

    public static MobSlot getSlotFor(UUID player) {
        return currentSlot.getOrDefault(player, MobSlot.MAIN_HAND);
    }

    public static void setSlot(UUID uniqueId, MobSlot slot) {
        currentSlot.put(uniqueId, slot);
    }

    @Override
    public Gui getGui(HumanEntity p) {
        if (!(p instanceof Player player)) return new ChestGui(3, "");
        boolean a = mobSaves.containsKey(player.getUniqueId());
        EntityType b = (EntityType) ValueGuiManager.getValueGui("entityType").getValue(player.getUniqueId());
        if (b != null) {
            if (a) {
                EntrySaveWrapper<MobEntry> plValue = mobSaves.get(player.getUniqueId());
                plValue.entry().setEntityType(b);
            } else {
                MobEntry mobEntry = new MobEntry();
                mobEntry.setEntityType(b);
                mobSaves.put(player.getUniqueId(), new EntrySaveWrapper<>(null, mobEntry));
            }
            ValueGuiManager.getValueGui("entityType").resetValue(player.getUniqueId());
        }
        SpawnEffect se = (SpawnEffect) ValueGuiManager.getValueGui("spawnEffect").getValue(player.getUniqueId());
        if (se != null) {
            if (a) {
                EntrySaveWrapper<MobEntry> plValue = mobSaves.get(player.getUniqueId());
                plValue.entry().setSpawnEffect(se);
            } else {
                MobEntry mobEntry = new MobEntry();
                mobEntry.setSpawnEffect(se);
                mobSaves.put(player.getUniqueId(), new EntrySaveWrapper<>(null, mobEntry));
            }
            ValueGuiManager.getValueGui("spawnEffect").resetValue(player.getUniqueId());
        }
        UUID pId = player.getUniqueId();
        ChestGui gui = new ChestGui(6, ChatColor.GREEN + "Mob Entry Creator");
        EquipmentWrapper createdItem = (EquipmentWrapper) ValueGuiManager.getValueGui("mobEquipment").getValue(pId);
        EntrySaveWrapper<MobEntry> mobInfo;
        if (mobSaves.containsKey(p.getUniqueId())) {
            if (createdItem != null) {
                MobEntry entry = mobSaves.get(p.getUniqueId()).entry();
                entry.setEquipment(createdItem.slot(), createdItem.item());
                entry.setEquipmentDropChance(createdItem.slot(), createdItem.dropChance());
                ValueGuiManager.getValueGui("mobEquipment").resetValue(player.getUniqueId());
            }
            mobInfo = mobSaves.get(p.getUniqueId());
        } else {
            EntrySaveWrapper<MobEntry> value = new EntrySaveWrapper<>(null, new MobEntry());
            if (createdItem != null) {
                value.entry().setEquipment(createdItem.slot(), createdItem.item());
                value.entry().setEquipmentDropChance(createdItem.slot(), createdItem.dropChance());
                ValueGuiManager.getValueGui("mobEquipment").resetValue(player.getUniqueId());
            }
            mobSaves.put(player.getUniqueId(), value);
            mobInfo = value;
        }
        StaticPane mainPane = new StaticPane(0, 0, 9, 6);
        mainPane.fillWith(new ItemStack(Material.GRAY_STAINED_GLASS_PANE));
        MobEntry entry = mobInfo.entry();

        //Create all equipment slots
        ItemStack helmetItem = ItemBuilder.builder(entry.getEquipment(MobSlot.HELMET)).setName(ChatColor.GRAY + "Helmet").build();
        Util.addEditable(helmetItem);
        Util.addEquipmentSlot(mainPane, player, "mobEntryCreate", MobSlot.HELMET, helmetItem, 1, 1);
        ItemStack chestplateItem = ItemBuilder.builder(entry.getEquipment(MobSlot.CHESTPLATE)).setName(ChatColor.GRAY + "Chestplate").build();
        Util.addEditable(chestplateItem);
        Util.addEquipmentSlot(mainPane, player, "mobEntryCreate", MobSlot.CHESTPLATE, chestplateItem, 1, 2);
        ItemStack leggingsItem = ItemBuilder.builder(entry.getEquipment(MobSlot.LEGGINGS)).setName(ChatColor.GRAY + "Leggings").build();
        Util.addEditable(leggingsItem);
        Util.addEquipmentSlot(mainPane, player, "mobEntryCreate", MobSlot.LEGGINGS, leggingsItem, 1, 3);
        ItemStack bootsItem = ItemBuilder.builder(entry.getEquipment(MobSlot.BOOTS)).setName(ChatColor.GRAY + "Boots").build();
        Util.addEditable(bootsItem);
        Util.addEquipmentSlot(mainPane, player, "mobEntryCreate", MobSlot.BOOTS, bootsItem, 1, 4);
        ItemStack mainHandItem = ItemBuilder.builder(entry.getEquipment(MobSlot.MAIN_HAND)).setName(ChatColor.GRAY + "Main Hand").build();
        Util.addEditable(mainHandItem);
        Util.addEquipmentSlot(mainPane, player, "mobEntryCreate", MobSlot.MAIN_HAND, mainHandItem, 2, 2);
        ItemStack offHandItem = ItemBuilder.builder(entry.getEquipment(MobSlot.OFF_HAND)).setName(ChatColor.GRAY + "Off Hand").build();
        Util.addEditable(offHandItem);
        Util.addEquipmentSlot(mainPane, player, "mobEntryCreate", MobSlot.OFF_HAND, offHandItem, 0, 2);

        ItemStack minItem = ItemBuilder.builder(Material.PAPER).setName(ChatColor.BLUE + "Minimum amount").setLore(ChatColor.GRAY + "" + entry.min()).build();
        Util.addTextInputLink(mainPane, player, "mobEntryCreate", ChatColor.RED + "Enter minimum value or \"cancel\" to cancel", minItem, 5, 2, pl -> {
            boolean hasValue = mobSaves.containsKey(pl.getUniqueId());
            Integer currentInput = Util.getTextInputAsInt(pl);
            if (currentInput == null) {
                Util.invalid("Invalid value!", pl);
                return;
            }
            if (hasValue) {
                entry.setMin(currentInput);
            } else {
                MobEntry newEntry = new MobEntry();
                newEntry.setMin(currentInput);
                mobSaves.put(pl.getUniqueId(), new EntrySaveWrapper<>(null, newEntry));
            }
        });
        ItemStack maxItem = ItemBuilder.builder(Material.PAPER).setName(ChatColor.BLUE + "Maximum amount").setLore(ChatColor.GRAY + "" + entry.max()).build();
        Util.addTextInputLink(mainPane, player, "mobEntryCreate", ChatColor.RED + "Enter maximum value or \"cancel\" to cancel", maxItem, 5, 3, pl -> {
            boolean hasValue = mobSaves.containsKey(pl.getUniqueId());
            Integer currentInput = Util.getTextInputAsInt(pl);
            if (currentInput == null) {
                Util.invalid("Invalid value!", pl);
                return;
            }
            if (hasValue) {
                entry.setMax(currentInput);
            } else {
                MobEntry newEntry = new MobEntry();
                newEntry.setMax(currentInput);
                mobSaves.put(pl.getUniqueId(), new EntrySaveWrapper<>(null, newEntry));
            }
        });
        ItemStack health = ItemBuilder.builder(Material.PAPER).setName(ChatColor.BLUE + "Mob Health").setLore(ChatColor.GRAY + "" + entry.health()).build();
        Util.addTextInputLink(mainPane, player, "mobEntryCreate", ChatColor.RED + "Enter new mob health or \"cancel\" to cancel", health, 3, 2, pl -> {
            boolean hasValue = mobSaves.containsKey(pl.getUniqueId());
            Integer currentInput = Util.getTextInputAsInt(pl);
            if (currentInput == null) {
                Util.invalid("Invalid value!", pl);
                return;
            }
            if (hasValue) {
                entry.setHealth(currentInput);
            } else {
                MobEntry newEntry = new MobEntry();
                newEntry.setHealth(currentInput);
                mobSaves.put(pl.getUniqueId(), new EntrySaveWrapper<>(null, newEntry));
            }
        });
        ItemStack speedMultiplier = ItemBuilder.builder(Material.PAPER).setName(ChatColor.BLUE + "Speed Multiplier").setLore(ChatColor.GRAY + "" + entry.speedMultiplier()).build();
        Util.addTextInputLink(mainPane, player, "mobEntryCreate", ChatColor.RED + "Enter new speed multiplier or \"cancel\" to cancel", speedMultiplier, 3, 3, pl -> {
            boolean hasValue = mobSaves.containsKey(pl.getUniqueId());
            Double currentInput = Util.getTextInputAsDouble(pl);
            if (currentInput == null) {
                Util.invalid("Invalid value!", pl);
                return;
            }
            if (hasValue) {
                entry.setSpeedMultiplier(currentInput);
            } else {
                MobEntry newEntry = new MobEntry();
                newEntry.setSpeedMultiplier(currentInput);
                mobSaves.put(pl.getUniqueId(), new EntrySaveWrapper<>(null, newEntry));
            }
        });
        mainPane.addItem(new GuiItem(ItemBuilder.builder(Material.NAME_TAG).setName(ChatColor.RED + "Display Name").setLore(ChatColor.GRAY + "Current display name: " + mobInfo.entry().name()).build(), event -> {
            ChatListener.startChatListen(player, "mobEntryCreate", ChatColor.RED + "Enter new display name!", pl -> {
                String input = ChatListener.getCurrentInput(pl);
                if (input.length() < 2 || input.length() > 30) {
                    Util.invalid("Characters must be at least 2 and at most 29!", pl);
                    return;
                }
                if (!Util.isValidFilePath(input)) {
                    Util.invalid("Invalid File Name!", pl);
                    return;
                }
                if (!mobSaves.containsKey(pl.getUniqueId())) {
                    mobSaves.put(pl.getUniqueId(), new EntrySaveWrapper<>(null, new MobEntry()));
                }
                mobSaves.get(pl.getUniqueId()).entry().setName(input);
            });
            player.closeInventory();
        }), 4, 3);

        Util.addRenameButton(mainPane, player, mobSaves, new MobEntry(), "mobEntryCreate", 4, 2);
        Util.addReturnButton(mainPane, player, "entryCreation", 0, 5);
        mainPane.addItem(new GuiItem(ItemBuilder.builder(Material.BLUE_CONCRETE).setName(ChatColor.BLUE + "Create Mob Entry").build(), item -> {
            EntrySaveWrapper<MobEntry> save = mobSaves.get(player.getUniqueId());
            if (save == null) {
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
            player.sendMessage(ChatColor.GREEN + "Successfully created mob entry with name " + save.name() + "!");
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
            GuiManager.openInventory(player, "entryManagement");
        }), 8, 2);
        ItemStack entityItem = ItemBuilder.builder(Material.ZOMBIE_HEAD).setName(ChatColor.RED + mobInfo.entry().entityType().toString()).setLore(ChatColor.GRAY + "Current entity type!").build();
        ItemStack spawnEffectItem = ItemBuilder.builder(Material.FIREWORK_ROCKET).setName(ChatColor.RED + mobInfo.entry().spawnEffect().toString()).setLore(ChatColor.GRAY + "Current spawn effect!").build();
        Util.addSelectionLink(mainPane, player, "mobEntryCreate", "entityTypeSelect", entityItem, 6, 2);
        Util.addSelectionLink(mainPane, player, "mobEntryCreate", "spawnEffectSelect", spawnEffectItem, 6, 3);
        gui.addPane(mainPane);
        Util.setClickSoundTo(Sound.BLOCK_METAL_PRESSURE_PLATE_CLICK_OFF, gui);
        return gui;
    }


}
