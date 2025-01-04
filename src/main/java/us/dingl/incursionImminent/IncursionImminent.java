package us.dingl.incursionImminent;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import us.dingl.incursionImminent.Commands.*;
import us.dingl.incursionImminent.Listeners.Boss.*;
import us.dingl.incursionImminent.Listeners.CustomDeathMessageListener;
import us.dingl.incursionImminent.Listeners.Murasama.LeftClickChargedListener;
import us.dingl.incursionImminent.Listeners.SplinteredSoulbow.BowMeleeDamageListener;
import us.dingl.incursionImminent.Listeners.SplinteredSoulbow.OffhandListener;
import us.dingl.incursionImminent.Listeners.SplinteredSoulbow.RightClickListener;
import us.dingl.incursionImminent.Listeners.Murasama.HoldCrouchListener;
import us.dingl.incursionImminent.Utils.ItemUtil;
import us.dingl.incursionImminent.Utils.PlayerUtils;

import java.util.*;

/**
 * Main class for the ConnorPlugin.
 * This class is responsible for initializing and managing the plugin.
 */
public final class IncursionImminent extends JavaPlugin {

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

    public final Location spawnLocation = new Location(null, 0, 0, 0);

    private final Map<UUID, Integer> bossHealthSegments = new HashMap<>();

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
        Objects.requireNonNull(getCommand("testrichtext")).setExecutor(new TestRichTextCommand());
        Objects.requireNonNull(getCommand("setspawn")).setExecutor(new SetSpawnLocationCommand(this));
        Objects.requireNonNull(getCommand("summonboss")).setExecutor(new SummonBossCommand(this));

        // Register tab completers here
        Objects.requireNonNull(getCommand("toggledm")).setTabCompleter(new ToggleDebugMode(this));
        Objects.requireNonNull(getCommand("setspawn")).setTabCompleter(new SetSpawnLocationCommand(this));
    }

    /**
     * Registers the event listeners for the plugin.
     * This method binds the event listeners to the plugin.
     */
    private void registerListeners() {
        // Register listeners here
        // bow
        getServer().getPluginManager().registerEvents(new BowMeleeDamageListener(this), this);
        getServer().getPluginManager().registerEvents(new RightClickListener(this), this);
        getServer().getPluginManager().registerEvents(new OffhandListener(), this);

        // murasama
        getServer().getPluginManager().registerEvents(new HoldCrouchListener(this), this);
        getServer().getPluginManager().registerEvents(new LeftClickChargedListener(this), this);

        // custom death message
        getServer().getPluginManager().registerEvents(new CustomDeathMessageListener(), this);

        // boss
        getServer().getPluginManager().registerEvents(new BossDamageListener(), this);
        getServer().getPluginManager().registerEvents(new EndermanBlockPlaceListener(this), this);
        getServer().getPluginManager().registerEvents(new BossDeathListener(this), this);
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

                    player.getWorld().spawnParticle(Particle.RAID_OMEN, player.getLocation().add(0, 2.5, 0), 0);
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

        // Summon boss minions when at marker
        new BukkitRunnable() {

            @Override
            public void run() {
                if (new BossUtils().doesBossExist()) {
                    BossUtils bossUtils = new BossUtils();
                    LivingEntity boss = bossUtils.getBoss();
                    double health = boss.getHealth();
                    double maxHealth = Objects.requireNonNull(boss.getAttribute(Attribute.GENERIC_MAX_HEALTH)).getValue();
                    double healthPerSegment = maxHealth / 10.0;
                    int currentSegment = (int) Math.ceil(health / healthPerSegment);

                    World bossWorld = boss.getWorld();
                    Location bossLocation = boss.getLocation();
                    UUID bossUUID = boss.getUniqueId();

                    if (!bossHealthSegments.containsKey(bossUUID) || bossHealthSegments.get(bossUUID) != currentSegment) {
                        bossHealthSegments.put(bossUUID, currentSegment);
                        List<Minion> entities = new ArrayList<>();
                        Scoreboard scoreboard = getServer().getScoreboardManager().getMainScoreboard();
                        Team team = Optional.ofNullable(scoreboard.getTeam("RC9")).orElseGet(() -> scoreboard.registerNewTeam("RC9"));

                        if (currentSegment == 9) {
                            EntityType entityType = EntityType.ENDERMITE;
                            Integer amount = 5;
                            List<PotionEffectType> effects = Collections.singletonList(PotionEffectType.SPEED);
                            List<Integer> durations = Collections.singletonList(PotionEffect.INFINITE_DURATION);
                            List<Integer> amplifiers = Collections.singletonList(2);
                            Minion minion = new Minion(entityType, amount, effects, durations, amplifiers);

                            entities.add(minion);
                        } else if (currentSegment == 8) {
                            EntityType entityType1 = EntityType.ENDERMITE;
                            Integer amount1 = 5;
                            List<PotionEffectType> effects1 = Collections.singletonList(PotionEffectType.SPEED);
                            List<Integer> durations1 = Collections.singletonList(PotionEffect.INFINITE_DURATION);
                            List<Integer> amplifiers1 = Collections.singletonList(2);
                            Minion minion1 = new Minion(entityType1, amount1, effects1, durations1, amplifiers1);

                            EntityType entityType2 = EntityType.BLAZE;
                            Integer amount2 = 1;
                            List<PotionEffectType> effects2 = Collections.singletonList(PotionEffectType.HEALTH_BOOST);
                            List<Integer> durations2 = Collections.singletonList(PotionEffect.INFINITE_DURATION);
                            List<Integer> amplifiers2 = Collections.singletonList(1);
                            Minion minion2 = new Minion(entityType2, amount2, effects2, durations2, amplifiers2);

                            entities.add(minion1);
                            entities.add(minion2);
                        }

                        MinionSummoningHandler.summonMinions(entities, bossWorld, bossLocation, team);
                    }
                }
            }
        }.runTaskTimer(this, 0L, 10L);
    }
}