package me.partlysunny;

import me.partlysunny.blocks.LuckyBlockType;
import me.partlysunny.blocks.StandManager;
import me.partlysunny.blocks.loot.CustomLootTable;
import me.partlysunny.blocks.loot.LootTableEntry;
import me.partlysunny.commands.SLBCommand;
import me.partlysunny.commands.SLBTabCompleter;
import me.partlysunny.commands.subcommands.GiveSubCommand;
import me.partlysunny.commands.subcommands.HelpSubCommand;
import me.partlysunny.listeners.BreakListener;
import me.partlysunny.listeners.LoadListener;
import me.partlysunny.listeners.PlaceListener;
import me.partlysunny.particle.BlockParticleEffect;
import me.partlysunny.particle.EffectType;
import me.partlysunny.util.Util;
import me.partlysunny.util.classes.ItemBuilder;
import me.partlysunny.util.classes.Pair;
import me.partlysunny.version.Version;
import me.partlysunny.version.VersionManager;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

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
        //Load saved lucky block types
        LuckyBlockType.loadTypes();
        //Create particle effect ticker
        new Ticker();
        //Register subcommands
        registerCommands();
        registerListeners();
        initDefaults();
        ConsoleLogger.console("Enabled SimpleLuckyBlocks on version " + v.get());
    }

    @Override
    public void onDisable() {
        manager.disable();
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

    private void initDefaults() {
        LuckyBlockType.registerType(new LuckyBlockType(
                "basic",
                Material.YELLOW_STAINED_GLASS,
                Util.convert(Util.HeadType.BASE64, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjM4YzBkMmYxZWMyNjc1NGRjYTNjN2NkYWUzMWYxZjE2NDg4M2Q0NTNlNjg4NjQzZGEwNDc1NjhlN2ZhNWNjOSJ9fX0="),
                new CustomLootTable(
                        "basic",
                        3,
                        new Pair<>(new LootTableEntry(new ItemStack(Material.IRON_INGOT), 1, 3), 2),
                        new Pair<>(new LootTableEntry(ItemBuilder.builder(Material.STICK).setName("KB stick").addEnchantment(Enchantment.KNOCKBACK, 1).build(), 1, 1), 1),
                        new Pair<>(new LootTableEntry(new ItemStack(Material.OAK_PLANKS), 4, 7), 6),
                        new Pair<>(new LootTableEntry(new ItemStack(Material.COBBLESTONE), 3, 5), 4)),
                new BlockParticleEffect(Particle.GLOW, 3, EffectType.AURA)
        ));
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new LoadListener(), this);
        getServer().getPluginManager().registerEvents(new PlaceListener(), this);
        getServer().getPluginManager().registerEvents(new BreakListener(), this);
    }
}
