package me.partlysunny.gui.guis.loot.table;

import me.partlysunny.blocks.loot.CustomLootTable;

public class TableSaveWrapper {

    private final CustomLootTable table;
    private String name;

    public TableSaveWrapper(String name, CustomLootTable table) {
        this.name = name;
        this.table = table;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String name() {
        return name;
    }

    public CustomLootTable table() {
        return table;
    }
}
