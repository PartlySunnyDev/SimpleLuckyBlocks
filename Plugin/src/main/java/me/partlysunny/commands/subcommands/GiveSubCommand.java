package me.partlysunny.commands.subcommands;

import me.partlysunny.blocks.LuckyBlockType;
import me.partlysunny.util.CommandUtils;
import me.partlysunny.util.Util;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class GiveSubCommand implements SLBSubCommand {
    @Override
    public String getId() {
        return "give";
    }

    @Override
    public String getDescription() {
        return "Give yourself lucky blocks!";
    }

    @Override
    public void execute(CommandSender executor, String[] args) {
        if (executor instanceof Player p) {
            if (args.length < 2) {
                p.sendMessage(ChatColor.RED + "Specify a lucky block type AND a player (/slb give <player> <luckyBlockType>)!");
                return;
            }
            Entity[] selected = CommandUtils.getTargets(executor, args[0]);
            if (selected == null || selected.length < 1) {
                p.sendMessage(ChatColor.RED + "Please specify a valid selector!");
                return;
            }
            String typeId = args[1];
            int amount = 1;
            if (args.length > 2) {
                try {
                    amount = Integer.parseInt(args[2]);
                } catch (NumberFormatException e) {
                    p.sendMessage(ChatColor.RED + "Specify a valid amount!");
                    return;
                }
            }
            LuckyBlockType type = LuckyBlockType.getType(typeId);
            if (type == null) {
                p.sendMessage(ChatColor.RED + "Invalid lucky block type!");
                return;
            }
            for (Entity e : selected) {
                if (e instanceof Player player) {
                    ItemStack block = Util.produceLuckyBlock(type);
                    block.setAmount(amount);
                    player.getInventory().addItem(block);
                }
            }
        }
    }
}
