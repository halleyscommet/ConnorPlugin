package us.dingl.connorPlugin.Listeners.Murasama;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import us.dingl.connorPlugin.ConnorPlugin;
import us.dingl.connorPlugin.Utils.ItemUtil;
import us.dingl.connorPlugin.Utils.PlayerUtils;

import java.util.UUID;

/**
 * Listener class that handles player crouch events.
 */
public class HoldCrouchListener implements Listener {

    private final ConnorPlugin plugin;
    private final PlayerUtils utils;
    private final ItemUtil itemUtil;

    /**
     * Constructor for HoldCrouchListener.
     *
     * @param plugin The main plugin instance.
     */
    public HoldCrouchListener(ConnorPlugin plugin) {
        this.plugin = plugin;
        this.utils = new PlayerUtils();
        this.itemUtil = new ItemUtil();
    }

    /**
     * Event handler for PlayerToggleSneakEvent.
     *
     * @param event The PlayerToggleSneakEvent.
     */
    @EventHandler
    public void onCrouch(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();
        ItemStack item = player.getInventory().getItemInMainHand();

        // Check if the player is sneaking and holding the specific sword
        if (event.isSneaking() && item.isSimilar(itemUtil.giveSword())) {
            plugin.sneakingPlayers.add(playerUUID);

            // Check if the player is on solid ground
            if (utils.isOnSolid(player)) {

                // Schedule a task to play the sound effect and spawn particles
                new BukkitRunnable() {
                    private int count = 0;
                    private double phi = 0;

                    @Override
                    public void run() {
                        // Stop if the player is no longer sneaking or has been removed from the set
                        if (!plugin.sneakingPlayers.contains(playerUUID)) {
                            cancel();
                            return;
                        }

                        // Play the sound
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, (1.0f + (0.5f * count)));
                        count++;

                        // Calculate positions for left, middle, and right particles
                        Location loc = player.getLocation();
                        loc.setY(loc.getY() + 1); // Adjust height if needed
                        Location left = loc.clone().add(loc.getDirection().rotateAroundY(Math.toRadians(45)).normalize().multiply(1.5));
                        Location middle = loc.clone().add(loc.getDirection().normalize().multiply(1));
                        Location right = loc.clone().add(loc.getDirection().rotateAroundY(Math.toRadians(-45)).normalize().multiply(1.5));

//                        Particle.DustOptions dust = new Particle.DustOptions(org.bukkit.Color.RED, 1);
                        ItemStack dust = new ItemStack(Material.REDSTONE_BLOCK);

                        // Spawn particles at left, middle, and right positions
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

                        // Spawn rotating particle rings around the player
                        if (count >= 4) {
                            phi = phi + Math.PI / 16; // Increase the angle to rotate the rings
                            double radius = 1.6; // Set a constant radius for all rings
                            plugin.chargedPlayers.add(playerUUID);

                            // Spawn particles in three rings around the player
                            for (int ring = 1; ring <= 3; ring++) {
                                for (double t = 0; t < 2 * Math.PI; t += Math.PI / 16) {
                                    double x = radius * Math.cos(t + phi);
                                    double z = radius * Math.sin(t + phi);
                                    double y = radius * Math.sin(t + phi);

                                    // Rotate around the Y and Z axes
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

                    /**
                     * Helper method to rotate a vector around the Y axis.
                     *
                     * @param v The vector to rotate.
                     * @param angle The angle to rotate by.
                     * @return The rotated vector.
                     */
                    private Vector rotateAroundAxisY(Vector v, double angle) {
                        double x = v.getX() * Math.cos(angle) - v.getZ() * Math.sin(angle);
                        double z = v.getX() * Math.sin(angle) + v.getZ() * Math.cos(angle);
                        return v.setX(x).setZ(z);
                    }

                    /**
                     * Helper method to rotate a vector around the Z axis.
                     *
                     * @param v The vector to rotate.
                     * @param angle The angle to rotate by.
                     * @return The rotated vector.
                     */
                    private Vector rotateAroundAxisZ(Vector v, double angle) {
                        double x = v.getX() * Math.cos(angle) - v.getY() * Math.sin(angle);
                        double y = v.getX() * Math.sin(angle) + v.getY() * Math.cos(angle);
                        return v.setX(x).setY(y);
                    }
                }.runTaskTimer(plugin, 0, 10L); // 20 ticks = 1 second
            } else {
                plugin.sneakingPlayers.remove(playerUUID);
            }
        } else {
            plugin.sneakingPlayers.remove(playerUUID);
        }
    }
}