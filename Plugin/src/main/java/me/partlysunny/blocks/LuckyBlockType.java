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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static me.partlysunny.util.Util.processText;

public final class LuckyBlockType {

    private static final Map<String, LuckyBlockType> types = new HashMap<>();
    private final String id;
    private final String displayName;
    private final Material blockType;
    @Nullable
    private final ItemStack innerItem;
    private final String lootTable;
    private final BlockParticleEffect e;
    @Nullable
    private final ShapedRecipe r;

    public LuckyBlockType(String id, String displayName, Material blockType, @Nullable ItemStack innerItem, String lootTable,
                          BlockParticleEffect e, @Nullable ShapedRecipe r) {
        this.id = id;
        this.displayName = displayName;
        this.blockType = blockType;
        this.innerItem = innerItem;
        this.lootTable = lootTable;
        this.e = e;
        this.r = r;
    }

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
        Material mat = Material.getMaterial(Util.getOrError(name, "blockType"));
        String lootTable = Util.getOrError(name, "lootTable");
        String displayName = Util.getOrDefault(name, "displayName", "Lucky Block");
        ItemStack innerItem = null;
        BlockParticleEffect e = null;
        ShapedRecipe sr = null;
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
            ConfigurationSection recipeInfo = Util.getOrError(name, "recipe");
            List<String> slots = Util.getOrError(recipeInfo, "slots");
            Map<Character, Material> keys = new HashMap<>();
            ConfigurationSection keysSection = Util.getOrError(recipeInfo, "keys");
            for (String c : keysSection.getKeys(true)) {
                keys.put(c.charAt(0), Material.getMaterial(Util.getOrError(keysSection, c)));
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

    public String id() {
        return id;
    }

    public String displayName() {
        return displayName;
    }

    public Material blockType() {
        return blockType;
    }

    @Nullable
    public ItemStack innerItem() {
        return innerItem;
    }

    public String lootTable() {
        return lootTable;
    }

    public BlockParticleEffect e() {
        return e;
    }

    @Nullable
    public ShapedRecipe r() {
        return r;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (LuckyBlockType) obj;
        return Objects.equals(this.id, that.id) &&
                Objects.equals(this.displayName, that.displayName) &&
                Objects.equals(this.blockType, that.blockType) &&
                Objects.equals(this.innerItem, that.innerItem) &&
                Objects.equals(this.lootTable, that.lootTable) &&
                Objects.equals(this.e, that.e) &&
                Objects.equals(this.r, that.r);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, displayName, blockType, innerItem, lootTable, e, r);
    }

    @Override
    public String toString() {
        return "LuckyBlockType[" +
                "id=" + id + ", " +
                "displayName=" + displayName + ", " +
                "blockType=" + blockType + ", " +
                "innerItem=" + innerItem + ", " +
                "lootTable=" + lootTable + ", " +
                "e=" + e + ", " +
                "r=" + r + ']';
    }


}
