package starandserpent.minecraft.criticalfixes;

import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class WeatherCommands implements CommandExecutor {

    private final JavaPlugin plugin;
    private Server server;

    public WeatherCommands(JavaPlugin plugin) {
        this.plugin = plugin;
        this.server = plugin.getServer();
    }

    @Override public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {

        // /sun
        if (command.getName().equalsIgnoreCase("sun")) {

            // Set weather to clear for current world of commandSender.
            var minuteInTicks = 20 * 60;

            Player player = (Player) commandSender;
            World world = player.getWorld();

            world.setClearWeatherDuration(minuteInTicks * 8);

            return true;
        }

        // /rain
        if (command.getName().equalsIgnoreCase("rain")) {

            Player player = (Player) commandSender;
            World world = player.getWorld();

            world.setStorm(true);

            return true;
        }

        // /thunder
        if (command.getName().equalsIgnoreCase("thunder")) {

            Player player = (Player) commandSender;
            World world = player.getWorld();

            world.setThundering(true);

            return true;
        }

        return false;
    }

}
