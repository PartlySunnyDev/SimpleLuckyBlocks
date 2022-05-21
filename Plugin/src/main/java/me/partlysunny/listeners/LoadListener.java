package me.partlysunny.listeners;

import de.tr7zw.nbtapi.NBTChunk;
import de.tr7zw.nbtapi.NBTCompound;
import me.partlysunny.SimpleLuckyBlocksCore;
import me.partlysunny.blocks.LuckyBlockManager;
import me.partlysunny.blocks.LuckyBlockType;
import me.partlysunny.util.Util;
import org.bukkit.Chunk;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

public class LoadListener implements Listener {

    @EventHandler
    public void onWorldLoad(WorldLoadEvent e) {
        for (Chunk c : e.getWorld().getLoadedChunks()) {
            processChunk(c);
        }
    }

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent e) {
        processChunk(e.getChunk());
    }

    private void processChunk(Chunk c) {
        NBTChunk nbtc = new NBTChunk(c);
        NBTCompound blocks = nbtc.getPersistentDataContainer().getOrCreateCompound("blocks");
        for (String s : blocks.getKeys()) {
            NBTCompound nbt = blocks.getCompound(s);
            if (nbt.hasKey("luckyType")) {
                String[] pos = s.split("_");
                Block block = c.getBlock(Integer.parseInt(pos[0]) % 16, Integer.parseInt(pos[1]) % 16, Integer.parseInt(pos[2]) % 16);
                LuckyBlockManager.loadAsLuckyBlock(block, LuckyBlockType.getType(nbt.getString("luckyType")));
            }
        }
        for (Entity en : c.getEntities()) {
            if (en.getPersistentDataContainer().get(new NamespacedKey(JavaPlugin.getPlugin(SimpleLuckyBlocksCore.class), "special"), PersistentDataType.BYTE) == (byte) 1) {
                en.remove();
            }
        }

    }

}
