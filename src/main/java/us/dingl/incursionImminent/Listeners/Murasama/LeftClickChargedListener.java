package us.dingl.incursionImminent.Listeners.Murasama;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import us.dingl.incursionImminent.IncursionImminent;
import us.dingl.incursionImminent.CrossProjectile.ShootCrossProjectile;
import us.dingl.incursionImminent.Utils.ItemUtil;

public class LeftClickChargedListener implements Listener {

    private final IncursionImminent plugin;

    public LeftClickChargedListener(IncursionImminent plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onLeftClickCharged(PlayerInteractEvent event) {
        if (event.getAction().isLeftClick()) {
            ItemUtil itemUtil = new ItemUtil();
            ShootCrossProjectile shootCross = new ShootCrossProjectile(plugin);
            ItemStack item = event.getItem();

            if (item != null && !item.isSimilar(itemUtil.giveSword())) {
                return;
            }

            if (!plugin.chargedPlayers.containsKey(event.getPlayer().getUniqueId())) {
                return;
            }

            int charge = plugin.chargedPlayers.getOrDefault(event.getPlayer().getUniqueId(), 0);
            if (charge <= 0) {
                return;
            }

            shootCross.performShot(event.getPlayer(), 20);
            plugin.chargedPlayers.put(event.getPlayer().getUniqueId(), charge - 1);
        }
    }
}
