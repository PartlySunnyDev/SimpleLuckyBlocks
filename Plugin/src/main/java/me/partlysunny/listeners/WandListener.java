package me.partlysunny.listeners;

import de.tr7zw.nbtapi.NBTItem;
import me.partlysunny.blocks.loot.entry.wand.WandType;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class WandListener implements Listener {

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        if (e.getHand() != EquipmentSlot.HAND) return;
        Player player = e.getPlayer();
        ItemStack onHand = player.getInventory().getItemInMainHand();
        if (onHand.getType() == Material.AIR || onHand == null) {
            return;
        }
        NBTItem nbti = new NBTItem(onHand);
        if (!nbti.hasKey("wandType")) {
            return;
        }
        WandType t = WandType.valueOf(nbti.getString("wandType"));
        t.action(nbti.getInteger("power"), player);
        nbti.setInteger("uses", nbti.getInteger("uses") - 1);
        if (nbti.getInteger("uses") < 1) {
            onHand.setAmount(onHand.getAmount() - 1);
            player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 1, 1);
            return;
        }
        nbti.applyNBT(onHand);
        ItemMeta m = onHand.getItemMeta();
        List<String> lore = m.getLore();
        lore.remove(lore.size() - 1);
        lore.add(ChatColor.DARK_GRAY + "Uses: " + ChatColor.GRAY + nbti.getInteger("uses") + "/" + nbti.getInteger("maxUses"));
        m.setLore(lore);
        onHand.setItemMeta(m);
    }

}
