package me.partlysunny.blocks.loot.entry.item.wand;

import de.tr7zw.nbtapi.NBTItem;
import me.partlysunny.util.Util;
import me.partlysunny.util.classes.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class Wand {

    private final WandType type;
    private final int uses;
    private final Material material;

    public Wand(WandType type, int uses, Material material) {
        this.type = type;
        this.uses = uses;
        this.material = material;
    }

    public ItemStack generate(String displayName, List<String> lore, int minPower, int maxPower) {
        int powerValue = Util.getRandomBetween(minPower, maxPower);
        List<String> toEdit = new ArrayList<>(lore);
        toEdit.add(ChatColor.DARK_GRAY + "Uses: " + ChatColor.GRAY + uses + "/" + uses);
        toEdit.add(0, ChatColor.LIGHT_PURPLE + "Power: " + ChatColor.DARK_PURPLE + powerValue);
        ItemStack wand = ItemBuilder.builder(material).setName(displayName).setLore(toEdit.toArray(new String[0])).build();
        NBTItem nbti = new NBTItem(wand);
        nbti.setInteger("power", powerValue);
        nbti.setInteger("uses", uses);
        nbti.setInteger("maxUses", uses);
        nbti.setString("wandType", type.toString());
        nbti.applyNBT(wand);
        return wand;
    }
}
