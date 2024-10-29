package starandserpent.minecraft.criticalfixes;

import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class RegenerationFix implements Listener {

    private final JavaPlugin plugin;
    private Server server;
    private long regenerationTimeoutInSeconds = 60L;

    public RegenerationFix(JavaPlugin plugin) {
        this.plugin = plugin;
        this.server = plugin.getServer();
        registerRegenerationTimer();
    }

    // On player regeneration.
     @EventHandler public void onPlayerRegenerate(EntityRegainHealthEvent event) {
        // If entity is a player
        if (!(event.getEntity() instanceof org.bukkit.entity.Player)) {
            return;
        }

        var regainReason = event.getRegainReason();
        if (regainReason == EntityRegainHealthEvent.RegainReason.SATIATED) {
            event.setCancelled(true);
        }
     }

     public void registerRegenerationTimer() {
         server.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
             for (var player : server.getOnlinePlayers()) {
                 tryToRegenerateHealthFromBeingSatiated(player);
             }
         }, 0L, regenerationTimeoutInSeconds * 20L);
     }

    private void tryToRegenerateHealthFromBeingSatiated(Player player) {

        // If creative, skip
        if (player.getGameMode() == org.bukkit.GameMode.CREATIVE) {
            return;
        }

        // Healing time.
        var playerHealth = player.getHealth();
        if (playerHealth + 1 <= 20) {

            // Only if player is over hunger level of 15.
            var playerHunger = player.getFoodLevel();
            if (playerHunger < 15) {
                return;
            }

            player.setHealth(playerHealth + 1);
        }

    }

}
