package starandserpent.minecraft.criticalfixes;

import org.bukkit.Server;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class SnowballImprovements implements Listener {

    private final JavaPlugin plugin;
    private Server server;

    public SnowballImprovements(JavaPlugin plugin) {
        this.plugin = plugin;
        server = plugin.getServer();
    }

    // On snowball hit
    @EventHandler public void onSnowballHit(ProjectileHitEvent event) {

        // Check if the projectile is a snowball.
        if (event.getEntity().getType() != EntityType.SNOWBALL) {
            return;
        }

        // Check if the hit target was a player
        var hitEntity = event.getHitEntity();
        if (hitEntity == null) {
            return;
        }

        if (hitEntity.getType() != EntityType.PLAYER) {
            return;
        }

        var player = (Player) hitEntity;

        // Show freezing particle effect on the hitEntity.
        int currentFreeze = player.getFreezeTicks();
        if (currentFreeze >= player.getMaxFreezeTicks()) {
            return;
        }

        int moreFreeze = 20;
        player.setFreezeTicks(currentFreeze + moreFreeze);
        // Play snow break sound at location.
        var world = player.getWorld();
        if (world == null) {
            return;
        }

        world.playSound(player.getLocation(), Sound.BLOCK_SNOW_HIT, 1.0F, 1.0F);
    }

}
