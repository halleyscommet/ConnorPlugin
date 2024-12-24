package us.dingl.connorPlugin.CrossProjectile;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import us.dingl.connorPlugin.ConnorPlugin;

import java.util.List;
import java.util.Random;

public class ShootCrossProjectile {

    private final ConnorPlugin plugin;

    public ShootCrossProjectile(ConnorPlugin plugin) {
        this.plugin = plugin;
    }

    public void performShot(Player player, double range) {
        // Get the player's eye location and direction
        Location eyeLocation = player.getEyeLocation();
        Vector direction = eyeLocation.getDirection();

        // Define additional eye locations for vertical, horizontal, and diagonal ray traces
        Location eyeLocation2 = eyeLocation.clone().add(0.45, 0, 0);
        Location eyeLocation3 = eyeLocation.clone().add(-0.45, 0, 0);
        Location eyeLocation4 = eyeLocation.clone().add(0, 0, 0.45);
        Location eyeLocation5 = eyeLocation.clone().add(0, 0, -0.45);
        Location eyeLocation6 = eyeLocation.clone().add(0, 0.45, 0);
        Location eyeLocation7 = eyeLocation.clone().add(0, -0.45, 0);
        Location eyeLocation8 = eyeLocation.clone().add(0.45, 0.45, 0);
        Location eyeLocation9 = eyeLocation.clone().add(-0.45, -0.45, 0);
        Location eyeLocation10 = eyeLocation.clone().add(0, 0.45, 0.45);
        Location eyeLocation11 = eyeLocation.clone().add(0, -0.45, -0.45);

        // Check if the player has debug mode enabled
        boolean isDebugMode = plugin.debugMode.getOrDefault(player.getUniqueId(), false);

        // Perform the initial ray trace
        performRayTrace(player, eyeLocation, direction, range + 1, isDebugMode);

        // Create a random integer to decide between vertical, horizontal, and diagonal ray traces
        Random random = new Random();
        int traceType = random.nextInt(3);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (traceType == 0) {
                    // Perform vertical ray traces
                    performRayTrace(player, eyeLocation6, direction, range, isDebugMode);
                    performRayTrace(player, eyeLocation7, direction, range, isDebugMode);
                } else if (traceType == 1) {
                    // Perform horizontal ray traces
                    performRayTrace(player, eyeLocation2, direction, range, isDebugMode);
                    performRayTrace(player, eyeLocation3, direction, range, isDebugMode);
                    performRayTrace(player, eyeLocation4, direction, range, isDebugMode);
                    performRayTrace(player, eyeLocation5, direction, range, isDebugMode);
                } else {
                    // Perform diagonal ray traces
                    performRayTrace(player, eyeLocation8, direction, range, isDebugMode);
                    performRayTrace(player, eyeLocation9, direction, range, isDebugMode);
                    performRayTrace(player, eyeLocation10, direction, range, isDebugMode);
                    performRayTrace(player, eyeLocation11, direction, range, isDebugMode);
                }
            }
        }.runTaskLater(plugin, 3L);
    }

    private void performRayTrace(Player player, Location startLocation, Vector direction, double range, boolean isDebugMode) {
        new BukkitRunnable() {
            double i = 0;

            @Override
            public void run() {
                if (i >= range) {
                    cancel();
                    return;
                }

                Location currentPoint = startLocation.clone().add(direction.clone().multiply(i));
                Particle.DustOptions dustOptions = new Particle.DustOptions(org.bukkit.Color.RED, 0.5f);
                currentPoint.getWorld().spawnParticle(Particle.DUST, currentPoint, 1, 0, 0, 0, 1000, dustOptions);

                List<Entity> nearbyEntities = player.getWorld().getNearbyEntities(currentPoint, 0.25, 0.25, 0.25).stream()
                        .filter(entity -> entity != player)
                        .toList();

                if (!nearbyEntities.isEmpty()) {
                    Entity hitEntity = nearbyEntities.getFirst();
                    Location hitLocation = hitEntity.getLocation();
                    Damageable target;
                    if (hitEntity instanceof Damageable) {
                        target = (Damageable) hitEntity;
                    } else {
                        cancel();
                        return;
                    }

                    double distance = player.getLocation().distance(hitLocation);
                    double damage = 1;

                    target.damage(0);
                    double newHealth = target.getHealth() - damage;
                    if (newHealth <= 0) {
                        target.setHealth(0);
                    } else {
                        target.setHealth(newHealth);
                    }

                    if (isDebugMode) {
                        player.sendMessage("Hit entity: " + hitEntity.getName() + " with damage: " + damage);
                        player.sendMessage("Entity health: " + target.getHealth());
                        player.sendMessage("Distance: " + distance);
                    }

                    player.getWorld().spawnParticle(Particle.HEART, hitLocation, 5);
                    cancel();
                    return;
                }

                if (currentPoint.getBlock().getType().isSolid()) {
                    if (isDebugMode) {
                        player.sendMessage("Hit block at: " + currentPoint.getBlockX() + ", " + currentPoint.getBlockY() + ", " + currentPoint.getBlockZ());
                        player.sendMessage("Block type: " + currentPoint.getBlock().getType());
                        player.sendMessage("Distance: " + i);
                    }
                    cancel();
                    return;
                }

                i += 1;
            }
        }.runTaskTimer(plugin, 0L, 1L); // Adjust the delay (1L) as needed
    }
}