package starandserpent.minecraft.criticalfixes;

import org.bukkit.Server;
import org.bukkit.block.data.type.TNT;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class BetterTNT implements Listener {

    private final JavaPlugin plugin;
    private Server server;

    public BetterTNT(JavaPlugin plugin) {
        this.plugin = plugin;
        this.server = plugin.getServer();
    }

    // On explosion event, cancel the explosion and create a bigger one.

    @EventHandler void onExplode(EntityExplodeEvent event) {
        var location = event.getLocation();
        var world = location.getWorld();
        var explodingEntity = event.getEntity();

        // Only in Kuumaa
        if (!world.getName().equals("Kuumaa")) {
            return;
        }

        // If exploding entity is TNT, create a bigger explosion.
        if (explodingEntity.getType() != EntityType.TNT &&
            explodingEntity.getType() != EntityType.TNT_MINECART) {
            return;
        }

        // Cancel event
        event.setCancelled(true);

        var power = 5;
        var radius = power * 2;
        world.createExplosion(location, radius, true, true);
    }

}
