package us.dingl.incursionImminent.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import us.dingl.incursionImminent.Utils.ItemUtil;

public class GiveSword implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        // Check if the command sender is one of the allowed users
        if (commandSender.getName().equals("Ronnoc_0999") || commandSender.getName().equals("halleyscommet")) {
            // Check if the command sender is a player
            if (commandSender instanceof Player player) {
                // Give the player a custom bow
                player.getInventory().addItem(new ItemUtil().giveSword());
                return true;
            }
            return false;
        }
        return false;
    }
}
