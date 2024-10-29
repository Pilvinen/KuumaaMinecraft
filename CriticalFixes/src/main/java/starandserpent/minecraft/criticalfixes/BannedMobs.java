package starandserpent.minecraft.criticalfixes;

import org.bukkit.Server;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.HashSet;

public class BannedMobs implements Listener {

    private final JavaPlugin plugin;
    private Server server;

    // Banned mobs.
    HashSet<EntityType> bannedMobs = new HashSet<>() {{
        add(EntityType.PHANTOM);
    }};

    public BannedMobs(JavaPlugin plugin) {
        this.plugin = plugin;
        server = plugin.getServer();
    }

    // Prevent banned mobs from spawning.
    @EventHandler void onMobSpawn(CreatureSpawnEvent event) {
        var entity = event.getEntity();
        var entityType = entity.getType();
        if (bannedMobs.contains(entityType)) {
            event.setCancelled(true);
        }
    }

}
