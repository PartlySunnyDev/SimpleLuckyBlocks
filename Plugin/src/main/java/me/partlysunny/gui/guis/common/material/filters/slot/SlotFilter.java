package me.partlysunny.gui.guis.common.material.filters.slot;

import me.partlysunny.gui.guis.common.material.filters.MaterialFilter;
import me.partlysunny.gui.guis.loot.entry.creation.mob.equipment.MobSlot;
import org.bukkit.Material;

public abstract class SlotFilter implements MaterialFilter {
    @Override
    public boolean doesQualify(Material m) {
        return getSlot().matchesFilter(m);
    }

    protected abstract MobSlot getSlot();
}
