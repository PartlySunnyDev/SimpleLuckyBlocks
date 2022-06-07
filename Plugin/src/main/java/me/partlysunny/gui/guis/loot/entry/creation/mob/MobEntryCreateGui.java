package me.partlysunny.gui.guis.loot.entry.creation.mob;

import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.gui.type.util.Gui;
import me.partlysunny.blocks.loot.entry.mob.MobEntry;
import me.partlysunny.gui.GuiInstance;
import me.partlysunny.gui.guis.common.ValueGuiManager;
import me.partlysunny.gui.guis.loot.entry.creation.EntrySaveWrapper;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MobEntryCreateGui implements GuiInstance {

    private static final Map<UUID, EntrySaveWrapper<MobEntry>> mobSaves = new HashMap<>();
    private static final Map<UUID, MobSlot> currentSlot = new HashMap<>();

    @Override
    public Gui getGui(HumanEntity p) {
        if (!(p instanceof Player player)) return new ChestGui(3, "");
        UUID pId = player.getUniqueId();
        ChestGui gui = new ChestGui(6, ChatColor.GREEN + "Mob Entry Creator");
        ItemStack createdItem = (ItemStack) ValueGuiManager.getValueGui("itemMaker").getValue(pId);

        return null;
    }


}
