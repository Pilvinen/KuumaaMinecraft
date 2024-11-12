package starandserpent.minecraft.criticalfixes;

import com.nivixx.ndatabase.api.NDatabase;
import com.nivixx.ndatabase.api.repository.Repository;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class ServerStatistics implements Listener, CommandExecutor {

    private final JavaPlugin plugin;
    private Server server;

    private Repository<UUID, ServerStatisticsData> repository;
    private Repository<String, UniqueVisitor> repositoryOfUniqueVisitors;

    // Use a constant UUID for the server
    UUID serverId = UUID.fromString("7fdf1af4-4fab-4e5f-9d34-cc05b1028549");

    ServerStatisticsData serverStatisticsData;

    public ServerStatistics(JavaPlugin plugin) {
        this.plugin = plugin;
        this.server = plugin.getServer();

        repository = NDatabase.api().getOrCreateRepository(ServerStatisticsData.class);
        repositoryOfUniqueVisitors = NDatabase.api().getOrCreateRepository(UniqueVisitor.class);

        // Fetch the current ServerStatisticsData asynchronously
        ServerStatisticsData serverStatisticsData = repository.get(serverId);

        if (serverStatisticsData == null) {
            // If no data exists, create a new entry
            this.serverStatisticsData = new ServerStatisticsData(serverId);
        } else {
            this.serverStatisticsData = serverStatisticsData;
        }

        // Increment the restart count.
        this.serverStatisticsData.incrementRestartCount();

        // Update the ServerStatisticsData object in the repository
        repository.upsert(this.serverStatisticsData);

    }

    @Override public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        // /tilastoja
        // /statistics
        if (command.getName().equalsIgnoreCase("tilastoja")
        ||command.getName().equalsIgnoreCase("statistics")) {
            showServerStatistics(sender);
            return true;
        }

        return false;
    }

    private void showServerStatistics(CommandSender sender) {

        // Build message.
        String[] message = new String[7];
        message[0] = "             §e" + Symbols.MOON + "§f Kuumaan tilastotietoja §e" + Symbols.MOON + "§f";
        message[1] = " ";
        message[2] = "Palvelin on uudelleenkäynnistetty §b" + serverStatisticsData.getRestartCount() + "§f kertaa";
        message[3] = "alkaen §b" + serverStatisticsData.restartCountTrackingStart +" §f(§b" + daysPassedSinceTrackingStarted() + " §fvuorokautta sitten),";
        message[4] = "eli keskimäärin §b" + getAverageRestartsPerDay() + " §fkertaa päivässä, tai";
        message[5] = "arviolta §b" + getAverageTimeBetweenRestarts() + " §fminuutin välein.";
        message[6] = "Palvelimella on vieraillut §b" + getUniqueVisitorCount() + "§f pelaajaa.";

        // Send message
//        sender.spigot().sendMessage(message);
        SystemNotifications.privateBroadcast(server, sender, message);
    }

    // Use value from database.
    private String daysPassedSinceTrackingStarted() {
        var restartCountTrackingStartString = serverStatisticsData.getRestartCountTrackingStart();
        // The format is "DD.MM. YYYY"
        Date restartCountTrackingStart = null;
        try {
            restartCountTrackingStart = new SimpleDateFormat("dd.MM.yyyy").parse(restartCountTrackingStartString);
        } catch (ParseException ignored) {
        }

        if (restartCountTrackingStart == null) {
            return "0";
        }

        Date currentDate = new Date();
        long difference = currentDate.getTime() - restartCountTrackingStart.getTime();
        long days = difference / (1000 * 60 * 60 * 24);

        return String.valueOf(days);
    }


    // Count unique visitors in repositoryOfUniqueVisitors.
    private long getUniqueVisitorCount() {
        long count = repositoryOfUniqueVisitors.streamAllValues().count();
        return count;
    }

    private int getAverageRestartsPerDay() {
        var restartCountTrackingStartString = serverStatisticsData.getRestartCountTrackingStart();
        // The format is "DD.MM. YYYY"
        Date restartCountTrackingStart = null;
        try {
            restartCountTrackingStart = new SimpleDateFormat("dd.MM.yyyy").parse(restartCountTrackingStartString);
        } catch (ParseException ignored) {
        }

        if (restartCountTrackingStart == null) {
            return -1;
        }

        Date currentDate = new Date();
        long difference = currentDate.getTime() - restartCountTrackingStart.getTime();
        long days = difference / (1000 * 60 * 60 * 24);

        if (days == 0) {
            return -1;
        }

        return serverStatisticsData.getRestartCount() / (int) days;
    }

    private int getAverageTimeBetweenRestarts() {
        var averageRestartsPerDay = getAverageRestartsPerDay();
        int minutesInDay = 24 * 60;
        int timeBetweenRestarts = minutesInDay / averageRestartsPerDay;
        return timeBetweenRestarts;
    }

    @EventHandler public void onPlayerJoin(PlayerJoinEvent event) {
        var player = event.getPlayer();
        var playerUUID = player.getUniqueId().toString();

        // Check if the player has visited before.
        var uniqueVisitor = repositoryOfUniqueVisitors.get(playerUUID);

        if (uniqueVisitor == null) {
            // If the player is new, create a new entry.
            uniqueVisitor = new UniqueVisitor(playerUUID, playerUUID);
            repositoryOfUniqueVisitors.upsert(uniqueVisitor);
        }

    }

}
