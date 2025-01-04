package us.dingl.incursionImminent.Listeners.Boss;

import org.bukkit.entity.Enderman;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.scoreboard.Team;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BossDamageListener implements Listener {

    private final Map<UUID, Integer> bossHealthSegments = new HashMap<>();

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Enderman boss && event.getDamager() instanceof Player player) {
            Team bossTeam = player.getServer().getScoreboardManager().getMainScoreboard().getTeam("RC9");

            if (bossTeam != null && bossTeam.hasEntry(boss.getUniqueId().toString()) && bossTeam.hasEntry(player.getName())) {
                event.setCancelled(true);
            }
        }
    }
}