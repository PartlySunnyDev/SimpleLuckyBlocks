package me.partlysunny.blocks;

import de.tr7zw.nbtapi.NBTItem;
import me.partlysunny.ConsoleLogger;
import me.partlysunny.SimpleLuckyBlocksCore;
import me.partlysunny.particle.BlockParticleEffect;
import me.partlysunny.particle.EffectType;
import me.partlysunny.util.Util;
import me.partlysunny.util.classes.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nullable;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static me.partlysunny.util.Util.processText;

public record LuckyBlockType(String id, String displayName, Material blockType, @Nullable ItemStack innerItem, String lootTable,
                             BlockParticleEffect e, @Nullable ShapedRecipe r) {

    private static final Map<String, LuckyBlockType> types = new HashMap<>();

    public static void registerType(LuckyBlockType type) {
        types.put(type.id, type);
        JavaPlugin.getPlugin(SimpleLuckyBlocksCore.class).getServer().addRecipe(type.r);
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
        ShapedRecipe sr = null;
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
        String substring = childName.substring(0, childName.length() - 4);
        if (name.contains("recipe")) {
            ConfigurationSection recipeInfo = name.getConfigurationSection("recipe");
            List<String> slots = recipeInfo.getStringList("slots");
            Map<Character, Material> keys = new HashMap<>();
            ConfigurationSection keysSection = recipeInfo.getConfigurationSection("keys");
            for (String c : keysSection.getKeys(true)) {
                keys.put(c.charAt(0), Material.getMaterial(keysSection.getString(c)));
            }
            ItemStack block;
            if (innerItem == null) {
                block = ItemBuilder.builder(mat).setName(processText(displayName)).build();
            } else {
                block = innerItem.clone();
                ItemMeta itemMeta = block.getItemMeta();
                itemMeta.setDisplayName(processText(displayName));
                block.setItemMeta(itemMeta);
            }
            NBTItem nbti = new NBTItem(block);
            nbti.setString("luckyType", substring);
            nbti.applyNBT(block);
            ShapedRecipe r = new ShapedRecipe(new NamespacedKey(JavaPlugin.getPlugin(SimpleLuckyBlocksCore.class), substring), block);
            r.shape(slots.toArray(new String[0]));
            for (Character c : keys.keySet()) {
                r.setIngredient(c, keys.get(c));
            }
            sr = r;
        }
        registerType(new LuckyBlockType(substring, displayName, mat, innerItem, lootTable, e, sr));
    }

}
