package starandserpent.minecraft.criticalfixes;

import org.bukkit.Server;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class RespawnFix implements Listener {

    private final JavaPlugin plugin;
    private Server server;

    public RespawnFix(JavaPlugin plugin) {
        this.plugin = plugin;

        // Get server.
        server = plugin.getServer();
    }

    // On respawn, set life to half hearth.
    @EventHandler public void onRespawn(PlayerRespawnEvent playerRespawnEvent) {
        var player = playerRespawnEvent.getPlayer();

        new BukkitRunnable() {
            @Override
            public void run() {
                player.setHealth(1);
                player.setFoodLevel(3);
            }
        }.runTaskLater(plugin, 1L);
    }

}
