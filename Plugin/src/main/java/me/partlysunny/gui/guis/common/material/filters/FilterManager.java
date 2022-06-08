package me.partlysunny.gui.guis.common.material.filters;

import me.partlysunny.gui.guis.common.material.filters.slot.*;

import java.util.HashMap;
import java.util.Map;

public class FilterManager {

    private static final Map<String, MaterialFilter> filters = new HashMap<>();

    public static void registerFilter(String id, MaterialFilter filter) {
        filters.put(id, filter);
    }

    public static MaterialFilter getFilter(String id) {
        return filters.get(id);
    }

    public static void unregisterFilter(String id) {
        filters.remove(id);
    }

    public static void initFilters() {
        registerFilter("meta", new MetaFilter());
        registerFilter("helmet", new HelmetFilter());
        registerFilter("chestplate", new ChestplateFilter());
        registerFilter("leggings", new LeggingsFilter());
        registerFilter("boots", new BootsFilter());
        registerFilter("main_hand", new MainHandFilter());
        registerFilter("off_hand", new OffHandFilter());
    }

}
