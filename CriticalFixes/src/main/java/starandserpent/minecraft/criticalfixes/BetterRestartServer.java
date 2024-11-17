package starandserpent.minecraft.criticalfixes;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class BetterRestartServer implements CommandExecutor {

    private final JavaPlugin plugin;
    private Server server;

    public BetterRestartServer(JavaPlugin plugin) {
        this.plugin = plugin;
        this.server = plugin.getServer();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (command.getName().equalsIgnoreCase("ukk")) {
            if (!(sender instanceof ConsoleCommandSender) && !sender.isOp()) {
                sender.sendMessage("You do not have permission to use this command.");
                return true;
            }

            // Send the broadcast message using SystemNotifications
            String[] message = {"Varoitus, ystävät! Palvelin käynnistyy uudelleen 10 sekunnin kuluttua."};
            SystemNotifications.publicBroadcast(plugin.getServer(), sender, message);

            // Schedule the server restart after 10 seconds (200 ticks)
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "restart");
            }, 200L);

            return true;
        }
        return false;    }

}
