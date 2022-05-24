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
            for (int i = -(power * 2); i < (power * 2); i += 2) {
                for (int j = -(power * 2); j < (power * 2); j += 2) {
                    for (int k = 0; k < power; k += 2) {
                        player.getWorld().spawnParticle(Particle.DRIP_LAVA, l.clone().add(i, k, j), 1, 0.5, 0.5, 0.5, 0);
                    }
                }
            }
            for (Entity e : l.getWorld().getNearbyEntities(l, (power * 2), (power * 2), (power * 2))) {
                if (e instanceof LivingEntity le && !e.equals(player)) {
                    le.damage((power * 2) / 3d);
                }
            }
        }, 0, 2, power * 20);
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
