package me.partlysunny.blocks.triggers;

import org.bukkit.entity.EntityType;

public class MobTrigger {
    private final EntityType e;
    private final double chance;
    private final TriggerReward reward;
    private final String message;

    public MobTrigger(EntityType e, double chance, TriggerReward reward, String message) {
        this.e = e;
        this.chance = chance;
        this.reward = reward;
        this.message = message;
    }

    public String message() {
        return message;
    }

    public EntityType e() {
        return e;
    }

    public double chance() {
        return chance;
    }

    public TriggerReward reward() {
        return reward;
    }
}
