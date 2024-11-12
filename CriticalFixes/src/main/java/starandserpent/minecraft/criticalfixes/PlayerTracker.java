package starandserpent.minecraft.criticalfixes;

import com.nivixx.ndatabase.api.NDatabase;
import com.nivixx.ndatabase.api.repository.Repository;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.*;

public class PlayerTracker implements Listener, CommandExecutor {
    private final JavaPlugin plugin;
    private Server server;
    Repository<String, PlayerTrackerData> repository;

    // List of login Date time stamps by UUID.
    private final Map<String, Date> playerLoginTimes = new HashMap<>();


    public PlayerTracker(JavaPlugin plugin) {
        this.plugin = plugin;
        server = plugin.getServer();

        // Get the NDatabase repository.
        repository = NDatabase.api().getOrCreateRepository(PlayerTrackerData.class);
    }

    // Join event handler.
    @EventHandler public void onPlayerJoin(org.bukkit.event.player.PlayerJoinEvent event) {
        // Get the player UUID.
        String playerUUID = event.getPlayer().getUniqueId().toString();

        // Add the player login time.
        playerLoginTimes.put(playerUUID, new Date());

        // Get the player tracker data.
        PlayerTrackerData playerTrackerData = repository.get(playerUUID);

        // If the player tracker data does not exist, create it.
        if (playerTrackerData == null) {
            playerTrackerData = new PlayerTrackerData(playerUUID);
        }

        // Set the last known player name.
        playerTrackerData.lastKnownName = event.getPlayer().getName();

        // Set last seen to current time.
        playerTrackerData.lastSeen = new Date();

        // Increment the player visits.
        playerTrackerData.visits++;

        // Save the player tracker data.
        repository.upsert(playerTrackerData);
    }

    // Quit event handler.
    @EventHandler public void onPlayerQuit(org.bukkit.event.player.PlayerQuitEvent event) {
        // Get the player UUID.
        String playerUUID = event.getPlayer().getUniqueId().toString();

        // Get the player tracker data.
        PlayerTrackerData playerTrackerData = repository.get(playerUUID);

        // If the player tracker data does not exist, create it. Weird edge case.
        if (playerTrackerData == null) {
            playerTrackerData = new PlayerTrackerData(playerUUID);
            repository.upsert(playerTrackerData);
            return; // Something weird happened. This entry should been created already, let's bail out.
        }

        // Set last seen to current time.
        playerTrackerData.lastSeen = new Date();

        // Calculate the playtime in minutes for current session.
        Date loginTime = playerLoginTimes.get(playerUUID);
        Date logoutTime = new Date();
        long playtime = (logoutTime.getTime() - loginTime.getTime()) / 1000 / 60; // Convert milliseconds to minutes.
        // Increment the playtime.
        playerTrackerData.playtimeMinutes += playtime;

        // Save the player tracker data.
        repository.upsert(playerTrackerData);

        // Remove the player login time since they logged out.
        playerLoginTimes.remove(playerUUID);
    }

    @Override public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        // /pelaajat
        // /players
        if (command.getName().equalsIgnoreCase("pelaajat")
                ||command.getName().equalsIgnoreCase("players")
                ||command.getName().equalsIgnoreCase("top10")) {
            showPlayerStatistics(sender);
            return true;
        }

        // /pelaaja <pelaajanimi>
        // /player <playername>
        // /seen <playername>
        if (command.getName().equalsIgnoreCase("pelaaja")
                || command.getName().equalsIgnoreCase("player")
                || command.getName().equalsIgnoreCase("seen")
                || command.getName().equalsIgnoreCase("missä")) {
            if (args.length < 1) {
                sender.sendMessage("Anna pelaajan nimi.");
                return false;
            } else if (args.length > 1) {
                sender.sendMessage("Liikaa argumentteja.");
                return false;
            }
            showSinglePlayerStatistics(sender, args[0]);
            return true;
        }

