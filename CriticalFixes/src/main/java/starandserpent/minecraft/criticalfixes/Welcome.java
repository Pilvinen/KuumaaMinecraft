package starandserpent.minecraft.criticalfixes;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Server;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class Welcome implements Listener {

    private final JavaPlugin plugin;
    private Server server;

    private final int zeroInitialDelay = 22;
    private final int firstNoLogoDuration = 13;
    private final int secondLogoDuration = 14;
    private final int thirdWeirdFaceDuration = 15;
    private final int fourthLogoDuration = 15;

    public Welcome(JavaPlugin plugin) {
        this.plugin = plugin;
        this.server = plugin.getServer();
    }

    @EventHandler public void onPlayerLogin(PlayerJoinEvent event) {
        var player = event.getPlayer();

        if (player.isOnline()) {
            showLogoSequence(player);
        }
    }

    private void showLogoSequence(Player player) {

        // Runs fifth.
        Runnable showLogo2 = () -> {
            sendMessage(player, ""); // Remove actionbar message.
        };

        // Runs fourth
        Runnable showLogo4 = () -> {
            sendMessage(player, Symbols.KUUMAA_LOGO2.literal);
            runDelayedTask(showLogo2, fourthLogoDuration);
        };

        // Runs third.
        Runnable showLogo3 = () -> {
            sendMessage(player, Symbols.KUUMAA_LOGO3.literal);
            player.playSound(player, "kuumaa:login.burp", 1.0f, 1.0f);
            runDelayedTask(showLogo2, thirdWeirdFaceDuration);
        };

        // Runs second.
        Runnable showNoLogo = () -> {
            sendMessage(player, Symbols.KUUMAA_LOGO2.literal);
            player.playSound(player, Sound.ITEM_SHIELD_BLOCK, 1.0f, 1.0f);
            runDelayedTask(showLogo3, secondLogoDuration);
        };

        // Runs first.
        Runnable showLogo1 = () -> {
            sendMessage(player, Symbols.KUUMAA_LOGO1.literal);
            runDelayedTask(showNoLogo, firstNoLogoDuration);
        };

        // Start the sequence by executing the first one after delay.
        runDelayedTask(showLogo1, zeroInitialDelay);
    }

    private void runDelayedTask(Runnable task, int delay) {
        new BukkitRunnable() {
            @Override public void run() {
                task.run();
            }
        }.runTaskLater(plugin, delay);
    }

    private void sendMessage(Player player, String message) {
        if (player.isOnline()) {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(message));
        }
    }
}