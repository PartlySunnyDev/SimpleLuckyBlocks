package me.partlysunny.listeners;

import me.partlysunny.blocks.LuckyBlockManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;

public class BreakListener implements Listener {

    @EventHandler
    public void onPlayerBreakBlock(BlockBreakEvent e) {
        Block block = e.getBlock();
        Location location = block.getLocation();
        cancelIfLuckyBlock(block, e);
        LuckyBlockManager.breakLuckyBlock(location);
        block.getWorld().playSound(location, Sound.BLOCK_STONE_BREAK, 1, 1);
        block.getWorld().setBlockData(location, Material.AIR.createBlockData());
    }

    @EventHandler
    public void onBlockExplode(BlockExplodeEvent e) {
        cancelIfLuckyBlock(e.getBlock(), e);
    }

    @EventHandler
    public void onBlockBurn(BlockBurnEvent e) {
        cancelIfLuckyBlock(e.getBlock(), e);
    }

    @EventHandler
    public void onBlockOther(BlockFadeEvent e) {
        cancelIfLuckyBlock(e.getBlock(), e);
    }

    @EventHandler
    public void onBlockDecay(LeavesDecayEvent e) {
        cancelIfLuckyBlock(e.getBlock(), e);
    }

    private void cancelIfLuckyBlock(Block b, Cancellable e) {
        if (LuckyBlockManager.isLuckyBlock(b.getLocation())) {
            e.setCancelled(true);
        }
    }
}
