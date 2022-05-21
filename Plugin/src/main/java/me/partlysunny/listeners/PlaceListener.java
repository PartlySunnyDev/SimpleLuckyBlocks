package me.partlysunny.listeners;

import de.tr7zw.nbtapi.NBTItem;
import me.partlysunny.blocks.LuckyBlockManager;
import me.partlysunny.blocks.LuckyBlockType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

public class PlaceListener implements Listener {

    @EventHandler
    public void blockPlaceEvent(BlockPlaceEvent e) {
        LuckyBlockType t = getLuckyBlockInfo(e.getItemInHand());
        if (t == null) {
            return;
        }
        e.getBlockPlaced().setBlockData(t.blockType().createBlockData(), true);
        LuckyBlockManager.loadAsLuckyBlock(e.getBlockPlaced(), t);
    }

    private LuckyBlockType getLuckyBlockInfo(ItemStack i) {
        NBTItem nbti = new NBTItem(i);
        if (!nbti.hasKey("luckyType")) {
            return null;
        }
        return LuckyBlockType.getType(nbti.getString("luckyType"));
    }

}
