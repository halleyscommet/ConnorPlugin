package us.dingl.incursionImminent.Listeners.Murasama;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import us.dingl.incursionImminent.IncursionImminent;
import us.dingl.incursionImminent.Utils.ItemUtil;
import us.dingl.incursionImminent.Utils.PlayerUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HoldCrouchListener implements Listener {

    private final IncursionImminent plugin;
    private final PlayerUtils utils;
    private final ItemUtil itemUtil;
    private static final Map<UUID, Integer> playerCounts = new HashMap<>();

    public HoldCrouchListener(IncursionImminent plugin) {
        this.plugin = plugin;
        this.utils = new PlayerUtils();
        this.itemUtil = new ItemUtil();
        Bukkit.getPluginManager().registerEvents(new ProjectileBounceListener(), plugin);
    }

    @EventHandler
    public void onCrouch(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();
        ItemStack item = player.getInventory().getItemInMainHand();

        if (event.isSneaking() && item.isSimilar(itemUtil.giveSword())) {
            plugin.sneakingPlayers.add(playerUUID);

            if (utils.isOnSolid(player)) {
                new BukkitRunnable() {
                    private int count = 0;
                    private double phi = 0;

                    @Override
                    public void run() {
                        if (!plugin.sneakingPlayers.contains(playerUUID)) {
                            cancel();
                            return;
                        }

                        playerCounts.put(playerUUID, count);
                        if (count < 3) {
                            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, (0.5f + (0.25f * count)));
                        } else if (count == 3) {
                            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 2.0f); // Higher pitch for the last note
                        }
                        count++;

                        playerCounts.put(playerUUID, count);

                        Location loc = player.getLocation();
                        loc.setY(loc.getY() + 1);
                        Location left = loc.clone().add(loc.getDirection().rotateAroundY(Math.toRadians(45)).normalize().multiply(1.5));
                        Location middle = loc.clone().add(loc.getDirection().normalize().multiply(1));
                        Location right = loc.clone().add(loc.getDirection().rotateAroundY(Math.toRadians(-45)).normalize().multiply(1.5));

                        ItemStack dust = new ItemStack(Material.REDSTONE_BLOCK);

                        if (count == 1) {
                            player.getWorld().spawnParticle(Particle.ITEM, left, 0, dust);
                        } else if (count == 2) {
                            player.getWorld().spawnParticle(Particle.ITEM, left, 0, dust);
                            player.getWorld().spawnParticle(Particle.ITEM, middle, 0, dust);
                        } else if (count == 3) {
                            player.getWorld().spawnParticle(Particle.ITEM, left, 0, dust);
                            player.getWorld().spawnParticle(Particle.ITEM, middle, 0, dust);
                            player.getWorld().spawnParticle(Particle.ITEM, right, 0, dust);
                        }

                        if (count >= 4) {
                            phi = phi + Math.PI / 16;
                            double radius = 1.6;
                            plugin.chargedPlayers.put(playerUUID, 3);

                            for (int ring = 1; ring <= 3; ring++) {
                                player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1f, 1.2f); // Play sound for each ring

                                for (double t = 0; t < 2 * Math.PI; t += Math.PI / 16) {
                                    double x = radius * Math.cos(t + phi);
                                    double z = radius * Math.sin(t + phi);
                                    double y = radius * Math.sin(t + phi);

                                    Vector vector = new Vector(x, y, z);
                                    vector = rotateAroundAxisY(vector, Math.toRadians((45 * count) * ring));
                                    vector = rotateAroundAxisZ(vector, Math.toRadians((30 * count) * ring));

                                    loc.add(vector.getX(), vector.getY(), vector.getZ());
                                    player.getWorld().spawnParticle(Particle.ITEM, loc, 0, dust);
                                    loc.subtract(vector.getX(), vector.getY(), vector.getZ());
                                }
                            }
                        }
                    }

                    private Vector rotateAroundAxisY(Vector v, double angle) {
                        double x = v.getX() * Math.cos(angle) - v.getZ() * Math.sin(angle);
                        double z = v.getX() * Math.sin(angle) + v.getZ() * Math.cos(angle);
                        return v.setX(x).setZ(z);
                    }

                    private Vector rotateAroundAxisZ(Vector v, double angle) {
                        double x = v.getX() * Math.cos(angle) - v.getY() * Math.sin(angle);
                        double y = v.getX() * Math.sin(angle) + v.getY() * Math.cos(angle);
                        return v.setX(x).setY(y);
                    }
                }.runTaskTimer(plugin, 0, 5L);
            } else {
                plugin.sneakingPlayers.remove(playerUUID);
            }
        } else {
            plugin.sneakingPlayers.remove(playerUUID);
            playerCounts.put(playerUUID, 0); // Reset the player count to 0 when they stop sneaking
        }
    }

    public class ProjectileBounceListener implements Listener {

        @EventHandler
        public void onProjectileHit(ProjectileHitEvent event) {
            Entity hitEntity = event.getHitEntity();
            if (hitEntity instanceof Player player) {
                UUID playerUUID = player.getUniqueId();
                int count = playerCounts.getOrDefault(playerUUID, 0);
                if (count >= 4) {
                    event.setCancelled(true);
                    Projectile projectile = event.getEntity();
                    Vector velocity = projectile.getVelocity();
                    Vector reflected = velocity.multiply(-0.8);
                    projectile.setVelocity(reflected);

                    player.getWorld().playSound(player.getLocation(), Sound.ENTITY_BLAZE_HURT, 1000f, 2f);

                    // Create a particle trail for the deflected projectile
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            if (!projectile.isValid() || projectile.isOnGround()) {
                                this.cancel();
                                return;
                            }
                            projectile.getWorld().spawnParticle(Particle.CRIT, projectile.getLocation(), 5, 0.1, 0.1, 0.1, 0.01);
                        }
                    }.runTaskTimer(plugin, 0L, 1L); // Adjust the interval as needed
                }
            }
        }
    }
}