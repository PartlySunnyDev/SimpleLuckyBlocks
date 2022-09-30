package me.partlysunny.gui.guis.block.creation.recipe;

import me.partlysunny.SimpleLuckyBlocksCore;
import me.partlysunny.blocks.LuckyBlockType;
import me.partlysunny.util.Util;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public class ShapedRecipeWrapper {

    private final Material[][] recipe = new Material[3][3];
    private String name;

    public ShapedRecipeWrapper() {
        this("");
    }

    public ShapedRecipeWrapper(String name) {
        this.name = name;
    }

    public void setSlot(int x, int y, Material m) {
        recipe[x][y] = m;
    }

    public ShapedRecipe build() {
        ShapedRecipe returned = new ShapedRecipe(NamespacedKey.fromString(name, JavaPlugin.getPlugin(SimpleLuckyBlocksCore.class)), Util.produceLuckyBlock(LuckyBlockType.getType(name)));
        String s = "abcdefghi";
        int count = 0;
        Map<Material, Character> charMap = new HashMap<>();
        for (Material[] m : recipe) {
            for (Material mm : m) {
                if (!charMap.containsKey(mm)) {
                    if (mm == null) {
                        charMap.put(null, ' ');
                    } else {
                        charMap.put(mm, s.charAt(count));
                    }
                    count++;
                }
            }
        }
        for (Material m : charMap.keySet()) {
            if (m != null) {
                returned.setIngredient(charMap.get(m), m);
            }
        }
        count = 0;
        String[] shape = new String[3];
        for (Material[] m : recipe) {
            StringBuilder string = new StringBuilder();
            for (Material mm : m) {
                string.append(charMap.get(mm));
            }
            shape[count] = string.toString();
            count++;
        }
        returned.shape(shape);
        return returned;
    }

    public Material getSlot(int i, int j) {
        return recipe[i][j];
    }
}
