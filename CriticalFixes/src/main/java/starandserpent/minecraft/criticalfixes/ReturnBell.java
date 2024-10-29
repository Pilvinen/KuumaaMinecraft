package starandserpent.minecraft.criticalfixes;

import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.ChatMessageType;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BellRingEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import java.util.HashMap;
import java.util.HashSet;

public class ReturnBell implements Listener {

    private final JavaPlugin plugin;
    private Server server;

    // Bell counter for player.
    private static HashMap<Player, Integer> bellCounter = new HashMap<>();

    // Bell tasks for player.
    private static HashMap<Player, BukkitTask> bellTasks = new HashMap<>();

    // Prevent further ringing of the bell after 3 rings HashMap.
    private static HashSet<Player> preventBellAbuse = new HashSet<>();

    public ReturnBell(JavaPlugin plugin) {
        this.plugin = plugin;
        server = plugin.getServer();
    }

    @EventHandler public void onRingingBell(BellRingEvent event) {

        // Check if the world is TemplateWorld.
        var eventEntity = event.getEntity();
        if (eventEntity == null) {
            return;
        }
        var world = eventEntity.getWorld();
        var worldName = world.getName();

        if (worldName.equals("TemplateWorld")) {

            // Check that entity is a player.
            if (eventEntity instanceof Player player) {

                if (preventBellAbuse.contains(player)) {
                    return;
                }

                // Get the player's current bell counter.
                var playerBellCounter = bellCounter.get(player);

                if (playerBellCounter == null) {
                    playerBellCounter = 1;
                    bellCounter.put(player, playerBellCounter);

                    // Start bukkit task which runs after 5 seconds and removed the player's bell counter.
                    var bukkitTask = Bukkit.getScheduler().runTaskLater(plugin, () -> {
                        // Reset the player's bell counter if it exists.
                        bellCounter.remove(player);
                    }, 100L);

                    bellTasks.put(player, bukkitTask);
                }

                // If the player has rung the bell 3 times, return to the main world.
                if (bellCounter.get(player) >= 3) {

                    // Send action message to player
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("Omituinen tuntemus valtaa sinut."));

                    preventBellAbuse.add(player);

                    // BukkitTask to prevent further playing of the bell.
                    Bukkit.getScheduler().runTaskLater(plugin, () -> {
                        preventBellAbuse.remove(player);
                    }, 200L);

                    // Teleport player to Kuumaa after a delayed bukkit task
                    Bukkit.getScheduler().runTaskLater(plugin, () -> {
                        var targetWorld = Bukkit.getWorld("Kuumaa");
                        if (targetWorld == null) {
                            return;
                        }
                        var spawn = targetWorld.getSpawnLocation();
                        player.teleport(spawn);
                    }, 60L);


                    // Delete the player's bell counter.
                    bellCounter.remove(player);

                    // Cancel the task.
                    var task = bellTasks.get(player);
                    if (task != null) {
                        bellTasks.remove(player);
                        task.cancel();
                        bellCounter.remove(player);
                    }

                } else {
                    // Increment the player's bell counter.
                    bellCounter.put(player, bellCounter.get(player) + 1);
                }
            }

        }

    }
}
