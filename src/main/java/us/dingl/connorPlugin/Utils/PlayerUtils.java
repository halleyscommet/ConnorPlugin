package us.dingl.connorPlugin.Utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PlayerUtils {

    public boolean isOnSolid(Player player) {
        Location loc = player.getLocation();
        loc.setY(loc.getY() - 0.1);
        Material mat = loc.getBlock().getType();

        return mat.isSolid();
    }

    /**
     * Gets the player corresponding to the given UUID.
     *
     * @param uuid The UUID of the player.
     * @return The Player object, or null if the player is not online.
     */
    public static Player getPlayerByUUID(UUID uuid) {
        return Bukkit.getPlayer(uuid);
    }
}
