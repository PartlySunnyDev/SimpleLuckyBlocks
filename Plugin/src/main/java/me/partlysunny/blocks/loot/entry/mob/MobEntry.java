package me.partlysunny.blocks.loot.entry.mob;

import me.partlysunny.blocks.loot.entry.EntryType;
import me.partlysunny.blocks.loot.entry.IEntry;
import me.partlysunny.gui.guis.loot.entry.creation.mob.equipment.MobSlot;
import me.partlysunny.util.Util;
import me.partlysunny.util.classes.ItemBuilder;
import me.partlysunny.util.classes.Pair;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class MobEntry implements IEntry {

    private static final ItemStack emptySlot = ItemBuilder.builder(Material.BLACK_STAINED_GLASS_PANE).setName(ChatColor.RED + "Empty Slot!").build();
    private final Map<MobSlot, Pair<ItemStack, Double>> equipment;
    private EntityType entityType;
    private int health;
    private double speedMultiplier;
    private String name;
    private SpawnEffect spawnEffect;
    private int min;
    private int max;

    public MobEntry() {
        this(EntityType.ARROW, 0, 0, 10, 1, "", SpawnEffect.NONE, new HashMap<>());
    }

    public MobEntry(EntityType entityType, int min, int max, int health, double speedMultiplier, String name, SpawnEffect spawnEffect, Map<MobSlot, Pair<ItemStack, Double>> equipment) {
        this.entityType = entityType;
        this.health = health;
        this.speedMultiplier = speedMultiplier;
        this.name = name;
        this.spawnEffect = spawnEffect;
        this.max = max;
        this.min = min;
        this.equipment = equipment;
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
                for (MobSlot s : this.equipment.keySet()) {
                    equipment.setItem(s.corresponding(), getEquipment(s));
                }
                for (MobSlot s : this.equipment.keySet()) {
                    s.modify(equipment, getEquipmentDropChance(s));
                }
                if (health != -1) {
                    AttributeInstance attribute = le.getAttribute(Attribute.GENERIC_MAX_HEALTH);
                    attribute.setBaseValue(health);
                    le.setHealth(health);
                }
                if (speedMultiplier != -1) {
                    AttributeInstance attribute = le.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED);
                    attribute.setBaseValue(attribute.getBaseValue() * speedMultiplier);
                }
                if (!name.equals("")) {
                    le.setCustomName(name);
                    le.setCustomNameVisible(true);
                }
            }
            spawnEffect.play(realLoc);
        }
    }

    public void setEquipment(MobSlot slot, ItemStack s) {
        if (!equipment.containsKey(slot)) {
            equipment.put(slot, new Pair<>(emptySlot.clone(), 0d));
        }
        Pair<ItemStack, Double> info = equipment.get(slot);
        info.setA(s);
    }

    public void setEquipmentDropChance(MobSlot slot, Double f) {
        if (!equipment.containsKey(slot)) {
            equipment.put(slot, new Pair<>(emptySlot.clone(), 0d));
        }
        Pair<ItemStack, Double> info = equipment.get(slot);
        info.setB(f);
    }

    public ItemStack getEquipment(MobSlot slot) {
        Pair<ItemStack, Double> info = equipment.getOrDefault(slot, new Pair<>(emptySlot.clone(), 0d));
        if (info.a().getItemMeta() == null) {
            throw new IllegalArgumentException("Equipment has no meta!");
        }
        return info.a();
    }

    public Double getEquipmentDropChance(MobSlot slot) {
        Pair<ItemStack, Double> info = equipment.getOrDefault(slot, new Pair<>(emptySlot.clone(), 0d));
        return info.b();
    }

    public EntityType entityType() {
        return entityType;
    }

    public void setEntityType(EntityType entityType) {
        this.entityType = entityType;
    }

    public int health() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public double speedMultiplier() {
        return speedMultiplier;
    }

    public void setSpeedMultiplier(double speedMultiplier) {
        this.speedMultiplier = speedMultiplier;
    }

    public String name() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SpawnEffect spawnEffect() {
        return spawnEffect;
    }

    public void setSpawnEffect(SpawnEffect spawnEffect) {
        this.spawnEffect = spawnEffect;
    }

    public int min() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public int max() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    @Override
    public YamlConfiguration getSave() {
        YamlConfiguration config = new YamlConfiguration();
        config.set("entryType", "mob");
        config.set("entityType", entityType.toString());
        config.set("min", min);
        config.set("max", max);
        config.set("health", health);
        config.set("speedMultiplier", speedMultiplier);
        config.set("name", name);
        config.set("spawnEffect", spawnEffect.toString());
        YamlConfiguration equipmentSection = new YamlConfiguration();
        for (MobSlot s : equipment.keySet()) {
            Pair<ItemStack, Double> info = equipment.get(s);
            YamlConfiguration itemSection = new YamlConfiguration();
            itemSection.set("dropChance", info.b());
            itemSection.set("material", info.a().getType().toString());
            itemSection.set("enchantments", Util.getEnchantSection(info.a().getEnchantments()));
            equipmentSection.set(s.toString(), itemSection);
        }
        config.set("equipment", equipmentSection);
        return config;
    }

    @Override
    public EntryType getEntryType() {
        return EntryType.MOB;
    }

}
