package starandserpent.minecraft.criticalfixes;

import org.bukkit.Server;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityKnockbackEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class DamageKnockbackFix implements Listener {

    private final JavaPlugin plugin;
    private final Server server;

    public DamageKnockbackFix(JavaPlugin plugin) {
        this.plugin = plugin;
        server = plugin.getServer();
    }

    @EventHandler public void onKnockBack(EntityKnockbackEvent event) {
        // Get damage source.
        event.setCancelled(true);

        EntityKnockbackEvent.KnockbackCause knockbackCause = event.getCause();
        if (knockbackCause == EntityKnockbackEvent.KnockbackCause.DAMAGE
        || knockbackCause == EntityKnockbackEvent.KnockbackCause.UNKNOWN) {

            // Cancel knockback from damage and unknown reason.
            event.setCancelled(true);
        }
    }

}
