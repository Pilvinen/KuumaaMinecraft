package starandserpent.minecraft.criticalfixes;

import com.comphenix.protocol.ProtocolManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;

public class Hook implements Listener {

    private final JavaPlugin plugin;
    private Server server;
    private ProtocolManager protocolManager;

    // HashSet of player that are hooked
    private static final HashSet<Player> hookedPlayers = new HashSet<>();

    public Hook(JavaPlugin plugin) {
        this.plugin = plugin;
        this.server = plugin.getServer();
    }

    // On placing a hook block event (cyan stained glass pane).
    @EventHandler public void onBlockPlace(BlockPlaceEvent event) {
        var placedBlock = event.getBlock();
        if (placedBlock == null) {
            return;
        }

        var placedBlockType = placedBlock.getType();
        if (placedBlockType == Material.CYAN_STAINED_GLASS_PANE) {
            // Get world of the clicked block.
            var world = placedBlock.getWorld();
            var blockLocation = placedBlock.getLocation();

            // Delay the spawning by a few ticks.
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                Location spawnLocation = blockLocation.add(0.2, -2.35, 0.5);

                ArmorStand armorStand = (ArmorStand) world.spawnEntity(spawnLocation, EntityType.ARMOR_STAND);
                armorStand.setGravity(false);
                armorStand.setSilent(true);
                armorStand.setCustomName("HookStand");
                armorStand.setBasePlate(false);
                armorStand.setVisible(false);
                armorStand.setInvulnerable(true);
                armorStand.addEquipmentLock(EquipmentSlot.HEAD, ArmorStand.LockType.ADDING_OR_CHANGING);
                armorStand.addEquipmentLock(EquipmentSlot.FEET, ArmorStand.LockType.ADDING_OR_CHANGING);
                armorStand.addEquipmentLock(EquipmentSlot.CHEST, ArmorStand.LockType.ADDING_OR_CHANGING);
                armorStand.addEquipmentLock(EquipmentSlot.HAND, ArmorStand.LockType.ADDING_OR_CHANGING);
                armorStand.addEquipmentLock(EquipmentSlot.LEGS, ArmorStand.LockType.ADDING_OR_CHANGING);
                armorStand.addEquipmentLock(EquipmentSlot.OFF_HAND, ArmorStand.LockType.ADDING_OR_CHANGING);

                armorStand.teleport(spawnLocation);
            }, 5L); // Delay of 5 ticks

        }

    }

    @EventHandler public void onBlockBreak(BlockBreakEvent event) {
        var brokenBlock = event.getBlock();
        if (brokenBlock == null) {
            return;
        }

        var blockedBlockType = brokenBlock.getType();
        if (blockedBlockType == Material.CYAN_STAINED_GLASS_PANE) {
            // Get world of the clicked block.
            var world = brokenBlock.getWorld();
            var blockLocation = brokenBlock.getLocation();
            // There should be a pig at the location of the glass pane, let's remove it.
            var entities = world.getNearbyEntities(blockLocation, 1, 1, 1);
            for (var entity : entities) {
                if (entity instanceof ArmorStand) {
                    entity.remove();
                }
            }
        }
    }

    @EventHandler public void onPlayerInteract(PlayerInteractEvent event) {
        Action action = event.getAction();
        if (action == Action.RIGHT_CLICK_BLOCK) {
            var clickedBlock = event.getClickedBlock();
            if (clickedBlock == null) {
                return;
            }

            var clickedBlockType = clickedBlock.getType();
            if (clickedBlockType == Material.CYAN_STAINED_GLASS_PANE) {
                // Let's place the player on the hook block. There should be a pig at the location of the glass pane.
                var world = clickedBlock.getWorld();
                var blockLocation = clickedBlock.getLocation();
                var entities = world.getNearbyEntities(blockLocation, 1, 1, 1);
                for (var entity : entities) {
                    if (entity instanceof ArmorStand) {
                        var armorStand = (ArmorStand) entity;
                        var player = event.getPlayer();

//                        player.teleport(pig.getLocation());
                        // Make player ride the pig.
                        armorStand.addPassenger(player);

                        // Add player as hooked
                        hookedPlayers.add(player);

                        // Damage player by 1 heart every 1 second.
                        final boolean[] isPlayerDying = {false};
                        var task = new org.bukkit.scheduler.BukkitRunnable() {
                            @Override
                            public void run() {
                                // If player is going to die from the damage dealt, cancel the task and remove the player from the pig
                                // and enter back into survival mode.
                                if (player.getHealth() <= 2) {
                                    isPlayerDying[0] = true;
                                    armorStand.removePassenger(player);
                                    player.damage(2);
                                    cancel();
                                }
                                player.damage(2);
                            }
                        }.runTaskTimer(plugin, 20L, 20);

                        server.getScheduler().runTaskLater(plugin, () -> {
                            task.cancel();
                            armorStand.removePassenger(player);
                            // Remove from hooked on release
                            hookedPlayers.remove(player);
                        }, 200L);

                        if (isPlayerDying[0]) {
                            armorStand.removePassenger(player);
                        }

                        break;
                    }
                }
            }
        }
    }

    // Remove from hooked on logout or connection loss
    @EventHandler public void onPlayerQuit(PlayerQuitEvent event) {
        var player = event.getPlayer();
        if (hookedPlayers.contains(player)) {
            hookedPlayers.remove(player);
        }
    }

    // Remove from hooked on death
    @EventHandler public void onPlayerDeath(PlayerDeathEvent event) {
        var player = event.getEntity();
        if (hookedPlayers.contains(player)) {
            hookedPlayers.remove(player);
        }
    }

    // Public static method for checking if player is hooked.
    public static boolean isPlayerHooked(Player player) {
        return hookedPlayers.contains(player);
    }

}
