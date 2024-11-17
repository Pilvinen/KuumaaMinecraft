package starandserpent.minecraft.criticalfixes;

import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.ChatMessageType;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.type.Bell;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BellRingEvent;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;

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
        this.server = plugin.getServer();
//        System.out.println("ReturnBell initialized.");
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
//        System.out.println("onBlockPlace called.");

        // Get the item placed by the player.
        ItemStack itemInHand = event.getItemInHand();
//        System.out.println("Item in hand: " + itemInHand);

        // Check if the item is a bell.
        if (itemInHand.getType() == Material.BELL) {
            var name = Objects.requireNonNull(itemInHand.getItemMeta()).getDisplayName();
            System.out.println("Bell name: " + name);
            if (!name.equals("Outokello")) {
                return;
            }
        } else {
            return;
        }

        var block = event.getBlockPlaced();
        var locationXYZ = block.getLocation().toString().replaceAll("[^a-z0-9/._-]", "_");
//        System.out.println("Block placed at: " + locationXYZ);
        PersistentDataContainer dataContainer = block.getChunk().getPersistentDataContainer();
        dataContainer.set(new NamespacedKey(plugin, "Outokello" + locationXYZ), PersistentDataType.STRING, "Outokello");

        // Set the custom name in the block's PersistentDataContainer
        BlockState state = block.getState();
        PersistentDataContainer blockDataContainer = state.getChunk().getPersistentDataContainer();
        blockDataContainer.set(new NamespacedKey(plugin, "Outokello"), PersistentDataType.STRING, "Outokello");
        state.update();
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
//        System.out.println("onBlockBreak called.");
        Block block = event.getBlock();
        if (block.getState() instanceof Bell) {
            BlockState state = block.getState();
            PersistentDataContainer dataContainer = state.getChunk().getPersistentDataContainer();
            // Check block break location for key.
            var locationXYZ = block.getLocation().toString();
//            System.out.println("Block broken at: " + locationXYZ);
            dataContainer.remove(new NamespacedKey(plugin, "Outokello" + locationXYZ));
        }
    }

    @EventHandler
    public void onRingingBell(BellRingEvent event) {
//        System.out.println("onRingingBell called.");

        // Check if the world is TemplateWorld.
        var eventEntity = event.getEntity();
        if (eventEntity == null) {
//            System.out.println("Event entity is null.");
            return;
        }
        var world = eventEntity.getWorld();
        var worldName = world.getName();
//        System.out.println("World name: " + worldName);

        if (eventEntity instanceof Player player) {
            Block block = event.getBlock();
            // Get data from persistent data container.
            BlockState state = block.getState();
            PersistentDataContainer dataContainer = state.getChunk().getPersistentDataContainer();
            String locationKey = block.getLocation().toString().replaceAll("[^a-z0-9/._-]", "_");
            String customName = dataContainer.get(new NamespacedKey(plugin, "Outokello" + locationKey), PersistentDataType.STRING);
//            System.out.println("Custom name: " + customName);

            if ("Outokello".equals(customName)) {
                handleBellRing(player, worldName.equals("TemplateWorld"));
            }
        }
    }

    private void handleBellRing(Player player, boolean isTemplateWorld) {
//        System.out.println("handleBellRing called for player: " + player.getName());
        if (preventBellAbuse.contains(player)) {
//            System.out.println("Player is in preventBellAbuse set.");
            return;
        }

        // Check if the player has any items in their inventory or armor slots
        boolean hasItems = false;
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null) {
                hasItems = true;
                break;
            }
        }
        if (!hasItems) {
            for (ItemStack item : player.getInventory().getArmorContents()) {
                if (item != null) {
                    hasItems = true;
                    break;
                }
            }
        }

        // Get the player's current bell counter.
        var playerBellCounter = bellCounter.get(player);
//        System.out.println("Player bell counter: " + playerBellCounter);

        if (playerBellCounter == null) {
            playerBellCounter = 1;
            bellCounter.put(player, playerBellCounter);
//            System.out.println("Player bell counter initialized to 1.");

            // Start bukkit task which runs after 5 seconds and removed the player's bell counter.
            var bukkitTask = Bukkit.getScheduler().runTaskLater(plugin, () -> {
                // Reset the player's bell counter if it exists.
                bellCounter.remove(player);
//                System.out.println("Player bell counter reset.");
            }, 100L);

            bellTasks.put(player, bukkitTask);

            // Send warning message if the player has items
            if (hasItems) {
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("Kun kello soi, kaikki tavarasi muuttuvat oudon läpikuultaviksi ja sinut valtaa uhkaava tunne."));
            }
        }

        // If the player has rung the bell 3 times, handle the world transition.
        if (bellCounter.get(player) >= 3) {
//            System.out.println("Player has rung the bell 3 times.");

            // Send action message to player
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("Omituinen tuntemus valtaa sinut."));

            preventBellAbuse.add(player);

            // BukkitTask to prevent further playing of the bell.
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                preventBellAbuse.remove(player);
//                System.out.println("Player removed from preventBellAbuse set.");
            }, 200L);

            // Teleport player to the target world after a delayed bukkit task
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                var targetWorld = Bukkit.getWorld(isTemplateWorld ? "Kuumaa" : "TemplateWorld");
                if (targetWorld == null) {
//                    System.out.println("Target world not found.");
                    return;
                }
                var spawn = targetWorld.getSpawnLocation();
                player.teleport(spawn);
                var playerLocation = player.getLocation();
                // Lightning strike effect without damage.
                targetWorld.strikeLightningEffect(playerLocation);
//                System.out.println("Player teleported to " + targetWorld.getName());
                // Clear the player's inventory.
                player.getInventory().clear();
                // Set player's game mode.
                var currentWorld = player.getWorld();
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    if (currentWorld.getName().equals("TemplateWorld")) {
                        player.setGameMode(GameMode.CREATIVE);
                    } else {
                        player.setGameMode(GameMode.SURVIVAL);
                    }

                    // Lightning strike effect without damage.
                    var currentPlayerLocation = player.getLocation();
                    currentWorld.strikeLightningEffect(currentPlayerLocation);

                }, 5L); // 5 ticks = 0.25 second delay
            }, 60L);

            // Delete the player's bell counter.
            bellCounter.remove(player);

            // Cancel the task.
            var task = bellTasks.get(player);
            if (task != null) {
                bellTasks.remove(player);
                task.cancel();
                bellCounter.remove(player);
//                System.out.println("Player bell task cancelled and counter removed.");
            }

        } else {
            // Increment the player's bell counter.
            bellCounter.put(player, bellCounter.get(player) + 1);
//            System.out.println("Player bell counter incremented to: " + bellCounter.get(player));
        }
    }

}