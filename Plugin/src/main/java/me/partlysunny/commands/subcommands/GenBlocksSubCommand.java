package me.partlysunny.commands.subcommands;

import me.partlysunny.SimpleLuckyBlocksCore;
import me.partlysunny.blocks.LuckyBlockType;
import me.partlysunny.worldedit.WorldEditHook;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class GenBlocksSubCommand implements SLBSubCommand {

    @Override
    public String getId() {
        return "genblocks";
    }

    @Override
    public String getDescription() {
        return "Generates lucky blocks randomly on top of selected blocks! (Requires WorldEdit)";
    }

    @Override
    public String getUsage() {
        return " <blockType> <blockCount>";
    }

    @Override
    public void execute(CommandSender executor, String[] args) {
        if (!SimpleLuckyBlocksCore.isWorldEdit) {
            executor.sendMessage(ChatColor.RED + "This command requires WorldEdit! Please add WorldEdit to your server to use this command!");
            return;
        }
        if (args.length < 2) {
            executor.sendMessage(ChatColor.RED + "Usage is /slb genblocks" + getUsage());
            return;
        }
        LuckyBlockType t = LuckyBlockType.getType(args[0]);
        if (t == null) {
            executor.sendMessage(ChatColor.RED + "Please specify a valid block type!");
            return;
        }
        int blockCount;
        try {
            blockCount = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            executor.sendMessage(ChatColor.RED + "Please specify a valid number as the block count!");
            return;
        }
        WorldEditHook.placeRandomLuckyBlocksInSelection(executor, blockCount, t);
        executor.sendMessage(ChatColor.GREEN + "Successfully added blocks!");
    }
}
