package me.partlysunny.listeners;

import me.partlysunny.util.Util;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;

public class RL implements Listener {

    private static int counter = 0;

    @EventHandler
    public void sneak(PlayerToggleSneakEvent e) {
        if (e.isSneaking()) {
            counter = 0;
            Location location = e.getPlayer().getLocation().clone();
            Util.scheduleRepeatingCancelTask(() -> {
                int points = 20;
                double radius = counter;
                for (int i = 0; i < points; i++) {
                    double angle = 2 * Math.PI * i / points;
                    Location point = location.clone().add(radius * Math.sin(angle), 0.0d, radius * Math.cos(angle));
                    location.getWorld().spawnParticle(Particle.DUST_COLOR_TRANSITION, point.clone(), 1, 0.0d, 0.0d, 0.0d, 0d, new Particle.DustTransition(Color.fromRGB(255, 255, 255), Color.fromBGR(0, 152, 255), 2.5F));
                    location.getWorld().spawnParticle(Particle.SNOWBALL, point.clone(), 3);
                }
                counter++;
            }, 0, 4, 20);
        }
    }

}
