package me.partlysunny.blocks.loot.entry.potion;

import me.partlysunny.blocks.loot.entry.IEntry;
import me.partlysunny.util.classes.Pair;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PotionEntry implements IEntry {

    private final Map<PotionEffectType, Pair<Integer, Integer>> effects = new HashMap<>();

    public PotionEntry(List<Pair<PotionEffectType, Pair<Integer, Integer>>> effects) {
        //Pair<duration, amplifier :)>
        for (Pair<PotionEffectType, Pair<Integer, Integer>> effect : effects) {
            this.effects.put(effect.a(), effect.b());
        }
    }

    @Override
    public void execute(Location l, Player p) {
        for (PotionEffectType effect : effects.keySet()) {
            p.addPotionEffect(new PotionEffect(effect, effects.get(effect).a(), effects.get(effect).b()));
        }
    }
}
