package starandserpent.minecraft.criticalfixes;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.block.Block;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public final class DrowningFix implements Listener {

    private final JavaPlugin plugin;
    private Server server;

    // List of drowning players.
    private List<Player> drowningPlayers = new ArrayList<>();

    // Define forbidden blocks underwater.
    private List<Material> forbiddenBlocks = List.of(
            Material.ACACIA_FENCE_GATE,
            Material.BAMBOO_FENCE_GATE,
            Material.BIRCH_FENCE_GATE,
            Material.CHERRY_FENCE_GATE,
            Material.CRIMSON_FENCE_GATE,
            Material.DARK_OAK_FENCE_GATE,
            Material.JUNGLE_FENCE_GATE,
            Material.MANGROVE_FENCE_GATE,
            Material.OAK_FENCE_GATE,
            Material.SPRUCE_FENCE_GATE,
            Material.WARPED_FENCE_GATE
    );

    public DrowningFix(JavaPlugin plugin) {
        this.plugin = plugin;

        // Get server.
        server = plugin.getServer();

        // Register timer that runs every 20 ticks, i.e. once per second.
        server.getScheduler().runTaskTimer(plugin, task -> onDrown(), 0, 20);

        // Run once on login.
        checkDrowning();
    }


    @EventHandler public void onPlayerMove(PlayerMoveEvent event) {
        checkDrowning();
    }

    private void checkDrowning() {
        // Get all players.
        var players = server.getOnlinePlayers();

        // Loop all players
        for (Player player : players) {

            if (isDrowning(player)) {
                addDrowningPlayer(player);
            } else {
                removeDrowningPlayer(player);
            }

        }
    }

    private boolean isDrowning(Player player) {
        var playerHeadLocation = player.getEyeLocation();
        boolean isForbiddenBlock = isForbiddenBlockAtHead(playerHeadLocation);
        if (!isForbiddenBlock) return false;
        var blocksAroundHead = getBlocksAroundHead(player, 1);
        return containsWater(blocksAroundHead);
    }

    private void addDrowningPlayer(Player player) {
        // If isn't drowning already, make player drown.
        if (drowningPlayers.contains(player)) return;
        drowningPlayers.add(player);
    }

    private void removeDrowningPlayer(Player player) {
        drowningPlayers.remove(player);
    }

    private boolean isForbiddenBlockAtHead(Location playerHeadLocation) {
        var blockAtPlayerHead = playerHeadLocation.getBlock();
        var blockTypeAtHead = blockAtPlayerHead.getType();
        return forbiddenBlocks.contains(blockTypeAtHead);
    }

    private List<Block> getBlocksAroundHead(Player player, int radius) {
        List<Block> blocks = new ArrayList<>();
        Location location = player.getLocation();

        for (int x = -radius; x <= radius; x++) {
            for (int y = 0; y <= radius; y++) { // Check only upwards for head block
                for (int z = -radius; z <= radius; z++) {
                    // Offset the player's location by current coordinates
                    Block block = location.clone().add(x, y, z).getBlock();
                    blocks.add(block);
                }
            }
        }
        return blocks;
    }

    private boolean containsWater(List<Block> blocks) {
        for (Block block : blocks) {
            if (block.getType() == Material.WATER) {
                return true;
            }
        }
        return false;
    }

    private void onDrown() {
        // Loop all drowning players
        for (Player player : drowningPlayers) {
            // Drown player
            subtractAir(player);
        }
    }

    private void subtractAir(Player player) {
        // Set player's remaining air to 0, for show mostly since MC tries to reset it to full air.
        // But at least you get some indication that you're drowning.
        player.setRemainingAir(0);
        // Give player 2 damage.
        var location = player.getLocation();
        player.damage(2.0, DamageSource.builder(DamageType.DROWN).withDamageLocation(location).build());
    }

}
