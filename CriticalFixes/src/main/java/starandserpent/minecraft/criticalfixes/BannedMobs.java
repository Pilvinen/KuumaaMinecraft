package starandserpent.minecraft.criticalfixes;

import org.bukkit.Server;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;

public class BannedMobs implements Listener {

    private final JavaPlugin plugin;
    private Server server;
    private final Random random = new Random();

    // Banned mobs with their respective ban probabilities (in percentage).
    Map<EntityType, Integer> bannedMobs = new HashMap<>() {{
        put(EntityType.PHANTOM, 100);
        put(EntityType.WANDERING_TRADER, 10);
    }};

    public BannedMobs(JavaPlugin plugin) {
        this.plugin = plugin;
        server = plugin.getServer();
    }

    // Prevent banned mobs from spawning based on their ban probability.
    @EventHandler
    void onMobSpawn(CreatureSpawnEvent event) {
        var entity = event.getEntity();
        var entityType = entity.getType();
        if (bannedMobs.containsKey(entityType)) {
            int probability = bannedMobs.get(entityType);
            if (random.nextInt(100) < probability) {
                event.setCancelled(true);
            }
        }
    }

}
