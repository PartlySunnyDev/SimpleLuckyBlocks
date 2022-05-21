package me.partlysunny.commands.subcommands;

import de.tr7zw.nbtapi.NBTItem;
import me.partlysunny.blocks.LuckyBlockType;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
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
            if (args.length < 1) {
                p.sendMessage(ChatColor.RED + "Specify a lucky block type!");
                return;
            }
            String typeId = args[0];
            int amount = 1;
            if (args.length > 1) {
                try {
                    amount = Integer.parseInt(args[1]);
                } catch (NumberFormatException e) {
                    p.sendMessage(ChatColor.RED + "Specify a valid amount!");
                    return;
                }
            }
            LuckyBlockType type = LuckyBlockType.getType(typeId);
            ItemStack block = new ItemStack(type.blockType());
            NBTItem nbti = new NBTItem(block);
            nbti.setString("luckyType", type.id());
            nbti.applyNBT(block);
            block.setAmount(amount);
            p.getInventory().addItem(block);
        }
    }
}
