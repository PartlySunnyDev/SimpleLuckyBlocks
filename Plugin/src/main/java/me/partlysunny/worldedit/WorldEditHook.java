package me.partlysunny.worldedit;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.function.mask.SolidBlockMask;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.session.ClipboardHolder;
import jline.internal.Nullable;
import me.partlysunny.ConsoleLogger;
import org.apache.commons.io.FilenameUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

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
                    .maskSource(new SolidBlockMask(es))
                    .build();
            Operations.complete(operation);
        } catch (WorldEditException e) {
            ConsoleLogger.error("An internal WorldEdit related error occurred. Please contact the developer :)");
        }
    }


}
