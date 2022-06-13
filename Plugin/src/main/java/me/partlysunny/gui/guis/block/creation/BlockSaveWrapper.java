package me.partlysunny.gui.guis.block.creation;

import me.partlysunny.blocks.LuckyBlockType;
import me.partlysunny.gui.guis.Renamable;

public class BlockSaveWrapper implements Renamable {

    private String name;
    private LuckyBlockType type;

    public BlockSaveWrapper(String name, LuckyBlockType type) {
        this.name = name;
        this.type = type;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    public LuckyBlockType type() {
        return type;
    }

    public void setType(LuckyBlockType type) {
        this.type = type;
    }
}
