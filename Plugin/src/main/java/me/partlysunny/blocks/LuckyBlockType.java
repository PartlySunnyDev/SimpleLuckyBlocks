package me.partlysunny.blocks;

import me.partlysunny.blocks.loot.CustomLootTable;
import me.partlysunny.particle.BlockParticleEffect;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public record LuckyBlockType(String id, Material blockType, @Nullable ItemStack innerItem, CustomLootTable lootTable,
                             BlockParticleEffect e) {

    private static final Map<String, LuckyBlockType> types = new HashMap<>();

    public static void registerType(LuckyBlockType type) {
        types.put(type.id, type);
    }

    public static void unregisterType(LuckyBlockType type) {
        types.remove(type.id);
    }

    public static LuckyBlockType getType(String id) {
        return types.get(id);
    }

    public static void loadTypes() {

    }

}
