package us.dingl.connorPlugin.Listeners.Boss;

import org.bukkit.entity.EntityType;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class Minion {

    private final EntityType entityType;
    private final Integer amount;
    private final List<PotionEffectType> effects;
    private final List<Integer> durations;
    private final List<Integer> amplifiers;

    public Minion(EntityType entityType, Integer amount, List<PotionEffectType> effects, List<Integer> durations, List<Integer> amplifiers) {
        this.entityType = entityType;
        this.amount = amount;
        this.effects = effects;
        this.durations = durations;
        this.amplifiers = amplifiers;
    }

    public EntityType getEntityType() {
        return entityType;
    }

    public Integer getAmount() {
        return amount;
    }

    public List<PotionEffectType> getEffects() {
        return effects;
    }

    public List<Integer> getDurations() {
        return durations;
    }

    public List<Integer> getAmplifiers() {
        return amplifiers;
    }
}
