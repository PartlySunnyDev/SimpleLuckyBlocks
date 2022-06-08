package me.partlysunny.gui.guis.loot.entry.creation.mob.equipment;

import me.partlysunny.gui.guis.common.material.filters.FilterManager;
import org.bukkit.Material;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;

import java.math.BigDecimal;
import java.util.function.BiConsumer;

public enum MobSlot {

    HELMET(EquipmentSlot.HEAD, EntityEquipment::setHelmetDropChance, new String[]{"_HELMET", "SKULL", "HEAD", "CARVED_PUMPKIN"}),
    CHESTPLATE(EquipmentSlot.CHEST, EntityEquipment::setChestplateDropChance, new String[]{"_CHESTPLATE", "ELYTRA"}),
    LEGGINGS(EquipmentSlot.LEGS, EntityEquipment::setLeggingsDropChance, new String[]{"_LEGGINGS"}),
    BOOTS(EquipmentSlot.FEET, EntityEquipment::setBootsDropChance, new String[]{"_BOOTS"}),
    MAIN_HAND(EquipmentSlot.HAND, EntityEquipment::setItemInMainHandDropChance, new String[]{""}),
    OFF_HAND(EquipmentSlot.OFF_HAND, EntityEquipment::setItemInOffHandDropChance, new String[]{""});
    private final EquipmentSlot corresponding;
    private final BiConsumer<EntityEquipment, Float> setDropChance;
    private final String[] filter;

    MobSlot(EquipmentSlot corresponding, BiConsumer<EntityEquipment, Float> setDropChance, String[] filter) {
        this.corresponding = corresponding;
        this.setDropChance = setDropChance;
        this.filter = filter;
    }

    public static MobSlot valueOfOrNull(String s) {
        try {
            return MobSlot.valueOf(s);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public boolean matchesFilter(Material m) {
        for (String s : filter) {
            if (m.toString().endsWith(s)) {
                return true;
            }
        }
        return false;
    }

    public EquipmentSlot corresponding() {
        return corresponding;
    }

    public void modify(EntityEquipment e, Double f) {
        setDropChance.accept(e, new BigDecimal(f).floatValue());
    }

    public Material getValidMaterial() {
        for (Material m : Material.values()) {
            if (matchesFilter(m) && FilterManager.getFilter("meta").doesQualify(m)) {
                return m;
            }
        }
        return Material.WOODEN_AXE;
    }
}
