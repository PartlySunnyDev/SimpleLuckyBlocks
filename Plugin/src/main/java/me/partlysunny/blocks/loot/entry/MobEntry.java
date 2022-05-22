package me.partlysunny.blocks.loot.entry;

import me.partlysunny.util.Util;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

public class MobEntry implements IEntry {

    private final EntityType entityType;
    private final ItemStack[] armorPieces;
    private final ItemStack heldItem;
    private final int health;
    private final double speedMultiplier;
    private final String name;
    private final SpawnEffect spawnEffect;
    private final int min;
    private final int max;
    private final float helmDrop;
    private final float chestDrop;
    private final float legsDrop;
    private final float bootsDrop;
    private final float mainHandDrop;

    public MobEntry(EntityType entityType, int min, int max, ItemStack[] armorPieces, ItemStack heldItem, int health, double speedMultiplier, String name, SpawnEffect spawnEffect, float helmDrop, float chestDrop, float legsDrop, float bootsDrop, float mainHandDrop) {
        this.entityType = entityType;
        this.armorPieces = armorPieces;
        this.heldItem = heldItem;
        this.health = health;
        this.speedMultiplier = speedMultiplier;
        this.name = name;
        this.spawnEffect = spawnEffect;
        this.max = max;
        this.min = min;
        this.helmDrop = helmDrop;
        this.chestDrop = chestDrop;
        this.legsDrop = legsDrop;
        this.bootsDrop = bootsDrop;
        this.mainHandDrop = mainHandDrop;
    }

    public MobEntry(EntityType entityType, int min, int max, ItemStack[] armorPieces, ItemStack heldItem, int health, double speedMultiplier, String name, SpawnEffect spawnEffect) {
        this(entityType, min, max, armorPieces, heldItem, health, speedMultiplier, name, spawnEffect, 0, 0, 0, 0, 0);
    }

    public MobEntry(EntityType entityType, int min, int max, String name, SpawnEffect spawnEffect) {
        this(entityType, min, max, new ItemStack[]{null, null, null, null}, null, -1, -1, name, spawnEffect);
    }

    @Override
    public void execute(Location l, Player p) {
        World world = l.getWorld();
        if (world == null) {
            return;
        }
        for (int i = 0; i < Util.getRandomBetween(min, max); i++) {
            Location realLoc = l.add(Util.RAND.nextDouble(4) - 2, 0, Util.RAND.nextDouble(4) - 2);
            Entity e = world.spawnEntity(realLoc, entityType);
            if (e instanceof LivingEntity le) {
                EntityEquipment equipment = le.getEquipment();
                equipment.setArmorContents(armorPieces);
                equipment.setItemInMainHand(heldItem);
                equipment.setBootsDropChance(bootsDrop);
                equipment.setHelmetDropChance(helmDrop);
                equipment.setLeggingsDropChance(legsDrop);
                equipment.setChestplateDropChance(chestDrop);
                equipment.setItemInMainHandDropChance(mainHandDrop);
                if (health != -1) {
                    le.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(health);
                }
                if (speedMultiplier != -1) {
                    AttributeInstance attribute = le.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED);
                    attribute.setBaseValue(attribute.getBaseValue() * 2);
                }
                if (!name.equals("")) {
                    le.setCustomName(name);
                    le.setCustomNameVisible(true);
                }
            }
            spawnEffect.play(realLoc);
        }
    }

}
