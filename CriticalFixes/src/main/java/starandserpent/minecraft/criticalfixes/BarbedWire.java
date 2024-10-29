package starandserpent.minecraft.criticalfixes;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public class BarbedWire implements Listener {

    private final JavaPlugin plugin;
    private final Server server;

    private final Material barbedWireMaterial = Material.COBWEB;

    public BarbedWire(JavaPlugin plugin) {
        this.plugin = plugin;
        server = plugin.getServer();
    }

    // When moving through barbed wire, player takes damage.
    @EventHandler void onPlayerMoveThroughBarbedWire(PlayerMoveEvent event) {
        var fromLocation = event.getFrom();
        var fromBlock = fromLocation.getBlock();
        var fromBlockType = fromBlock.getType();
        var fromHeadLocation = fromLocation.clone().add(0, 1, 0);
        var fromHeadBlock = fromHeadLocation.getBlock();
        var fromHeadBlockType = fromHeadBlock.getType();

        var toLocation = event.getTo();
        if (toLocation == null) {
            return;
        }

        var toBlock = toLocation.getBlock();
        var toBlockType = toBlock.getType();
        var toHeadLocation = toLocation.clone().add(0, 1, 0);
        var toHeadBlock = toHeadLocation.getBlock();
        var toHeadBlockType = toHeadBlock.getType();

        // Check if x,y,z position has changed between to and from - ignoring yaw and pitch.
        if (toLocation.getX() == fromLocation.getX()
                && toLocation.getY() == fromLocation.getY()
                && toLocation.getZ() == fromLocation.getZ()) {
            return;
        }

        // Get the movement trajectory of the player.
        var player = event.getPlayer();
        var isGoingToHitBarbedWire = checkForBarbedWire(player, event);

        // Player is walking through barbed wire.
        if (barbedWireMaterial.equals(fromBlockType)
            || barbedWireMaterial.equals(toBlockType)
            || barbedWireMaterial.equals(fromHeadBlockType)
            || barbedWireMaterial.equals(toHeadBlockType)
            || isGoingToHitBarbedWire) {

            var playerGameMode = player.getGameMode();

            // Player needs to be in survival mode.
            if (playerGameMode != GameMode.SURVIVAL) {
                return;
            }

            // Player takes damage.
            var damageLocation = player.getLocation();
            event.getPlayer().damage(1.0, DamageSource.builder(DamageType.GENERIC).withDamageLocation(damageLocation).build());

            // Show blood particles.
            var amount = 5;
            var spread = 0.2f;
            var speed = 1.0f;
            var livingEntity = event.getPlayer();
            EffectsLibrary.showBloodParticles(livingEntity, spread, amount, speed);

            // Bleed on ground.
            EffectsLibrary.bleedOnBlocks(livingEntity);
        }
    }

    private final double howCloseYouHaveToBeToBeConsideredHittingTheBarbedWire = 0.5;

    public boolean checkForBarbedWire(Player player, PlayerMoveEvent event) {
        // Check that the event's from and to locations are not null
        if (event.getTo() == null) {
            return false;
        }

        // Get the player's movement direction and normalize it to get the unit vector
        var to = event.getTo().clone(); // Need to clone, or we'll end up modifying the player's location!
        var from = event.getFrom().clone();  // Need to clone, or we'll end up modifying the player's location!

        Vector unitVector = to.subtract(from).toVector().normalize();

        // Multiply the unit vector by 1 to get the prediction vector
        Vector predictionVector = unitVector.multiply(howCloseYouHaveToBeToBeConsideredHittingTheBarbedWire);

        // Add the prediction vector to the player's current location to get the predicted location
        var playerLocation = player.getLocation();
        Location predictedLocation = playerLocation.add(predictionVector);

        // Check if the block at the predicted location is a barbed wire block
        if (predictedLocation.getBlock().getType() == barbedWireMaterial) {
            // The player is predicted to hit barbed wire.
            return true;
        } else {
            // The player is not predicted to hit barbed wire.
            return false;
        }
    }

}
