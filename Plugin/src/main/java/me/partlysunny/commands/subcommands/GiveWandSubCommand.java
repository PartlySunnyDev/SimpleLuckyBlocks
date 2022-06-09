package me.partlysunny.commands.subcommands;

import me.partlysunny.blocks.loot.entry.wand.Wand;
import me.partlysunny.blocks.loot.entry.wand.WandManager;
import me.partlysunny.util.CommandUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class GiveWandSubCommand implements SLBSubCommand {


    @Override
    public String getId() {
        return "givewand";
    }

    @Override
    public String getDescription() {
        return "Gives a custom wand!";
    }

    @Override
    public String getUsage() {
        return " <selector> <wandType> <amount> <power>";
    }

    @Override
    public void execute(CommandSender executor, String[] args) {
        if (executor instanceof Player p) {
            if (args.length < 4) {
                p.sendMessage(ChatColor.RED + "Correct usage is /slb givewand" + getUsage());
                return;
            }
            Entity[] selected = CommandUtils.getTargets(executor, args[0]);
            if (selected == null || selected.length < 1) {
                p.sendMessage(ChatColor.RED + "Please specify a valid selector!");
                return;
            }
            String typeId = args[1];
            int amount;
            try {
                amount = Integer.parseInt(args[2]);
            } catch (NumberFormatException e) {
                p.sendMessage(ChatColor.RED + "Specify a valid amount!");
                return;
            }
            int power;
            try {
                power = Integer.parseInt(args[3]);
            } catch (NumberFormatException e) {
                p.sendMessage(ChatColor.RED + "Specify a valid power amount!");
                return;
            }
            Wand wand = WandManager.getWand(typeId);
            if (wand == null) {
                p.sendMessage(ChatColor.RED + "Please specify a valid wand (As defined by the wands folder in the config)!");
                return;
            }
            for (Entity e : selected) {
                if (e instanceof Player player) {
                    for (int i = 0; i < amount; i++) {
                        player.getInventory().addItem(wand.generate(ChatColor.GOLD + "Command Given Wand", new ArrayList<>(), power, power));
                    }
                    player.sendMessage("Successfully gave you " + amount + " wands of type " + typeId);
                }
            }
        }
    }
}
