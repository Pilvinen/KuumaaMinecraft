package starandserpent.minecraft.criticalfixes;

import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.TranslatableComponent;
import org.bukkit.GameMode;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.Console;

public class GamemodeFixes implements CommandExecutor {

    private final JavaPlugin plugin;
    private Server server;

    public GamemodeFixes(JavaPlugin plugin) {
        this.plugin = plugin;

        // Get server.
        server = plugin.getServer();
    }


    @Override public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        // Check for permissions and validate.

        if (sender == null) {
            return false;
        }

        if (args.length < 1 && (sender instanceof ConsoleCommandSender)) {
            sender.spigot().sendMessage(new TextComponent("If you run the command from console you need to provide a player name."));
            return false;
        }

        if (args.length > 1) {
            sender.spigot().sendMessage(new TextComponent("Too many arguments."));
            return false;
        }

        if (!(sender instanceof ConsoleCommandSender) && !sender.isOp()) {
            sender.spigot().sendMessage(new TextComponent(new TranslatableComponent("commands.help.failed")));
            return false;
        }

        // Commands.

        // /creative and /creative <player>
        if (command.getName().equalsIgnoreCase("creative")) {
            GameMode gameMode = GameMode.CREATIVE;
            return setGamemode(gameMode, sender, args);
        }

        // /survival and /survival <player>
        if (command.getName().equalsIgnoreCase("survival")) {
            GameMode gameMode = GameMode.SURVIVAL;
            return setGamemode(gameMode, sender, args);
        }

        // /adventure and /adventure <player>
        if (command.getName().equalsIgnoreCase("adventure")) {
            GameMode gameMode = GameMode.ADVENTURE;
            return setGamemode(gameMode, sender, args);
        }

        // /spectator and /spectator <player>
        if (command.getName().equalsIgnoreCase("spectator")) {
            GameMode gameMode = GameMode.SPECTATOR;
            return setGamemode(gameMode, sender, args);
        }

        return false;
    }

    private boolean setGamemode(GameMode gameMode, CommandSender sender, String[] args) {

        Player player = getPlayerOrNull(sender, args);

        // Something went wrong, we don't have target for command, return false.
        if (player == null) {
            sender.spigot().sendMessage(new TextComponent("Player not found."));
            return false;
        }

        // Everything is fine, set the game mode.
        player.setGameMode(gameMode);

        // Inform the sender or sender + player depending on the context.
        if (player == sender) { // Used to command on yourself.
            TranslatableComponent successSelf = new TranslatableComponent("gameMode.changed", gameMode);
            sender.spigot().sendMessage(successSelf);
        } else { // Used the command on someone else.
            var successOther = new TextComponent(new TranslatableComponent("commands.gamemode.success.other", gameMode.toString()));
            sender.spigot().sendMessage(successOther);

            var successSelf = new TextComponent(new TranslatableComponent("gameMode.changed", gameMode.toString()));
            player.spigot().sendMessage(successSelf);
        }

        return true;
    }

    private Player getPlayerOrNull(CommandSender sender, String[] args) {

        // Null by default.
        Player player = null;

        // No arguments were provided, use command sender as the target.
        if (args.length == 0) {
            player = server.getPlayer(sender.getName());

        // Argument was provided, use the argument as the target.
        } else if (args.length == 1) {
            player = server.getPlayer(args[0]);
        }

        return player;
    }

}
