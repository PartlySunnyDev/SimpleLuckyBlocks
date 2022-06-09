package me.partlysunny.gui.guis.loot.entry.creation;

import me.partlysunny.gui.GuiManager;
import me.partlysunny.gui.guis.loot.entry.creation.command.CommandEntryCreateGui;
import me.partlysunny.gui.guis.loot.entry.creation.item.ItemEntryCreateGui;
import me.partlysunny.gui.guis.loot.entry.creation.mob.MobEntryCreateGui;
import me.partlysunny.gui.guis.loot.entry.creation.potion.PotionEntryCreateGui;
import me.partlysunny.gui.guis.loot.entry.creation.wand.WandEntryCreateGui;

import java.util.HashMap;
import java.util.Map;

public class CreateGuiManager {

    private static final Map<String, EntryCreateGui<?>> createGuis = new HashMap<>();

    public static void registerCreateGui(String id, EntryCreateGui<?> createGui) {
        createGuis.put(id, createGui);
        GuiManager.registerGui(id + "Create", createGui);
    }

    public static EntryCreateGui<?> getCreateGui(String id) {
        return createGuis.get(id);
    }

    public static void unregisterCreateGui(String id) {
        createGuis.remove(id);
    }

    public static void init() {
        registerCreateGui("potionEntry", new PotionEntryCreateGui());
        registerCreateGui("commandEntry", new CommandEntryCreateGui());
        registerCreateGui("itemEntry", new ItemEntryCreateGui());
        registerCreateGui("mobEntry", new MobEntryCreateGui());
        registerCreateGui("wandEntry", new WandEntryCreateGui());
    }

}
