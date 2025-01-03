package us.dingl.connorPlugin.Listeners.Boss;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Enderman;
import org.bukkit.scoreboard.Team;

public class BossUtils {

    public boolean doesBossExist() {
        for (Entity entity : Bukkit.getWorlds().getFirst().getEntities()) {
            if (entity instanceof Enderman boss) {
                Team bossTeam = boss.getServer().getScoreboardManager().getMainScoreboard().getTeam("RC9");
                if (bossTeam != null && bossTeam.hasEntry(boss.getUniqueId().toString())) {
                    return true;
                }
            }
        }
        return false;
    }

    public Enderman getBoss() {
        for (Entity entity : Bukkit.getWorlds().getFirst().getEntities()) {
            if (entity instanceof Enderman boss) {
                Team bossTeam = boss.getServer().getScoreboardManager().getMainScoreboard().getTeam("RC9");
                if (bossTeam != null && bossTeam.hasEntry(boss.getUniqueId().toString())) {
                    return boss;
                }
            }
        }
        return null;
    }

    public void removeBoss() {
        for (Entity entity : Bukkit.getWorlds().getFirst().getEntities()) {
            if (entity instanceof Enderman boss) {
                Team bossTeam = boss.getServer().getScoreboardManager().getMainScoreboard().getTeam("RC9");
                if (bossTeam != null && bossTeam.hasEntry(boss.getUniqueId().toString())) {
                    boss.remove();
                }
            }
        }
    }
}