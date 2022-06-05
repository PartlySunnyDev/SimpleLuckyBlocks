package me.partlysunny.blocks.triggers;

import me.partlysunny.ConsoleLogger;
import me.partlysunny.SimpleLuckyBlocksCore;
import me.partlysunny.util.Util;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class TriggerManager {

    private static final Map<String, BlockTrigger> blockTriggers = new HashMap<>();
    private static final Map<String, MobTrigger> mobTriggers = new HashMap<>();

    public static void registerBlockTrigger(String id, BlockTrigger e) {
        blockTriggers.put(id, e);
    }

    public static void unregisterBlockTrigger(String id) {
        blockTriggers.remove(id);
    }

    public static BlockTrigger getBlockTrigger(String id) {
        return blockTriggers.get(id);
    }

    public static Collection<BlockTrigger> getBlockTriggers() {
        return blockTriggers.values();
    }

    public static void registerMobTrigger(String id, MobTrigger e) {
        mobTriggers.put(id, e);
    }

    public static void unregisterMobTrigger(String id) {
        mobTriggers.remove(id);
    }

    public static MobTrigger getMobTrigger(String id) {
        return mobTriggers.get(id);
    }

    public static Collection<MobTrigger> getMobTriggers() {
        return mobTriggers.values();
    }

    public static void loadTriggers() {
        blockTriggers.clear();
        mobTriggers.clear();
        File dir = new File(JavaPlugin.getPlugin(SimpleLuckyBlocksCore.class).getDataFolder() + "/triggers");
        File[] directoryListing = dir.listFiles();
        if (directoryListing != null) {
            for (File child : directoryListing) {
                loadType(child.getName(), YamlConfiguration.loadConfiguration(child));
            }
        } else {
            ConsoleLogger.error("FATAL ERROR: triggers directory not found");
        }
    }

    private static void loadType(String childName, YamlConfiguration name) {
        String triggerType = Util.getOrError(name, "triggerType");
        double chance = Util.getOrError(name, "chance");
        String message = Util.getOrDefault(name, "message", "");
        ConfigurationSection reward = Util.getOrError(name, "reward");
        TriggerReward r = new TriggerReward(Util.getOrError(reward, "luckyBlockType"), Util.getOrError(reward, "min"), Util.getOrError(reward, "max"));
        String realName = childName.substring(0, childName.length() - 4);
        switch (triggerType) {
            case "block" -> {
                registerBlockTrigger(realName, new BlockTrigger(Material.getMaterial(Util.getOrError(name, "material")), chance, r, message));
            }
            case "entity" -> {
                registerMobTrigger(realName, new MobTrigger(EntityType.valueOf(Util.getOrError(name, "type")), chance, r, message));
            }
        }
    }

}
