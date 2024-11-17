package starandserpent.minecraft.criticalfixes;

import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class MobCheck implements CommandExecutor {

    private final JavaPlugin plugin;
    private Server server;

    public MobCheck(JavaPlugin plugin) {
        this.plugin = plugin;
        this.server = plugin.getServer();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof ConsoleCommandSender)) {
            if (!sender.isOp()) {
                sender.sendMessage("You need to be OP to use this command.");
                return false;
            }
        }

        if (command.getName().equalsIgnoreCase("mobcheck")) {
            return checkMobs(sender, command, label, args);
        }

        return false;
    }

    private boolean checkMobs(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 0) {
            sender.sendMessage("Usage: /mobcheck");
            return false;
        }

        String kuumaaString = "Kuumaa";
        World kuumaaWorld = server.getWorld(kuumaaString);
        Map<EntityType, Integer> entityCountKuumaa = countEntities(kuumaaWorld);

        String kuumaaNetherString = "Kuumaa_nether";
        World kuumaaNetherWorld = server.getWorld(kuumaaNetherString);
        Map<EntityType, Integer> entityCountKuumaaNether = countEntities(kuumaaNetherWorld);

        sender.sendMessage("Entities in world " + kuumaaString + ":");
        entityCountKuumaa.entrySet().stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                .forEach(entry -> sender.sendMessage(formatEntityName(entry.getKey()) + ": " + entry.getValue()));

        sender.sendMessage("Entities in world " + kuumaaNetherString + ":");
        entityCountKuumaaNether.entrySet().stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                .forEach(entry -> sender.sendMessage(formatEntityName(entry.getKey()) + ": " + entry.getValue()));

        return true;
    }

    private Map<EntityType, Integer> countEntities(World world) {
        Map<EntityType, Integer> entityCount = new HashMap<>();
        for (Entity entity : world.getEntities()) {
            EntityType type = entity.getType();
            entityCount.put(type, entityCount.getOrDefault(type, 0) + 1);
        }
        return entityCount;
    }

    private String formatEntityName(EntityType type) {
        String name = type.name().toLowerCase().replace('_', ' ');
        return Character.toUpperCase(name.charAt(0)) + name.substring(1);
    }
}