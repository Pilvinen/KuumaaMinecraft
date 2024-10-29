package starandserpent.minecraft.criticalfixes;

import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.block.data.BlockData;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class BucketFix implements Listener {

    private final JavaPlugin plugin;
    private Server server;


    public BucketFix(JavaPlugin plugin) {
        this.plugin = plugin;
        this.server = plugin.getServer();
    }

    // When pouring with water bucket
    @EventHandler public void onPlayerBucket(PlayerBucketEmptyEvent event) {

        var blockToFill = event.getBlock();
        var blockToFillType = blockToFill.getType();

        if (blockToFillType != Material.AIR && blockToFillType != Material.CAVE_AIR) {
            return;
        }

        // If player is in the air, cancel the event. You're not doing stupid bucket jumps here.
        var player = event.getPlayer();
        var blockBelowPlayer = player.getLocation().getBlock().getRelative(0,-1,0).getType() == Material.AIR;
        if (blockBelowPlayer) {
            event.setCancelled(true);
            return;
        }

        // Set target block to water
        blockToFill.setType(Material.WATER);

        // Get the BlockData for the block
        BlockData blockData = blockToFill.getBlockData();

        // Check if the BlockData is an instance of Levelled (which it should be for water)
        if (blockData instanceof org.bukkit.block.data.Levelled levelled) {

            // Cancel event
            event.setCancelled(true);

            // Bukkit delay
            server.getScheduler().runTaskLater(plugin, () -> {
                // Set the level to 8 so that the water is "falling"
                levelled.setLevel(8);

                // Set the block's BlockData to the updated Levelled instance
                blockToFill.setBlockData(levelled);
            }, 8);

        }
    }

}
