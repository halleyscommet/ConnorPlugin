package us.dingl.incursionImminent.Listeners.Boss;

import org.bukkit.entity.Enderman;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.scoreboard.Team;
import us.dingl.incursionImminent.IncursionImminent;

public class EndermanBlockPlaceListener implements Listener {

    private final IncursionImminent plugin;

    public EndermanBlockPlaceListener(IncursionImminent plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEndermanPlaceBlock(EntityChangeBlockEvent event) {
        if (event.getEntity() instanceof Enderman) {
            Team bossTeam = plugin.getServer().getScoreboardManager().getMainScoreboard().getTeam("RC9");

            if (bossTeam != null && bossTeam.hasEntry(event.getEntity().getUniqueId().toString())) {
                event.setCancelled(true);
            }
        }
    }
}