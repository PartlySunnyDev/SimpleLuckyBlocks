package me.partlysunny.blocks;

import me.partlysunny.ConsoleLogger;
import me.partlysunny.SimpleLuckyBlocksCore;
import me.partlysunny.particle.BlockParticleEffect;
import me.partlysunny.particle.EffectType;
import me.partlysunny.util.Util;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nullable;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public record LuckyBlockType(String id, String displayName, Material blockType, @Nullable ItemStack innerItem, String lootTable,
                             BlockParticleEffect e) {

    private static final Map<String, LuckyBlockType> types = new HashMap<>();

    public static void registerType(LuckyBlockType type) {
        types.put(type.id, type);
    }

    public static void unregisterType(String id) {
        types.remove(id);
    }

    public static LuckyBlockType getType(String id) {
        return types.get(id);
    }

    public static void loadTypes() {
        File dir = new File(JavaPlugin.getPlugin(SimpleLuckyBlocksCore.class).getDataFolder() + "/blocks");
        File[] directoryListing = dir.listFiles();
        if (directoryListing != null) {
            for (File child : directoryListing) {
                loadType(child.getName(), YamlConfiguration.loadConfiguration(child));
            }
        } else {
            ConsoleLogger.error("FATAL ERROR: blocks directory not found");
        }
    }

    private static void loadType(String childName, YamlConfiguration name) {
        Material mat = Material.getMaterial(name.getString("blockType"));
        String lootTable = name.getString("lootTable");
        String displayName = "Lucky Block";
        ItemStack innerItem = null;
        BlockParticleEffect e = null;
        if (name.contains("displayName")) {
            displayName = name.getString("displayName");
        }
        if (name.contains("innerItem")) {
            ConfigurationSection itemInfo = name.getConfigurationSection("innerItem");
            String type = itemInfo.getString("type");
            switch (type) {
                case "block" -> innerItem = new ItemStack(Material.getMaterial(itemInfo.getString("material")));
                case "skull" -> innerItem = Util.convert(Util.HeadType.BASE64, itemInfo.getString("value"));
            }
        }
        if (name.contains("blockParticleEffect")) {
            ConfigurationSection effectInfo = name.getConfigurationSection("blockParticleEffect");
            EffectType type = EffectType.valueOf(effectInfo.getString("type").toUpperCase());
            int frequency = effectInfo.getInt("frequency");
            Particle particle = Particle.valueOf(effectInfo.getString("particle"));
            e = new BlockParticleEffect(particle, frequency, type);
        }
        registerType(new LuckyBlockType(childName.substring(0, childName.length() - 4), displayName, mat, innerItem, lootTable, e));
    }

}
