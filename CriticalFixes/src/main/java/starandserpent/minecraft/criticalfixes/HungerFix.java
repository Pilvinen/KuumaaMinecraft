package starandserpent.minecraft.criticalfixes;

import org.bukkit.GameMode;
import org.bukkit.Server;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public class HungerFix implements Listener {

    private final JavaPlugin plugin;
    private Server server;
    private long hungerTimeoutInSeconds = 450L; // 600 = 10 minutes, 300 = 5 minutes, 450 = 7.5 minutes (2,5h for full hunger)

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

        // If not in survival, skip.
        var playerMode = player.getGameMode();
        if (playerMode != GameMode.SURVIVAL) {
            return;
        }

        // Hunger time.
        var playerHunger = player.getFoodLevel();
        var playerSaturation = player.getSaturation();

        // Reduce saturation first if it's greater than zero.
        if (playerSaturation > 0) {
            player.setSaturation(playerSaturation - 1);
        } else if (playerHunger > 0) {
            player.setFoodLevel(playerHunger - 1);
        }

    }

}
