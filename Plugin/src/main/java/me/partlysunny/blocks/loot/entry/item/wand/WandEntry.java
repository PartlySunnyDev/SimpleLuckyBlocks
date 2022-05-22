package me.partlysunny.blocks.loot.entry.item.wand;

import me.partlysunny.blocks.loot.entry.IEntry;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.List;

public class WandEntry implements IEntry {

    private final String displayName;
    private final List<String> lore;
    private final int minPower;
    private final int maxPower;
    private final String wandType;

    public WandEntry(String displayName, List<String> lore, int minPower, int maxPower, String wandType) {
        this.displayName = displayName;
        this.lore = lore;
        this.minPower = minPower;
        this.maxPower = maxPower;
        this.wandType = wandType;
    }

    @Override
    public void execute(Location l, Player p) {
        World world = l.getWorld();
        if (world == null) {
            return;
        }
        world.dropItemNaturally(l, WandManager.getWand(wandType).generate(displayName, lore, minPower, maxPower));
    }
}
