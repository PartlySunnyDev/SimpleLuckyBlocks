package me.partlysunny.gui.guis.loot.entry.creation.mob;

import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;

import java.util.function.BiConsumer;

public enum MobSlot {

    HELMET(EquipmentSlot.HEAD, EntityEquipment::setHelmetDropChance),
    CHESTPLATE(EquipmentSlot.CHEST, EntityEquipment::setChestplateDropChance),
    LEGGINGS(EquipmentSlot.LEGS, EntityEquipment::setLeggingsDropChance),
    BOOTS(EquipmentSlot.FEET, EntityEquipment::setBootsDropChance),
    MAIN_HAND(EquipmentSlot.HAND, EntityEquipment::setItemInMainHandDropChance),
    OFF_HAND(EquipmentSlot.OFF_HAND, EntityEquipment::setItemInOffHandDropChance);
    private final EquipmentSlot corresponding;
    private final BiConsumer<EntityEquipment, Float> setDropChance;

    MobSlot(EquipmentSlot corresponding, BiConsumer<EntityEquipment, Float> setDropChance) {
        this.corresponding = corresponding;
        this.setDropChance = setDropChance;
    }

    public static MobSlot valueOfOrNull(String s) {
        try {
            return MobSlot.valueOf(s);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public EquipmentSlot corresponding() {
        return corresponding;
    }

    public void modify(EntityEquipment e, Double f) {
        setDropChance.accept(e, f.floatValue());
    }
}
