package us.dingl.incursionImminent.Commands;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import us.dingl.incursionImminent.IncursionImminent;

import java.util.List;

public class SetSpawnLocationCommand implements CommandExecutor, TabExecutor {

    private final IncursionImminent plugin;

    public SetSpawnLocationCommand(IncursionImminent plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (!(commandSender instanceof Player player)) {
            commandSender.sendMessage("This command can only be used by players.");
            return true;
        }
        if (!player.getName().equals("halleyscommet") && !player.getName().equals("Ronnoc_0999")) {
            commandSender.sendMessage("You do not have permission to use this command.");
            return true;
        }
        if (strings.length < 3) {
            commandSender.sendMessage("Usage: /setspawn <x> <y> <z>");
            return true;
        }

        int x, y, z;
        try {
            x = Integer.parseInt(strings[0]);
            y = Integer.parseInt(strings[1]);
            z = Integer.parseInt(strings[2]);
        } catch (NumberFormatException e) {
            commandSender.sendMessage("Invalid coordinates.");
            return true;
        }

        plugin.spawnLocation.setWorld(player.getWorld());
        plugin.spawnLocation.setX(x);
        plugin.spawnLocation.setY(y);
        plugin.spawnLocation.setZ(z);

        commandSender.sendMessage("Spawn location set to " + x + ", " + y + ", " + z + " in world \"" + player.getWorld().getName() + "\"");

        return true;
    }


    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (!(commandSender instanceof Player player)) {
            return List.of();
        }

        Location playerLookingAt = player.getTargetBlock(null, 5).getLocation();
        if (strings.length == 1) {
            return List.of(String.valueOf((int) playerLookingAt.getX()));
        } else if (strings.length == 2) {
            return List.of(String.valueOf((int) playerLookingAt.getY()));
        } else if (strings.length == 3) {
            return List.of(String.valueOf((int) playerLookingAt.getZ()));
        } else {
            return List.of();
        }
    }
}
