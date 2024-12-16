package starandserpent.minecraft.criticalfixes;

import com.nivixx.ndatabase.api.NDatabase;
import com.nivixx.ndatabase.api.repository.Repository;
import org.bukkit.Server;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class WhoWasHereWhileYouWereGone implements Listener {

    private final JavaPlugin plugin;
    private Server server;
    private static Repository<String, PlayerLoginInfo> repository;

    public WhoWasHereWhileYouWereGone(JavaPlugin plugin) {
        this.plugin = plugin;
        this.server = plugin.getServer();
        this.repository = NDatabase.api().getOrCreateRepository(PlayerLoginInfo.class);
    }

    @EventHandler(priority = EventPriority.LOW) public void onPlayerJoin(PlayerJoinEvent event) {
        UUID playerId = event.getPlayer().getUniqueId();
        long currentTime = System.currentTimeMillis();
        String playerName = event.getPlayer().getName();

        // Get the player's last logout time
        PlayerLoginInfo lastLoginInfo = repository.get(playerId.toString());

        if (lastLoginInfo == null) {
            // If the player has no previous logout info, they are a new player.

            String[] firstTimeWelcomeMessage;

            var currentWorld = event.getPlayer().getWorld().getName();
            if (currentWorld.equals("Tyhjyys")) {
                firstTimeWelcomeMessage = new String[] {
                        "Tervetuloa Kuumaahan, " + playerName + "!",
                        "Uudet pelaajat päästetään alkuspawnilta",
                        "lyhyen haastattelun jälkeen.",
                        "Palvelimen ylläpitäjänä toimii Pilvinen."
                };
            } else {
                firstTimeWelcomeMessage = new String[]{
                        "Tervetuloa Kuumaahan, " + playerName + "!",
                        "Palvelimen ylläpitäjänä toimii Pilvinen."
                };
            }

            SystemNotifications.privateBroadcast(server, event.getPlayer(), firstTimeWelcomeMessage);

            event.getPlayer().sendMessage("Ai niin, " + event.getPlayer().getName() + ". Liity Kuumaan Discordiin:");
            event.getPlayer().sendMessage("https://discord.gg/esmqVrPG8d");
            event.getPlayer().sendMessage("Niin pysyt kartalla palvelimen tapahtumista.");

            // Save the player's login info
            repository.upsert(new PlayerLoginInfo(playerId.toString(), event.getPlayer().getName(), currentTime));
        } else {
            // If the player has previous logout info, they are a returning player.
            long lastLogoutTime = lastLoginInfo.getTimestamp();

            // Print out how long ago you were online.
            long timeSinceLastLogout = currentTime - lastLogoutTime;
            // Make it human readable.
            long seconds = timeSinceLastLogout / 1000;
            long minutes = seconds / 60;
            long hours = minutes / 60;
            long days = hours / 24;

            var welcomeMessage = new String[]{
                    "Tervetuloa kotiin, " + playerName + "!",
                    "Olit poissa " + days + " päivää, " + hours % 24 + " tuntia ja " + minutes % 60 + " min."
            };

            SystemNotifications.privateBroadcast(server, event.getPlayer(), welcomeMessage);

            // Get the list of players who logged in after the player's last logout time
            List<PlayerLoginInfo> logins = repository.streamAllValues()
                    .filter(info -> info.getTimestamp() > lastLogoutTime && !info.getPlayerId().equals(playerId.toString()))
                    .collect(Collectors.toList());

            if (logins.isEmpty()) {
                event.getPlayer().sendMessage("Ei muita kävijöitä sitten viime vierailusi.");
            } else {
                var visitorCountSinceLastVisit = logins.size();
                StringBuilder messageBuilder = new StringBuilder(visitorCountSinceLastVisit + " kävijää sitten viime vierailusi:\n");
                for (int i = 0; i < logins.size(); i++) {
                    String loginPlayerName = logins.get(i).getPlayerName();
                    if (messageBuilder.length() + loginPlayerName.length() + 2 > 256) { // +2 for ", "
                        event.getPlayer().sendMessage(messageBuilder.toString());
                        messageBuilder = new StringBuilder();
                    }
                    if (!messageBuilder.isEmpty()) {
                        if (i == logins.size() - 1) {
                            messageBuilder.append(". ");
                        } else {
                            messageBuilder.append(", ");
                        }
                    }
                    messageBuilder.append(loginPlayerName);
                }
                if (!messageBuilder.isEmpty()) {
                    event.getPlayer().sendMessage(messageBuilder.toString());
                }
            }

            // Update the player's logout info
            lastLoginInfo.setTimestamp(currentTime);
            repository.upsert(lastLoginInfo);

        }
    }

    private void deleteFuckedUpDatabase() {
        repository.deleteAll();
//          Print
        System.out.println("Database deleted!!!!");
    }

    @EventHandler public void onPlayerQuit(PlayerQuitEvent event) {
        UUID playerId = event.getPlayer().getUniqueId();
        String playerName = event.getPlayer().getName();
        long currentTime = System.currentTimeMillis();

        // Update the player's logout info
        var lastLoginInfo = repository.get(playerId.toString());
        if (lastLoginInfo != null) {
            lastLoginInfo.setTimestamp(currentTime);
            repository.upsert(lastLoginInfo);
        } else {
            repository.upsert(new PlayerLoginInfo(playerId.toString(), playerName, currentTime));
        }
    }

    public static boolean hasPlayerBeenHereBefore(UUID playerId) {
        return repository.get(playerId.toString()) != null;
    }

}