package us.dingl.connorPlugin.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import us.dingl.connorPlugin.ConnorPlugin;
import us.dingl.connorPlugin.CrossProjectile.ShootCrossProjectile;
import us.dingl.connorPlugin.Hitscan.ShootHitscan;

/**
 * Command executor for the /shoothitscantest command.
 * This command allows players to perform a hitscan test with a specified range.
 */
public class ShootCrossProjectileTest implements CommandExecutor {

    private final ConnorPlugin plugin;

    /**
     * Constructor for ShootHitscanTest.
     *
     * @param plugin The instance of the ConnorPlugin.
     */
    public ShootCrossProjectileTest(ConnorPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Executes the given command, returning its success.
     *
     * @param commandSender Source of the command
     * @param command       Command which was executed
     * @param s             Alias of the command which was used
     * @param strings       Passed command arguments
     * @return true if a valid command, otherwise false
     */
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (commandSender instanceof Player player) {
            // Check if the player has the required permission
            if (player.hasPermission("connorplugin.shoothitscantest")) {
                // Ensure a range argument is provided
                if (strings.length == 0) {
                    player.sendMessage("You must specify a range for the hitscan test!");
                    return false;
                } else {
                    try {
                        // Parse the range argument and perform the hitscan
                        double range = Double.parseDouble(strings[0]);
                        ShootCrossProjectile shootCrossProjectile = new ShootCrossProjectile(plugin);
                        shootCrossProjectile.performShot(player, range);
                        return true;
                    } catch (NumberFormatException e) {
                        player.sendMessage("Invalid range specified!");
                        return false;
                    }
                }
            } else {
                player.sendMessage("You do not have permission to use this command!");
                return false;
            }
        } else {
            commandSender.sendMessage("You must be a player to use this command!");
            return false;
        }
    }
}