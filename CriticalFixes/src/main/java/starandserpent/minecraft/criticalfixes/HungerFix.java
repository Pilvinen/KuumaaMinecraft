package starandserpent.minecraft.criticalfixes;

import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class HungerFix implements Listener {

    private final JavaPlugin plugin;
    private Server server;
    private long hungerTimeoutInSeconds = 300L; // 600 = 10 minutes, 300 = 5 minutes.

    public HungerFix(JavaPlugin plugin) {
        this.plugin = plugin;
        this.server = plugin.getServer();
        registerHungerTimer();
    }

    private void registerHungerTimer() {
        server.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            for (var player : server.getOnlinePlayers()) {
                tryToMakePlayerHungry(player);
            }
        }, 0L, hungerTimeoutInSeconds * 20L);
    }

    private void tryToMakePlayerHungry(Player player) {

        // If creative, skip
        if (player.getGameMode() == org.bukkit.GameMode.CREATIVE) {
            return;
        }

        // Hunger time.
        var playerHunger = player.getFoodLevel();
        if (playerHunger > 0) {
            player.setFoodLevel(playerHunger - 1);
        }
    }

}
