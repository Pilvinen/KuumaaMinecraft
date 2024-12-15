package starandserpent.minecraft.criticalfixes;

import com.nivixx.ndatabase.api.NDatabase;
import com.nivixx.ndatabase.api.query.NQuery;
import com.nivixx.ndatabase.api.repository.Repository;
import org.bukkit.Server;
import org.bukkit.event.EventHandler;
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
    private final Repository<UUID, PlayerLoginInfo> repository;

    public WhoWasHereWhileYouWereGone(JavaPlugin plugin) {
        this.plugin = plugin;
        this.server = plugin.getServer();
        this.repository = NDatabase.api().getOrCreateRepository(PlayerLoginInfo.class);
    }

    @EventHandler public void onPlayerJoin(PlayerJoinEvent event) {
        UUID playerId = event.getPlayer().getUniqueId();
        long currentTime = System.currentTimeMillis();

        // Get the player's last logout time
        PlayerLoginInfo lastLogoutInfo = repository.findOne(NQuery.predicate("$.playerId == '" + playerId + "'")).orElse(null);
        if (lastLogoutInfo == null) {
            // If the player has no previous logout info, they are a new player.
            event.getPlayer().sendMessage("Tervetuloa palvelimelle!");
        } else {
            // If the player has previous logout info, they are a returning player.
            event.getPlayer().sendMessage("Tervetuloa takaisin palvelimelle!");
        }

        if (lastLogoutInfo != null) {
            long lastLogoutTime = lastLogoutInfo.getTimestamp();

            // Print out how long ago you were online.
            long timeSinceLastLogout = currentTime - lastLogoutTime;
            // Make it human readable.
            long seconds = timeSinceLastLogout / 1000;
            long minutes = seconds / 60;
            long hours = minutes / 60;
            long days = hours / 24;
            event.getPlayer().sendMessage("Viime käynnistäsi on " + days + " päivää, " + hours % 24 + " tuntia ja " + minutes % 60 + " min.");

            // Get the list of players who logged in after the player's last logout time
            List<PlayerLoginInfo> logins = repository.streamAllValues()
                    .filter(info -> info.getTimestamp() > lastLogoutTime && !info.getPlayerId().equals(playerId))
                    .collect(Collectors.toList());

            if (logins.isEmpty()) {
                event.getPlayer().sendMessage("Ei kävijöitä sitten viime vierailusi.");
            } else {
                var visitorCountSinceLastVisit = logins.size();
                event.getPlayer().sendMessage(visitorCountSinceLastVisit + " kävijää sitten viime vierailusi:");
                for (PlayerLoginInfo info : logins) {
                    event.getPlayer().sendMessage(info.getPlayerName());
                }
            }
        }

        // Update the player's login info
        repository.upsert(new PlayerLoginInfo(playerId, event.getPlayer().getName(), currentTime));
    }

    @EventHandler public void onPlayerQuit(PlayerQuitEvent event) {
        UUID playerId = event.getPlayer().getUniqueId();
        String playerName = event.getPlayer().getName();
        long currentTime = System.currentTimeMillis();

        // DEBUG
        System.out.println("Player " + playerName + " quit at " + currentTime);

        // Update the player's logout info
        repository.upsert(new PlayerLoginInfo(playerId, playerName, currentTime));
    }
}