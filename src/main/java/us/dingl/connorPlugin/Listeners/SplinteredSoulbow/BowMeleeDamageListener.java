package us.dingl.connorPlugin.Listeners.SplinteredSoulbow;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import us.dingl.connorPlugin.ConnorPlugin;
import us.dingl.connorPlugin.Utils.ItemUtil;

import java.util.UUID;

/**
 * Listener class that handles melee damage events when a player is holding a bow.
 */
public class BowMeleeDamageListener implements Listener {

    private final ConnorPlugin plugin;

    /**
     * Constructor for BowMeleeDamageListener.
     *
     * @param plugin The instance of the ConnorPlugin.
     */
    public BowMeleeDamageListener(ConnorPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Event handler for EntityDamageByEntityEvent.
     * This method is called when an entity is damaged by another entity.
     * If the damager is a player holding a bow, it calculates and sets the damage based on a cooldown.
     *
     * @param event The EntityDamageByEntityEvent.
     */
    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player player) {
            ItemStack itemInHand = player.getInventory().getItemInMainHand();

            // Check if the player is holding a bow
            if (itemInHand.isSimilar(new ItemUtil().giveBow())) {
                UUID playerUUID = player.getUniqueId();
                long currentTime = System.currentTimeMillis();
                long lastTime = plugin.lastAttackTime.getOrDefault(playerUUID, 0L);

                // Calculate the elapsed time since the last attack in ticks
                long elapsedTicks = (currentTime - lastTime) / 50L; // 1 tick = 50 ms

                // Calculate the cooldown progress (0.0 to 1.0)
                double cooldownProgress = Math.min(1.0, (double) elapsedTicks / ConnorPlugin.COOLDOWN_TICKS);

                // Apply scaled damage based on the cooldown progress
                double maxDamage = 8.0;
                double damage = maxDamage * cooldownProgress;

                // Set the damage
                event.setDamage(damage);

                // Update the last attack time
                if (cooldownProgress >= 1.0) {
                    plugin.lastAttackTime.put(playerUUID, currentTime);
                } else {
                    // Cancel the event if the player is on cooldown
                    event.setCancelled(true);
                }
            }
        }
    }
}