package me.partlysunny.gui.guis.loot.entry.creation.mob.equipment;

import org.bukkit.inventory.ItemStack;

public class EquipmentWrapper {

    private final MobSlot slot;
    private ItemStack item;
    private double dropChance;

    public EquipmentWrapper(MobSlot slot, ItemStack item, double dropChance) {
        this.slot = slot;
        this.item = item;
        this.dropChance = dropChance;
    }

    public MobSlot slot() {
        return slot;
    }

    public ItemStack item() {
        return item;
    }

    public void setItem(ItemStack item) {
        this.item = item;
    }

    public double dropChance() {
        return dropChance;
    }

    public void setDropChance(double dropChance) {
        this.dropChance = dropChance;
    }
}
