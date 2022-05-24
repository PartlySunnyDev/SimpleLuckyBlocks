package me.partlysunny.commands.subcommands;

import org.bukkit.command.CommandSender;

public interface SLBSubCommand {

    String getId();

    String getDescription();

    String getUsage();

    void execute(CommandSender executor, String[] args);

}
