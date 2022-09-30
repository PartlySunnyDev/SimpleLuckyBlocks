package me.partlysunny.gui.guis.block.creation.recipe;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.gui.type.util.Gui;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import me.partlysunny.ConsoleLogger;
import me.partlysunny.gui.GuiManager;
import me.partlysunny.gui.SelectGui;
import me.partlysunny.gui.SelectGuiManager;
import me.partlysunny.util.Util;
import me.partlysunny.util.classes.ItemBuilder;
import me.partlysunny.util.classes.Pair;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RecipeSelectGui extends SelectGui<ShapedRecipeWrapper> {

    private static final Map<UUID, Pair<Integer, Integer>> slots = new HashMap<>();

    public static void setSlots(UUID player, int x, int y) {
        if (x < 0 || x > 2) {
            ConsoleLogger.console("Slot x must be 0, 1, 2");
            return;
        }
        if (y < 0 || y > 2) {
            ConsoleLogger.error("Slot y must be 0, 1, 2");
            return;
        }
        slots.put(player, new Pair<>(x, y));
    }

    public static Pair<Integer, Integer> getSlots(UUID player) {
        return slots.get(player);
    }

    private static void addRecipeSlot(StaticPane pane, Player player, int slotX, int slotY, int recipeAreaX, int recipeAreaY, Material currentMaterial) {
        pane.addItem(new GuiItem(Util.addEditable(ItemBuilder.builder(currentMaterial).build()), event -> {
            SelectGuiManager.getSelectGui("material").setReturnTo(player.getUniqueId(), "recipeSelect");
            GuiManager.openInventory(player, "materialSelect");
        }), slotX + recipeAreaX, slotY + recipeAreaY);
    }

    @Override
    public Gui getGui(HumanEntity p) {
        if (!(p instanceof Player player)) return new ChestGui(3, "");
        UUID pId = player.getUniqueId();
        Util.handleSelectInput("material", player, values, new ShapedRecipeWrapper(), Material.class, (wrapper, material) -> {
            int x = slots.get(player.getUniqueId()).a();
            int y = slots.get(player.getUniqueId()).b();
            wrapper.setSlot(x, y, material);
        });
        ShapedRecipeWrapper recipeInfo = values.getOrDefault(player.getUniqueId(), new ShapedRecipeWrapper());
        ChestGui gui = new ChestGui(5, ChatColor.BLUE + "Create Recipe");
        StaticPane mainPane = new StaticPane(0, 0, 9, 5);
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                addRecipeSlot(mainPane, player, i, j, 3, 1, recipeInfo.getSlot(i, j));
            }
        }
        mainPane.addItem(new GuiItem(ItemBuilder.builder(Material.GREEN_CONCRETE).setName(ChatColor.BLUE + "Submit").build(), event -> {

        }), 7, 2);
        Util.addReturnButton(mainPane, player, getReturnTo(player), 0, 4);
        gui.addPane(mainPane);
        Util.setClickSoundTo(Sound.BLOCK_METAL_PRESSURE_PLATE_CLICK_OFF, gui);
        return null;
    }

    @Override
    protected ShapedRecipeWrapper getValueFromString(String s) {
        return null;
    }
}
