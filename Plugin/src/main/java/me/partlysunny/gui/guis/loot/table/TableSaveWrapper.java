package me.partlysunny.gui.guis.loot.table;

import me.partlysunny.blocks.loot.CustomLootTable;
import me.partlysunny.gui.guis.Renamable;

public class TableSaveWrapper implements Renamable {

    private final CustomLootTable table;
    private String name;

    public TableSaveWrapper(String name, CustomLootTable table) {
        this.name = name;
        this.table = table;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String name() {
        return name;
    }

    public CustomLootTable table() {
        return table;
    }
}
