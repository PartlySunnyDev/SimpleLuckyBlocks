package me.partlysunny.blocks.loot.entry;

import me.partlysunny.ConsoleLogger;
import me.partlysunny.SimpleLuckyBlocksCore;
import me.partlysunny.blocks.loot.entry.command.CommandEntry;
import me.partlysunny.blocks.loot.entry.item.ItemEntry;
import me.partlysunny.blocks.loot.entry.item.wand.WandEntry;
import me.partlysunny.blocks.loot.entry.mob.MobEntry;
import me.partlysunny.blocks.loot.entry.mob.SpawnEffect;
import me.partlysunny.blocks.loot.entry.potion.PotionEntry;
import me.partlysunny.blocks.loot.entry.structure.StructureEntry;
import me.partlysunny.util.Util;
import me.partlysunny.util.classes.ItemBuilder;
import me.partlysunny.util.classes.Pair;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffectType;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static me.partlysunny.util.Util.*;

public class LootEntryManager {

    private static final Map<String, IEntry> entryTypes = new HashMap<>();

    public static void registerEntry(String id, IEntry e) {
        entryTypes.put(id, e);
    }

    public static void unregisterEntry(String id) {
        entryTypes.remove(id);
    }

    public static IEntry getEntry(String id) {
        return entryTypes.get(id);
    }

    public static void loadEntries() {
        File dir = new File(JavaPlugin.getPlugin(SimpleLuckyBlocksCore.class).getDataFolder() + "/lootEntries");
        File[] directoryListing = dir.listFiles();
        if (directoryListing != null) {
            for (File child : directoryListing) {
                loadEntry(child.getName(), YamlConfiguration.loadConfiguration(child));
            }
        } else {
            ConsoleLogger.error("FATAL ERROR: lootEntries directory not found");
        }
    }

    private static void loadEntry(String childName, YamlConfiguration name) {
        if (!name.contains("entryType")) {
            ConsoleLogger.error("Invalid loot entry found " + name.getName());
            return;
        }
        String entryType = name.getString("entryType");
        String realName = childName.substring(0, childName.length() - 4);
        switch (entryType) {
            case "item" -> {
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
                registerEntry(realName, new ItemEntry(b.build(), min, max));
            }
            case "mob" -> {
                EntityType t = EntityType.valueOf(Util.getOrError(name, "entityType"));
                SpawnEffect e = SpawnEffect.valueOf(Util.getOrError(name, "spawnEffect"));
                int min = Util.getOrError(name, "min");
                int max = Util.getOrError(name, "max");
                int health = Util.getOrDefault(name, "health", -1);
                double speedMultiplier = Util.getOrDefault(name, "speedMultiplier", -1d);
                ItemStack[] armorPieces = new ItemStack[4];
                ItemStack itemOnHand = null;
                float dropH = 0;
                float dropC = 0;
                float dropL = 0;
                float dropB = 0;
                float dropM = 0;
                String customName = Util.getOrDefault(name, "name", "");
                if (name.contains("armorPieces")) {
                    ConfigurationSection armor = Util.getOrError(name, "armorPieces");
                    for (String s : armor.getKeys(false)) {
                        ConfigurationSection pieceInfo = getOrError(armor, s);
                        ItemBuilder b = loadItemSection(pieceInfo);
                        switch (s) {
                            case "helmet" -> {
                                armorPieces[3] = b.build();
                                dropH = getOrDefault(pieceInfo, "dropChance", 0);
                            }
                            case "boots" -> {
                                armorPieces[0] = b.build();
                                dropB = getOrDefault(pieceInfo, "dropChance", 0);
                            }
                            case "chestplate" -> {
                                armorPieces[2] = b.build();
                                dropC = getOrDefault(pieceInfo, "dropChance", 0);
                            }
                            case "leggings" -> {
                                armorPieces[1] = b.build();
                                dropL = getOrDefault(pieceInfo, "dropChance", 0);
                            }
                        }
                    }
                }
                if (name.contains("mainHand")) {
                    ConfigurationSection mainHandInfo = Util.getOrError(name, "mainHand");
                    ItemBuilder b = loadItemSection(mainHandInfo);

                    itemOnHand = b.build();
                }
                registerEntry(realName, new MobEntry(t, min, max, armorPieces, itemOnHand, health, speedMultiplier, customName, e, dropH, dropC, dropL, dropB, dropM));
            }
            case "potion" -> {
                ConfigurationSection effects = Util.getOrError(name, "effects");
                List<Pair<PotionEffectType, Pair<Integer, Integer>>> theEffects = new ArrayList<>();
                for (String effect : effects.getKeys(false)) {
                    ConfigurationSection effectInfo = Util.getOrError(effects, effect);
                    PotionEffectType t = PotionEffectType.getByKey(NamespacedKey.minecraft(Util.getOrError(effectInfo, "id").toString().toLowerCase()));
                    int duration = Util.getOrDefault(effectInfo, "duration", 20);
                    int lvl = Util.getOrDefault(effectInfo, "lvl", 1);
                    theEffects.add(new Pair<>(t, new Pair<>(duration, lvl)));
                }
                registerEntry(realName, new PotionEntry(theEffects));
            }
            case "wand" -> {
                String wand = Util.getOrError(name, "wand");
                int minPower = Util.getOrError(name, "minPower");
                int maxPower = Util.getOrError(name, "maxPower");
                String displayName = processText(Util.getOrError(name, "name"));
                List<String> lore = processTexts(Util.getOrError(name, "lore"));
                registerEntry(realName, new WandEntry(displayName, lore, minPower, maxPower, wand));
            }
            case "command" -> {
                List<String> commands = Util.getOrError(name, "commands");
                registerEntry(realName, new CommandEntry(commands));
            }
            case "structure" -> {
                if (!SimpleLuckyBlocksCore.isWorldEdit) {
                    return;
                }
                String structure = Util.getOrError(name, "structure");
                double offsetX = Util.getOrDefault(name, "offsetX", 0d), offsetY = Util.getOrDefault(name, "offsetY", 0d), offsetZ = Util.getOrDefault(name, "offsetZ", 0d);
                registerEntry(realName, new StructureEntry(structure, offsetX, offsetY, offsetZ));
            }
            default -> {
                ConsoleLogger.error("Invalid entry type found in " + name.getName());
            }
        }
    }

    public static ItemBuilder loadItemSection(ConfigurationSection mainHandInfo) {
        Material m = Material.valueOf(mainHandInfo.getString("material"));
        ItemBuilder b = ItemBuilder.builder(m);
        if (mainHandInfo.contains("enchantments")) {
            ConfigurationSection enchantments = mainHandInfo.getConfigurationSection("enchantments");
            for (String z : enchantments.getKeys(false)) {
                ConfigurationSection info = enchantments.getConfigurationSection(z);
                b.addEnchantment(Enchantment.getByKey(NamespacedKey.minecraft(info.getString("id").toLowerCase())), info.getInt("lvl"));
            }
        }
        return b;
    }

}
