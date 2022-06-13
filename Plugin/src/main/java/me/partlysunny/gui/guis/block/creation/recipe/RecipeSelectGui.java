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
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ShapedRecipe;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RecipeSelectGui extends SelectGui<ShapedRecipe> {

    private static final Map<UUID, Pair<Integer, Integer>> slots = new HashMap<>();

    @Override
    public Gui getGui(HumanEntity p) {
        if (!(p instanceof Player player)) return new ChestGui(3, "");
        UUID pId = player.getUniqueId();
        boolean a = values.containsKey(pId);
        Material b = (Material) SelectGuiManager.getSelectGui("material").getValue(player.getUniqueId());
        if (b != null) {
            int x = slots.get(pId).a();
            int y = slots.get(pId).b();
            if (a) {

            } else {

            }
            SelectGuiManager.getSelectGui("material").resetValue(player.getUniqueId());
        }
        return null;
    }

    @Override
    protected ShapedRecipe getValueFromString(String s) {
        return null;
    }

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
}
