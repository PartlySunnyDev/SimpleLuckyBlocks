package me.partlysunny.gui.guis.loot.entry.creation.potion;

import org.bukkit.potion.PotionEffectType;

public class PotionEntryEffectWrapper {
    
    private PotionEffectType type;
    private int duration;
    private int amplifier;

    public PotionEntryEffectWrapper() {
        this(PotionEffectType.ABSORPTION, 0, 0);
    }

    public PotionEntryEffectWrapper(PotionEffectType type, int duration, int amplifier) {
        this.type = type;
        this.duration = duration;
        this.amplifier = amplifier;
    }

    public PotionEffectType type() {
        return type;
    }

    public void setType(PotionEffectType type) {
        this.type = type;
    }

    public int duration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int amplifier() {
        return amplifier;
    }

    public void setAmplifier(int amplifier) {
        this.amplifier = amplifier;
    }
}
