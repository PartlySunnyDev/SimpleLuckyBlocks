package me.partlysunny.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class SLBTabCompleter implements TabCompleter {
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (args.length == 1) {
            String arg = args[args.length - 1];
            return SLBCommand.subCommands.keySet().stream()
                    .filter(s -> (arg.isEmpty() || s.startsWith(arg.toLowerCase(Locale.ENGLISH))))
                    .collect(Collectors.toList());
        }
        return null;
    }
}
