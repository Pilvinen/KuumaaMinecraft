package starandserpent.minecraft.criticalfixes;

import org.bukkit.Server;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class PermissionManager implements Listener {

    private final JavaPlugin plugin;
    private Server server;

    public PermissionManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.server = plugin.getServer();
    }


    @EventHandler void onPlayerLogin(PlayerJoinEvent event) {
        var player = event.getPlayer();
        var world = player.getWorld();
        var worldName = world.getName();

        // If we're not in the introduction, we can set permissions.
        if (worldName.equals("Tyhjyys")) {
            return;
        }

        // But not for player "Pilvinen" - and not for ops.
        var playerName = player.getName();
        if (playerName.equals("Pilvinen") || player.isOp()) {
            return;
        }

        // Give the permission after a short delay.
        server.getScheduler().runTaskLater(plugin, () -> {
            // Give the player the "pelaaja" permission so that Discord group will be set.
            server.dispatchCommand(player, "lp user " + playerName + " parent add pelaaja");
        }, 20L);

    }

}
