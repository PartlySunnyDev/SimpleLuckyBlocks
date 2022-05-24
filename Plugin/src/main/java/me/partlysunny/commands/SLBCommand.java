package me.partlysunny.commands;

import me.partlysunny.commands.subcommands.SLBSubCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class SLBCommand implements CommandExecutor {

    public static Map<String, SLBSubCommand> subCommands = new HashMap<>();

    public static void registerSubCommand(SLBSubCommand c) {
        subCommands.put(c.getId(), c);
    }

    public static boolean executeSubCommand(String id, CommandSender exe, String[] args) {
        SLBSubCommand slbSubCommand = subCommands.get(id);
        if (slbSubCommand == null) {
            return false;
        }
        slbSubCommand.execute(exe, args);
        return true;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (commandSender instanceof Player p) {
            if (strings.length == 0) {
                executeSubCommand("help", commandSender, new String[] {});
                return true;
            }
            String subCommand = strings[0];
            ArrayList<String> newArgs = new ArrayList<>(Arrays.asList(strings));
            newArgs.remove(0);
            if (!executeSubCommand(subCommand, commandSender, newArgs.toArray(new String[0]))) {
                p.sendMessage(ChatColor.RED + "That command does not exist!");
            }
        }
        return true;
    }

}
