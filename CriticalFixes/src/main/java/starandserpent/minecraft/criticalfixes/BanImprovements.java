package starandserpent.minecraft.criticalfixes;

import org.bukkit.*;
import org.bukkit.ban.ProfileBanList;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.profile.PlayerProfile;
import org.jetbrains.annotations.NotNull;
import java.time.Duration;
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

public class BanImprovements implements CommandExecutor, Listener {

    private final JavaPlugin plugin;
    private Server server;

    private final String defaultBanReason = "sopimaton käytös";
    private final String systemBannerName = "Kuumaan järjestelmä";

    // private final Duration defaultBanDuration = (Duration) null; // Forever.
    private final Duration defaultBanDuration = Duration.ofDays(365);

    public BanImprovements(JavaPlugin plugin) {
        this.plugin = plugin;
        this.server = plugin.getServer();
    }


    @Override public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        // Check for permissions.
        boolean permissionOk = false;

        // Command was run by a player.
        if ((sender instanceof Player)) {

            // And they are OP. Allow it.
            if (sender.isOp()) {
                permissionOk = true;

            // No permissions to run the command. Disallow.
            } else {
                sender.sendMessage("You need to be OP to use this command.");
                return true;
            }
        }

        // The command was run from console, allow it.
        if ((sender instanceof ConsoleCommandSender)) {
            permissionOk = true;
        }

        // If the permission check failed, return false.
        if (!permissionOk) {
            sender.sendMessage("You do not have the permission to run this command.");
            return true;
        }

        // Permissions OK. Continue with the command.

        // /porttikielto <playername>
        // /porttikielto <playername> <duration>
        // /porttikielto <playername> <duration> <reason>
        // /porttikielto <playername> <reason>
        if (command.getName().equalsIgnoreCase("porttikielto")) {

            // Show usage information if no arguments.
            if (args.length == 0) {
                return false;
            }

            return porttikielto(sender, command, label, args);
        }

        // /armahda <playername>
        // /armahda <playername> <reason>
        if (command.getName().equalsIgnoreCase("armahda")) {

            // Show usage information if no arguments.
            if (args.length == 0) {
                return false;
            }

            return armahda(sender, command, label, args);
        }

