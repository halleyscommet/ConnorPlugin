package us.dingl.incursionImminent.Listeners;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import us.dingl.incursionImminent.Utils.ItemUtil;

import java.util.List;

public class CustomDeathMessageListener implements Listener {

    private final ItemUtil itemUtil;

    public CustomDeathMessageListener() {
        this.itemUtil = new ItemUtil();
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player target = event.getEntity();
        ItemStack sword = itemUtil.giveSword();
        ItemStack bow = itemUtil.giveBow();

        List<Entity> nearbyEntities = target.getWorld().getNearbyEntities(target.getLocation(), 80, 80, 80).stream()
                .filter(entity -> entity != target) // Ignore the player
                .toList();

        if (nearbyEntities.isEmpty()) {
            return;
        }

        for (Entity entity : nearbyEntities) {
            if (!(entity instanceof Player killer)) {
                continue;
            }

            if (killer.getInventory().getItemInMainHand().isSimilar(sword) || killer.getInventory().getItemInMainHand().isSimilar(bow)) {
                event.deathMessage(null); // Cancel the death message
            }
        }
    }
}
