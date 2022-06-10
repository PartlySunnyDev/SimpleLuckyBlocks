package me.partlysunny.gui;

import me.partlysunny.gui.guis.common.*;
import me.partlysunny.gui.guis.common.item.ItemMakerSelectGui;
import me.partlysunny.gui.guis.common.item.enchant.EnchantCreationSelectGui;
import me.partlysunny.gui.guis.common.item.enchant.EnchantModifierSelectGui;
import me.partlysunny.gui.guis.common.material.MaterialSelectGui;
import me.partlysunny.gui.guis.loot.entry.creation.mob.equipment.MobEquipmentSelectGui;
import me.partlysunny.gui.guis.loot.entry.creation.potion.PotionEntrySectionSelectGui;

import java.util.HashMap;
import java.util.Map;

public class SelectGuiManager {

    private static final Map<String, SelectGui<?>> valueGuis = new HashMap<>();

    public static void registerValueGui(String id, SelectGui<?> valueGui) {
        valueGuis.put(id, valueGui);
        GuiManager.registerGui(id + "Select", valueGui);
    }

    public static SelectGui<?> getValueGui(String id) {
        return valueGuis.get(id);
    }

    public static void unregisterValueGui(String id) {
        valueGuis.remove(id);
    }

    public static void init() {
        registerValueGui("enchantment", new EnchantmentSelectGui());
        registerValueGui("material", new MaterialSelectGui());
        registerValueGui("entityType", new EntityTypeSelectGui());
        registerValueGui("potionEffectType", new PotionEffectTypeSelectGui());
        registerValueGui("potionEntrySection", new PotionEntrySectionSelectGui());
        registerValueGui("itemMaker", new ItemMakerSelectGui());
        registerValueGui("enchantModifier", new EnchantModifierSelectGui());
        registerValueGui("enchantCreation", new EnchantCreationSelectGui());
        registerValueGui("mobEquipment", new MobEquipmentSelectGui());
        registerValueGui("spawnEffect", new SpawnEffectSelectGui());
        registerValueGui("wandType", new WandTypeSelectGui());
        registerValueGui("wand", new WandSelectGui());
        registerValueGui("structure", new StructureSelectGui());
    }

}
