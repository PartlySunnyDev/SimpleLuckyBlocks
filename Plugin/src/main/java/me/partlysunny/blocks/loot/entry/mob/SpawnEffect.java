package me.partlysunny.blocks.loot.entry.mob;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;

import java.util.function.Consumer;

public enum SpawnEffect {

    LIGHTNING(l -> l.getWorld().strikeLightningEffect(l)),
    EXPLOSION_EFFECT(l -> {
        l.getWorld().spawnParticle(Particle.EXPLOSION_HUGE, l, 1);
        l.getWorld().playSound(l, Sound.ENTITY_GENERIC_EXPLODE, 1, 1);
    }),
    EXPLOSION_REAL(l -> {
        l.getWorld().createExplosion(l, 2);
    }),
    NONE(l -> {
    }),
    ;

    private final Consumer<Location> effect;

    SpawnEffect(Consumer<Location> effect) {
        this.effect = effect;
    }

    public void play(Location l) {
        effect.accept(l);
    }
}
