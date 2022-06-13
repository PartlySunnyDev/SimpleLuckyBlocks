package me.partlysunny.gui;

import me.partlysunny.gui.guis.block.creation.recipe.RecipeSelectGui;
import me.partlysunny.gui.guis.common.*;
import me.partlysunny.gui.guis.common.item.ItemMakerSelectGui;
import me.partlysunny.gui.guis.common.item.enchant.EnchantCreationSelectGui;
import me.partlysunny.gui.guis.common.item.enchant.EnchantModifierSelectGui;
import me.partlysunny.gui.guis.common.material.MaterialSelectGui;
import me.partlysunny.gui.guis.loot.entry.creation.mob.equipment.MobEquipmentSelectGui;
import me.partlysunny.gui.guis.loot.entry.creation.potion.PotionEntrySectionSelectGui;
import me.partlysunny.gui.guis.loot.table.entry.TableEntrySelectGui;

import java.util.HashMap;
import java.util.Map;

public class SelectGuiManager {

    private static final Map<String, SelectGui<?>> selectGuis = new HashMap<>();

    public static void registerSelectGui(String id, SelectGui<?> selectGui) {
        selectGuis.put(id, selectGui);
        GuiManager.registerGui(id + "Select", selectGui);
    }

    public static SelectGui<?> getSelectGui(String id) {
        return selectGuis.get(id);
    }

    public static void unregisterSelectGui(String id) {
        selectGuis.remove(id);
    }

    public static void init() {
        registerSelectGui("enchantment", new EnchantmentSelectGui());
        registerSelectGui("material", new MaterialSelectGui());
        registerSelectGui("entityType", new EntityTypeSelectGui());
        registerSelectGui("potionEffectType", new PotionEffectTypeSelectGui());
        registerSelectGui("potionEntrySection", new PotionEntrySectionSelectGui());
        registerSelectGui("itemMaker", new ItemMakerSelectGui());
        registerSelectGui("enchantModifier", new EnchantModifierSelectGui());
        registerSelectGui("enchantCreation", new EnchantCreationSelectGui());
        registerSelectGui("mobEquipment", new MobEquipmentSelectGui());
        registerSelectGui("spawnEffect", new SpawnEffectSelectGui());
        registerSelectGui("wandType", new WandTypeSelectGui());
        registerSelectGui("wand", new WandSelectGui());
        registerSelectGui("structure", new StructureSelectGui());
        registerSelectGui("tableEntry", new TableEntrySelectGui());
        registerSelectGui("entryType", new EntryTypeSelectGui());
        registerSelectGui("tableType", new TableTypeSelectGui());
        registerSelectGui("recipe", new RecipeSelectGui());
    }

}
