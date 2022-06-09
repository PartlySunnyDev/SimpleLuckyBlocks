package me.partlysunny.blocks.loot.entry.wand;

import me.partlysunny.blocks.loot.entry.EntryType;
import me.partlysunny.blocks.loot.entry.IEntry;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class WandEntry implements IEntry {

    private String displayName;
    private List<String> lore;
    private int minPower;
    private int maxPower;
    private String wandType;

    public WandEntry(String displayName, List<String> lore, int minPower, int maxPower, String wandType) {
        this.displayName = displayName;
        this.lore = lore;
        this.minPower = minPower;
        this.maxPower = maxPower;
        this.wandType = wandType;
    }

    public WandEntry() {
        this("", new ArrayList<>(List.of()), 0, 0, "lightning");
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public List<String> lore() {
        return lore;
    }

    public void setLore(List<String> lore) {
        this.lore = lore;
    }

    public void setMinPower(int minPower) {
        this.minPower = minPower;
    }

    public void setMaxPower(int maxPower) {
        this.maxPower = maxPower;
    }

    public void setWandType(String wandType) {
        this.wandType = wandType;
    }

    public String displayName() {
        return displayName;
    }

    public int minPower() {
        return minPower;
    }

    public int maxPower() {
        return maxPower;
    }

    public String wandType() {
        return wandType;
    }

    @Override
    public void execute(Location l, Player p) {
        World world = l.getWorld();
        if (world == null) {
            return;
        }
        world.dropItemNaturally(l, WandManager.getWand(wandType).generate(displayName, lore, minPower, maxPower));
    }

    @Override
    public YamlConfiguration getSave() {
        YamlConfiguration config = new YamlConfiguration();
        config.set("entryType", "wand");
        config.set("name", displayName);
        config.set("lore", lore);
        config.set("wand", wandType);
        config.set("minPower", minPower);
        config.set("maxPower", maxPower);
        return config;
    }

    @Override
    public EntryType getEntryType() {
        return EntryType.WAND;
    }
}
