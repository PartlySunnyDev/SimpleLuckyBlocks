package me.partlysunny;

import me.partlysunny.blocks.LuckyBlockType;
import me.partlysunny.blocks.StandManager;
import me.partlysunny.blocks.triggers.TriggerManager;
import me.partlysunny.commands.SLBCommand;
import me.partlysunny.commands.SLBTabCompleter;
import me.partlysunny.commands.subcommands.*;
import me.partlysunny.gui.GuiManager;
import me.partlysunny.gui.guis.MainPageGui;
import me.partlysunny.gui.guis.common.ValueGuiManager;
import me.partlysunny.gui.guis.loot.LootMenuGui;
import me.partlysunny.gui.guis.loot.entry.EntryCreationGui;
import me.partlysunny.gui.guis.loot.entry.EntryManagementGui;
import me.partlysunny.gui.guis.loot.entry.creation.item.ItemEntryCreateGui;
import me.partlysunny.gui.guis.loot.entry.creation.potion.PotionEntryCreateGui;
import me.partlysunny.gui.textInput.ChatListener;
import me.partlysunny.listeners.*;
import me.partlysunny.util.Util;
import me.partlysunny.version.Version;
import me.partlysunny.version.VersionManager;
import me.partlysunny.worldedit.WorldEditHook;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.security.CodeSource;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static me.partlysunny.blocks.loot.LootTableManager.loadLootTables;
import static me.partlysunny.blocks.loot.entry.LootEntryManager.loadEntries;
import static me.partlysunny.blocks.loot.entry.item.wand.WandManager.loadWands;
import static me.partlysunny.worldedit.StructureManager.loadStructures;

public final class SimpleLuckyBlocksCore extends JavaPlugin {

    public static boolean isWorldEdit = false;
    private static VersionManager manager;

    public static VersionManager manager() {
        return manager;
    }

    @Override
    public void onEnable() {
        //Get version
        Version v = new Version(this.getServer().getVersion());
        ConsoleLogger.console("Enabling SimpleLuckyBlocks...");
        //Load modules (currently not used)
        manager = new VersionManager(this);
        manager.checkServerVersion();
        try {
            manager.load();
        } catch (ReflectiveOperationException e) {
            ConsoleLogger.error("This version (" + v.get() + ") is not supported by SimpleLuckyBlocks!", "Shutting down plugin...");
            setEnabled(false);
            return;
        }
        if (Bukkit.getPluginManager().isPluginEnabled("WorldEdit")) {
            isWorldEdit = WorldEditHook.init();
        } else {
            isWorldEdit = false;
        }
        manager.enable();
        Metrics metrics = new Metrics(this, 15259);
        //Copy in default files if not existent
        try {
            initDefaults();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        //Create particle effect ticker
        new Ticker();
        //Register subcommands
        registerCommands();
        registerListeners();
        try {
            loadStructures();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        loadWands();
        //Load loot table entries
        loadEntries();
        //Load loot tables (combinations of these entries)
        loadLootTables();
        //Load saved lucky block types
        LuckyBlockType.loadTypes();
        TriggerManager.loadTriggers();
        LoadListener.load(getServer());
        ValueGuiManager.init();
        registerGuis();
        ConsoleLogger.console("Enabled SimpleLuckyBlocks on version " + v.get());
    }

    @Override
    public void onDisable() {
        if (manager != null) {
            manager.disable();
        }
        //Kill all armor stands
        StandManager.killAll();
        ConsoleLogger.console("Disabling SimpleLuckyBlocks...");
    }

    private void registerGuis() {
        GuiManager.registerGui("lootMenu", new LootMenuGui());
        GuiManager.registerGui("mainPage", new MainPageGui());
        GuiManager.registerGui("entryCreation", new EntryCreationGui());
        GuiManager.registerGui("entryManagement", new EntryManagementGui());
        GuiManager.registerGui("potionEntryCreate", new PotionEntryCreateGui());
        GuiManager.registerGui("itemEntryCreate", new ItemEntryCreateGui());
    }

    private void registerCommands() {
        SLBCommand.registerSubCommand(new HelpSubCommand());
        SLBCommand.registerSubCommand(new GiveSubCommand());
        SLBCommand.registerSubCommand(new GiveWandSubCommand());
        SLBCommand.registerSubCommand(new GenBlocksSubCommand());
        SLBCommand.registerSubCommand(new LuckyMenuSubCommand());
        getCommand("slb").setExecutor(new SLBCommand());
        getCommand("slb").setTabCompleter(new SLBTabCompleter());
    }

    private void initDefaults() throws IOException {
        if (!getDataFolder().exists()) {
            getDataFolder().mkdir();
        }
        process("blocks");
        process("lootEntries");
        process("lootTables");
        process("wands");
        process("triggers");
        process("structures");
        copyFileWithName("READ.txt");
        copyFileWithName("config.yml");
    }

    private void copyFileWithName(String key) throws IOException {
        File f = getDataFolder();
        if (!f.exists()) {
            f.mkdir();
        }
        CodeSource src = SimpleLuckyBlocksCore.class.getProtectionDomain().getCodeSource();
        if (src != null) {
            URL jar = src.getLocation();
            ZipInputStream zip = new ZipInputStream(jar.openStream());
            while (true) {
                ZipEntry e = zip.getNextEntry();
                if (e == null)
                    break;
                String name = e.getName();
                if (name.equals(key)) {
                    File destination = new File(f + "/" + key);
                    Util.copy(name, destination, false);
                }
            }
        }
    }

    private void process(String key) throws IOException {
        process(key, false);
    }

    private void process(String key, boolean rezip) throws IOException {
        File f = new File(getDataFolder(), key);
        if (!f.exists()) {
            f.mkdir();
        }
        CodeSource src = SimpleLuckyBlocksCore.class.getProtectionDomain().getCodeSource();
        if (src != null) {
            URL jar = src.getLocation();
            ZipInputStream zip = new ZipInputStream(jar.openStream());
            while (true) {
                ZipEntry e = zip.getNextEntry();
                if (e == null)
                    break;
                String name = e.getName();
                if (name.startsWith(key + "/") && !name.equals(key + "/")) {
                    File destination = new File(f + "/" + name.substring(key.length() + 1));
                    Util.copy(name, destination, rezip);
                }
            }
        }
    }

    private void registerListeners() {
        PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(new LoadListener(), this);
        pluginManager.registerEvents(new PlaceListener(), this);
        pluginManager.registerEvents(new BreakListener(), this);
        pluginManager.registerEvents(new WandListener(), this);
        pluginManager.registerEvents(new TriggerListener(), this);
        pluginManager.registerEvents(new ChatListener(), this);
    }
}
