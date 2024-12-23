package us.dingl.connorPlugin.Listeners.SplinteredSoulbow;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import us.dingl.connorPlugin.Utils.ItemUtil;

/**
 * Listener class for handling offhand item swap events.
 */
public class OffhandListener implements Listener {

    /**
     * Event handler for when a player swaps items between their main hand and offhand.
     *
     * @param event The PlayerSwapHandItemsEvent triggered when a player swaps items.
     */
    @EventHandler
    public void onPlayerSwapHandItems(PlayerSwapHandItemsEvent event) {
        ItemStack offhandItem = event.getOffHandItem();

        // Check if the offhand item is similar to the custom bow item
        if (offhandItem.isSimilar(new ItemUtil().giveBow())) {
            event.setCancelled(true); // Cancel the item swap event

            Player player = event.getPlayer();

            // Get the player's current location
            Location playerLocation = player.getLocation();

            // Spawn a flash particle effect at the player's location
            player.getWorld().spawnParticle(Particle.EXPLOSION_EMITTER, playerLocation, 999, 6, 3, 6, 20);

            // Apply various potion effects to the player
            player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 20 * 20, 1));
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 15 * 20, 6));
            player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 10 * 20, 4));
        }
    }
}