        return false;
    }


    private boolean porttikielto(CommandSender banner, Command command, String label, String[] args) {

        // /porttikielto <playername>
        if (args.length == 1) {

            //Bukkit.createPlayerProfile(name).update().thenAccept(null); // Does a web call if offline.

            var bannedPersonName = args[0];
            OfflinePlayer bannedPerson = getPlayerReference(bannedPersonName);

            if (bannedPerson == null) {
                banner.sendMessage("Pelaajaa " + bannedPersonName + " ei löytynyt. Porttikiellon antaminen epäonnistui.");
                return false;
            }

            var bannedPersonUUID = bannedPerson.getUniqueId();
            PlayerProfile bannedPersonProfile = bannedPerson.getPlayerProfile();

            var bannerName = getBannerName(banner);

            // Ban the player.
            boolean success = banPlayer(bannedPersonProfile, defaultBanReason, defaultBanDuration, banner, bannerName);
            if (!success) {
                banner.sendMessage("Porttikiellon antaminen epäonnistui tuntemattomasta syystä.");
                return false;
            }

            // Send a message to the banner exclaiming success and details.
            announceSuccessToBanner(banner, bannedPersonName, bannedPersonUUID, defaultBanDuration, defaultBanReason);

            // Broadcast the ban message.
            announceBanToEveryone(bannedPersonName, defaultBanDuration, defaultBanReason, bannerName);

            // Kick the player with a ban message.
            kickWithBanMessage(bannedPerson, defaultBanDuration, defaultBanReason, bannerName);

            return true;
        }

        // Get some extra information before we handle the rest of the commands.
        String bannedPersonName = args[0];
        Duration banDuration = defaultBanDuration;
        boolean secondArgIsDuration;
        secondArgIsDuration = checkIfSecondArgumentIsDuration(args);


        // /porttikielto <playername> <duration>
        if (args.length == 2 && secondArgIsDuration) {

            var bannedPerson = getPlayerReference(bannedPersonName);
            if (bannedPerson == null) {
                banner.sendMessage("Porttikiellon antaminen epäonnistui. Pelaajaa " + args[0] + " ei löytynyt.");
                return false;
            }

            var bannedPersonProfile = bannedPerson.getPlayerProfile();

            banDuration = getDurationFromArgs(args[1]);
            if (banDuration == null) {
                banner.sendMessage("Porttikiellon antaminen epäonnistui. Kesto ei ole kelvollinen.");
                return false;
            }

            // Ban the player.
            var bannerName = getBannerName(banner);

            // Check for success.
            boolean success = banPlayer(bannedPersonProfile, defaultBanReason, banDuration, banner, bannerName);
            if (!success) {
                banner.sendMessage("Porttikiellon antaminen epäonnistui tuntemattomasta syystä.");
                return false;
            }

            // Send a message to the banner exclaiming success and details.
            announceSuccessToBanner(banner, bannedPersonName, bannedPerson.getUniqueId(), banDuration, defaultBanReason);

            // Broadcast the ban message.
            announceBanToEveryone(bannedPersonName, banDuration, defaultBanReason, bannerName);

            // Kick the player with a ban message.
            kickWithBanMessage(bannedPerson, banDuration, defaultBanReason, bannerName);

            return true;

        // /porttikielto <playername> <duration> <reason>
        } else if (args.length >= 3 && secondArgIsDuration) {

            var bannedPerson = getPlayerReference(bannedPersonName);
            if (bannedPerson == null) {
                banner.sendMessage("Porttikiellon antaminen epäonnistui. Pelaajaa " + args[0] + " ei löytynyt.");
                return false;
            }

            var bannedPersonProfile = bannedPerson.getPlayerProfile();

            banDuration = getDurationFromArgs(args[1]);
            if (banDuration == null) {
                banner.sendMessage("Porttikiellon antaminen epäonnistui. Kesto ei ole kelvollinen.");
                return false;
            }

            var banReason = String.join(" ", Arrays.copyOfRange(args, 2, args.length)).trim();            if (banReason.isEmpty()) {
                banReason = defaultBanReason;
            }

            // Ban the player.
            var bannerName = getBannerName(banner);
            boolean success = banPlayer(bannedPersonProfile, banReason, banDuration, banner, bannerName);

            // Check for success.
            if (!success) {
                banner.sendMessage("Porttikiellon antaminen epäonnistui tuntemattomasta syystä.");
                return false;
            }

            // Send a message to the banner exclaiming success and details.
            announceSuccessToBanner(banner, bannedPersonName, bannedPerson.getUniqueId(), banDuration, banReason);

            // Broadcast the ban message.
            announceBanToEveryone(bannedPersonName, banDuration, banReason, bannerName);

            // Kick the player with a ban message.
            kickWithBanMessage(bannedPerson, banDuration, banReason, bannerName);

            return true;

        // /porttikielto <playername> <reason>
        } else if (args.length > 2) {

            var bannedPerson = getPlayerReference(bannedPersonName);
            if (bannedPerson == null) {
                banner.sendMessage("Porttikiellon antaminen epäonnistui. Pelaajaa " + args[0] + " ei löytynyt.");
                return false;
            }

            var bannedPersonProfile = bannedPerson.getPlayerProfile();

            var banReason = String.join(" ", args).replaceFirst(args[0], "").trim();
            if (banReason.isEmpty()) {
                banReason = defaultBanReason;
            }

            // Ban the player.
            var bannerName = getBannerName(banner);
            boolean success = banPlayer(bannedPersonProfile, banReason, defaultBanDuration, banner, bannerName);

            // Check for success.
            if (!success) {
                banner.sendMessage("Porttikiellon antaminen epäonnistui tuntemattomasta syystä.");
                return false;
            }

            // Send a message to the banner exclaiming success and details.
            announceSuccessToBanner(banner, bannedPersonName, bannedPerson.getUniqueId(), defaultBanDuration, banReason);

            // Broadcast the ban message.
            announceBanToEveryone(bannedPersonName, defaultBanDuration, banReason, bannerName);

            // Kick the player with a ban message.
            kickWithBanMessage(bannedPerson, defaultBanDuration, banReason, bannerName);

            return true;
        }

        return false;
    }

    private String getBannerName(CommandSender banner) {
        if (banner instanceof Player player) {
            return player.getName();
        } else {
            return systemBannerName;
        }
    }

    private void announceSuccessToBanner(CommandSender banner, String bannedPersonName, UUID bannedPersonUUID, Duration banDuration, String banReason) {
        banner.sendMessage(bannedPersonName + " sai porttikiellon.");
        banner.sendMessage("(" + bannedPersonUUID + ").");
        banner.sendMessage("Porttikiellon kesto: " + banDuration.toDays() + " vuorokautta.");
        banner.sendMessage("Syy: \"" + banReason + "\"");
        banner.sendMessage("");
    }

    private void kickWithBanMessage(OfflinePlayer bannedPerson, Duration banDuration, String banReason, String bannerName) {

        // Do it after a delay.
        server.getScheduler().runTaskLater(plugin, () -> {

            // Kick the player if they are online.
            var player = bannedPerson.getPlayer();
            if (player != null) {

                var banMessage = "\u2620\n"
                        + "§7Onneksi olkoon, sait §f§lporttikiellon§r§7:\n"
                        + "\"§f§l" + banReason + "§r§7\"\n"
                        + "§r§7\nPorttikielto on voimassa §f§l"
                        + banDuration.toDays()
                        + "§r§7 vuorokautta.\nPorttikiellon antoi §r§l"
                        + bannerName + "§r§7.\n"
                        + "\nAsiasta voi valittaa Kuumaan §f§lDiscord§r§7 palvelimella:\n"
                        + "§fhttps://discord.gg/esmqVrPG8d§r\n"
                        + "§r§7\nHauskaa päivänjatkoa!";

                player.kickPlayer(banMessage);
            }
        }, 80);
    }

    private boolean banPlayer(PlayerProfile bannedPersonProfile, String banReason, Duration banDuration, CommandSender banner, String bannerName) {
        // Get BanLists.
        ProfileBanList banList = server.getBanList(BanList.Type.PROFILE);

        // Ban the OfflinePlayer Profile.
        BanEntry<PlayerProfile> banEntry = banList.addBan(bannedPersonProfile, banReason, banDuration, bannerName);

        // Ban.
        if (banEntry == null) {
            return false;
        }

        banEntry.save();

        boolean isBanned = banList.isBanned(bannedPersonProfile);
        if (!isBanned) {
            return false;
        }

        // Successfully banned.
        return true;
    }

    private void announceBanToEveryone(String bannedPersonName, Duration banDuration, String banReason, String bannerName) {
        var warningTriangle = Symbols.YELLOW_WARNING_TRIANGLE.literal;
        var moon = Symbols.MEDIUM_MOON.toString();
        var space = Symbols.getPos(16);

        if (Objects.equals(banReason, defaultBanReason)) {
            banReason = "Porttikiellolle ei annettu julkista syytä";
        }

        var banBroadcastMessage = space + warningTriangle + "Huomio! Järjestelmätiedote!\n"
                + moon + bannerName + " antoi porttikiellon pelaajalle " + bannedPersonName + ".\n"
                + "\"" + banReason +"\"\n"
                + "Porttikielto on voimassa " + banDuration.toDays() + " vuorokautta.";

        server.broadcastMessage(banBroadcastMessage);
    }

    private Duration getDurationFromArgs(String arg) {
        try {
            int duration = Integer.parseInt(arg);
            // Validate. Cannot be negative, cannot be zero.
            if (duration <= 0) {
                return defaultBanDuration;
            }
            return Duration.ofDays(duration);
        } catch (NumberFormatException e) {
            return defaultBanDuration;
        }
    }

    private OfflinePlayer getPlayerReference(String bannedPlayerName) {
        UUID playerUUID = null;
        for (OfflinePlayer player : Bukkit.getOfflinePlayers()) {
            var playerName = player.getName();
            if (playerName == null) {
                continue;
            }

            if (playerName.equalsIgnoreCase(bannedPlayerName)) {
                playerUUID = player.getUniqueId();
                break;
            }
        }

        if (playerUUID != null) {
            return Bukkit.getOfflinePlayer(playerUUID);
        }

        return null;
    }

    private boolean checkIfSecondArgumentIsDuration(String[] args) {

        // Check if the argument count is correct, otherwise second argument can't be a number.
        if (args.length < 2) {
            return false;
        }

        // Check if the second argument is a number.
        try {
            int duration = Integer.parseInt(args[1]);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // /armahda <playername>, /armahda <playerName> <reason>
    private boolean armahda(CommandSender sender, Command command, String label, String[] args) {

        // /armahda <playerName>
        if (args.length == 1) {
            var playerName = args[0];
            var player = getPlayerReference(playerName);
            if (player == null) {
                sender.sendMessage("Pelaajaa " + playerName + " ei löytynyt. Armahdus epäonnistui.");
                return true;
            }

            // Get BanLists.
            ProfileBanList banList = server.getBanList(BanList.Type.PROFILE);

            var playerProfile = player.getPlayerProfile();

            // Is the player banned?
            boolean isBanned = banList.isBanned(playerProfile);
            if (!isBanned) {
                sender.sendMessage("Pelaaja " + playerName + " ei ole porttikiellossa. Ei tarvitse armahtaa.");
                return true;
            }

            // Pardon the player.
            banList.pardon(playerProfile);

            boolean isStillBanned = banList.isBanned(playerProfile);
            if (isStillBanned) {
                sender.sendMessage("Yritettiin armahtaa " + playerName + ", mutta armahdus epäonnistui tuntemattomasta syystä.");
                return true;
            }

            // Broadcast the pardon message.
            var pardonBroadcastMessage = "Pelaaja " + playerName + " armahdettiin.";
            server.broadcastMessage(pardonBroadcastMessage);

            return true;
        }

        // /armahda <playerName> <reason>
        if (args.length >= 2) {
            var playerName = args[0];
            var player = getPlayerReference(playerName);
            if (player == null) {
                sender.sendMessage("Pelaajaa " + playerName + " ei löytynyt. Armahdus epäonnistui.");
                return true;
            }

            // Get BanLists.
            ProfileBanList banList = server.getBanList(BanList.Type.PROFILE);

            var playerProfile = player.getPlayerProfile();

            // Is the player banned?
            boolean isBanned = banList.isBanned(playerProfile);
            if (!isBanned) {
                sender.sendMessage("Pelaaja " + playerName + " ei ole porttikiellossa. Ei tarvitse armahtaa.");
                return true;
            }

            // Pardon the player.
            banList.pardon(playerProfile);

            boolean isStillBanned = banList.isBanned(playerProfile);
            if (isStillBanned) {
                sender.sendMessage("Yritettiin armahtaa " + playerName + ", mutta armahdus epäonnistui tuntemattomasta syystä.");
                return true;
            }

            // Combine multiple args to get reason string.
            var pardonReason = String.join(" ", args).replaceFirst(args[0], "").trim();
            var pardonMessage = "Pelaaja " + playerName + " armahdettiin. \"" + pardonReason +"\"";

            // Broadcast the pardon message.
            server.broadcastMessage(pardonMessage);

            return true;
        }

        return false;
    }

    // Use high priority to kick the player before they can do anything.
    @EventHandler(priority = EventPriority.HIGHEST) public void onPlayerLoginEvent(PlayerLoginEvent event) {

        var player = event.getPlayer();
        var playerProfile = player.getPlayerProfile();

        // Get BanLists.
        ProfileBanList banList = server.getBanList(BanList.Type.PROFILE);

        // Is the player banned?
        boolean isBanned = banList.isBanned(playerProfile);
        if (!isBanned) {
            return;
        }

        // Get the ban entry.
        var banEntry = banList.getBanEntry(playerProfile);
        if (banEntry == null) {
            return;
        }

        // Get the ban reason.
        var banReason = banEntry.getReason();
        if (banReason == null) {
            banReason = defaultBanReason;
        }

        // Get the ban duration.
        var banDuration = banEntry.getExpiration();

        // Get the banner name.
        var bannedName = banEntry.getSource();

        // Check for playerloginevent getresult
        var result = event.getResult();
        if (result == PlayerLoginEvent.Result.KICK_BANNED) {

            assert banDuration != null;
            var banMessage = buildBanMessage(player.getName(), banReason, banDuration, bannedName);

            event.disallow(PlayerLoginEvent.Result.KICK_BANNED, banMessage);
        }

    }

    private String buildBanMessage(String bannedName, String banReason, Date banDuration, String bannerName) {

        var howMuchBanRemaining = banDuration.getTime() - System.currentTimeMillis();
        var howMuchBanInDays = howMuchBanRemaining / (1000 * 60 * 60 * 24);
        var banDurationRounded = Math.round(howMuchBanInDays);

        String banMessage= "\u2620\n"
                + "§7Tervehdys, §f§l" + bannedName + "§r§7, sinulla on §f§lporttikielto§r§7:\n"
                + "\"§f§l" + banReason + "§r§7\"\n"
                + "§r§7\nPorttikielto on voimassa vielä §f§l"
                + banDurationRounded
                + "§r§7 vuorokautta.\n" +
                "Porttikiellon antoi §f§l"
                + bannerName + "§r§7.\n"
                + "\nAsiasta voi valittaa Kuumaan §f§lDiscord§r§7 palvelimella:\n"
                + "§fhttps://discord.gg/esmqVrPG8d§r\n"
                + "§r§7\nHauskaa päivänjatkoa!";
        return banMessage;
    }

    // Static method for checking if player is banned or not from UUID.
    public static boolean isPlayerBanned(UUID playerUuid) {
        var banList = Bukkit.getServer().getBanList(BanList.Type.PROFILE);

        PlayerProfile playerProfile = null;
        try {
            playerProfile = Bukkit.createPlayerProfile(playerUuid);
        } catch (Exception e) {
            // Can't be found so not banned.
            return false;
        }

        // Check if player is banned by UUID or name.
        var playerName = playerProfile.getName();
        if (playerName == null) {
            // Can't be found for some reason, so I guess not banned.
            return false;
        }

        return banList.isBanned(playerName);
    }

    // Static method for checking if player is banned or not from player name.
    public static boolean isPlayerBanned(String playerName) {
        var banList = Bukkit.getServer().getBanList(BanList.Type.PROFILE);

        PlayerProfile playerProfile = null;
        try {
            playerProfile = Bukkit.createPlayerProfile(playerName);
        } catch (Exception e) {
            // Can't be found so not banned.
            return false;
        }

        // Check if player is banned by name.
        return banList.isBanned(playerName);
    }

}
