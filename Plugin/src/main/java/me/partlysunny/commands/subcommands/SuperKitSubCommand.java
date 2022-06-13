package me.partlysunny.commands.subcommands;

import me.partlysunny.util.classes.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;

/**
 * Joke class for testing your mobs
 */
public class SuperKitSubCommand implements SLBSubCommand {
    @Override
    public String getId() {
        return "superkit";
    }

    @Override
    public String getDescription() {
        return "Gives you a super kit";
    }

    @Override
    public String getUsage() {
        return "";
    }

    @Override
    public void execute(CommandSender executor, String[] args) {
        if (executor instanceof Player p) {
            PlayerInventory inventory = p.getInventory();
            inventory.addItem(
                    ItemBuilder.builder(Material.NETHERITE_HELMET).setUnbreakable(true).addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 5).build(),
                    ItemBuilder.builder(Material.NETHERITE_CHESTPLATE).setUnbreakable(true).addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 5).build(),
                    ItemBuilder.builder(Material.NETHERITE_LEGGINGS).setUnbreakable(true).addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 5).build(),
                    ItemBuilder.builder(Material.NETHERITE_BOOTS).setUnbreakable(true).addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 5).build(),
                    ItemBuilder.builder(Material.NETHERITE_SWORD).setUnbreakable(true).addEnchantment(Enchantment.DAMAGE_ALL, 5).addEnchantment(Enchantment.SWEEPING_EDGE, 5).build(),
                    ItemBuilder.builder(Material.NETHERITE_AXE).setUnbreakable(true).addEnchantment(Enchantment.DAMAGE_ALL, 5).addEnchantment(Enchantment.DIG_SPEED, 10).build(),
                    ItemBuilder.builder(Material.SHIELD).setUnbreakable(true).build(),
                    ItemBuilder.builder(Material.GOLDEN_APPLE).setAmount(12).build(),
                    ItemBuilder.builder(Material.COOKED_BEEF).setAmount(64).build(),
                    ItemBuilder.builder(Material.OAK_PLANKS).setAmount(64).build(),
                    ItemBuilder.builder(Material.OAK_PLANKS).setAmount(64).build(),
                    ItemBuilder.builder(Material.OAK_PLANKS).setAmount(64).build()
            );
        }
    }
}
