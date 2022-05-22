package me.partlysunny.blocks.loot.entry;

import me.partlysunny.ConsoleLogger;
import me.partlysunny.SimpleLuckyBlocksCore;
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
                Material material = Material.valueOf(name.getString("material"));
                int min = name.getInt("min");
                int max = name.getInt("max");
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
                    b.setName(name.getString("name"));
                }
                if (name.contains("lore")) {
                    b.setLore(name.getStringList("lore").toArray(new String[0]));
                }
                for (Pair<Enchantment, Integer> p : enchants) {
                    b.addEnchantment(p.a(), p.b());
                }
                //Register the entry
                registerEntry(realName, new ItemEntry(b.build(), min, max));
            }
            case "mob" -> {
                EntityType t = EntityType.valueOf(name.getString("entityType"));
                SpawnEffect e = SpawnEffect.valueOf(name.getString("spawnEffect"));
                int min = name.getInt("min");
                int max = name.getInt("max");
                int health = -1;
                double speedMultiplier = -1;
                ItemStack[] armorPieces = new ItemStack[4];
                ItemStack itemOnHand = null;
                float dropH = 0;
                float dropC = 0;
                float dropL = 0;
                float dropB = 0;
                float dropM = 0;
                String customName = "";
                if (name.contains("name")) {
                    customName = name.getString("name");
                }
                if (name.contains("health")) {
                    health = name.getInt("health");
                }
                if (name.contains("speedMultiplier")) {
                    speedMultiplier = name.getDouble("speedMultiplier");
                }
                if (name.contains("armorPieces")) {
                    ConfigurationSection armor = name.getConfigurationSection("armorPieces");
                    for (String s : armor.getKeys(false)) {
                        ConfigurationSection pieceInfo = armor.getConfigurationSection(s);
                        Material m = Material.valueOf(pieceInfo.getString("material"));
                        ItemBuilder b = ItemBuilder.builder(m);
                        if (pieceInfo.contains("enchantments")) {
                            ConfigurationSection enchantments = pieceInfo.getConfigurationSection("enchantments");
                            for (String z : enchantments.getKeys(false)) {
                                ConfigurationSection info = enchantments.getConfigurationSection(z);
                                b.addEnchantment(Enchantment.getByKey(NamespacedKey.minecraft(info.getString("id").toLowerCase())), info.getInt("lvl"));
                            }
                        }
                        switch (s) {
                            case "helmet" -> {
                                armorPieces[3] = b.build();
                                if (pieceInfo.contains("dropChance")) {
                                    dropH = (float) pieceInfo.getDouble("dropChance");
                                }
                            }
                            case "boots" -> {
                                armorPieces[0] = b.build();
                                if (pieceInfo.contains("dropChance")) {
                                    dropB = (float) pieceInfo.getDouble("dropChance");
                                }
                            }
                            case "chestplate" -> {
                                armorPieces[2] = b.build();
                                if (pieceInfo.contains("dropChance")) {
                                    dropC = (float) pieceInfo.getDouble("dropChance");
                                }
                            }
                            case "leggings" -> {
                                armorPieces[1] = b.build();
                                if (pieceInfo.contains("dropChance")) {
                                    dropL = (float) pieceInfo.getDouble("dropChance");
                                }
                            }
                        }
                    }
                }
                if (name.contains("mainHand")) {
                    ConfigurationSection mainHandInfo = name.getConfigurationSection("mainHand");
                    Material m = Material.valueOf(mainHandInfo.getString("material"));
                    ItemBuilder b = ItemBuilder.builder(m);
                    if (mainHandInfo.contains("enchantments")) {
                        ConfigurationSection enchantments = mainHandInfo.getConfigurationSection("enchantments");
                        for (String z : enchantments.getKeys(false)) {
                            ConfigurationSection info = enchantments.getConfigurationSection(z);
                            b.addEnchantment(Enchantment.getByKey(new NamespacedKey(JavaPlugin.getPlugin(SimpleLuckyBlocksCore.class), info.getString("id").toUpperCase())), info.getInt("lvl"));
                        }
                    }
                    itemOnHand = b.build();
                }
                registerEntry(realName, new MobEntry(t, min, max, armorPieces, itemOnHand, health, speedMultiplier, customName, e, dropH, dropC, dropL, dropB, dropM));
            }
            case "potion" -> {
                ConfigurationSection effects = name.getConfigurationSection("effects");
                List<Pair<PotionEffectType, Pair<Integer, Integer>>> theEffects = new ArrayList<>();
                for (String effect : effects.getKeys(false)) {
                    ConfigurationSection effectInfo = effects.getConfigurationSection(effect);
                    PotionEffectType t = PotionEffectType.getByKey(NamespacedKey.minecraft(effectInfo.getString("id").toLowerCase()));
                    int duration = effectInfo.getInt("duration");
                    int lvl = effectInfo.getInt("lvl");
                    theEffects.add(new Pair<>(t, new Pair<>(duration, lvl)));
                }
                registerEntry(realName, new PotionEntry(theEffects));
            }
            default -> {
                ConsoleLogger.error("Invalid entry type found in " + name.getName());
            }
        }
    }

}
