package starandserpent.minecraft.criticalfixes;

import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

// This is a plugin which adds commands to the game for setting warp locations and stores them
// on the disc as yaml files. The plugin also adds a command to teleport to the warp location.
public class Warp implements CommandExecutor {

    private final JavaPlugin plugin;
    private Server server;

    // Warp locations collection
    private List<WarpLocation> warpLocations = new ArrayList<>();

    public Warp(JavaPlugin plugin) {

        this.plugin = plugin;

        // Get server.
        server = plugin.getServer();
    }

    // Minecraft command to set warp location to current location of the calling player with OP permission.
    // The warp location is stored in a yaml file on the disc.
    // The command is /setwarp <warpname>
    // The yaml file is stored in the plugin data folder.
    // The yaml file is named warps.yml

    // warp <playername> <warpname> command to teleport a player to a warp location.
    @Override public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        // Check for permissions.
        if (!sender.isOp()) {
            sender.sendMessage("You need to be OP to use this command.");
            return false;
        }

        // /setwarp <warpname>
        if (command.getName().equalsIgnoreCase("setwarp")) {
            return setWarp(sender, command, label, args);
        }

        // /warp <playername> <warpname> and /warp <warpname>
        if (command.getName().equalsIgnoreCase("warp")) {
            return warp(sender, command, label, args);
        }

        // /warplist
        if (command.getName().equalsIgnoreCase("warplist")) {
            return warpList(sender, command, label, args);
        }

        // /deletewarp <warpname>
        if (command.getName().equalsIgnoreCase("deletewarp")) {
            return deleteWarp(sender, command, label, args);
        }

        return false;
    }

    private boolean setWarp(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) {
            var warpName = args[0];
            var player = (Player) sender;
            var location = player.getLocation();
            var worldName = location.getWorld().getName();
            var x = location.getX();
            var y = location.getY();
            var z = location.getZ();
            var yaw = location.getYaw();
            var pitch = location.getPitch();
            var warpLocation = new WarpLocation(warpName, worldName, x, y, z, yaw, pitch);
            warpLocations.add(warpLocation);
            warpLocation.save(warpName);
            sender.sendMessage("Warp " + warpName + " set to your location.");
            return true;
        }

        // Send usage information.
        sender.sendMessage("Usage: /setwarp <warpname>");
        return false;
    }

    // deletewarp <warpname> command to delete a warp location.
    private boolean deleteWarp(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) {
            var warpName = args[0];
            var warpLocation = getWarpLocation(warpName);
            if (warpLocation == null) {
                sender.sendMessage("Warp " + warpName + " not found.");
                return false;
            }
            File file = new File("plugins/CriticalFixes/Warps/"+ warpName +".yml");
            if (!file.exists()) {
                sender.sendMessage("Warp file for " + warpName + " does not exist.");
                return false;
            }
            boolean deleteSuccess = file.delete();
            if (!deleteSuccess) {
                sender.sendMessage("Failed to delete warp " + warpName + ".");
                return false;
            }
            warpLocations.remove(warpLocation);
            sender.sendMessage("Warp " + warpName + " deleted.");
            return true;
        }

        // Send usage information.
        sender.sendMessage("Usage: /deletewarp <warpname>");
        return false;
    }


    private boolean warp(CommandSender sender, Command command, String label, String[] args) {

        if (args.length == 2) {
            var playerName = args[0];
            var warpName = args[1];
            var player = server.getPlayer(playerName);

            if (player == null) {
                sender.sendMessage("Player " + playerName + " not found.");
                return false;
            }

            var warpLocation = getWarpLocation(warpName);
            if (warpLocation == null) {
                sender.sendMessage("Warp " + warpName + " not found.");
                return false;
            }

            // Everything went OK, teleport player to warp location.
            player.teleport(warpLocation.getLocation());
            sender.sendMessage("Player " + playerName + " teleported to warp " + warpName + ".");

            return true;
        }

        // warp <warpname> (ie. assume the player is the sender)

        if (args.length == 1) {
            var warpName = args[0];
            // Get player from sender
            var player = (Player) sender;
            var playerName = player.getName();

            var warpLocation = getWarpLocation(warpName);
            if (warpLocation == null) {
                sender.sendMessage("Warp " + warpName + " not found.");
                return false;
            }

            player.teleport(warpLocation.getLocation());
            sender.sendMessage("Teleporting you to warp " + warpName + ".");

            return true;
        }

        sender.sendMessage("Usage: /warp <warpname>");
        sender.sendMessage("Usage: /warp <playername> <warpname>");
        return false;
    }


    // warplist, list all warps
    private boolean warpList(CommandSender sender, Command command, String label, String[] args) {
        // If no warps, return.
        if (warpLocations.isEmpty()) {
            sender.sendMessage("No warps found.");
            return true;
        }

        sender.sendMessage("Warp: ");
        for (var warpLocation : warpLocations) {
            sender.sendMessage(warpLocation.getWarpName());
        }
        return true;
    }

    // Return the warp location with the name passed as parameter from the collection of warp locations.
    private WarpLocation getWarpLocation(String warpName) {
        for (var warpLocation : warpLocations) {
            if (warpLocation.getWarpName().equals(warpName)) {
                return warpLocation;
            }
        }
        return null;
    }

    // On enable load all warps from the yaml files under CriticalFixes/Warps/ and store them in memory
    // in a collection of warp locations. Wrap in try-catch to handle exceptions.
    public void loadWarps() {

        // Create (write on disc) folders "CriticalFixes/Warps/" under the plugins folder if they doesn't exist yet.
        var warpsFolder = new File("plugins/CriticalFixes/Warps/");
        if (!warpsFolder.exists()) {
            warpsFolder.mkdirs();
        }

        // Failed to create folders.
        if (!warpsFolder.exists()) {
            return;
        }

        for (var file : Objects.requireNonNull(warpsFolder.listFiles())) {
            if (file.getName().endsWith(".yml")) {
                try {
                    var warpLocation = WarpLocation.load(file);
                    warpLocations.add(warpLocation);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
