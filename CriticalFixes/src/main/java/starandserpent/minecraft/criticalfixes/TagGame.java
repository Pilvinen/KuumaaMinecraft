package starandserpent.minecraft.criticalfixes;

import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TagGame implements Listener {

    private final JavaPlugin plugin;
    private Server server;

    Scoreboard gameOfTagScoreboard;
    ScoreboardManager scoreboardManager;
    Team tagPlayersTeam;

    // List of tag players.
    List<Player> tagPlayers = new ArrayList<>();

    // Timer for tag score.
    BukkitTask tagScoreTimer;

    Player playerWhoIsIt;
    Player whoWasItTheLastTime;
    Date whenWasItTheLastTime;

    public TagGame(JavaPlugin plugin) {
        this.plugin = plugin;
        server = plugin.getServer();

        createGameOfTagScoreBoard();
    }

    // In a game of tag the player who is "it" has a red flag.
    // The rest of the players have white flags indicating that they are participating in the game.
    // When the player who is "it" touches another player, the red flag is transferred to the touched player
    // and the white flag is transferred to the player who was "it".
    // When you hit someone with a flag in off-hand, they don't take damage.
    // The flags are placed in off-hand.

    // On shoot by bow.
    @EventHandler public void onPlayerShootBow(ProjectileHitEvent event) {

        ProjectileSource attacker = event.getEntity().getShooter();

        // Check if the attacker is a player
        if (!(attacker instanceof Player attackerPlayer)) {
            return;
        }

        var victim = event.getHitEntity();
        if (victim == null) {
            return;
        }

        if (victim.getType() != EntityType.PLAYER) {
            return;
        }

        Player victimPlayer = (Player) victim;

        // Check if the attacker is holding a flag in head.
        var attackerItemInHead = attackerPlayer.getInventory().getHelmet();

        if (attackerItemInHead == null) {
            return;
        }

        // Check if the player is holding a white flag in hand or off-hand.
        var attackerFlagInHead = attackerItemInHead.getType() == Material.WHITE_BANNER || attackerItemInHead.getType() == Material.RED_BANNER;

        // If there was any kind of white flag or red flag cancel event.
        if (attackerFlagInHead) {
            event.setCancelled(true);
        }

        var victimFlag = victimPlayer.getInventory().getHelmet();
        if (victimFlag == null) {
            return;
        }

        var tagFlag = attackerPlayer.getInventory().getHelmet();
        if (tagFlag == null) {
            return;
        }

        // You're not it.
        if (!(tagFlag.getType().equals(Material.RED_BANNER))) {
            return;
        }

        attemptMakingATag(attackerPlayer, victimPlayer, victimFlag, tagFlag);
    }

    public void attemptMakingATag(Player attackerPlayer, Player victimPlayer, ItemStack victimFlag, ItemStack tagFlag) {
        // Now that we have references to the items, we can swap them.
        attackerPlayer.getInventory().setHelmet(victimFlag);
        victimPlayer.getInventory().setHelmet(tagFlag);

        playerWhoIsIt = victimPlayer;

        // Loop all players holding white or red flags in radius of 500 blocks and announce that player is it!
        for (Player loopedPlayer : server.getOnlinePlayers()) {
            if (loopedPlayer.getLocation().distance(victimPlayer.getLocation()) < 500) {
                var helmet = loopedPlayer.getInventory().getHelmet();
                if (helmet == null) {
                    continue;
                }
                if (helmet.getType() == Material.WHITE_BANNER
                        || helmet.getType() == Material.RED_BANNER) {
                    loopedPlayer.sendTitle("", victimPlayer.getName() + " on hippa!", 10, 70, 20);
                }
            }
        }
    }

    // On player hit
    @EventHandler public void onPlayerHit(EntityDamageByEntityEvent event) {

        var attacker = event.getDamager();
        var victim = event.getEntity();

        // Check if the attacker is a player
        if (attacker.getType() != EntityType.PLAYER) {
            return;
        }

        // Check if the victim is a player
        if (victim.getType() != EntityType.PLAYER) {
            return;
        }

        // Check if the attacker is holding a flag in hand or off-hand.
        var attackerPlayer = (Player) attacker;
        var attackerItemInHead = attackerPlayer.getInventory().getHelmet();

        if (attackerItemInHead == null) {
            return;
        }

        // Check if the player is holding a white flag in head.
        var attackerFlagInHead = attackerItemInHead.getType() == Material.WHITE_BANNER || attackerItemInHead.getType() == Material.RED_BANNER;

        // If there was any kind of white flag or red flag cancel event.
        if (attackerFlagInHead) {
            event.setCancelled(true);
        }

        // You're not it.
        if (!(attackerItemInHead.getType().equals(Material.RED_BANNER))) {
            return;
        }

        // You are it. Check if the victim is playing the game.
        var victimPlayer = (Player) victim;
        var victimItemInHead = victimPlayer.getInventory().getHelmet();
        if (victimItemInHead == null) {
            return;
        }

        var victimFlagInHead = victimItemInHead.getType() == Material.WHITE_BANNER;

        // If not, exit.
        if (!victimFlagInHead) {
            return;
        }

        // We have established that attacker is it and the victim is playing the game.
        // Transfer the red flag to the victim and the white flag to the attacker.
        ItemStack tagFlag;
        tagFlag = attackerItemInHead;

        if (tagFlag.getType() != Material.RED_BANNER) {
            return;
        }

        ItemStack victimFlag;
        victimFlag = victimItemInHead;

        if (victimFlag.getType() != Material.WHITE_BANNER) {
            return;
        }

        attemptMakingATag(attackerPlayer, victimPlayer, victimFlag, tagFlag);

    }

    @EventHandler public void onPlayerQuit(PlayerQuitEvent event) {
        var player = event.getPlayer();

        // If player is playing tag.
        if (tagPlayers.contains(player)) {
            // Remove player from game of tag.
            removeFromGameOfTag(player);
        }
    }

    @EventHandler public void onPlayerDeath(PlayerDeathEvent event) {
        var player = (Player) event.getEntity();

        // If player is playing tag.
        if (tagPlayers.contains(player)) {
            // Remove player from game of tag.
            removeFromGameOfTag(player);
        }
    }

    public void createGameOfTagScoreBoard() {
        scoreboardManager = server.getScoreboardManager();
        if (scoreboardManager != null) {
            gameOfTagScoreboard = scoreboardManager.getNewScoreboard();
        }

        if (gameOfTagScoreboard == null) {
            return;
        }

        gameOfTagScoreboard.registerNewTeam("Hippa!");
        tagPlayersTeam = gameOfTagScoreboard.getTeam("Hippa!");
        if (tagPlayersTeam != null) {
            tagPlayersTeam.setPrefix("§c");
        }

        var criteria = Criteria.create("dummy");
        gameOfTagScoreboard.registerNewObjective("Hippa!", criteria, "Hippa!");

        Objective objective = gameOfTagScoreboard.getObjective("Hippa!");
        if (objective == null) {
            return;
        }

        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setDisplayName("Hippa!");

        tagScoreTimer = server.getScheduler().runTaskTimer(plugin, () -> {
            for (Player player : server.getOnlinePlayers()) {
                ItemStack helmet = player.getInventory().getHelmet();
                if (helmet != null && (helmet.getType() == Material.WHITE_BANNER || helmet.getType() == Material.RED_BANNER)) {
                    if (!tagPlayers.contains(player)) {
                        addPlayerToGameOfTag(player);
                    }
                } else if (tagPlayers.contains(player)) {
                    removeFromGameOfTag(player);
                }

                if (player == playerWhoIsIt) {
                    subtractScore(player, 1);
                    continue;
                }

                // If distance to the player who is tag is less than 10 blocks increase score by 2.
                if (playerWhoIsIt != null && player.getLocation().distance(playerWhoIsIt.getLocation()) < 10) {
                    increaseScore(player, 3);
                } else if (playerWhoIsIt != null && player.getLocation().distance(playerWhoIsIt.getLocation()) < 50) {
                    increaseScore(player, 2);
                } else if (playerWhoIsIt != null && player.getLocation().distance(playerWhoIsIt.getLocation()) >= 50) {
                    subtractScore(player, 1);
                }
            }
        }, 0L, 20L);

    }

    private void subtractScore(Player player, int scoreToSubtract) {
        Objective objective = gameOfTagScoreboard.getObjective("Hippa!");
        if (objective == null) {
            return;
        }

        Score score = objective.getScore(player.getName());

        // If score is already 0, don't subtract.
        if (score.getScore() == 0) {
            return;
        }

        // Subtract score.
        score.setScore(score.getScore() - scoreToSubtract);
    }


    public void addPlayerToGameOfTag(Player player) {
        if (tagPlayers.contains(player)) {
            return;
        }

        // Add to list
        tagPlayers.add(player);

        tagPlayersTeam.addEntry(player.getName());
        resetScore(player);

        player.setScoreboard(gameOfTagScoreboard);
    }

    public void removeFromGameOfTag(Player player) {
        resetScore(player);
        tagPlayersTeam.removeEntry(player.getName());
        var scoreboard = player.getScoreboard();
        scoreboard.resetScores(player.getName());
        var newScoreboard = scoreboardManager.getNewScoreboard();
        player.setScoreboard(newScoreboard);
        tagPlayers.remove(player);
    }

    public void increaseScore(Player player, int increaseScoreAmount) {
        Objective objective = gameOfTagScoreboard.getObjective("Hippa!");
        if (objective == null) {
            return;
        }

        Score score = objective.getScore(player.getName());
        score.setScore(score.getScore() + increaseScoreAmount);
    }

    public void resetScore(Player player) {
        Objective objective = gameOfTagScoreboard.getObjective("Hippa!");
        if (objective == null) {
            return;
        }

        Score score = objective.getScore(player.getName());
        score.setScore(0);
    }

}
