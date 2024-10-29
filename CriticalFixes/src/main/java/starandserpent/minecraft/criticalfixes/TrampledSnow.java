package starandserpent.minecraft.criticalfixes;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.block.data.type.Snow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.UUID;

public class TrampledSnow implements Listener {

    private final JavaPlugin plugin;
    private Server server;

    // Keep track of last location all players were at.
    // This is to prevent snow from being trampled constantly into snow layers that are too thin.
    private final HashMap<UUID, Location> lastPlayerLocations = new HashMap<>();


    public TrampledSnow(JavaPlugin plugin) {
        this.plugin = plugin;
        server = plugin.getServer();
    }

    // On player trampling snow block it turns into lower snow layer but only up to half of max height.
    // This is to prevent snow from being trampled into snow layers that are too thin.
    @EventHandler public void onPlayerTrampleSnow(PlayerMoveEvent event) {

        Location locationFrom = event.getFrom();
        var worldFrom = locationFrom.getWorld();
        if (worldFrom == null) {
            return;
        }

        var currentRoundedLocation = new Location(worldFrom, Math.floor(locationFrom.getX()), Math.floor(locationFrom.getY()), Math.floor(locationFrom.getZ()));

        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();
        Location lastPlayerLocation = lastPlayerLocations.putIfAbsent(playerId, currentRoundedLocation);

        if (lastPlayerLocation != null && lastPlayerLocation.equals(currentRoundedLocation)) {
            return;
        }

        lastPlayerLocations.put(playerId, currentRoundedLocation);

        // If player is sneaking, exit early.
        if (player.isSneaking()) {
            return;
        }

        var block = locationFrom.getBlock();
        var blockType = block.getType();

        if (blockType != Material.SNOW) {
            return;
        }

        if (block.getBlockData() instanceof Snow snow) {
            var layers = snow.getLayers();
            var minLayers = 1;
            if (layers <= minLayers) {
                return;
            }

            snow.setLayers(layers - 1);
            block.setBlockData(snow);
        }

    }

    // On breaking snow block it turns into lower snow layer.
    @EventHandler public void onPlayerBreakSnow(BlockBreakEvent event) {

        var block = event.getBlock();
        var blockType = block.getType();

        // Set block type to snow at max layers.
        if (blockType == Material.SNOW_BLOCK) {
            event.setCancelled(true);
            block.setType(Material.SNOW);
            var blockData = (Snow) block.getBlockData();
            blockData.setLayers(8);
            block.setBlockData(blockData);
            return;
        }


        // Erode snow layers.
        if (blockType == Material.SNOW) {

            if (block.getBlockData() instanceof Snow snow) {
                var layers = snow.getLayers();
                var minLayers = 1;
                if (layers <= minLayers) {
                    return;
                }

                // Cancel event
                event.setCancelled(true);

                snow.setLayers(layers - 1);
                block.setBlockData(snow);
            }
        }

    }
}
