package me.partlysunny.particle;

import org.apache.logging.log4j.util.TriConsumer;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;

public enum EffectType {

    AURA((particle, location, frequency) -> {
        World w = location.getWorld();
        if (w == null) {
            return;
        }
        w.spawnParticle(particle, location.add(0.5, 0.5, 0.5), (int) frequency, 0.5, 0.5, 0.5, 0);
    }),
    RING((particle, location, frequency) -> {
        int points = frequency * 2;
        double radius = 0.8d;

        location.add(0.5, 0, 0.5);

        for (int i = 0; i < points; i++) {
            double angle = 2 * Math.PI * i / points;
            Location point = location.clone().add(radius * Math.sin(angle), 0.0d, radius * Math.cos(angle));
            for (int j = 0; j < 3; j++) {
                location.getWorld().spawnParticle(particle, point.clone().add(0, j * 0.3f, 0), 1, 0.1d, 0.1d, 0.1d, 0d);
            }
        }
    });

    private final TriConsumer<Particle, Location, Integer> tick;

    EffectType(TriConsumer<Particle, Location, Integer> tick) {
        this.tick = tick;
    }

    public void tick(Particle p, Location l, int frequency) {
        tick.accept(p, l, frequency);
    }

}
