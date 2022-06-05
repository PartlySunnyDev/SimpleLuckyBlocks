package me.partlysunny.gui;

import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GuiManager {

    private static final Map<String, GuiInstance> guis = new HashMap<>();
    private static final Map<UUID, String> previousGuis = new HashMap<>();
    private static final Map<UUID, String> currentGuis = new HashMap<>();

    public static void openInventory(Player p, String id) {
        GuiInstance guiInstance = guis.get(id);
        if (guiInstance == null) {
            return;
        }
        UUID uniqueId = p.getUniqueId();
        if (currentGuis.containsKey(uniqueId)) {
            previousGuis.put(uniqueId, currentGuis.get(uniqueId));
        }
        currentGuis.put(uniqueId, id);
        guiInstance.openFor(p);
    }

    public static void registerGui(String id, GuiInstance gui) {
        guis.put(id, gui);
    }

    public static void unregisterGui(String id) {
        guis.remove(id);
    }

    @Nullable
    public static String getPreviousGui(UUID player) {
        return previousGuis.get(player);
    }

    @Nullable
    public static String getCurrentGui(UUID player) {
        return currentGuis.get(player);
    }

}
