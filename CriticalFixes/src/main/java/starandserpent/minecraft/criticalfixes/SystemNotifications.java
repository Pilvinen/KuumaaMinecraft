package starandserpent.minecraft.criticalfixes;

import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class SystemNotifications implements CommandExecutor, Listener {

    private final JavaPlugin plugin;
    private Server server;

    private static final int spacingBetweenCharacters = 2;
//    private static final int spacingBetweenCharacters = 0; // DEBUG

    public SystemNotifications(JavaPlugin plugin) {
        this.plugin = plugin;
        this.server = plugin.getServer();
    }

    @Override public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (command.getName().equalsIgnoreCase("broadcast")
            || command.getName().equalsIgnoreCase("ilmoitus")
            || command.getName().equalsIgnoreCase("ilmoita")) {

            // Check permissions. Is console? Is op?
            if (!(sender instanceof ConsoleCommandSender)) {
                if (!sender.isOp()) {
                    sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
                    return true;
                }
            }

            if (args.length == 0) {
                return false;
            }

            publicBroadcast(server, sender, args);
            return true;
        }

        return false;
    }

    public static BroadcastBoxes createBroadcastBoxes(Server server, CommandSender sender, String[] args) {

        final String systemName = "Kuumaa";

        // Add timestamp in format [hh:mm:].
        String timestamp = new java.text.SimpleDateFormat("HH:mm").format(new java.util.Date());

        // Color and format the timestamp.
        TextComponent formattedTime = new TextComponent(ChatColor.DARK_GRAY + "[" + ChatColor.YELLOW + timestamp + ChatColor.DARK_GRAY + "]" + ChatColor.RESET);

        // Calculate total pixel width of each word in args and store them, together with the word.
        // This is used to calculate the width of the message box. Use FontWidths class.

        // Header placement.
        TextComponent topBox = new TextComponent(Symbols.SYSTEM_CONTAINER_TOP.literal);
        topBox.addExtra(new TextComponent(Symbols.getNeg(170)));
        topBox.addExtra(formattedTime);
        topBox.addExtra(new TextComponent(" "));
        topBox.addExtra(new TextComponent(ChatColor.DARK_GRAY + "[" + ChatColor.RESET));
        topBox.addExtra(new TextComponent(ChatColor.YELLOW + systemName));
        topBox.addExtra(new TextComponent(ChatColor.DARK_GRAY + "]" + ChatColor.RESET));

        // Middle system box element.
        TextComponent[] middleBoxes = getMiddleBoxes(args);

        // Bottom system box element.
        TextComponent bottomBox = new TextComponent(Symbols.SYSTEM_CONTAINER_BOTTOM.literal);

        return new BroadcastBoxes(topBox, middleBoxes, bottomBox);
    }


    public static void privateBroadcast(Server server, CommandSender sender, String[] message) {
        BroadcastBoxes boxes = createBroadcastBoxes(server, sender, message);

        sender.spigot().sendMessage(boxes.getTopBox());

        for(TextComponent middleBox : boxes.getMiddleBox()) {
            sender.spigot().sendMessage(middleBox);
        }

        sender.spigot().sendMessage(boxes.getBottomBox());
    }

    public static void publicBroadcast(Server server, CommandSender sender, String[] message) {

        // Join message array and create a new array and place everything in the zero index.
        String joinedMessage = String.join(" ", message);
        var mergedMessage = new String[] {joinedMessage};

        BroadcastBoxes boxes = createBroadcastBoxes(server, sender, mergedMessage);

        server.spigot().broadcast(boxes.getTopBox());

        for (TextComponent middleBox : boxes.getMiddleBox()) {
            server.spigot().broadcast(middleBox);
        }

        server.spigot().broadcast(boxes.getBottomBox());
    }

    private static TextComponent[] getMiddleBoxes(String[] args) {
        List<TextComponent> messages = new ArrayList<>();
//        final int maxLineWidthInPixels = 245;
        final int maxLineWidthInPixels = 460;
//        final int maxLineWidthInPixels = 243;

        for (String rawMessage : args) {

            // For this current line, create a new message middle component.
            TextComponent message = new TextComponent(Symbols.SYSTEM_CONTAINER_MIDDLE.literal);
            // Offset the message that follows by the width of the graphical element, negatively, so that
            // the text appears inside the graphical element instead of showing after it.
            message.addExtra(new TextComponent(Symbols.getNeg(250)));
            int currentWidth = 0;

            // Color codes should be included, but not counted as part of the width.
            boolean isColorCode = false;

            // Next we process each character in the message to calculate widths.
            int wordWidth = 0;
            StringBuilder word = new StringBuilder();
            for (char c : rawMessage.toCharArray()) {

                // Check if the current character starts a color code. Its width is not counted.
                if (c == '§') {
                    isColorCode = true;
                // We go here if the PREVIOUS character was a color code. So this width is not counted either.
                } else if (isColorCode) {
                    isColorCode = false; // And reset the flag.
                // Otherwise we do count the width of the character.
                } else {
                    wordWidth += FontWidths.getWidth(c) + spacingBetweenCharacters;
                }

                // And we always include the character in the word.
                word.append(c);

                // Space or last character in the message ends a word.
                boolean isSpace = c == ' ';
                boolean isLastCharacter = c == rawMessage.charAt(rawMessage.length() - 1);
                boolean widthIsOverflowing = currentWidth + wordWidth > maxLineWidthInPixels;
                if (isSpace || isLastCharacter || widthIsOverflowing) {

                    // Create new middle box element if the current message is too wide.
                    if (widthIsOverflowing) {
                        messages.add(message);
                        message = new TextComponent(Symbols.SYSTEM_CONTAINER_MIDDLE.literal);
                        message.addExtra(new TextComponent(Symbols.getNeg(250)));
                        currentWidth = 0;
                    }

                    // Add the word to the message.
                    message.addExtra(new TextComponent(word.toString()));
//                        currentWidth += wordWidth + FontWidths.getWidth(' ') + spacingBetweenCharacters;
                    currentWidth += wordWidth + spacingBetweenCharacters;

                    word = new StringBuilder();
                    wordWidth = 0;
                }
            }
            if (!message.getExtra().isEmpty()) {
                messages.add(message);
            }
        }
        return messages.toArray(new TextComponent[0]);
    }

}
