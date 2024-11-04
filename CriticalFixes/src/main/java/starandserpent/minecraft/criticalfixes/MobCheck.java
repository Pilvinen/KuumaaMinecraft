package starandserpent.minecraft.criticalfixes;

import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

// This is a plugin which adds commands to the game for setting warp locations and stores them
// on the disc as yaml files. The plugin also adds a command to teleport to the warp location.
public class MobCheck implements CommandExecutor {

    private final JavaPlugin plugin;
    private Server server;

    public MobCheck(JavaPlugin plugin) {

        this.plugin = plugin;

        // Get server.
        server = plugin.getServer();
    }

    // Minecraft command to check amount of spawned mobs
    // The command is /checkmobs
    @Override public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        // Check for permissions.
        if (!(sender instanceof ConsoleCommandSender)) {
            if (!sender.isOp()) {
                sender.sendMessage("You need to be OP to use this command.");
                return false;
            }
        }

        // /checkmobs
        if (command.getName().equalsIgnoreCase("mobcheck")) {
            return checkMobs(sender, command, label, args);
        }

        return false;
    }

    private boolean checkMobs(CommandSender sender, Command command, String label, String[] args) {

        // Tried to pass arguments to the command.
        if (args.length != 0) {
            // Send usage information.
            sender.sendMessage("Usage: /mobcheck");
            return false;
        }

        // Get the amount of mobs on the whole server for "Kuumaa" world.
        String kuumaaString = "Kuumaa";
        World kuumaaWorld = server.getWorld(kuumaaString);
        int mobCountKuumaa = kuumaaWorld.getEntities().size();
        // Get the amount of falling blocks.
        int fallingBlockCountKuumaa = kuumaaWorld.getEntitiesByClass(FallingBlock.class).size();

        String kuumaaNetherString = "Kuumaa_nether";
        World kuumaaNetherWorld = server.getWorld(kuumaaNetherString);
        int mobCountKuumaaNether = kuumaaNetherWorld.getEntities().size();
        // Get the amount of falling blocks.
        int fallingBlockCountKuumaaNether = kuumaaNetherWorld.getEntitiesByClass(FallingBlock.class).size();

        // Print the amount of mobs on the whole server.
        sender.sendMessage("Amount of entities in world " + kuumaaString + ": " + mobCountKuumaa);
        sender.sendMessage("Amount of falling blocks in world " + kuumaaString + ": " + fallingBlockCountKuumaa);
        sender.sendMessage("Amount of entities in world " + kuumaaNetherString + ": " + mobCountKuumaaNether);
        sender.sendMessage("Amount of falling blocks in world " + kuumaaNetherString + ": " + fallingBlockCountKuumaaNether);

        return true;
    }

}
