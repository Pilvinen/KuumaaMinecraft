package starandserpent.minecraft.criticalfixes;

import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

// Prevent doors and trapdoors and gates from being opened and closed by players in quick succession.
// This is to prevent intentional and annoying noise generation.

public class RenkutusFix implements Listener {

    private final JavaPlugin plugin;
    private Server server;

    // List of Materials to check for.
private final List<Material> materialsToPreventRenkutusOn = List.of(
        Material.ACACIA_DOOR,
        Material.BIRCH_DOOR,
        Material.DARK_OAK_DOOR,
        Material.IRON_DOOR,
        Material.JUNGLE_DOOR,
        Material.OAK_DOOR,
        Material.SPRUCE_DOOR,
        Material.CRIMSON_DOOR,
        Material.WARPED_DOOR,
        Material.ACACIA_TRAPDOOR,
        Material.BIRCH_TRAPDOOR,
        Material.DARK_OAK_TRAPDOOR,
        Material.IRON_TRAPDOOR,
        Material.JUNGLE_TRAPDOOR,
        Material.OAK_TRAPDOOR,
        Material.SPRUCE_TRAPDOOR,
        Material.CRIMSON_TRAPDOOR,
        Material.WARPED_TRAPDOOR,
        Material.ACACIA_FENCE_GATE,
        Material.BIRCH_FENCE_GATE,
        Material.DARK_OAK_FENCE_GATE,
        Material.JUNGLE_FENCE_GATE,
        Material.OAK_FENCE_GATE,
        Material.SPRUCE_FENCE_GATE,
        Material.CRIMSON_FENCE_GATE,
        Material.WARPED_FENCE_GATE
    );



    public RenkutusFix(JavaPlugin plugin) {
        this.plugin = plugin;

        // Get server.
        server = plugin.getServer();
    }


    // Fix for opening doors.
    @EventHandler public void onPlayerInteract(PlayerInteractEvent event) {
        var player = event.getPlayer();
        var block = event.getClickedBlock();
        if (block == null) return;
        var blockType = block.getType();
        if (materialsToPreventRenkutusOn.contains(blockType)) {
            if (player.hasPermission("renkutusfix.bypass")) return;
            if (player.hasMetadata("renkutusfix")) {
                event.setCancelled(true);
            } else {
                player.setMetadata("renkutusfix", new FixedMetadataValue(plugin, true));
                new BukkitRunnable() {
                    @Override public void run() {
                        player.removeMetadata("renkutusfix", plugin);
                    }
                }.runTaskLater(plugin, 10);
            }
        }
    }

}
