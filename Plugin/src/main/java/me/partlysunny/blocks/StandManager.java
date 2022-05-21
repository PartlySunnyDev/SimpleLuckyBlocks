package me.partlysunny.blocks;

import org.bukkit.entity.ArmorStand;

import java.util.ArrayList;
import java.util.List;

public class StandManager {

    public static final List<ArmorStand> stands = new ArrayList<>();

    public static void killAll() {
        for (ArmorStand s : stands) {
            s.remove();
        }
    }

}
