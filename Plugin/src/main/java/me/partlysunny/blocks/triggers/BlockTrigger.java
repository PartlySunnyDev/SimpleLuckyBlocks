package me.partlysunny.blocks.triggers;

import org.bukkit.Material;

public class BlockTrigger {

    private final Material m;
    private final double chance;
    private final TriggerReward reward;
    private final String message;

    public BlockTrigger(Material m, double chance, TriggerReward reward, String message) {
        this.m = m;
        this.chance = chance;
        this.reward = reward;
        this.message = message;
    }

    public String message() {
        return message;
    }

    public Material m() {
        return m;
    }

    public double chance() {
        return chance;
    }

    public TriggerReward reward() {
        return reward;
    }
}
