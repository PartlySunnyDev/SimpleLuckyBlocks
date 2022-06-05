package me.partlysunny.blocks.loot.entry;

import me.partlysunny.ConsoleLogger;
import me.partlysunny.SimpleLuckyBlocksCore;
import me.partlysunny.util.classes.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashMap;
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
        entryTypes.clear();
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
        EntryType t = EntryType.getByName(entryType);
        IEntry apply = t.processFunction().apply(name);
        if (apply == null) {
            return;
        }
        registerEntry(realName, apply);
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
