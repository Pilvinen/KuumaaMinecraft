package starandserpent.minecraft.criticalfixes;

import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

public class HideNames implements Listener {
    private final JavaPlugin plugin;
    private Server server;
    private Team hideNamesTeam;
    private ScoreboardManager scoreboardManager;
    private Scoreboard mainScoreboard;

    public HideNames(JavaPlugin plugin) {
        this.plugin = plugin;
        this.server = plugin.getServer();

        // Initialize the team.
        this.scoreboardManager = server.getScoreboardManager();
        if (scoreboardManager == null) {
            return;
        }

        hideNamesTeam = scoreboardManager.getMainScoreboard().getTeam("hideNames");
        if (hideNamesTeam == null) {
            this.mainScoreboard = scoreboardManager.getMainScoreboard();
            hideNamesTeam = mainScoreboard.registerNewTeam("hideNames");

            // Set options to reasonable defaults.
            hideNamesTeam.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
            hideNamesTeam.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.ALWAYS);
            hideNamesTeam.setAllowFriendlyFire(true);
            hideNamesTeam.setCanSeeFriendlyInvisibles(false);
        }
    }

    // On join make player join scoreboard team "hideNames".
    @EventHandler public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        hideNamesTeam.addEntry(player.getName());
    }

    // On quitting the server. Not sure if really necessary, but hey.
    @EventHandler public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        hideNamesTeam.removeEntry(player.getName());
    }

}
