package us.dingl.connorPlugin.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import us.dingl.connorPlugin.Utils.ItemUtil;

/**
 * Command executor for the /givebow command.
 * This command allows specific players to receive a custom bow.
 */
public class GiveBow implements CommandExecutor {

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
        // Check if the command sender is one of the allowed users
        if (commandSender.getName().equals("Ronnoc_0999") || commandSender.getName().equals("halleyscommet")) {
            // Check if the command sender is a player
            if (commandSender instanceof Player player) {
                // Give the player a custom bow
                player.getInventory().addItem(new ItemUtil().giveBow());
                return true;
            }
            return false;
        }
        return false;
    }
}