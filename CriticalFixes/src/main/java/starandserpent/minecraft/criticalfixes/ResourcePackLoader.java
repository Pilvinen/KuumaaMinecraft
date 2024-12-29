package starandserpent.minecraft.criticalfixes;

import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class ResourcePackLoader implements CommandExecutor {

    private JavaPlugin plugin;
    private final Server server;

    public ResourcePackLoader(JavaPlugin plugin) {
        this.plugin = plugin;
        this.server = plugin.getServer();
    }

    @Override public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        // Prevent use from console.
        if ((sender instanceof ConsoleCommandSender)) {
            sender.sendMessage("This command cannot be used from console.");
            return true;
        }

        // Is op
        if (!(sender.isOp())) {
            sender.sendMessage("You do not have permission to use this command.");
            return true;
        }

        // The base command must be either /resourcepack or /respack or /resurssipaketti
        if (!command.getName().equalsIgnoreCase("resourcepack")
                && !command.getName().equalsIgnoreCase("respack")
                && !command.getName().equalsIgnoreCase("resurssipaketti")) {
            return false;
        }

        // If there are no arguments run for current player.
        if (args.length == 0) {
            Player player = (Player) sender;
            reloadResourcePack(player);
            return true;
        }

        // If there's an argument, and it is "all" or "everyone" or "kaikki" or "kaikille" or "pakota" or "force"
        // reload the resource pack for all players.
        if (args[0].equalsIgnoreCase("all")
                || args[0].equalsIgnoreCase("everyone")
                || args[0].equalsIgnoreCase("kaikki")
                || args[0].equalsIgnoreCase("kaikille")
                || args[0].equalsIgnoreCase("pakota")
                || args[0].equalsIgnoreCase("force")) {
            reloadResourcePackForAllPlayers(sender);
            return true;
        }

        return false;
    }

    private void reloadResourcePack(Player player) {
        player.setResourcePack("https://www.starandserpent.com/minecraft/kuumaa/Kuumaa_Resource_Pack.zip");
    }

    private void reloadResourcePackForAllPlayers(CommandSender sender) {

        // Delay and warn players.
        String[] message = { "Varoitus, ystävät! Varmista välittömästi,",
                "että olet turvallisessa ympäristössä!",
                "Resurssipaketin uusiksi lataaminen pakotetaan 23 sek kuluttua.",
                "Lataamisen aikana ei voi liikkua tai tehdä mitään.",
                "Ohjeiden noudattamatta jättäminen saattaa johtaa kuolemaan!"};
        SystemNotifications.publicBroadcast(server, sender, message);

        // Schedule the server restart after 23 seconds (460 ticks)
        server.getScheduler().runTaskLater(plugin, () -> {
            for (var player : server.getOnlinePlayers()) {
                reloadResourcePack(player);
            }
        }, 460L);

    }

}
