package me.partlysunny.util.classes;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class ConfigManager {

    public JavaPlugin plugin;
    public String fileName;

    public ConfigManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public YamlConfiguration createConfig(String name) {
        if (!name.endsWith(".yml")) {
            name = name + ".yml";
        }
        File file = new File(plugin.getDataFolder(), name);
        if (!file.exists()) {
            plugin.getDataFolder().mkdir();
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return YamlConfiguration.loadConfiguration(file);// returns the newly created configuration object.
    }

    public void saveConfig(String name, FileConfiguration config) {
        if (!name.endsWith(".yml")) {
            name = name + ".yml";
        }
        File file = new File(plugin.getDataFolder(), name);
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public YamlConfiguration getConfig(String name) {
        if (!name.endsWith(".yml")) {
            name = name + ".yml";
        }
        createConfig(name);
        File file = new File(plugin.getDataFolder(), name);
        return YamlConfiguration.loadConfiguration(file); // file found, load into config and return it.
    }

}