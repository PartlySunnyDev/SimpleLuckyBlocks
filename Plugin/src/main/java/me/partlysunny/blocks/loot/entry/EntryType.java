package me.partlysunny.blocks.loot.entry;

import me.partlysunny.SimpleLuckyBlocksCore;
import me.partlysunny.blocks.loot.entry.command.CommandEntry;
import me.partlysunny.blocks.loot.entry.item.ItemEntry;
import me.partlysunny.blocks.loot.entry.mob.MobEntry;
import me.partlysunny.blocks.loot.entry.mob.SpawnEffect;
import me.partlysunny.blocks.loot.entry.potion.PotionEntry;
import me.partlysunny.blocks.loot.entry.structure.StructureEntry;
import me.partlysunny.blocks.loot.entry.wand.WandEntry;
import me.partlysunny.gui.guis.loot.entry.creation.mob.equipment.MobSlot;
import me.partlysunny.gui.guis.loot.entry.creation.potion.PotionEntryEffectWrapper;
import me.partlysunny.util.Util;
import me.partlysunny.util.classes.ItemBuilder;
import me.partlysunny.util.classes.Pair;
import me.partlysunny.util.classes.PotionBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import java.util.*;
import java.util.function.Function;

import static me.partlysunny.blocks.loot.entry.LootEntryManager.loadItemSection;
import static me.partlysunny.util.Util.*;

public enum EntryType {

    ITEM("item", ItemBuilder.builder(Material.COBBLESTONE).setName(ChatColor.GRAY + "Item").build(), (name) -> {
        Material material = Material.valueOf(Util.getOrError(name, "material"));
        int min = Util.getOrError(name, "min");
        int max = Util.getOrError(name, "max");
        //Enchants
        List<Pair<Enchantment, Integer>> enchants = new ArrayList<>();
        if (name.contains("enchantments")) {
            ConfigurationSection enchantments = name.getConfigurationSection("enchantments");
            for (String s : enchantments.getKeys(false)) {
                ConfigurationSection info = enchantments.getConfigurationSection(s);
                enchants.add(new Pair<>(Enchantment.getByKey(NamespacedKey.minecraft(info.getString("id").toLowerCase())), info.getInt("lvl")));
            }
        }
        //Build the item
        ItemBuilder b = ItemBuilder.builder(material);
        if (name.contains("name")) {
            b.setName(processText(Util.getOrError(name, "name")));
        }
        if (name.contains("lore")) {
            b.setLore(processTexts(Util.getOrError(name, "lore")).toArray(new String[0]));
        }
        for (Pair<Enchantment, Integer> p : enchants) {
            b.addEnchantment(p.a(), p.b());
        }
        //Register the entry
        return new ItemEntry(b.build(), min, max);
    }),
    MOB("mob", ItemBuilder.builder(Util.convert(Util.HeadType.BASE64, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmM3MzJhZmRkNTNmYTgwZGJmMjI0ZmE1ZjhkMDIyN2FiZTU1M2UwMWU4ODIwYjlmYjA1NGZhYmY0ZGEzYjUwNSJ9fX0=")).setName(ChatColor.GOLD + "Mob").build(), (name) -> {
        EntityType t = EntityType.valueOf(Util.getOrError(name, "entityType"));
        SpawnEffect e = SpawnEffect.valueOf(Util.getOrError(name, "spawnEffect"));
        int min = Util.getOrError(name, "min");
        int max = Util.getOrError(name, "max");
        int health = Util.getOrDefault(name, "health", -1);
        double speedMultiplier = Util.getOrDefault(name, "speedMultiplier", -1d);
        String customName = Util.getOrDefault(name, "name", "");
        Map<MobSlot, Pair<ItemStack, Double>> loadedEquipment = new HashMap<>();
        if (name.contains("equipment")) {
            ConfigurationSection equipment = Util.getOrError(name, "equipment");
            for (String s : equipment.getKeys(false)) {
                ConfigurationSection pieceInfo = getOrError(equipment, s);
                ItemBuilder b = loadItemSection(pieceInfo);
                MobSlot mobSlot = MobSlot.valueOfOrNull(s.toUpperCase());
                if (mobSlot != null) {
                    Double dropChance = getOrDefault(pieceInfo, "dropChance", 0d);
                    loadedEquipment.put(mobSlot, new Pair<>(b.build(), dropChance));
                }
            }
        }
        return new MobEntry(t, min, max, health, speedMultiplier, customName, e, loadedEquipment);
    }),
    POTION("potion", PotionBuilder.builder(PotionBuilder.PotionFormat.SPLASH).setName(ChatColor.BLUE + "Potion").setPotionData(PotionType.SPEED, null).setLore().build(), (name) -> {
        ConfigurationSection effects = Util.getOrError(name, "effects");
        List<PotionEntryEffectWrapper> theEffects = new ArrayList<>();
        for (String effect : effects.getKeys(false)) {
            ConfigurationSection effectInfo = Util.getOrError(effects, effect);
            PotionEffectType t = PotionEffectType.getByKey(NamespacedKey.minecraft(Util.getOrError(effectInfo, "id").toString().toLowerCase()));
            int duration = Util.getOrDefault(effectInfo, "duration", 20);
            int lvl = Util.getOrDefault(effectInfo, "lvl", 1);
            theEffects.add(new PotionEntryEffectWrapper(t, duration, lvl));
        }
        return new PotionEntry(theEffects);
    }),
    WAND("wand", ItemBuilder.builder(Material.STICK).setName(ChatColor.LIGHT_PURPLE + "Wand").build(), name -> {
        String wand = Util.getOrError(name, "wand");
        int minPower = Util.getOrError(name, "minPower");
        int maxPower = Util.getOrError(name, "maxPower");
        String displayName = processText(Util.getOrError(name, "name"));
        List<String> lore = processTexts(Util.getOrError(name, "lore"));
        return new WandEntry(displayName, lore, minPower, maxPower, wand);
    }),
    COMMAND("command", ItemBuilder.builder(Material.COMMAND_BLOCK).setName(ChatColor.RED + "Command").build(), name -> {
        List<String> commands = Util.getOrError(name, "commands");
        return new CommandEntry(commands);
    }),
    STRUCTURE("structure", ItemBuilder.builder(Material.IRON_PICKAXE).setName(ChatColor.DARK_GRAY + "Structure").build(), name -> {
        if (!SimpleLuckyBlocksCore.isWorldEdit) {
            return null;
        }
        String structure = Util.getOrError(name, "structure");
        int offsetX = Util.getOrDefault(name, "offsetX", 0), offsetY = Util.getOrDefault(name, "offsetY", 0), offsetZ = Util.getOrDefault(name, "offsetZ", 0);
        return new StructureEntry(structure, offsetX, offsetY, offsetZ);
    });

    private final String id;
    private final ItemStack displayItem;
    private final Function<YamlConfiguration, IEntry> processFunction;

    EntryType(String id, ItemStack displayItem, Function<YamlConfiguration, IEntry> processFunction) {
        this.id = id;
        this.displayItem = displayItem;
        this.processFunction = processFunction;
    }

    public static EntryType getByName(String id) {
        for (EntryType t : values()) {
            if (Objects.equals(t.id, id)) {
                return t;
            }
        }
        return null;
    }

    public String id() {
        return id;
    }

    public ItemStack displayItem() {
        return displayItem;
    }

    public Function<YamlConfiguration, IEntry> processFunction() {
        return processFunction;
    }
}
