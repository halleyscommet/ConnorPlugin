package us.dingl.incursionImminent.Listeners.Boss;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Team;

import java.util.List;
import java.util.Objects;

public class MinionSummoningHandler {

    public static void summonMinions(List<Minion> entities, World world, Location location, Team team) {
        for (Minion entity : entities) {
            EntityType entityType = entity.getEntityType();
            Integer amount = entity.getAmount();
            List<PotionEffectType> effects = entity.getEffects();
            List<Integer> durations = entity.getDurations();
            List<Integer> amplifiers = entity.getAmplifiers();

            for (int i = 0; i < amount; i++) {
                LivingEntity spawnedMinion = (LivingEntity) world.spawnEntity(location, entityType);

                for (int j = 0; j < effects.size(); j++) {
                    PotionEffectType effect = effects.get(j);
                    int duration = durations.size() > j ? durations.get(j) : PotionEffect.INFINITE_DURATION;
                    int amplifier = amplifiers.size() > j ? amplifiers.get(j) : 0;
                    spawnedMinion.addPotionEffect(effect.createEffect(duration, amplifier));
                }

                spawnedMinion.setHealth(Objects.requireNonNull(spawnedMinion.getAttribute(Attribute.GENERIC_MAX_HEALTH)).getValue());
                team.addEntry(spawnedMinion.getUniqueId().toString());
            }
        }
    }
}