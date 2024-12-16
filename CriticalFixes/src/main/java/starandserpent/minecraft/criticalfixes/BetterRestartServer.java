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

    @Override public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (command.getName().equalsIgnoreCase("ukk")) {
            if (!(sender instanceof ConsoleCommandSender) && !sender.isOp()) {
                sender.sendMessage("You do not have permission to use this command.");
                return true;
            }

            // Check the number of online players
            if (server.getOnlinePlayers().isEmpty()) {
                // No players online, restart immediately
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "restart");
            }
            else if (server.getOnlinePlayers().size() == 1 && server.getOnlinePlayers().iterator().next().getName().equals("Pilvinen")) {
                // Only "Pilvinen" is online, restart immediately
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "restart");
            } else {
                // Construct the message with the parameter
                String additionalMessage = args.length > 0 ? String.join(" ", args) : "";
                String[] message = {"Varoitus, ystävät! Palvelin käynnistyy uudelleen 23 sek kuluttua. " + additionalMessage};

                // Send the broadcast message using SystemNotifications
                SystemNotifications.publicBroadcast(plugin.getServer(), sender, message);

                // Schedule the server restart after 23 seconds (460 ticks)
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "restart");
                }, 460L);
            }

            return true;
        }
        return false;
    }

}
