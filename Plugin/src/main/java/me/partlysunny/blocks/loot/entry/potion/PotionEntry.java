package me.partlysunny.blocks.loot.entry.potion;

import me.partlysunny.blocks.loot.entry.EntryType;
import me.partlysunny.blocks.loot.entry.IEntry;
import me.partlysunny.util.classes.Pair;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
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
        if (p == null) {
            return;
        }
        for (PotionEffectType effect : effects.keySet()) {
            p.addPotionEffect(new PotionEffect(effect, effects.get(effect).a(), effects.get(effect).b()));
        }
    }

    public void addEffect(PotionEffectType t, int duration, int lvl) {
        effects.put(t, new Pair<>(duration, lvl));
    }

    public void removeEffect(PotionEffectType t) {
        effects.remove(t);
    }

    @Override
    public YamlConfiguration getSave() {
        YamlConfiguration config = new YamlConfiguration();
        config.set("entryType", "potion");
        ConfigurationSection effects = config.createSection("effects");
        for (PotionEffectType effect : this.effects.keySet()) {
            ConfigurationSection effectInfo = effects.createSection(effect.getKey().getKey().toLowerCase());
            effectInfo.set("id", effect.getKey().getKey());
            effectInfo.set("lvl", this.effects.get(effect).b());
            effectInfo.set("duration", this.effects.get(effect).a());
        }
        return config;
    }

    @Override
    public EntryType getEntryType() {
        return EntryType.POTION;
    }

    public Pair<PotionEffectType, Pair<Integer, Integer>>[] getEffects() {
        Pair<PotionEffectType, Pair<Integer, Integer>>[] returned = new Pair[effects.size()];
        int count = 0;
        for (PotionEffectType t : effects.keySet()) {
            returned[count] = new Pair<>(t, effects.get(t));
            count++;
        }
        return returned;
    }

}
