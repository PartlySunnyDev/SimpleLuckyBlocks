package me.partlysunny.blocks.loot;

import me.partlysunny.ConsoleLogger;
import me.partlysunny.SimpleLuckyBlocksCore;
import me.partlysunny.util.Util;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LootTableManager {

    private static final Map<String, CustomLootTable> tables = new HashMap<>();

    public static void registerTable(String id, CustomLootTable e) {
        tables.put(id, e);
    }

    public static void unregisterTable(String id) {
        tables.remove(id);
    }

    public static CustomLootTable getTable(String id) {
        return tables.get(id);
    }

    public static void loadLootTables() {
        tables.clear();
        File dir = new File(JavaPlugin.getPlugin(SimpleLuckyBlocksCore.class).getDataFolder() + "/lootTables");
        File[] directoryListing = dir.listFiles();
        if (directoryListing != null) {
            for (File child : directoryListing) {
                loadTable(child.getName(), YamlConfiguration.loadConfiguration(child));
            }
        } else {
            ConsoleLogger.error("FATAL ERROR: lootTables directory not found");
        }
    }

    private static void loadTable(String childName, YamlConfiguration name) {
        int rolls = Util.getOrError(name, "rolls");
        List<TableEntryWrapper> entryInfos = new ArrayList<>();
        ConfigurationSection entries = Util.getOrError(name, "entries");
        for (String entry : entries.getKeys(false)) {
            ConfigurationSection entryInfo = Util.getOrError(entries, entry);
            String message = Util.getOrDefault(entryInfo, "message", "");
            entryInfos.add(new TableEntryWrapper(entry, Util.getOrError(entryInfo, "weight"), message));
        }
        registerTable(childName.substring(0, childName.length() - 4), new CustomLootTable(rolls, entryInfos));
    }

    public static String[] getEntryKeys() {
        return tables.keySet().toArray(new String[0]);
    }
}
