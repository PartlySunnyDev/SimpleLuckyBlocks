package me.partlysunny.blocks;

import me.partlysunny.SimpleLuckyBlocksCore;
import me.partlysunny.blocks.loot.LootTableManager;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nullable;

public class LuckyBlock {

    private final Block b;
    private final LuckyBlockType type;
    private final ArmorStand stand;


    public LuckyBlock(Block b, LuckyBlockType type) {
        this.b = b;
        this.type = type;
        //Creates a silent, invulnerable, marker armor stand which acts as the bit inside the block
        stand = (ArmorStand) b.getWorld().spawnEntity(b.getLocation().add(0.5, -1.25, 0.5), EntityType.ARMOR_STAND);
        stand.setSilent(true);
        stand.setMarker(true);
        stand.setGravity(false);
        stand.setInvisible(true);
        stand.setInvulnerable(true);
        stand.getEquipment().setItem(EquipmentSlot.HEAD, type.innerItem());
        stand.getPersistentDataContainer().set(new NamespacedKey(JavaPlugin.getPlugin(SimpleLuckyBlocksCore.class), "special"), PersistentDataType.BYTE, (byte) 1);
        StandManager.stands.add(stand);
    }

    public Block b() {
        return b;
    }

    public LuckyBlockType type() {
        return type;
    }

    public void dropLoot(@Nullable Player p) {
        LootTableManager.getTable(type.lootTable()).dropTableAt(b.getLocation(), p);
    }

    public void killStand() {
        stand.remove();
    }
}
