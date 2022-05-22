package me.partlysunny.blocks.loot;

import me.partlysunny.ConsoleLogger;
import me.partlysunny.SimpleLuckyBlocksCore;
import me.partlysunny.util.classes.Pair;
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
        int rolls = name.getInt("rolls");
        List<Pair<String, Integer>> entryInfos = new ArrayList<>();
        ConfigurationSection entries = name.getConfigurationSection("entries");
        for (String entry : entries.getKeys(false)) {
            entryInfos.add(new Pair<>(entry, entries.getInt(entry)));
        }
        registerTable(childName.substring(0, childName.length() - 4), new CustomLootTable(rolls, entryInfos));
    }

}
