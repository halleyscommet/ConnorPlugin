package us.dingl.incursionImminent.CrossProjectile;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import us.dingl.incursionImminent.IncursionImminent;
import net.kyori.adventure.text.Component;
import us.dingl.incursionImminent.Utils.ItemUtil;

import java.util.*;

public class ShootCrossProjectile {

    private final IncursionImminent plugin;
    private final ItemUtil itemUtil;
    private final Set<UUID> processedEntities = new HashSet<>();

    public ShootCrossProjectile(IncursionImminent plugin) {
        this.plugin = plugin;
        this.itemUtil = new ItemUtil();
    }

    public void performShot(Player player, double range) {
        // Get the player's eye location and direction
        Location eyeLocation = player.getEyeLocation();
        Vector direction = eyeLocation.getDirection();

        // Define additional eye locations for vertical, horizontal, and diagonal ray traces
        Location eyeLocation2 = eyeLocation.clone().add(0.65, 0, 0);
        Location eyeLocation3 = eyeLocation.clone().add(-0.65, 0, 0);
        Location eyeLocation4 = eyeLocation.clone().add(0, 0, 0.65);
        Location eyeLocation5 = eyeLocation.clone().add(0, 0, -0.65);
        Location eyeLocation6 = eyeLocation.clone().add(0, 0.65, 0);
        Location eyeLocation7 = eyeLocation.clone().add(0, -0.65, 0);
        Location eyeLocation8 = eyeLocation.clone().add(0.65, 0.65, 0);
        Location eyeLocation9 = eyeLocation.clone().add(-0.65, -0.65, 0);
        Location eyeLocation10 = eyeLocation.clone().add(0, 0.65, 0.65);
        Location eyeLocation11 = eyeLocation.clone().add(0, -0.65, -0.65);

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
                Particle.DustOptions dustOptions = new Particle.DustOptions(org.bukkit.Color.RED, 1.5f);
                currentPoint.getWorld().spawnParticle(Particle.DUST, currentPoint, 1, 0, 0, 0, 1000, dustOptions);

                List<Entity> nearbyEntities = player.getWorld().getNearbyEntities(currentPoint, 0.65, 0.65, 0.65).stream()
                        .filter(entity -> entity != player)
                        .toList();

                if (!nearbyEntities.isEmpty()) {
                    Entity hitEntity = nearbyEntities.getFirst();
                    if (processedEntities.contains(hitEntity.getUniqueId())) {
                        cancel();
                        return;
                    }
                    processedEntities.add(hitEntity.getUniqueId());

                    Location hitLocation = hitEntity.getLocation();
                    Damageable target;
                    if (hitEntity instanceof Damageable) {
                        target = (Damageable) hitEntity;
                    } else {
                        cancel();
                        return;
                    }

                    double distance = player.getLocation().distance(hitLocation);
                    double damage = 4;

                    target.damage(0);
                    double newHealth = target.getHealth() - damage;
                    target.getWorld().playSound(target.getLocation(), Sound.BLOCK_RESPAWN_ANCHOR_DEPLETE, 2, 2);
                    if (newHealth <= 0) {
                        target.setHealth(0);
                        if (!(target instanceof Player)) {
                            cancel();
                            return;
                        }

                        ItemStack sword = itemUtil.giveSword();
                        Component itemDetails = itemUtil.getItemDetails(sword);
                        Bukkit.broadcast(
                                Component.text(
                                        target.getName() + " was split in two by " + player.getName() + "-01 using The "
                                ).append(
                                        Component.text("§b[§c§kXIX§r §4Murasama §c§kXIX§b]§r")
                                                .hoverEvent(itemDetails)
                                ));
                    } else {
                        target.setHealth(newHealth);
                    }

                    if (isDebugMode) {
                        player.sendMessage("Hit entity: " + hitEntity.getName() + " with damage: " + damage);
                        player.sendMessage("Entity health: " + target.getHealth());
                        player.sendMessage("Distance: " + distance);
                    }

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
        }.runTaskTimer(plugin, 0L, 1L);
    }
}