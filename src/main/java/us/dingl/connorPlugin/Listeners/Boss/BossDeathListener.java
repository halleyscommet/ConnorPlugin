package us.dingl.connorPlugin.Listeners.Boss;

import org.bukkit.Material;
import org.bukkit.entity.Enderman;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scoreboard.Team;
import us.dingl.connorPlugin.ConnorPlugin;


public class BossDeathListener implements Listener {

    private final ConnorPlugin plugin;

    public BossDeathListener(ConnorPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBossDeath(EntityDeathEvent event) {
        if (event.getEntity() instanceof Enderman) {
            Team bossTeam = plugin.getServer().getScoreboardManager().getMainScoreboard().getTeam("RC9");

            if (bossTeam != null && bossTeam.hasEntry(event.getEntity().getUniqueId().toString())) {
                event.getDrops().clear();

                ItemStack item = new ItemStack(Material.END_PORTAL_FRAME);
                ItemMeta meta = item.getItemMeta();
                item.setAmount(1);
                item.setItemMeta(meta);

                event.getDrops().add(item);
            }
        }
    }
}