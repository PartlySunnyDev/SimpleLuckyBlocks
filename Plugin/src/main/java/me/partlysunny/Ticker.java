package me.partlysunny;

import me.partlysunny.blocks.LuckyBlock;
import me.partlysunny.blocks.LuckyBlockManager;
import me.partlysunny.particle.BlockParticleEffect;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class Ticker implements Runnable {

    public Ticker() {
        Bukkit.getScheduler().runTaskTimer(JavaPlugin.getPlugin(SimpleLuckyBlocksCore.class), this, 0, 1);
    }

    @Override
    public void run() {
        for (LuckyBlock b : LuckyBlockManager.getBlocks()) {
            BlockParticleEffect e = b.type().e();
            if (e != null) {
                e.tick(b.b().getLocation());
            }
        }
        LuckyBlockManager.updateBlocks();
    }
}
