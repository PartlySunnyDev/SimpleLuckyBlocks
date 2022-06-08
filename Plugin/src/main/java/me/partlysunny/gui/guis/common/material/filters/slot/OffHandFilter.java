package me.partlysunny.gui.guis.common.material.filters.slot;

import me.partlysunny.gui.guis.loot.entry.creation.mob.equipment.MobSlot;

public class OffHandFilter extends SlotFilter {
    @Override
    protected MobSlot getSlot() {
        return MobSlot.OFF_HAND;
    }
}
