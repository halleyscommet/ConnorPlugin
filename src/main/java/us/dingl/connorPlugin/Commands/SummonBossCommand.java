package us.dingl.connorPlugin.Commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.NotNull;
import us.dingl.connorPlugin.ConnorPlugin;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class SummonBossCommand implements CommandExecutor {

    private final ConnorPlugin plugin;

    public SummonBossCommand(ConnorPlugin plugin) {
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
        if (strings.length != 0) {
            commandSender.sendMessage("Usage: /summonboss");
            return true;
        }

        Location spawnLocation = plugin.spawnLocation;
        if (spawnLocation.getWorld() == null) {
            commandSender.sendMessage("Spawn location has not been set.");
            return true;
        }

        Location newSpawnLocation = new Location(spawnLocation.getWorld(), spawnLocation.getX(), spawnLocation.getY() + 2, spawnLocation.getZ());

        // set boss settings
        Enderman boss = (Enderman) spawnLocation.getWorld().spawnEntity(newSpawnLocation, EntityType.ENDERMAN);
        boss.customName(Component.text("§d§kXIX§r §5Stargate Igniter§r §d§kXIX§r"));
        boss.setCustomNameVisible(true);
        boss.setScreaming(true);
        boss.setPersistent(true);
        boss.setRemoveWhenFarAway(false);
        boss.setCanPickupItems(false);
        Objects.requireNonNull(boss.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE)).setBaseValue(
                Objects.requireNonNull(boss.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE)).getBaseValue() + 2
        );
        Objects.requireNonNull(boss.getAttribute(Attribute.GENERIC_MAX_HEALTH)).setBaseValue(400);
        boss.setHealth(400);
        boss.addPotionEffect(PotionEffectType.GLOWING.createEffect(Integer.MAX_VALUE, 0));

        // give boss a block
        boss.setCarriedBlock(Material.CHORUS_FLOWER.createBlockData());

        // give boss a team
        Scoreboard scoreboard = player.getServer().getScoreboardManager().getMainScoreboard();
        Team team = scoreboard.getTeam("RC9");
        if (team == null) {
            team = scoreboard.registerNewTeam("RC9");
        }
        team.addEntry(boss.getUniqueId().toString());

        // set team settings
        List<Player> players = Stream.of(
                player.getServer().getPlayer("halleyscommet"),
                player.getServer().getPlayer("Ronnoc_0999"),
                player.getServer().getPlayer("Collera"),
                player.getServer().getPlayer("TreeeKid"),
                player.getServer().getPlayer("Sirpigsalot123")
        ).filter(Objects::nonNull).toList();
        for (Player p : players) {
            team.addEntry(p.getName());
        }
        team.canSeeFriendlyInvisibles();
        team.setAllowFriendlyFire(false);
        team.displayName(Component.text("§bRC9§r"));
        team.color(NamedTextColor.AQUA);

        // create counter team
        Team counterTeam = scoreboard.getTeam("Resistance");
        if (counterTeam == null) {
            counterTeam = scoreboard.registerNewTeam("Resistance");
        }
        for (Player p : plugin.getServer().getOnlinePlayers()) {
            if (!team.hasEntry(p.getName())) {
                if (!players.contains(p)) {
                    counterTeam.addEntry(p.getName());
                }
            }
        }
        counterTeam.canSeeFriendlyInvisibles();
        counterTeam.setAllowFriendlyFire(false);
        counterTeam.displayName(Component.text("§cResistance§r"));
        counterTeam.color(NamedTextColor.RED);

        // create boss bar
        BossBar bossBar = Bukkit.createBossBar(
                "§d§kXIX§r §5Stargate Igniter§r §d§kXIX§r",
                BarColor.PINK,
                BarStyle.SEGMENTED_10
        );

        for (Player p : plugin.getServer().getOnlinePlayers()) {
            bossBar.addPlayer(p);
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                if (boss.isDead()) {
                    bossBar.removeAll();
                    cancel();
                }

                bossBar.setProgress(boss.getHealth() / Objects.requireNonNull(boss.getAttribute(Attribute.GENERIC_MAX_HEALTH)).getBaseValue());
            }
        }.runTaskTimer(plugin, 0L, 15L);

        return true;
    }
}
