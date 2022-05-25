package me.partlysunny.listeners;

import de.tr7zw.nbtapi.NBTChunk;
import de.tr7zw.nbtapi.NBTCompound;
import me.partlysunny.SimpleLuckyBlocksCore;
import me.partlysunny.blocks.LuckyBlockManager;
import me.partlysunny.blocks.LuckyBlockType;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

public class LoadListener implements Listener {

    public static void load(Server s) {
        for (World w : s.getWorlds()) {
            for (Chunk c : w.getLoadedChunks()) {
                processChunk(c);
            }
        }
    }

    private static void processChunk(Chunk c) {
        NBTChunk nbtc = new NBTChunk(c);
        NBTCompound blocks = nbtc.getPersistentDataContainer().getOrCreateCompound("blocks");
        for (Entity en : c.getEntities()) {
            PersistentDataContainer persistentDataContainer = en.getPersistentDataContainer();
            Byte special = persistentDataContainer.get(new NamespacedKey(JavaPlugin.getPlugin(SimpleLuckyBlocksCore.class), "special"), PersistentDataType.BYTE);
            if (special != null && special == (byte) 1) {
                en.remove();
            }
        }
        for (String s : blocks.getKeys()) {
            NBTCompound nbt = blocks.getCompound(s);
            if (nbt.hasKey("luckyType")) {
                String[] pos = s.split("_");
                Block block = c.getWorld().getBlockAt(Integer.parseInt(pos[0]), Integer.parseInt(pos[1]), Integer.parseInt(pos[2]));
                LuckyBlockManager.loadAsLuckyBlock(block, LuckyBlockType.getType(nbt.getString("luckyType")));
            }
        }

    }

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

    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent e) {
        NBTChunk nbtc = new NBTChunk(e.getChunk());
        NBTCompound blocks = nbtc.getPersistentDataContainer().getOrCreateCompound("blocks");
        for (String s : blocks.getKeys()) {
            NBTCompound nbt = blocks.getCompound(s);
            if (nbt.hasKey("luckyType")) {
                String[] pos = s.split("_");
                LuckyBlockManager.unloadBlock(new Location(e.getWorld(), Integer.parseInt(pos[0]), Integer.parseInt(pos[1]), Integer.parseInt(pos[2])));
            }
        }
    }

}
