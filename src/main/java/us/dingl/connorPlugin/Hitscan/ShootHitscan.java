package us.dingl.connorPlugin.Hitscan;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import us.dingl.connorPlugin.ConnorPlugin;

import java.util.List;

/**
 * This class handles the hitscan shooting mechanism for a player.
 */
public class ShootHitscan {

    private final ConnorPlugin plugin;

    /**
     * Constructor for ShootHitscan.
     *
     * @param plugin The instance of the ConnorPlugin.
     */
    public ShootHitscan(ConnorPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Performs a hitscan operation from the player's eye location in the direction they are looking.
     * The hitscan checks for entities and blocks within a specified range and applies damage to the first entity hit.
     *
     * @param player The player performing the hitscan.
     * @param range  The maximum range of the hitscan.
     */
    public void performHitscan(Player player, double range) {
        // Get the player's eye location and direction
        Location eyeLocation = player.getEyeLocation();
        Vector direction = eyeLocation.getDirection();

        // Check if the player has debug mode enabled
        boolean isDebugMode = plugin.debugMode.getOrDefault(player.getUniqueId(), false);

        // Step through the ray in small increments
        for (double i = 0; i < range; i += 0.25) { // Adjust step size for performance/accuracy
            Location currentPoint = eyeLocation.clone().add(direction.clone().multiply(i));
            currentPoint.getWorld().spawnParticle(Particle.TRIAL_SPAWNER_DETECTION_OMINOUS, currentPoint, 3, 0, 0, 0, 0);

            // Check for nearby entities
            List<Entity> nearbyEntities = player.getWorld().getNearbyEntities(currentPoint, 0.25, 0.25, 0.25).stream()
                    .filter(entity -> entity != player) // Ignore the player
                    .toList();

            if (!nearbyEntities.isEmpty()) {
                // Hit an entity
                Entity hitEntity = nearbyEntities.getFirst();
                Location hitLocation = hitEntity.getLocation();
                Damageable target;
                if (hitEntity instanceof Damageable) {
                    target = (Damageable) hitEntity;
                } else {
                    return;
                }

                // Get distance from player to hit location
                double distance = player.getLocation().distance(hitLocation);

                // Determine damage based on distance
                double damage = 0;
                if (distance <= 15) {
                    damage = 6.0;
                } else if (distance <= 30) {
                    damage = 3.0;
                } else if (distance <= 80) {
                    damage = 1.0;
                }

                target.damage(0);
                double newHealth = target.getHealth() - damage;
                if (newHealth <= 0) {
                    target.setHealth(0);
                } else {
                    target.setHealth(newHealth);
                }

                // Debug mode: show hit entity and damage
                if (isDebugMode) {
                    player.sendMessage("Hit entity: " + hitEntity.getName() + " with damage: " + damage);
                    player.sendMessage("Entity health: " + target.getHealth());
                    player.sendMessage("Distance: " + distance);
                }

                // Spawn particle effect at the hit location
                player.getWorld().spawnParticle(Particle.CRIT, hitLocation, 5);

                return; // Stop further ray tracing
            }

            // Check if we hit a block
            if (currentPoint.getBlock().getType().isSolid()) {
                // Debug mode: show hit block
                if (isDebugMode) {
                    player.sendMessage("Hit block at: " + currentPoint.getBlockX() + ", " + currentPoint.getBlockY() + ", " + currentPoint.getBlockZ());
                    player.sendMessage("Block type: " + currentPoint.getBlock().getType());
                    player.sendMessage("Distance: " + i);
                }
                return; // Stop further ray tracing
            }
        }
    }
}