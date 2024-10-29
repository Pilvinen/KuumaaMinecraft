package starandserpent.minecraft.criticalfixes;

import org.bukkit.Server;
import org.bukkit.Tag;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class PlayerDamageHandler implements Listener {

    private final JavaPlugin plugin;
    private Server server;

    public PlayerDamageHandler(JavaPlugin plugin) {
        this.plugin = plugin;
        server = plugin.getServer();
    }

    // When player takes damage.
    @EventHandler public void onPlayerTakingDamageFromEntity(EntityDamageByEntityEvent event) {

        var victim = event.getEntity();
        var attacker = event.getDamager();
        var attackerType = attacker.getType();

        // Victim is a player.
        if (!(victim instanceof Player player)) {
            return;
        }

        // Attacker is not an arrow.
        if (Tag.ENTITY_TYPES_ARROWS.isTagged(attackerType)) {
            return;
        }

        // Damage is large.
        var damageAmount = event.getDamage();
        if (damageAmount > 5) {
            EffectsLibrary.showDamageShake(player, 0.4f, 1.0f, 0.3f);
        } else if (damageAmount > 4) {
            EffectsLibrary.showDamageShake(player, 0.4f, 1.0f, 0.3f);
        } else if (damageAmount > 3) {
            EffectsLibrary.showDamageShake(player, 0.3f, 0.9f, 0.3f);
        } else if (damageAmount > 2) {
            EffectsLibrary.showDamageShake(player, 0.2f, 0.9f, 0.2f);
        } else if (damageAmount > 1) {
            EffectsLibrary.showDamageShake(player, 0.1f, 0.8f, 0.2f);
        }

    }

}
