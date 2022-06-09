package me.partlysunny.gui;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public abstract class SelectGui<T> implements GuiInstance {

    protected final Map<UUID, T> values = new HashMap<>();
    protected final Map<UUID, String> guiToReturn = new HashMap<>();

    public T getValue(UUID player) {
        return values.get(player);
    }

    public void returnTo(Player player) {
        GuiManager.openInventory(player, guiToReturn.get(player.getUniqueId()));
    }

    public void resetValue(UUID player) {
        values.remove(player);
    }

    public void update(UUID player, String value) {
        values.put(player, getValueFromString(value));
    }

    public void setReturnTo(UUID player, String gui) {
        guiToReturn.put(player, gui);
    }

    public String getReturnTo(Player p) {
        return guiToReturn.get(p.getUniqueId());
    }

    public void openWithValue(Player p, T value, String name) {
        this.values.put(p.getUniqueId(), value);
        GuiManager.openInventory(p, name);
    }

    protected abstract T getValueFromString(String s);

}
