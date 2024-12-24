package us.dingl.connorPlugin.Listeners.Murasama;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import us.dingl.connorPlugin.ConnorPlugin;
import us.dingl.connorPlugin.Hitscan.ShootHitscan;
import us.dingl.connorPlugin.Utils.ItemUtil;

public class RightClickChargedListener implements Listener {

    private final ConnorPlugin plugin;

    public RightClickChargedListener(ConnorPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onRightClickCharged(PlayerInteractEvent event) {
        if (event.getAction().isRightClick()) {
            ItemUtil itemUtil = new ItemUtil();
            ShootHitscan shootHitscan = new ShootHitscan(plugin);
            ItemStack item = event.getItem();
        }
    }
}
