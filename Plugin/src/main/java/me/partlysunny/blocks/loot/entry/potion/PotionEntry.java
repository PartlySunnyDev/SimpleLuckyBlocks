package me.partlysunny.blocks.loot.entry.potion;

import me.partlysunny.blocks.loot.entry.EntryType;
import me.partlysunny.blocks.loot.entry.IEntry;
import me.partlysunny.gui.guis.loot.entry.creation.potion.PotionEntryEffectWrapper;
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

    private final Map<PotionEffectType, PotionEntryEffectWrapper> effects = new HashMap<>();

    public PotionEntry(List<PotionEntryEffectWrapper> effects) {
        for (PotionEntryEffectWrapper effect : effects) {
            this.effects.put(effect.type(), effect);
        }
    }

    @Override
    public void execute(Location l, Player p) {
        if (p == null) {
            return;
        }
        for (PotionEffectType effect : effects.keySet()) {
            p.addPotionEffect(new PotionEffect(effect, effects.get(effect).duration(), effects.get(effect).amplifier()));
        }
    }

    public void addEffect(PotionEffectType t, int duration, int lvl) {
        effects.put(t, new PotionEntryEffectWrapper(t, duration, lvl));
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
            effectInfo.set("lvl", this.effects.get(effect).amplifier());
            effectInfo.set("duration", this.effects.get(effect).duration());
        }
        return config;
    }

    @Override
    public EntryType getEntryType() {
        return EntryType.POTION;
    }

    public PotionEntryEffectWrapper[] getEffects() {
        PotionEntryEffectWrapper[] returned = new PotionEntryEffectWrapper[effects.size()];
        int count = 0;
        for (PotionEffectType t : effects.keySet()) {
            returned[count] = effects.get(t);
            count++;
        }
        return returned;
    }

}
