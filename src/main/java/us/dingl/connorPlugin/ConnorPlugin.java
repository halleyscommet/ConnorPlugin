package us.dingl.connorPlugin;

import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import us.dingl.connorPlugin.Commands.GiveBow;
import us.dingl.connorPlugin.Commands.GiveSword;
import us.dingl.connorPlugin.Commands.ShootHitscanTest;
import us.dingl.connorPlugin.Commands.ToggleDebugMode;
import us.dingl.connorPlugin.Listeners.Murasama.LeftClickChargedListener;
import us.dingl.connorPlugin.Listeners.SplinteredSoulbow.BowMeleeDamageListener;
import us.dingl.connorPlugin.Listeners.SplinteredSoulbow.OffhandListener;
import us.dingl.connorPlugin.Listeners.SplinteredSoulbow.RightClickListener;
import us.dingl.connorPlugin.Listeners.Murasama.HoldCrouchListener;
import us.dingl.connorPlugin.Utils.ItemUtil;
import us.dingl.connorPlugin.Utils.PlayerUtils;

import java.util.*;

/**
 * Main class for the ConnorPlugin.
 * This class is responsible for initializing and managing the plugin.
 */
public final class ConnorPlugin extends JavaPlugin {

    /**
     * A map to track the last attack time of players.
     * The key is the player's UUID and the value is the timestamp of the last attack.
     */
    public final HashMap<UUID, Long> lastAttackTime = new HashMap<>();

    /**
     * A set to track players who are currently sneaking.
     * The set contains the UUIDs of sneaking players.
     */
    public final Set<UUID> sneakingPlayers = new HashSet<>();

    /**
     * A map to track players who are currently charged.
     * The key is the player's UUID and the value is a set of integers.
     */
    public final HashMap<UUID, Integer> chargedPlayers = new HashMap<>();

    /**
     * The cooldown period in ticks (1 second).
     */
    public static final long COOLDOWN_TICKS = 20L;

    /**
     * A map to track the debug mode status of players.
     * The key is the player's UUID and the value is a boolean indicating debug mode status.
     */
    public final HashMap<UUID, Boolean> debugMode = new HashMap<>();

    /**
     * Called when the plugin is enabled.
     * This method contains the startup logic for the plugin.
     */
    @Override
    public void onEnable() {
        // Plugin startup logic
        getLogger().info("ConnorPlugin has been enabled!");

        // Register commands and listeners
        registerCommands();
        registerListeners();

        // Make runnables
        makeRunnables();
    }

    /**
     * Called when the plugin is disabled.
     * This method contains the shutdown logic for the plugin.
     */
    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info("ConnorPlugin has been disabled!");
    }

    /**
     * Registers the commands for the plugin.
     * This method binds the command executors to their respective commands.
     */
    private void registerCommands() {
        // Register commands here
        Objects.requireNonNull(getCommand("givebow")).setExecutor(new GiveBow());
        Objects.requireNonNull(getCommand("givesword")).setExecutor(new GiveSword());
        Objects.requireNonNull(getCommand("shoothitscantest")).setExecutor(new ShootHitscanTest(this));
        Objects.requireNonNull(getCommand("shootcrossprojectiletest")).setExecutor(new ShootHitscanTest(this));
        Objects.requireNonNull(getCommand("toggledm")).setExecutor(new ToggleDebugMode(this));

        // Register tab completers here
        Objects.requireNonNull(getCommand("toggledm")).setTabCompleter(new ToggleDebugMode(this));
    }

    /**
     * Registers the event listeners for the plugin.
     * This method binds the event listeners to the plugin.
     */
    private void registerListeners() {
        // Register listeners here
        getServer().getPluginManager().registerEvents(new BowMeleeDamageListener(this), this);
        getServer().getPluginManager().registerEvents(new RightClickListener(this), this);
        getServer().getPluginManager().registerEvents(new OffhandListener(), this);

        getServer().getPluginManager().registerEvents(new HoldCrouchListener(this), this);
        getServer().getPluginManager().registerEvents(new LeftClickChargedListener(this), this);
    }

    /**
     * Creates and schedules the runnables for the plugin.
     */
    private void makeRunnables() {

        // Display if player is charged
        new BukkitRunnable() {

            @Override
            public void run() {
                int charge = chargedPlayers.keySet().stream()
                        .findFirst()
                        .map(PlayerUtils::getPlayerByUUID)
                        .map(player -> chargedPlayers.getOrDefault(player.getUniqueId(), 0))
                        .orElse(0);

                if (charge > 0) {
                    Player player = PlayerUtils.getPlayerByUUID(chargedPlayers.keySet().stream().findFirst().orElse(null));
                    Particle.DustOptions dust = new Particle.DustOptions(Color.ORANGE, 2);

                    player.getWorld().spawnParticle(Particle.DUST, player.getLocation().add(0, 2.5, 0), 0, dust);
                }
            }
        }.runTaskTimer(this, 0L, 1L);

        // Remove player from chargedPlayers map if they don't have the sword
        new BukkitRunnable() {

            @Override
            public void run() {
                for (UUID uuid : chargedPlayers.keySet()) {
                    Player player = PlayerUtils.getPlayerByUUID(uuid);
                    if (!player.getInventory().getItemInMainHand().isSimilar(new ItemUtil().giveSword())) {
                        chargedPlayers.remove(uuid);
                    }
                }
            }
        }.runTaskTimer(this, 0L, 20L);

        // Remove player from chargedPlayers map if they are no longer online
        new BukkitRunnable() {

            @Override
            public void run() {
                chargedPlayers.keySet().removeIf(uuid -> PlayerUtils.getPlayerByUUID(uuid) == null);
            }
        }.runTaskTimer(this, 0L, 200L);

        // Remove player from sneakingPlayers set if they are no longer sneaking
        new BukkitRunnable() {

            @Override
            public void run() {
                for (UUID uuid : sneakingPlayers) {
                    Player player = PlayerUtils.getPlayerByUUID(uuid);
                    if (!player.isSneaking()) {
                        sneakingPlayers.remove(uuid);
                    }
                }
            }
        }.runTaskTimer(this, 0L, 10L);

        // Remove player from sneakingPlayers set if they are no longer online
        new BukkitRunnable() {

            @Override
            public void run() {
                sneakingPlayers.removeIf(uuid -> PlayerUtils.getPlayerByUUID(uuid) == null);
            }
        }.runTaskTimer(this, 0L, 200L);

        // Remove player from lastAttackTime map if they are no longer online
        new BukkitRunnable() {

            @Override
            public void run() {
                for (UUID uuid : lastAttackTime.keySet()) {
                    if (PlayerUtils.getPlayerByUUID(uuid) == null) {
                        lastAttackTime.remove(uuid);
                    }
                }
            }
        }.runTaskTimer(this, 0L, 200L);

        // Remove player from debugMode map if they are no longer online
        new BukkitRunnable() {

            @Override
            public void run() {
                for (UUID uuid : debugMode.keySet()) {
                    if (PlayerUtils.getPlayerByUUID(uuid) == null) {
                        debugMode.remove(uuid);
                    }
                }
            }
        }.runTaskTimer(this, 0L, 200L);
    }
}