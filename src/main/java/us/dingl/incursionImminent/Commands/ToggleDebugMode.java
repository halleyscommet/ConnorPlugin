package us.dingl.incursionImminent.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import us.dingl.incursionImminent.IncursionImminent;

import java.util.List;

/**
 * Command executor and tab completer for the /toggledm command.
 * This command toggles the debug mode for the player who executes it.
 */
public class ToggleDebugMode implements CommandExecutor, TabCompleter {

    private final IncursionImminent plugin;

    /**
     * Constructor for ToggleDebugMode.
     *
     * @param plugin The instance of the ConnorPlugin.
     */
    public ToggleDebugMode(IncursionImminent plugin) {
        this.plugin = plugin;
    }

    /**
     * Executes the /toggledm command.
     *
     * @param commandSender The sender of the command.
     * @param command       The command that was executed.
     * @param s             The alias of the command that was used.
     * @param strings       The arguments passed to the command.
     * @return true if the command was successful, false otherwise.
     */
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (commandSender instanceof Player player) {
            if (commandSender.hasPermission("connorplugin.op")) {
                if (strings.length == 0) {
                    // Toggle debug mode
                    boolean debug = !plugin.debugMode.getOrDefault(player.getUniqueId(), false);
                    plugin.debugMode.put(player.getUniqueId(), debug);
                    player.sendMessage("Debug mode " + (debug ? "enabled" : "disabled"));
                    return true;
                } else {
                    if (strings[0].equalsIgnoreCase("true")) {
                        plugin.debugMode.put(player.getUniqueId(), true);
                        player.sendMessage("Debug mode enabled");
                        return true;
                    } else if (strings[0].equalsIgnoreCase("false")) {
                        plugin.debugMode.put(player.getUniqueId(), false);
                        player.sendMessage("Debug mode disabled");
                        return true;
                    } else {
                        return false;
                    }
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * Provides tab completion options for the /toggledm command.
     *
     * @param commandSender The sender of the command.
     * @param command       The command that was executed.
     * @param s             The alias of the command that was used.
     * @param strings       The arguments passed to the command.
     * @return A list of possible completions for the final argument, or an empty list if there are no completions.
     */
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (strings.length == 1) {
            return List.of("true", "false");
        } else {
            return List.of();
        }
    }
}