package me.partlysunny.commands.subcommands;

import me.partlysunny.gui.GuiManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LuckyMenuSubCommand implements SLBSubCommand {
    @Override
    public String getId() {
        return "luckymenu";
    }

    @Override
    public String getDescription() {
        return "Opens the lucky menu, where you can create your own lucky blocks, wands, and triggers!";
    }

    @Override
    public String getUsage() {
        return "";
    }

    @Override
    public void execute(CommandSender executor, String[] args) {
        if (!(executor instanceof Player p)) return;
        GuiManager.setInventory(p, "mainPage");
    }
}
