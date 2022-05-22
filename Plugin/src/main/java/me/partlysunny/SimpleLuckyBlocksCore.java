package me.partlysunny;

import me.partlysunny.blocks.LuckyBlockType;
import me.partlysunny.blocks.StandManager;
import me.partlysunny.commands.SLBCommand;
import me.partlysunny.commands.SLBTabCompleter;
import me.partlysunny.commands.subcommands.GiveSubCommand;
import me.partlysunny.commands.subcommands.HelpSubCommand;
import me.partlysunny.listeners.BreakListener;
import me.partlysunny.listeners.LoadListener;
import me.partlysunny.listeners.PlaceListener;
import me.partlysunny.listeners.WandListener;
import me.partlysunny.util.Util;
import me.partlysunny.version.Version;
import me.partlysunny.version.VersionManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.CodeSource;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static me.partlysunny.blocks.loot.LootTableManager.loadLootTables;
import static me.partlysunny.blocks.loot.entry.LootEntryManager.loadEntries;
import static me.partlysunny.blocks.loot.entry.item.wand.WandManager.loadWands;

public final class SimpleLuckyBlocksCore extends JavaPlugin {

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
        manager.enable();
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
        loadWands();
        //Load loot table entries
        loadEntries();
        //Load loot tables (combinations of these entries)
        loadLootTables();
        //Load saved lucky block types
        LuckyBlockType.loadTypes();
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

    private void registerCommands() {
        SLBCommand.registerSubCommand(new HelpSubCommand());
        SLBCommand.registerSubCommand(new GiveSubCommand());
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
            while(true) {
                ZipEntry e = zip.getNextEntry();
                if (e == null)
                    break;
                String name = e.getName();
                if (name.equals(key)) {
                    File destination = new File(f + "/" + key);
                    InputStream from = SimpleLuckyBlocksCore.class.getClassLoader().getResourceAsStream(name);
                    Util.copy(from, destination);
                }
            }
        }
    }

    private void process(String key) throws IOException {
        File f = new File(getDataFolder(), key);
        if (!f.exists()) {
            f.mkdir();
        }
        CodeSource src = SimpleLuckyBlocksCore.class.getProtectionDomain().getCodeSource();
        if (src != null) {
            URL jar = src.getLocation();
            ZipInputStream zip = new ZipInputStream(jar.openStream());
            while(true) {
                ZipEntry e = zip.getNextEntry();
                if (e == null)
                    break;
                String name = e.getName();
                if (name.startsWith(key + "/") && !name.equals(key + "/")) {
                    File destination = new File(f + "/" + name.substring(key.length() + 1));
                    InputStream from = SimpleLuckyBlocksCore.class.getClassLoader().getResourceAsStream(name);
                    Util.copy(from, destination);
                }
            }
        }
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new LoadListener(), this);
        getServer().getPluginManager().registerEvents(new PlaceListener(), this);
        getServer().getPluginManager().registerEvents(new BreakListener(), this);
        getServer().getPluginManager().registerEvents(new WandListener(), this);
    }
}
