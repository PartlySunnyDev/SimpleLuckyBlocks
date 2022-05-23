package me.partlysunny.blocks.triggers;

import me.partlysunny.blocks.LuckyBlockType;
import me.partlysunny.util.Util;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;

public class TriggerReward {

    private final String luckyBlockType;
    private final int min;
    private final int max;

    public TriggerReward(String luckyBlockType, int min, int max) {
        this.luckyBlockType = luckyBlockType;
        this.min = min;
        this.max = max;
    }

    public void reward(Location l) {
        LuckyBlockType t = LuckyBlockType.getType(luckyBlockType);
        ItemStack luckyBlock = Util.produceLuckyBlock(t);
        int amount = Util.getRandomBetween(min, max);
        luckyBlock.setAmount(amount);
        World world = l.getWorld();
        world.dropItemNaturally(l, luckyBlock);
    }

    public String luckyBlockType() {
        return luckyBlockType;
    }

    public int min() {
        return min;
    }

    public int max() {
        return max;
    }
}
