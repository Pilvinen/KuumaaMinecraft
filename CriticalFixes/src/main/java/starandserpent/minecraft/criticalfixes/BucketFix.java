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

        // Only handle water buckets.
        if (event.getBucket() != Material.WATER_BUCKET) {
            return;
        }

        var blockToFill = event.getBlock();
        var blockToFillType = blockToFill.getType();

        // If the block is not air or cave air, cancel the event. The player is doing something like opening the door or what ever?
        if (blockToFillType != Material.AIR && blockToFillType != Material.CAVE_AIR) {
            return;
        }

        // If player is in the air, cancel the event. You're not doing stupid bucket jumps here.
        var player = event.getPlayer();
        var blockBelowPlayer = player.getLocation().getBlock().getRelative(0,-1,0).getType() == Material.AIR;
        if (blockBelowPlayer) {
            event.setCancelled(true);
        }

    }

}
