package me.partlysunny.listeners;

import me.partlysunny.blocks.triggers.BlockTrigger;
import me.partlysunny.blocks.triggers.MobTrigger;
import me.partlysunny.blocks.triggers.TriggerManager;
import me.partlysunny.util.Util;
import org.bukkit.ChatColor;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import static me.partlysunny.util.Util.processText;

public class TriggerListener implements Listener {

    @EventHandler(priority = EventPriority.LOW)
    public void blockBreakEvent(BlockBreakEvent e) {
        for (BlockTrigger t : TriggerManager.getBlockTriggers()) {
            if (e.getBlock().getType() == t.m() && Util.RAND.nextDouble() < t.chance()) {
                t.reward().reward(e.getBlock().getLocation());
                if (!t.message().equals("")) {
                    e.getPlayer().sendMessage(ChatColor.GREEN + processText(t.message()));
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void entityKillEvent(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Player p) {
            if (e.getEntity() instanceof LivingEntity le) {
                if (le.getHealth() - e.getFinalDamage() <= 0) {
                    for (MobTrigger t : TriggerManager.getMobTriggers()) {
                        if (le.getType() == t.e() && Util.RAND.nextDouble() < t.chance()) {
                            t.reward().reward(le.getLocation());
                            if (!t.message().equals("")) {
                                p.sendMessage(ChatColor.GREEN + processText(t.message()));
                            }
                        }
                    }
                }
            }
        }
    }

}
