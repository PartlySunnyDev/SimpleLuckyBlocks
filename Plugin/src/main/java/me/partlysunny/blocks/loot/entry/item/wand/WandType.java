package me.partlysunny.blocks.loot.entry.item.wand;

import me.partlysunny.util.Util;
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
    })
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
