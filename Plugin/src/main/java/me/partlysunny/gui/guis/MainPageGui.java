package me.partlysunny.gui.guis;

import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.gui.type.util.Gui;
import me.partlysunny.gui.GuiInstance;
import me.partlysunny.util.Util;
import me.partlysunny.util.classes.ItemBuilder;
import me.partlysunny.util.classes.Pair;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

import static me.partlysunny.util.Util.getGeneralSelectionMenu;

public class MainPageGui implements GuiInstance {
    @Override
    public Gui getGui(HumanEntity p) {
        if (!(p instanceof Player player)) return new ChestGui(3, "");
        return getGeneralSelectionMenu(
                ChatColor.AQUA + "Simple" + ChatColor.YELLOW + "Lucky" + ChatColor.GREEN + "Blocks",
                player,
                new Pair<>("lootMenu", ItemBuilder.builder(Material.GOLD_INGOT).setName(ChatColor.GOLD + "Loot").setLore(ChatColor.GRAY + "Configure loot!").build()),
                new Pair<>("luckyBlockMenu", ItemBuilder.builder(Util.convert(Util.HeadType.BASE64, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjM4YzBkMmYxZWMyNjc1NGRjYTNjN2NkYWUzMWYxZjE2NDg4M2Q0NTNlNjg4NjQzZGEwNDc1NjhlN2ZhNWNjOSJ9fX0")).setName(ChatColor.AQUA + "Blocks").setLore(ChatColor.GRAY + "Create new blocks!").build()),
                new Pair<>("triggersMenu", ItemBuilder.builder(Material.TRIPWIRE_HOOK).setName(ChatColor.RED + "Triggers").setLore(ChatColor.GRAY + "Manage your triggers!").build()),
                new Pair<>("wandsMenu", ItemBuilder.builder(Material.STICK).setName(ChatColor.LIGHT_PURPLE + "Wands").setLore(ChatColor.GRAY + "Manage wands!").build())
        );
    }
}
