package me.partlysunny.blocks.loot.entry.item.wand;

import me.partlysunny.util.Util;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.function.BiConsumer;

public enum WandType {

    LIGHTNING("lightning", (power, player) -> {
        for (int i = 0; i < power; i++) {
            if (Util.RAND.nextBoolean()) {
                player.getWorld().strikeLightning(player.getLocation().add(Util.getRandomBetween(4, 8), 0, Util.getRandomBetween(4, 8)));
            } else {
                player.getWorld().strikeLightning(player.getLocation().add(Util.getRandomBetween(-8, -4), 0, Util.getRandomBetween(-8, -4)));
            }
        }
    }),
    FIRESTORM("firestorm", (power, player) -> {
        Location l = player.getLocation();
        Util.scheduleRepeatingCancelTask(() -> {
            for (int i = -power; i < power; i++) {
                for (int j = -power; j < power; j++) {
                    for (int k = -power; k < power; k++) {
                        player.getWorld().spawnParticle(Particle.FLAME, l.add(i, k, j), 1, 0.5, 0.5, 0.5);
                    }
                }
            }
            for (Entity e : l.getWorld().getNearbyEntities(l, power, power, power)) {
                if (e instanceof LivingEntity le) {
                    le.damage(power / 3d);
                }
            }
        }, 0, 10, power * 20);
    }),
    HEAL("heal", (power, player) -> {
        double maxHealth = player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
        player.setHealth(Math.min(player.getHealth() + power, maxHealth));
        player.getWorld().spawnParticle(Particle.HEART, player.getLocation().add(0, 1, 0), 3, 0.5, 0.5, 0.5);
    }),
    REJUVENATE("rejuvenate", (power, player) -> {
        Util.scheduleRepeatingCancelTask(() -> {
            double maxHealth = player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
            player.setHealth(Math.min(player.getHealth() + (power / 2d), maxHealth));
            player.getWorld().spawnParticle(Particle.HEART, player.getLocation().add(0, 1, 0), 3, 0.5, 0.5, 0.5);
        }, 0, 20, power * 20);
    }),
    ;

    private final String id;
    private final BiConsumer<Integer, Player> action;

    WandType(String id, BiConsumer<Integer, Player> action) {
        this.action = action;
        this.id = id;
    }

    public String id() {
        return id;
    }

    public void action(int power, Player player) {
        action.accept(power, player);
    }
}
