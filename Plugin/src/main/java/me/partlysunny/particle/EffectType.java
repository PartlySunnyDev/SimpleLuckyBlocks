package me.partlysunny.particle;

import org.apache.logging.log4j.util.TriConsumer;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;

public enum EffectType {

    SWIRL((particle, location, frequency) -> {
        // TODO swirl

    }),
    SPARK((particle, location, frequency) -> {
        // TODO spark
    }),
    AURA((particle, location, frequency) -> {
        World w = location.getWorld();
        if (w == null) {
            return;
        }
        w.spawnParticle(particle, location.add(0.5, 0.5, 0.5), (int) frequency, 0.5, 0.5, 0.5, 0);
    }),
    RING((particle, location, frequency) -> {
        // TODO ring
    });

    private final TriConsumer<Particle, Location, Integer> tick;

    EffectType(TriConsumer<Particle, Location, Integer> tick) {
        this.tick = tick;
    }

    public void tick(Particle p, Location l, int frequency) {
        tick.accept(p, l, frequency);
    }

}
