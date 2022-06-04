package me.partlysunny.gui.textInput;

import me.partlysunny.SimpleLuckyBlocksCore;
import me.partlysunny.gui.GuiManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.function.Consumer;

public class ChatListener implements Listener {

    private static final Map<UUID, String> currentInput = new HashMap<>();
    private static final Map<UUID, String> lastGui = new HashMap<>();
    private static final List<UUID> typing = new ArrayList<>();
    private static final Map<UUID, Consumer<Player>> todos = new HashMap<>();

    @EventHandler
    public void onChatMessage(AsyncPlayerChatEvent e) {
        if (e.isAsynchronous()) {
            UUID player = e.getPlayer().getUniqueId();
            if (typing.contains(player)) {
                e.setCancelled(true);
                currentInput.put(player, e.getMessage());
                Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(SimpleLuckyBlocksCore.class), () -> {
                    GuiManager.setInventory(e.getPlayer(), lastGui.get(player));
                    lastGui.remove(player);
                }, 1);
                todos.get(player).accept(e.getPlayer());
                typing.remove(player);
                todos.remove(player);
            }
        }
    }

    public static void startChatListen(Player p, String redirectGui, String message, Consumer<Player> toDo) {
        p.sendMessage(message);
        typing.add(p.getUniqueId());
        lastGui.put(p.getUniqueId(), redirectGui);
        currentInput.remove(p.getUniqueId());
        todos.put(p.getUniqueId(), toDo);
    }

    public static String getCurrentInput(Player p) {
        String s = currentInput.get(p.getUniqueId());
        currentInput.remove(p.getUniqueId());
        return s;
    }



}
