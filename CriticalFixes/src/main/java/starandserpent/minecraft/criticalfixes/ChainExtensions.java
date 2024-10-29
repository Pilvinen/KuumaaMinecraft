package starandserpent.minecraft.criticalfixes;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class ChainExtensions implements Listener {

    private final JavaPlugin plugin;
    private Server server;

    private final List<Material> cauldrons = List.of(
        Material.CAULDRON,
        Material.LAVA_CAULDRON,
        Material.WATER_CAULDRON,
        Material.POWDER_SNOW_CAULDRON
    );

    private final Material chainHarness = Material.GRAY_STAINED_GLASS_PANE;

    public ChainExtensions(JavaPlugin plugin) {
        this.plugin = plugin;
        this.server = plugin.getServer();
    }

    // On placing a chain or cauldron.
    @EventHandler public void onBlockPlace(BlockPlaceEvent event) {
        var placedBlock = event.getBlock();
        var placedBlockType = placedBlock.getType();

        // Chain is placed and connects with cauldron below.
        boolean isChainPlaced = placedBlockType == Material.CHAIN;
        if (isChainPlaced) {
            handlePlacingOfChain(placedBlock, event);
            return;
        }

        // Cauldron is placed and connects with chain above.
        boolean isCauldronPlaced = cauldrons.contains(placedBlockType);
        if (isCauldronPlaced) {
            handlePlacingOfCauldron(placedBlock);
        }
    }

    private void handlePlacingOfChain(Block placedBlock, BlockPlaceEvent event) {
        // Get type of block below.
        var blockBelow = placedBlock.getRelative(0, -1, 0);
        var blockBelowType = blockBelow.getType();
        boolean isCauldronBelowChain = cauldrons.contains(blockBelowType);
        if (isCauldronBelowChain) {
            Location spawnLocation = placedBlock.getLocation();

            // Wait for 1 tick and set the block to chain harness.
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                spawnLocation.getBlock().setType(chainHarness);
            }, 1L);
        }
    }

    private void handlePlacingOfCauldron(Block placedBlock) {
        // Get type of block above.
        var blockAbove = placedBlock.getRelative(0, 1, 0);
        var blockAboveType = blockAbove.getType();
        if (blockAboveType == Material.CHAIN) {
            Location chainLocation = blockAbove.getLocation();

            // Set the chain above to chain harness. It connects with the cauldron below.
            chainLocation.getBlock().setType(chainHarness);
        }
    }


    @EventHandler public void onBlockBreak(BlockBreakEvent event) {
        var brokenBlock = event.getBlock();
        var brokenBlockType = brokenBlock.getType();

        // Cauldron is broken, and it was connected with the chain above.
        boolean isCauldronBroken = cauldrons.contains(brokenBlockType);
        if (isCauldronBroken) {
            handleBreakingCauldron(brokenBlock);
            return;
        }

        // Chain harness is broken, we need to drop chain if it was broken with pickaxe.
        boolean isChainHarnessBroken = brokenBlockType == chainHarness;
        if (isChainHarnessBroken) {
            // If tool of player is pickaxe, drop chain.
            if (event.getPlayer().getInventory().getItemInMainHand().getType().name().contains("PICKAXE")) {
                // Drop chain item.
                brokenBlock.getWorld().dropItemNaturally(brokenBlock.getLocation(), new org.bukkit.inventory.ItemStack(Material.CHAIN));
            }
        }

    }

    private void handleBreakingCauldron(Block brokenBlock) {
        // Get type of block above.
        var blockAbove = brokenBlock.getRelative(0, 1, 0);
        var blockAboveType = blockAbove.getType();
        if (blockAboveType == chainHarness) {
            Location chainLocation = blockAbove.getLocation();

            // Set the chain harness back to chain now that it's not connected with anything anymore.
            chainLocation.getBlock().setType(Material.CHAIN);
        }
    }

}
