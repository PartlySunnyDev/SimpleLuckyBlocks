package me.partlysunny.worldedit;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.session.ClipboardHolder;
import me.partlysunny.ConsoleLogger;
import me.partlysunny.SimpleLuckyBlocksCore;
import me.partlysunny.blocks.LuckyBlockManager;
import me.partlysunny.blocks.LuckyBlockType;
import me.partlysunny.util.Util;
import org.apache.commons.io.FilenameUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class WorldEditHook {

    private static WorldEdit w = null;

    public static boolean init() {
        if (Bukkit.getPluginManager().isPluginEnabled("WorldEdit")) {
            ConsoleLogger.console("WorldEdit detected. Hooking into plugin...");
            w = WorldEdit.getInstance();
            return true;
        }
        ConsoleLogger.warn("WorldEdit was not detected. Some functionality may not be avaliable");
        return false;
    }

    public static Clipboard getFromInput(File f) throws IOException {
        if (w == null) {
            ConsoleLogger.warn("A world edit operation was requested without WorldEdit! Some functionality may not have activated!");
            return null;
        }
        ClipboardFormat byFile = ClipboardFormats.findByFile(f);
        if (byFile == null) {
            byFile = ClipboardFormats.findByAlias(FilenameUtils.getExtension(f.getName()));
            if (byFile == null) {
                return null;
            }
        }
        return byFile.getReader(new FileInputStream(f)).read();
    }

    public static void paste(String s, Location l) {
        if (w == null) {
            ConsoleLogger.warn("A world edit operation was requested without WorldEdit! Some functionality may not have activated!");
            return;
        }
        World world = l.getWorld();
        if (world == null) {
            return;
        }
        try (EditSession es = w.newEditSession(BukkitAdapter.adapt(world))) {
            Operation operation = new ClipboardHolder(StructureManager.getStructure(s).clipboard())
                    .createPaste(es)
                    .to(BlockVector3.at(l.getX(), l.getY(), l.getZ()))
                    .copyEntities(true)
                    .copyBiomes(false)
                    .ignoreAirBlocks(true)
                    .build();
            Operations.complete(operation);
        } catch (WorldEditException e) {
            ConsoleLogger.error("An internal WorldEdit related error occurred. Please contact the developer :)");
        }
    }

    public static void placeRandomLuckyBlocksInSelection(CommandSender executor, int blockCount, LuckyBlockType t) {
        Region selection;
        try {
            selection = WorldEditHook.w().getSessionManager().get(BukkitAdapter.adapt(executor)).getSelection();
        } catch (IncompleteRegionException e) {
            executor.sendMessage(ChatColor.RED + "Please select a valid region (use //wand and select)");
            return;
        }
        com.sk89q.worldedit.world.World world = selection.getWorld();
        if (world == null) {
            return;
        }
        org.bukkit.World bukkitWorld = BukkitAdapter.adapt(world);
        int placed = 0;
        for (BlockVector3 b : selection) {
            long l = Util.RAND.nextLong((long) selection.getLength() * selection.getWidth());
            if (l < blockCount && placed < blockCount) {
                Block bukkitBlock = getHighestBlock(world, b, selection.getBoundingBox().getMaximumY(), selection.getBoundingBox().getMinimumY());
                Location toSpawnLocation = bukkitBlock.getLocation().add(0, 1, 0);
                if (toSpawnLocation.getY() > SimpleLuckyBlocksCore.manager().getWorldMaxHeight(bukkitWorld)) {
                    continue;
                }
                bukkitWorld.setBlockData(toSpawnLocation, t.blockType().createBlockData());
                Block blockAt = bukkitWorld.getBlockAt(toSpawnLocation);
                Util.setToLuckyBlockType(blockAt, LuckyBlockType.getIdOfType(t));
                LuckyBlockManager.loadAsLuckyBlock(blockAt, t);
                placed++;
            }
        }
    }

    private static Block getHighestBlock(com.sk89q.worldedit.world.World w, BlockVector3 l, int maxY, int minY) {
        int highest = Integer.MIN_VALUE;
        for (int i = minY; i < maxY + 1; i++) {
            if (BukkitAdapter.adapt(w.getBlock(BlockVector3.at(l.getX(), i + 1, l.getZ())).getBlockType()).isAir() && !BukkitAdapter.adapt(w.getBlock(BlockVector3.at(l.getX(), i, l.getZ())).getBlockType()).isAir()) {
                highest = i;
            }
        }
        return BukkitAdapter.adapt(w).getBlockAt(new Location(BukkitAdapter.adapt(w), l.getX(), highest, l.getZ()));
    }

    public static WorldEdit w() {
        return w;
    }


}
