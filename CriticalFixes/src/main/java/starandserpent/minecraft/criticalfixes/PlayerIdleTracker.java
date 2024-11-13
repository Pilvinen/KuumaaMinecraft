package starandserpent.minecraft.criticalfixes;

import com.nivixx.ndatabase.api.NDatabase;
import com.nivixx.ndatabase.api.repository.Repository;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class PlayerIdleTracker implements Listener {
    private final JavaPlugin plugin;
    private final Server server;
    private static final Map<UUID, Long> lastActivityTime = new HashMap<>();
    private static final Map<UUID, Long> totalIdleTime = new HashMap<>();
    private static final Map<UUID, Long> currentIdleStartTime = new HashMap<>();
    private static final long idleThreshold = 3 * 60 * 1000; // 3 minutes in milliseconds
    private final List<PlayerLogoutListener> logoutListeners = new ArrayList<>();

    // Database for storing player idle data.
    Repository<String, PlayerIdleData> repository;

    public PlayerIdleTracker(JavaPlugin plugin) {
        this.plugin = plugin;
        this.server = plugin.getServer();

        // Get the NDatabase repository.
        repository = NDatabase.api().getOrCreateRepository(PlayerIdleData.class);

        startIdleCheckTask();
    }

    public void addLogoutListener(PlayerLogoutListener listener) {
        logoutListeners.add(listener);
    }

    public void removeLogoutListener(PlayerLogoutListener listener) {
        logoutListeners.remove(listener);
    }

    @EventHandler public void onPlayerMove(PlayerMoveEvent event) {
        updateLastActivityTime(event.getPlayer().getUniqueId());
    }

    @EventHandler public void onPlayerInteract(PlayerInteractEvent event) {
        updateLastActivityTime(event.getPlayer().getUniqueId());
    }

    @EventHandler public void onPlayerChat(AsyncPlayerChatEvent event) {
        updateLastActivityTime(event.getPlayer().getUniqueId());
    }

    @EventHandler public void onPlayerJoin(PlayerJoinEvent event) {
        updateLastActivityTime(event.getPlayer().getUniqueId());
    }

    @EventHandler public void onPlayerQuit(PlayerQuitEvent event) {
        UUID playerId = event.getPlayer().getUniqueId();

        long totalIdle = totalIdleTime.getOrDefault(playerId, 0L);

        // Update PlayerIdleData.
        PlayerIdleData playerIdleData = repository.get(playerId.toString());
        if (playerIdleData == null) {
            playerIdleData = new PlayerIdleData(playerId.toString());
        }
        playerIdleData.playtimeMinutes += totalIdle / 1000 / 60;
        repository.upsert(playerIdleData);

        firePlayerLogoutEvent(new PlayerLogoutEvent(event.getPlayer(), totalIdle));

        lastActivityTime.remove(playerId);
        totalIdleTime.remove(playerId);
        currentIdleStartTime.remove(playerId);
    }

    private void updateLastActivityTime(UUID playerUuid) {

        long currentTime = System.currentTimeMillis();

        // You made an action, idle ends.
        if (isPlayerIdle(playerUuid)) {
            // Calculate how long the idle time lasted.
            long idleStartTime = currentIdleStartTime.getOrDefault(playerUuid, currentTime);
            long idleDuration = currentTime - idleStartTime;

            // Update total idle time for this whole game session.
            totalIdleTime.put(playerUuid, totalIdleTime.getOrDefault(playerUuid, 0L) + idleDuration);

            // Reset the current idle start time.
            currentIdleStartTime.remove(playerUuid);
        }

        // Update the last activity time. We're doing stuff, baby!
        lastActivityTime.put(playerUuid, currentTime);
    }

    private void startIdleCheckTask() {

        new BukkitRunnable() {
            @Override
            public void run() {

                for (Player player : server.getOnlinePlayers()) {
                    UUID playerId = player.getUniqueId();

                    // Idle threshold has NOT been exceeded. Continue and check the next player.
                    if (!isPlayerIdle(playerId)) {
                        continue;
                    }

                    // Set the idle START time only if it hasn't been already set.
                    currentIdleStartTime.putIfAbsent(playerId, System.currentTimeMillis());

                    // Increment the total idle time for this session.
                    var currentTotalIdleTime = totalIdleTime.getOrDefault(playerId, 0L);
                    var minuteAsMillis = 1000 * 60;
                    totalIdleTime.put(playerId, currentTotalIdleTime + minuteAsMillis);
                }
            }
        }.runTaskTimer(plugin, 0L, 20L * 60); // Run every minute. 20 ticks is 1 second.
    }

    private void handleIdlePlayer(Player player) {
        // Handle idle player (e.g., send a message, kick, etc.)
        // DEBUG: Print debug message.
        System.out.println("Handling idle player " + player.getName());

        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("Olet nyt idle."));
    }

    public static boolean isPlayerIdle(UUID playerUuid) {
        long currentTime = System.currentTimeMillis();
        long lastActivity = lastActivityTime.getOrDefault(playerUuid, 0L);
        boolean isIdle = currentTime - lastActivity > idleThreshold;
        // Print debug information.
//        System.out.println("Player " + playerUuid + " is idle: " + isIdle + " for " + (currentTime - lastActivity) / 1000 + " seconds. currentTime: " + currentTime + " lastActivity: " + lastActivity);
        return isIdle;
    }

    public static long getTotalIdleTimeForAllTimeInMinutes(UUID playerUuid) {
        Repository<String, PlayerIdleData> repository = NDatabase.api().getOrCreateRepository(PlayerIdleData.class);
        PlayerIdleData playerIdleData = repository.get(playerUuid.toString());

        if (playerIdleData == null) {
            playerIdleData = new PlayerIdleData(playerUuid.toString());
        }

        var totalIdleMinutesForCurrentPlaySession = getTotalIdleTimeForPlaySession(playerUuid) / 1000 / 60;
        var totalIdleMinutesFromDatabase = playerIdleData.playtimeMinutes;
        var totalIdleMinutesForAllTime = totalIdleMinutesFromDatabase + totalIdleMinutesForCurrentPlaySession;

        return totalIdleMinutesForAllTime;
    }

    public static long getTotalIdleTimeForPlaySession(UUID playerUuid) {
        return totalIdleTime.getOrDefault(playerUuid, 0L);
    }

    public static long getCurrentIdleSessionLengthInMinutes(UUID playerUuid) {
        if (isPlayerIdle(playerUuid)) {
            long currentTime = System.currentTimeMillis();
            long idleStartTime = currentIdleStartTime.getOrDefault(playerUuid, currentTime);
            long idleDuration = (currentTime - idleStartTime) / 1000 / 60;

//            System.out.println("Idle session length for player " + playerUuid + ": " + idleDuration + " minutes.");
            return idleDuration;
        }
        return 0L;
    }

    private void firePlayerLogoutEvent(PlayerLogoutEvent event) {
        for (PlayerLogoutListener listener : logoutListeners) {
            listener.onPlayerLogout(event);
        }
    }

}


