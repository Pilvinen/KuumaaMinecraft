package starandserpent.minecraft.criticalfixes;

import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.Tag;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import javax.swing.text.html.HTML;

import static org.yaml.snakeyaml.tokens.Token.ID.Tag;

public class DamageEffects implements Listener {
    private final JavaPlugin plugin;
    private Server server;

    public DamageEffects(JavaPlugin plugin) {
        this.plugin = plugin;
        this.server = plugin.getServer();
    }

    // On damage event, apply effects
    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {

        // If player
        if (event.getEntity() instanceof org.bukkit.entity.Player) {
            org.bukkit.entity.Player player = (org.bukkit.entity.Player) event.getEntity();

            // If player is damaged by fall
            if (event.getCause() == EntityDamageEvent.DamageCause.FALL) {
                // And the fall damage is greater or equal to 5.
                if (event.getDamage() >= 5) {

                    // Show blood particles.

                    // Base amount is 5. Amount increases by 1 for every 1 damage.
                    var amount = (int) Math.floor(event.getDamage());
                    var spread = 0.5f;
                    var speed = 1f;
                    EffectsLibrary.showBloodParticles(player, spread, amount, speed);

                    // Bleed on ground 1 time for every 5 damage.
                    int timesToBleed = (int) Math.floor(event.getDamage() / 5);
                    for (int i = 0; i < timesToBleed; i++) {
                        EffectsLibrary.bleedOnBlocks(player);
                    }
                }
            }

            // If player is damaged by a sword or axe or trident or mace or arrow
            // or explosion or pickaxe or shovel or hoe or shears.

            var attacker = event.getDamageSource().getCausingEntity();
            if (attacker instanceof org.bukkit.entity.LivingEntity) {
                boolean bleedingAttack = false;

                LivingEntity livingAttacker = (org.bukkit.entity.LivingEntity) attacker;
                Material attackerWeapon = livingAttacker.getEquipment().getItemInMainHand().getType();

                if (org.bukkit.Tag.ITEMS_SWORDS.isTagged(attackerWeapon) ||
                        org.bukkit.Tag.ITEMS_AXES.isTagged(attackerWeapon) ||
                        org.bukkit.Tag.ITEMS_ARROWS.isTagged(attackerWeapon) ||
                        org.bukkit.Tag.ITEMS_PICKAXES.isTagged(attackerWeapon) ||
                        org.bukkit.Tag.ITEMS_SHOVELS.isTagged(attackerWeapon) ||
                        org.bukkit.Tag.ITEMS_HOES.isTagged(attackerWeapon) ||
                        attackerWeapon == Material.SHEARS ||
                        attackerWeapon == Material.TRIDENT ||
                        attackerWeapon == Material.MACE) {
                    bleedingAttack = true;
                }

                // Is explosion?
                if (event.getCause() == EntityDamageEvent.DamageCause.BLOCK_EXPLOSION ||
                        event.getCause() == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION) {
                    bleedingAttack = true;
                }

                if (bleedingAttack) {
                    // Show blood particles.

                    // Calculate amount of particles based on damage.
                    var amount = (int) Math.floor(event.getDamage());
                    var spread = 0.2f;
                    var speed = 1.0f;
                    EffectsLibrary.showBloodParticles(player, spread, amount, speed);

                    // Bleed on ground. 1 time for every 5 damage.
                    int timesToBleed = (int) Math.floor(event.getDamage() / 5);
                    for (int i = 0; i < timesToBleed; i++) {
                        EffectsLibrary.bleedOnBlocks(player);
                    }
                }
            }
        }
    }

}
