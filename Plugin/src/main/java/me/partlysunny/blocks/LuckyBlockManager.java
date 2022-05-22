package me.partlysunny.blocks;

import de.tr7zw.nbtapi.NBTBlock;
import me.partlysunny.util.Util;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class LuckyBlockManager {

    private static final Map<Location, LuckyBlock> blocks = new HashMap<>();

    public static void loadAsLuckyBlock(Block b, LuckyBlockType type) {
        blocks.put(b.getLocation(), new LuckyBlock(b, type));
    }

    public static boolean isLuckyBlock(Location l) {
        return blocks.containsKey(l);
    }

    public static void breakLuckyBlock(Player p, Location l) {
        if (blocks.containsKey(l)) {
            LuckyBlock b = blocks.get(l);
            new NBTBlock(b.b()).getData().removeKey("luckyType");
            b.killStand();
            b.dropLoot(p);
            blocks.remove(l);
        }
    }

    public static Collection<LuckyBlock> getBlocks() {
        return blocks.values();
    }

    public static void updateBlocks() {
        for (LuckyBlock b : getBlocks()) {
            Block block = b.b();
            Util.setToLuckyBlockType(block, b.type().id());
        }
    }

}
