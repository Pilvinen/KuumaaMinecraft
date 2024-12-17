package starandserpent.minecraft.criticalfixes;

import com.vdurmont.emoji.EmojiManager;
import com.vdurmont.emoji.EmojiParser;
import net.minso.chathead.API.ChatHeadAPI;
import net.minso.chathead.API.SkinSource;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class KuuChat implements Listener {
    private final JavaPlugin plugin;
    private Server server;

    // Logger.
    private Logger log = Logger.getLogger("KuuChat");
    private Handler handler;


    private ChatColor nameDefaultColor = org.bukkit.ChatColor.WHITE;
    private ChatColor nameHighlightColor = org.bukkit.ChatColor.YELLOW;
    private ChatColor resetColor = org.bukkit.ChatColor.GRAY;
    private ChatHeadAPI chatHeadAPI;
    private static HashMap<Player, Boolean> hatState = new HashMap<>();
    private HashMap<Player, ChatFaceCache> chatFaceCaches = new HashMap<>();
    private String steveFace = "";

    // HashMap to store join timestamps.
    private HashMap<UUID, Long> joinTimestamps = new HashMap<>();

    // HashMap of emojis and their type.
    private HashMap<String, Emotion> emojis = new HashMap<>() {{
        put(":-)", Emotion.HAPPY);
        put(":)", Emotion.HAPPY);
        put("=)", Emotion.HAPPY);
        put(":->", Emotion.HAPPY);
        put(":>", Emotion.HAPPY);
        put("=->", Emotion.HAPPY);
        put(":-]", Emotion.HAPPY);
        put(":]", Emotion.HAPPY);
        put("=]", Emotion.HAPPY);
        put(";-)", Emotion.HAPPY);
        put(";)", Emotion.HAPPY);
        put(";-]", Emotion.HAPPY);
        put(";]", Emotion.HAPPY);
        put(":-(", Emotion.SAD);
        put(":(", Emotion.SAD);
        put("=(", Emotion.SAD);
        put(":-E", Emotion.SAD);
        put(":E", Emotion.SAD);
        put("=E", Emotion.SAD);
        put(":-<", Emotion.SAD);
        put(":<", Emotion.SAD);
        put("=<", Emotion.SAD);
        put(":-[", Emotion.SAD);
        put(":[", Emotion.SAD);
        put("=[", Emotion.SAD);
        put(";-(", Emotion.SAD);
        put(";(", Emotion.SAD);
        put(";-<", Emotion.SAD);
        put(";<", Emotion.SAD);
        put(";-[", Emotion.SAD);
        put(";[", Emotion.SAD);
        put(":-D", Emotion.LAUGHING);
        put(":D", Emotion.LAUGHING);
        put("=D", Emotion.LAUGHING);
        put(":-P", Emotion.LAUGHING);
        put(":P", Emotion.LAUGHING);
        put("=P", Emotion.LAUGHING);
        put(":-O", Emotion.SHOCKED);
        put(":O", Emotion.SHOCKED);
        put("=O", Emotion.SHOCKED);
        put(":-|", Emotion.SERIOUS);
        put(":|", Emotion.SERIOUS);
        put("=|", Emotion.SERIOUS);
        put(":-/", Emotion.SERIOUS);
        put("=/", Emotion.SERIOUS);
        put(":-\\", Emotion.SERIOUS);
        put("=\\", Emotion.SERIOUS);
        put(":-I", Emotion.SERIOUS);
        put(":I", Emotion.SERIOUS);
        put("=I", Emotion.SERIOUS);
    }};

    // List of join messages.
    private String[] randomJoinMessages = {
            "Ilo ja riemu,",
            "Ystävämme",
            "Jippii, se on",
            "Vihdoinkin,",
            "Kaikkien kaveri,",
            "No mutta,",
            "Ilon päivä,",
            "Voi tavatonta,",
            "Hyvänen aika,",
            "Täysin odottamatta,",
            "Kuin tähdistä pudoten,",
            "Tänään on se päivä,",
            "Äkkiarvaamatta,",
            "Mutta sitten",
            "Katsokaa,",
            "Aivan erityinen",
            "Mahtavaa!",
            "Horjuvin askelin,",
            "Kuin tuulenvire,",
            "Epäröimättä,",
            "Höh,",
            "Hahaa,",
            "Mitä?",
            "Eikä!",
            "Pakko uskoa,",
            "Kenenkään pakottamatta,",
            "Ovi rämähti auki,",
            "Mitä nyt taas?",
            "\"Minulla ei ole peliongelmaa\"",
    };

    // List of leave messages.
    private String[] leaveMessages = {
            "murtui henkisesti",
            "antoi periksi",
            "kadotti mielenkiinnon",
            "päätti jatkaa myöhemmin",
            "katosi mystisesti",
            "kyllästyi hommaan",
            "häipyi yllättäen",
            "jätti pelin kesken",
            "luovutti tyylillä",
            "ei kestänyt enää",
            "valitsi elämän",
            "katsoi viisaaksi lopettaa",
            "vetäytyi takavasemmalle",
            "lähti muille maille",
            "poistui vähin äänin",
            "teki näyttävän lähdön",
            "joutui kiireisiin",
            "päätti ottaa tauon",
            "jätti kentän muille",
            "lähti katkolle",
            "löysi tärkeämpää tekemistä",
            "päätti säästää hermojaan",
            "päätti tehdä jotain muuta",
            "häipyi voittajan elkein",
            "päätti olla stressaamatta",
            "vetäytyi rauhoittumaan",
            "sai tarpeekseen",
            "poistui arvokkaasti",
            "siirtyi muihin haasteisiin",
    };

//    private String testFace = "IzQ1MTIwMCMyZTBhMDAjMmUwYTAwIzFmMDQwMCMyZTBhMDAjMmUwYTAwIzQ1MTIwMCM1OTFiMDAjMmUwYTAwIzJlMGEwMCMxZjA0MDAjY2E4NTYwIzJlMGEwMCMxZjA0MDAjMmUwYTAwIzQ1MTIwMCMxZjA0MDAjZTBhMzczI2U4Yjc4MyNlMGEzNzMjMmUwYTAwIzJlMGEwMCMyZTBhMDAjMmUwYTAwIzJlMGEwMCNmZmZlZmUjMGYwMjAwI2UwYTM3MyNmMmNiOTkjMGYwMjAwIzBmMDIwMCMxZjA0MDAjZThiNzgzI2ZmZmVmZSMyZDViNjYjZjJjYjk5I2YyY2I5OSMyZDViNjYjZmZmZmZmI2U4Yjc4MyNlMGEzNzMjZThiNzgzI2YyY2I5OSNmMmNiOTkjZmFkYmFhI2YyY2I5OSNlOGI3ODMjZTBhMzczIzJlMGEwMCNjYTg1NjAjZjJjYjk5IzZlNDEzNiM2NjNkMzIjZjJjYjk5I2NhODU2MCMyZTBhMDAjMWYwNDAwIzFmMDQwMCNlMGEzNzMjZjJjYjk5I2ZhZGJhYSNlMGEzNzMjMWYwNDAwIzFmMDQwMA==";
//    private String testFace = "IzQ1MTIwMCMyRTBBMDAjMkUwQTAwIzFGMDQwMCMyRTBBMDAjMkUwQTAwIzQ1MTIwMCM1OTFCMDAjMkUwQTAwIzJFMEEwMCMxRjA0MDAjQ0E4NTYwIzJFMEEwMCMxRjA0MDAjMkUwQTAwIzQ1MTIwMCMxRjA0MDAjRTBBMzczI0U4Qjc4MyNFMEEzNzMjMkUwQTAwIzJFMEEwMCMyRTBBMDAjMkUwQTAwIzJFMEEwMCNGRkZFRkUjMEYwMjAwI0UwQTM3MyNGMkNCOTkjMEYwMjAwIzBGMDIwMCMxRjA0MDAjRThCNzgzI0ZGRkVGRSMyRDVCNjYjRjJDQjk5I0YyQ0I5OSMyRDVCNjYjRkZGRkZGI0U4Qjc4MyNFMEEzNzMjNjYzRDMyI0YyQ0I5OSNGMkNCOTkjRkFEQkFBI0YyQ0I5OSM2NjNEMzIjRTBBMzczIzJFMEEwMCNDQTg1NjAjNjYzRDMyIzZFNDEzNiM2NjNEMzIjNjYzRDMyI0NBODU2MCMyRTBBMDAjMUYwNDAwIzFGMDQwMCNFMEEzNzMjRjJDQjk5I0ZBREJBQSNFMEEzNzMjMUYwNDAwIzFGMDQwMA==";

//    private String testFaceGzippedBase64 = "H4sIAAAAAAAAA3WQUQ4DIQhED8MFEJTVT9117n+kNi1kY+v6M+GFYRDKJQkzyeR+SwLnP+idpaXxZDh7LfYwxe0O30wPpVnHUTWqdeZWAExMYrDcUyDnaC2gSwR9E2AoyCRXGWZhcAn4eT8rhX0xoF+j96i2f/BLeIvNnNTITC+VgNtjredZ8xwunS+QuofZwAEAAA==";

    public KuuChat(JavaPlugin plugin) throws IOException {
        this.plugin = plugin;

        // Get server.
        server = plugin.getServer();
        // Initialize the ChatHeadAPI instance
        ChatHeadAPI.initialize(plugin);
        chatHeadAPI = ChatHeadAPI.getInstance();

        // Plan B. Everything fails. Use Steve's head.
        this.steveFace = chatHeadAPI.getHeadAsString(UUID.randomUUID(), false, SkinSource.LOCALFILE);

        // Initialize the FileHandler.
        // %d - date/time
        String directoryPath = plugin.getDataFolder().getAbsolutePath() + "/KuuChat/Logs/";
        Files.createDirectories(Paths.get(directoryPath)); // This line creates the directory if it does not exist
        String pattern = directoryPath + "KuuChat-%d.log";
        handler = new DailyRollingFileHandler(pattern);
        handler.setFormatter(new CustomChatLogFormatter());
        log.setUseParentHandlers(false);
        log.addHandler(handler);

        // Log server start messages.
        var logTimestamp = new java.text.SimpleDateFormat("dd.MM.yyyy, HH:mm").format(new java.util.Date());
        var logMessage = String.format("[%s] Palvelin käynnistyi.", logTimestamp);
        log.info(logMessage);
    }

    public static void setHatState(Player player, boolean isHatEnabled) {
        hatState.put(player, isHatEnabled);
    }

    private boolean getHatState(Player player) {
        return hatState.getOrDefault(player, false);
    }

    // On player joining the server.
    @EventHandler(priority = EventPriority.HIGHEST) public void onPlayerJoin(PlayerJoinEvent event) {
        cacheChatFacesForPlayer(event);

        boolean isFirstJoin = !WhoWasHereWhileYouWereGone.hasPlayerBeenHereBefore(event.getPlayer().getUniqueId());

        // Set custom join message.
        var chatTimestamp = new java.text.SimpleDateFormat("HH:mm").format(new java.util.Date());
        var randomJoinMessage = randomJoinMessages[(int) (Math.random() * randomJoinMessages.length)];

        if (isFirstJoin) {
            event.setJoinMessage(ChatColor.GREEN + "[" + chatTimestamp + "] " + event.getPlayer().getName() + " saapui palvelimelle ensimmäistä kertaa!" + ChatColor.RESET);
        } else {
            event.setJoinMessage(ChatColor.YELLOW + "[" + chatTimestamp + "] " + randomJoinMessage + " " + event.getPlayer().getName() + " saapui palvelimelle." + ChatColor.RESET);
        }

        // Log join messages.
        var logTimestamp = new java.text.SimpleDateFormat("dd.MM.yyyy, HH:mm").format(new java.util.Date());
        var logMessage = String.format("[%s] %s liittyi palvelimelle.", logTimestamp, event.getPlayer().getName());
        log.info(logMessage);

        // Store join timestamp.
        joinTimestamps.put(event.getPlayer().getUniqueId(), System.currentTimeMillis());
    }

    // Get and cache hat and no hat faces for the player.
    private void cacheChatFacesForPlayer(PlayerJoinEvent event) {

        CompletableFuture.runAsync(() -> {

            var player = event.getPlayer();
            var playerUUID = player.getUniqueId();

            boolean hatFaceNotFound = true;
            boolean hatlessFaceNotFound = true;

            String hatFace = "";
            String hatlessFace = "";

            String minotarHatFace = "";
            String minotarHatlessFace = "";
            String crafatarHatFace = "";
            String crafatarHatlessFace = "";
            String mojangHatFace = "";
            String mojangHatlessFace = "";

            // Try to get head from Minotar.

            mojangHatFace = chatHeadAPI.getHeadAsString(playerUUID, true, SkinSource.MOJANG);
            mojangHatlessFace = chatHeadAPI.getHeadAsString(playerUUID, false, SkinSource.MOJANG);

            boolean mojangHatFaceReceived = !Objects.equals(mojangHatFace, "");
            if (mojangHatFaceReceived) {
                hatFace = mojangHatFace;
                hatFaceNotFound = false;
            }

            boolean mojangHatlessFaceReceived = !Objects.equals(mojangHatlessFace, "");
            if (mojangHatlessFaceReceived) {
                hatlessFace = mojangHatlessFace;
                hatlessFaceNotFound = false;
            }

            // Try to get head from Crafatar.

            if (hatFaceNotFound) {
                crafatarHatFace = chatHeadAPI.getHeadAsString(playerUUID, true, SkinSource.CRAFATAR);

                boolean crafatarHatFaceReceived = !Objects.equals(crafatarHatFace, "");
                if (crafatarHatFaceReceived) {
                    hatFace = crafatarHatFace;
                    hatFaceNotFound = false;
                }
            }

            if (hatlessFaceNotFound) {
                crafatarHatlessFace = chatHeadAPI.getHeadAsString(playerUUID, false, SkinSource.CRAFATAR);

                boolean crafatarHatlessFaceReceived = !Objects.equals(crafatarHatlessFace, "");
                if (crafatarHatlessFaceReceived) {
                    hatlessFace = crafatarHatlessFace;
                    hatlessFaceNotFound = false;
                }
            }

            // Try to get head from Mojang.

            if (hatFaceNotFound) {
                minotarHatFace = chatHeadAPI.getHeadAsString(playerUUID, true, SkinSource.MINOTAR);

                boolean minotarHatFaceReceived = !Objects.equals(minotarHatFace, "");
                if (minotarHatFaceReceived) {
                    hatFace = minotarHatFace;
                    hatFaceNotFound = false;
                }
            }

            if (hatlessFaceNotFound) {
                minotarHatlessFace = chatHeadAPI.getHeadAsString(playerUUID, false, SkinSource.MINOTAR);

                boolean minotarHatlessFaceReceived = !Objects.equals(minotarHatlessFace, "");
                if (minotarHatlessFaceReceived) {
                    hatlessFace = minotarHatlessFace;
                    hatlessFaceNotFound = false;
                }
            }

            // Everything failed, assign Steve face to cache.

            if (hatFaceNotFound) {
                hatFace = steveFace;
            }

            if (hatlessFaceNotFound) {
                hatlessFace = steveFace;
            }

            chatFaceCaches.put(player, new ChatFaceCache(hatFace, hatlessFace));
        });
    }

    // On player logging out. Remove player from hatState map.
    @EventHandler public void onPlayerQuit(PlayerQuitEvent event) {
        chatFaceCaches.remove(event.getPlayer());
        hatState.remove(event.getPlayer());

        // Retrieve and remove the join timestamp.
        var joinTimestamp = joinTimestamps.get(event.getPlayer().getUniqueId());
        joinTimestamps.remove(event.getPlayer().getUniqueId());

        // Calculate the duration the player stayed on the server.
        var durationMinutes = 0L;
        if (joinTimestamp != null) {
            var durationMillis = System.currentTimeMillis() - joinTimestamp;
            durationMinutes = TimeUnit.MILLISECONDS.toMinutes(durationMillis);
        }

        // Set custom join message.
        var chatTimestamp = new java.text.SimpleDateFormat("HH:mm").format(new java.util.Date());
        var randomLeaveMessage = leaveMessages[(int) (Math.random() * leaveMessages.length)];
        event.setQuitMessage(ChatColor.YELLOW + "[" + chatTimestamp + "] " + event.getPlayer().getName() + " " + randomLeaveMessage + " pelattuaan " + durationMinutes + " min." + ChatColor.RESET);

        // Log leave messages.
        var logTimestamp = new java.text.SimpleDateFormat("dd.MM.yyyy, HH:mm").format(new java.util.Date());
        var logMessage = String.format("[%s] %s poistui palvelimelta.", logTimestamp, event.getPlayer().getName());
        log.info(logMessage);
    }

    // On server shutting down, log the event.
    public void onDisable() {

        // Log server shut down messages.
        var timestamp = new java.text.SimpleDateFormat("dd.MM.yyyy, HH:mm").format(new java.util.Date());
        var logMessage = String.format("[%s] Palvelin sammutetaan.", timestamp);

        // Log the shutdown message.
        log.info(logMessage);

        // Close the FileHandler.
        handler.close();
    }

    // Method to wrap emojis with colors
    private static String formatEmojisWithColor(String input, org.bukkit.ChatColor colorPrefix, org.bukkit.ChatColor colorReset) {
        // Replace emojis using EmojiParser
        return EmojiParser.parseFromUnicode(input, emoji -> colorPrefix + emoji.getEmoji().getUnicode() + colorReset);
    }

    // Cancel chat messages.
    @EventHandler public void onPlayerChat(AsyncPlayerChatEvent event) throws IOException {
        event.setCancelled(true);

        // Update player activity by calling custom player event. They chatted, they are no longer idle.
        PlayerIdleTracker.onCustomPlayerEvent(event.getPlayer().getUniqueId());

        String message = event.getMessage();

        // Replace all aliases with their emojis.
        String parsedMessage = EmojiParser.parseToUnicode(message);
        String replacedMessage = formatEmojisWithColor(parsedMessage, nameDefaultColor, resetColor);

        tryToGetLastEmoji(event.getPlayer(), replacedMessage);

        // Add timestamp in format [dd.MM.yyy, HH:mm:].
        var logTimestamp = new java.text.SimpleDateFormat("dd.MM.yyyy, HH:mm").format(new java.util.Date());

        // Simpler timestamp for chat.
        var timestamp = new java.text.SimpleDateFormat("HH:mm").format(new java.util.Date());

        // Color and format the timestamp.
        var formattedTime = ChatColor.DARK_GRAY + "[" + resetColor + timestamp + org.bukkit.ChatColor.DARK_GRAY + "]" + resetColor;

        // Set name to white.
        var player = event.getPlayer();
        var playerUUID = player.getUniqueId();
        String playerName = player.getName();

        // Special case for Tyhjyys world. They are all captains.
        if (player.getWorld().getName().equals("Tyhjyys")) {
            playerName = "Kapteeni " + playerName;
        }

        // Get chat head graphics.
        boolean isHatEnabled = getHatState(player);
        var cachedFacesForThisPlayer = chatFaceCaches.get(player);
        var formattedPlayerHead = cachedFacesForThisPlayer.getHatFace(isHatEnabled); // Uncomment after testing.

        // DEBUG: TEST BASE64 STRING
//        var playerUUID = player.getUniqueId();
//        var testingFace = Base64Gzip.decompressGzippedBase64(testFaceGzippedBase64);
//        var formattedPlayerHead =  chatHeadAPI.getHeadAsStringFromBase64(playerUUID, testingFace);
        // DEBUG: TEST BASE64 STRING

        String senderWorld = player.getWorld().getName();
        boolean canSendToAndReceiveFromTyhjyys = playerName.equals("Pilvinen");

        // Send the message to each player individually.
        for (Player recipient : server.getOnlinePlayers()) {

            // World-specific message sending rules:
            // Pilvinen can send and receive messages from and to players in Tyhjyys.
            // People in Tyhjyys can see each other's messages.
            // No one else can send messages to people in Tyhjyys or receive messages from them.

            // World-specific message sending rules:
            // 1. Pilvinen can send/receive messages to/from anyone.
            // 2. People in Tyhjyys can only send to others in Tyhjyys or to Pilvinen.
            // 3. No one outside Tyhjyys can send messages to people in Tyhjyys unless they are Pilvinen.
            var recipientWorld = recipient.getWorld().getName();
            boolean canSendMessage = canSendToAndReceiveFromTyhjyys || (
                    senderWorld.equals("Tyhjyys") && (recipientWorld.equals("Tyhjyys") || recipient.getName().equals("Pilvinen")) ||
                            !senderWorld.equals("Tyhjyys") && !recipientWorld.equals("Tyhjyys")
            );

            // Skip this recipient if not allowed by the special rules for Tyhjyys.
            if (!canSendMessage) {
                continue;
            }

            // Format the chat message nicer.
//            var formattedMessage = nameDefaultColor + replacedMessage;
            var formattedMessage = resetColor + replacedMessage;

            String formattedPlayerName = nameDefaultColor + playerName + resetColor;

            // Check if the player's name is mentioned in the message.
            var recipientName = recipient.getName();
            boolean recipientIsMentioned = replacedMessage.toLowerCase().contains(recipientName.toLowerCase());
            if (recipientIsMentioned) {

                // If the player's name is mentioned, highlight the sender's name for this recipient.
                formattedPlayerName = nameHighlightColor + playerName + resetColor;

                // Highlight all instances of the recipient's name in the message.
                formattedMessage = highLightMentionsInMessage(replacedMessage, recipientName);

                // Play sound effect for the recipient.
                recipient.playSound(recipient, "minecraft:block.note_block.bell", 0.8f, 1.0f);
            }

            // Put together the message.
            var chatMessage = String.format("%s %s %s: %s", formattedTime, formattedPlayerHead, formattedPlayerName, formattedMessage);

            recipient.sendMessage(chatMessage);

            // AFK handling.
            if (recipientIsMentioned) {
                boolean recipientIsAFK = PlayerIdleTracker.isPlayerIdle(playerUUID);
                // If the recipient is AFK, send message to original sender notifying of the status.
                if (recipientIsAFK) {
                    // Warning message for AFK recipient for bothering the owner.
                    if (recipientName.equals("Pilvinen")) {
                        player.sendMessage(ChatColor.RED + "[" + timestamp + "]" + " Hei, " + playerName + ", kamu. Pilvinen on AFK ja melko kiireinen! Mikäli et saa vastausta ja asiasi koskee palvelimen ylläpitoa, bugi-ilmoitusta, ehdotusta, tai muuta kiireellistä asiaa, varminta on jättää viesti Kuumaan Discord palvelimella sille soveltuvalle kanavalle." + ChatColor.RESET);
                    } else {
                        player.sendMessage(ChatColor.YELLOW + "[" + timestamp + "] " + recipientName + " on ollut AFK " + PlayerIdleTracker.getCurrentIdleSessionLengthInMinutes(playerUUID) + " min." + ChatColor.RESET);
                    }
                }
            }
        }

        // Log message.
        var logMessage = String.format("[%s] %s: %s", logTimestamp, playerName, message);
        log.info(logMessage);

    }

    private void tryToGetLastEmoji(Player player, String message) {
        // Get the last emoji in the message.
        Emotion lastEmotion = null;
        String lowerCaseMessage = message.toLowerCase();
        for (var emoji : emojis.keySet()) {
            if (lowerCaseMessage.contains(emoji.toLowerCase())) {
                lastEmotion = emojis.get(emoji);
            }
        }

        if (lastEmotion == null) {
            return;
        }

        // Get the type of the last emoji.
        Emoticons.iFeel(player, lastEmotion);
    }

    private String highLightMentionsInMessage(String message, String recipientName) {

        // Split the message by ,.:!-?_ and whitespace, use lookbehind so we don't remove the delimiters.
        var splitMessage = message.split("(?<=\\s|,|\\.|!|\\?|-|_)|(?=\\s+|,|\\.|!|\\?|-|_)");

        // Find all the indices of the recipient's name mentioned in the message.
        for (int i = 0; i < splitMessage.length; i++) {
            if (splitMessage[i].equalsIgnoreCase(recipientName)) {
                // Highlight the recipient's name for this recipient.
                splitMessage[i] = nameHighlightColor + splitMessage[i] + resetColor;
            }
        }
        // Put the message back together.
        var formattedMessage = String.join("", splitMessage);
        return formattedMessage;
    }


}
