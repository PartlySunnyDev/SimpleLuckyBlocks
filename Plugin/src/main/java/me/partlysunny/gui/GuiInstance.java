package me.partlysunny.gui;

import com.github.stefvanschie.inventoryframework.gui.type.util.Gui;
import org.bukkit.entity.HumanEntity;

public interface GuiInstance {
    Gui getGui(HumanEntity p);

    default void openFor(HumanEntity e) {
        getGui(e).show(e);
    }
}
