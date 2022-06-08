package me.partlysunny.worldedit;

import com.sk89q.worldedit.extent.clipboard.Clipboard;
import me.partlysunny.ConsoleLogger;
import me.partlysunny.SimpleLuckyBlocksCore;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class StructureManager {

    private static final Map<String, ClipboardWrapper> structures = new HashMap<>();

    public static void registerStructure(ClipboardWrapper structure) {
        structures.put(structure.id(), structure);
    }

    public static ClipboardWrapper getStructure(String id) {
        return structures.get(id);
    }

    public static void unregisterStructure(String id) {
        structures.remove(id);
    }

    public static void loadStructures() throws IOException {
        structures.clear();
        if (!SimpleLuckyBlocksCore.isWorldEdit) {
            ConsoleLogger.warn("WorldEdit was not found! Structures will not work!");
            return;
        }
        File dir = new File(JavaPlugin.getPlugin(SimpleLuckyBlocksCore.class).getDataFolder().getAbsolutePath() + "/structures");
        File[] directoryListing = dir.listFiles();
        if (directoryListing != null) {
            for (File child : directoryListing) {
                loadStructure(child);
            }
        } else {
            ConsoleLogger.error("FATAL ERROR: structures directory not found");
        }
    }

    private static void loadStructure(File file) throws IOException {
        Clipboard fromInput = WorldEditHook.getFromInput(file);
        if (fromInput == null) {
            return;
        }
        registerStructure(new ClipboardWrapper(file.getName().replaceFirst("[.][^.]+$", ""), fromInput));
    }

}