        return false;
    }

    private void showPlayerStatistics(@NotNull CommandSender sender) {
        // Create top 10 list of players by playtime.
        PlayerTrackerData[] topPlayers = getTopPlayers(10);

        // Prepare the message lines.
        List<String> messageLines = new ArrayList<>();

        // Send the top players to the sender.
        messageLines.add("Top " + topPlayers.length + " kovimmat pelaajat peliajan mukaan:");

        int position = 1;
        for (PlayerTrackerData player : topPlayers) {
            // Get player name from UUID.
            String playerName = player.lastKnownName;
            if (playerName == null || playerName.isEmpty()) {
                playerName = "Tuntematon";
            }
            messageLines.add(position + ". " + playerName + ", " + player.playtimeMinutes + " min, " + player.visits + " vierailua.");
            position++;
        }

        // Convert the list to an array and call privateBroadcast.
        String[] messageArray = messageLines.toArray(new String[0]);
        SystemNotifications.privateBroadcast(server, sender, messageArray);
    }

    private PlayerTrackerData[] getTopPlayers(int howMany) {
        // Create a map to store the combined playtime data.
        Map<String, PlayerTrackerData> combinedData = new HashMap<>();

        // Iterate through the currently online players.
        for (var player : server.getOnlinePlayers()) {
            String playerUUID = player.getUniqueId().toString();
            PlayerTrackerData playerData = repository.get(playerUUID);

            if (playerData == null) {
                playerData = new PlayerTrackerData(playerUUID);
            }

            // Calculate the playtime for the current session.
            Date loginTime = playerLoginTimes.get(playerUUID);
            long currentSessionPlaytime = (new Date().getTime() - loginTime.getTime()) / 1000 / 60; // Convert milliseconds to minutes.

            // Add the current session playtime to the total playtime.
            playerData.playtimeMinutes += currentSessionPlaytime;

            // Add to the combined data map.
            combinedData.put(playerUUID, playerData);
        }

        // Add the playtime data from the database for players who are not online.
        repository.streamAllValues().forEach(playerData -> {
            if (!combinedData.containsKey(playerData.getKey())) {
                combinedData.put(playerData.getKey(), playerData);
            }
        });

        // Sort the combined data by playtime and fetch the top players.
        return combinedData.values().stream()
                .sorted((a, b) -> Long.compare(b.playtimeMinutes, a.playtimeMinutes))
                .limit(howMany)
                .toArray(PlayerTrackerData[]::new);
    }


    private void showSinglePlayerStatistics(@NotNull CommandSender sender, @NotNull String arg) {
        // Get the player UUID.
        UUID playerUUIDGuid = server.getOfflinePlayer(arg).getUniqueId();
        String playerUUID = playerUUIDGuid.toString();

        // Get the player tracker data.
        PlayerTrackerData playerTrackerData = repository.get(playerUUID);

        // If the player tracker data does not exist, create it.
        if (playerTrackerData == null) {
            playerTrackerData = new PlayerTrackerData(playerUUID);
        }

        // Get the player name.
        String playerName = playerTrackerData.lastKnownName;
        if (playerName == null || playerName.isEmpty()) {
            sender.sendMessage("Pelaajaa ei löytynyt.");
            return;
        }

        // Calculate the playtime for the current session if the player is online.
        if (server.getPlayer(playerUUID) != null) {
            Date loginTime = playerLoginTimes.get(playerUUID);
            long currentSessionPlaytime = (new Date().getTime() - loginTime.getTime()) / 1000 / 60; // Convert milliseconds to minutes.
            playerTrackerData.playtimeMinutes += currentSessionPlaytime;
        }

        // Create a map to store the combined playtime data.
        Map<String, PlayerTrackerData> combinedData = new HashMap<>();

        // Iterate through the currently online players.
        for (var player : server.getOnlinePlayers()) {
            String onlinePlayerUUID = player.getUniqueId().toString();
            PlayerTrackerData onlinePlayerData = repository.get(onlinePlayerUUID);

            if (onlinePlayerData == null) {
                onlinePlayerData = new PlayerTrackerData(onlinePlayerUUID);
            }

            // Calculate the playtime for the current session.
            Date loginTime = playerLoginTimes.get(onlinePlayerUUID);
            long currentSessionPlaytime = (new Date().getTime() - loginTime.getTime()) / 1000 / 60; // Convert milliseconds to minutes.

            // Add the current session playtime to the total playtime.
            onlinePlayerData.playtimeMinutes += currentSessionPlaytime;

            // Add to the combined data map.
            combinedData.put(onlinePlayerUUID, onlinePlayerData);
        }

        // Add the playtime data from the database for players who are not online.
        repository.streamAllValues().forEach(playerData -> {
            if (!combinedData.containsKey(playerData.getKey())) {
                combinedData.put(playerData.getKey(), playerData);
            }
        });

        // Calculate the player's rank based on the combined playtime data.
        long playerPlaytime = playerTrackerData.playtimeMinutes;
        long rank = combinedData.values().stream()
                .filter(data -> data.playtimeMinutes > playerPlaytime)
                .count() + 1;

        // Prepare the message lines.
        List<String> messageLines = new ArrayList<>();

        // Check if player is online now.
        Player player = Bukkit.getPlayer(playerUUIDGuid);
        boolean isOnlineNow = false;
        if (player != null) {
            isOnlineNow = player.isOnline();
        }

        if (isOnlineNow) {
            messageLines.add(playerName + " on parhaillaan palvelimella.");
            // Otherwise show the last seen time.
        } else if (playerTrackerData.lastSeen == null) {
            messageLines.add(playerName + " ei ole koskaan käynyt palvelimella.");
            // Convert the list to an array and call privateBroadcast.
            String[] messageArray = messageLines.toArray(new String[0]);
            SystemNotifications.privateBroadcast(server, sender, messageArray);

            return;
        } else {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
            String formattedDate = dateFormat.format(playerTrackerData.lastSeen);
            messageLines.add(playerName + " on nähty " + formattedDate + ".");

            // Output how long ago it was in minutes.
            long difference = new Date().getTime() - playerTrackerData.lastSeen.getTime();
            long minutes = difference / (1000 * 60);
            messageLines.add("Siitä on " + minutes + " min, kun " + playerName + " nähtiin.");
        }

        // Send the player statistics to the sender.
        messageLines.add(playerName + " on sijalla " + rank + ". " + playerTrackerData.playtimeMinutes + " min, " + playerTrackerData.visits + " vierailua.");
        // Print average playtime per visits.
        if (playerTrackerData.visits > 0) {
            long averagePlaytimePerVisit = playerTrackerData.playtimeMinutes / playerTrackerData.visits;
            messageLines.add(playerName + " pelaa ~" + averagePlaytimePerVisit + " min/vierailu.");
        }

        // Convert the list to an array and call privateBroadcast.
        String[] messageArray = messageLines.toArray(new String[0]);
        SystemNotifications.privateBroadcast(server, sender, messageArray);
    }

}
