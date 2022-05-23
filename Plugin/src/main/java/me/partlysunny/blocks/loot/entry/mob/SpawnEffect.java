package me.partlysunny.blocks.loot.entry.mob;

import org.bukkit.Location;

import java.util.function.Consumer;

public enum SpawnEffect {

    LIGHTNING(l -> l.getWorld().strikeLightningEffect(l)),
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
