package us.dingl.connorPlugin.Listeners.SplinteredSoulbow;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import us.dingl.connorPlugin.ConnorPlugin;
import us.dingl.connorPlugin.Hitscan.ShootHitscan;
import us.dingl.connorPlugin.Utils.ItemUtil;

/**
 * Listener class that handles right-click interactions by players.
 */
public class RightClickListener implements Listener {

    private final ConnorPlugin plugin;

    /**
     * Constructor for RightClickListener.
     *
     * @param plugin The instance of the ConnorPlugin.
     */
    public RightClickListener(ConnorPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Event handler for PlayerInteractEvent.
     * This method is called when a player interacts (right-clicks) with an item.
     * If the player is holding a custom bow, it performs a hitscan operation.
     *
     * @param event The PlayerInteractEvent.
     */
    @EventHandler
    public void onRightClick(PlayerInteractEvent event) {
        if (event.getAction().isRightClick()) {
            ItemUtil itemUtil = new ItemUtil();
            ShootHitscan shootHitscan = new ShootHitscan(plugin);
            ItemStack item = event.getItem();

            if (item != null && item.isSimilar(itemUtil.giveBow())) {
                event.setCancelled(true);
                Player player = event.getPlayer();
                shootHitscan.performHitscan(player, 80);
            }
        }
    }
}