package starandserpent.minecraft.criticalfixes;

import net.minso.chathead.API.ChatHeadAPI;
import net.minso.chathead.API.SkinSource;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
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
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;

public class KuuChat implements Listener {
    private final JavaPlugin plugin;
    private Server server;

    // Logger.
    private Logger log = Logger.getLogger("KuuChat");
    private Handler handler;


    private ChatColor nameDefaultColor = ChatColor.WHITE;
    private ChatColor nameHighlightColor = ChatColor.YELLOW;
    private ChatColor resetColor = ChatColor.GRAY;
    private ChatHeadAPI chatHeadAPI;
    private static HashMap<Player, Boolean> hatState = new HashMap<>();
    private HashMap<Player, ChatFaceCache> chatFaceCaches = new HashMap<>();
    private String steveFace = "";

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
    @EventHandler public void onPlayerJoin(PlayerJoinEvent event) {
        cacheChatFacesForPlayer(event);

        // Log join messages.
        var logTimestamp = new java.text.SimpleDateFormat("dd.MM.yyyy, HH:mm").format(new java.util.Date());
        var logMessage = String.format("[%s] %s liittyi palvelimelle.", logTimestamp, event.getPlayer().getName());
        log.info(logMessage);
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

    // Cancel chat messages.
    @EventHandler public void onPlayerChat(AsyncPlayerChatEvent event) throws IOException {
        event.setCancelled(true);

        var message = event.getMessage();

        tryToGetLastEmoji(event.getPlayer(), message);

        // Add timestamp in format [dd.MM.yyy, HH:mm:].
        var logTimestamp = new java.text.SimpleDateFormat("dd.MM.yyyy, HH:mm").format(new java.util.Date());

        // Simpler timestamp for chat.
        var timestamp = new java.text.SimpleDateFormat("HH:mm").format(new java.util.Date());

        // Color and format the timestamp.
        var formattedTime = ChatColor.DARK_GRAY + "[" + resetColor + timestamp + org.bukkit.ChatColor.DARK_GRAY + "]" + resetColor;

        // Set name to white.
        var player = event.getPlayer();
        var playerName = player.getName();

        // Get chat head graphics.
        boolean isHatEnabled = getHatState(player);
        var cachedFacesForThisPlayer = chatFaceCaches.get(player);
        var formattedPlayerHead = cachedFacesForThisPlayer.getHatFace(isHatEnabled); // Uncomment after testing.

        // DEBUG: TEST BASE64 STRING
//        var playerUUID = player.getUniqueId();
//        var testingFace = Base64Gzip.decompressGzippedBase64(testFaceGzippedBase64);
//        var formattedPlayerHead =  chatHeadAPI.getHeadAsStringFromBase64(playerUUID, testingFace);
        // DEBUG: TEST BASE64 STRING

        // Send the message to each player individually.
        for (Player recipient : server.getOnlinePlayers()) {

            // Format the chat message nicer.
            var formattedMessage = resetColor + message;

            var formattedPlayerName = nameDefaultColor + playerName + resetColor;

            // Check if the player's name is mentioned in the message.
            var recipientName = recipient.getName();
            boolean recipientIsMentioned = message.toLowerCase().contains(recipientName.toLowerCase());
            if (recipientIsMentioned) {
                // If the player's name is mentioned, highlight the sender's name for this recipient.
                formattedPlayerName = nameHighlightColor + playerName + resetColor;

                // Highlight all instances of the recipient's name in the message.
                formattedMessage = highLightMentionsInMessage(message, recipientName);

                // Play sound effect for the recipient.
                recipient.playSound(recipient, "minecraft:block.note_block.bell", 0.8f, 1.0f);
            }

            // Put together the message.
            var chatMessage = String.format("%s %s %s: %s", formattedTime, formattedPlayerHead, formattedPlayerName, formattedMessage);

            recipient.sendMessage(chatMessage);

            // Log message.
            var logMessage = String.format("[%s] %s: %s", logTimestamp, playerName, message);
            log.info(logMessage);
        }

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
