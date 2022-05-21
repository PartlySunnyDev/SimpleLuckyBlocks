package me.partlysunny.particle;

import org.bukkit.Location;
import org.bukkit.Particle;

public class BlockParticleEffect {

    private final Particle p;
    private final int frequency;
    private final EffectType type;

    public BlockParticleEffect(Particle p, int frequency, EffectType type) {
        this.p = p;
        this.frequency = frequency;
        this.type = type;
    }

    public void tick(Location l) {
        type.tick(p, l, frequency);
    }

    public Particle p() {
        return p;
    }

    public int frequency() {
        return frequency;
    }

    public EffectType type() {
        return type;
    }
}
