package us.dingl.connorPlugin.CrossProjectile;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import us.dingl.connorPlugin.ConnorPlugin;

import java.util.List;

public class ShootCrossProjectile {

    private final ConnorPlugin plugin;

    public ShootCrossProjectile(ConnorPlugin plugin) {
        this.plugin = plugin;
    }

    public void performShot(Player player, double range) {
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
                player.getWorld().spawnParticle(Particle.HEART, hitLocation, 5);

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
