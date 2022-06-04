package me.partlysunny.gui.guis.loot.entry.creation.potion;

import me.partlysunny.blocks.loot.entry.EntryType;
import me.partlysunny.gui.guis.loot.entry.EntrySaveInfo;
import me.partlysunny.util.classes.Pair;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PotionEntryInfo implements EntrySaveInfo {

    private final Map<PotionEffectType, Pair<Integer, Integer>> effects = new HashMap<>();
    private String name;

    public PotionEntryInfo(List<Pair<PotionEffectType, Pair<Integer, Integer>>> effects) {
        for (Pair<PotionEffectType, Pair<Integer, Integer>> effect : effects) {
            this.effects.put(effect.a(), effect.b());
        }
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

    public String name() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
