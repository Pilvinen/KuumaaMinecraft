package starandserpent.minecraft.criticalfixes;

import com.nivixx.ndatabase.api.NDatabase;
import com.nivixx.ndatabase.api.repository.Repository;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.type.TrapDoor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.Arrays;
import java.util.stream.Stream;

public class BetterTrapdoors implements Listener {

    private final JavaPlugin plugin;
    private Server server;
    Repository<String, BetterTrapdoorsLocationBasedData> repository;

    public BetterTrapdoors(JavaPlugin plugin) {
        this.plugin = plugin;
        this.server = plugin.getServer();

        // Get the NDatabase repository.
        repository = NDatabase.api().getOrCreateRepository(BetterTrapdoorsLocationBasedData.class);
    }

    // On server shutting down, do maintenance.
    @EventHandler public void onServerShutdown(PluginDisableEvent event) {

        System.out.println("CriticalFixes, BetterTrapdoors: Server shutting down. Doing database maintenance.");

        var values = repository.streamAllValues().toArray();
        int invalidEntriesCount = 0;
        for (var value : values) {

            // Check if there's a trapdoor at the location.
            BetterTrapdoorsLocationBasedData betterTrapdoorsLocationBasedData = (BetterTrapdoorsLocationBasedData) value;
            Location location = betterTrapdoorsLocationBasedData.getLocation();
            if (location == null) {
                continue;
            }

            Block block = location.getBlock();
            var blockType = block.getType();
            if (Tag.TRAPDOORS.isTagged(blockType)) {
                continue;
            }

            // If there's no trapdoor at the location, delete the entry.
            repository.delete(betterTrapdoorsLocationBasedData.getKey());
            System.out.println("CriticalFixes, BetterTrapdoors: Deleted database entry for a missing trapdoor at location: " + location);
            invalidEntriesCount++;
        }

        System.out.println("CriticalFixes, BetterTrapdoors: Database maintenance done. There were " + Arrays.stream(values).count() + " valid entries and " + invalidEntriesCount + " invalid ones.");
    }


    @EventHandler public void onBlockPlace(BlockPlaceEvent event) {

        // Sneak placing trapdoors will make them open sideways. Otherwise, do default behavior.
        Player player = event.getPlayer();
        boolean isSneaking = player.isSneaking();
        if (!isSneaking) {
            return;
        }

        // Is it a trapdoor, though?
        Block block = event.getBlock();
        Material placedBlockType = block.getType();
        if (!Tag.TRAPDOORS.isTagged(placedBlockType)) {
            return;
        }

        // It is sideways opening trapdoor. Let's store its location and facings in the database.

        // Set the initial state of the sideways trapdoor. Otherwise default position is horizontal, laying down.

        // Get the facing of the block itself, not the player.
        var blockData = block.getBlockData();
        if (!(blockData instanceof TrapDoor trapDoor)) {
            return;
        }

        BlockFace facing = trapDoor.getFacing();

        setTrapdoorFacing(block, facing);

        // Get location of the placed block.
        Location blockLocation = block.getLocation();

        // We also need the facing, for that we need the block state.

        // The closed facing will become just the facing.
        BlockFace closedBlockFace = trapDoor.getFacing();
        Facing closedFacing = Facing.fromBlockFace(closedBlockFace);
        if (closedFacing == null) { // Something went wrong, bail out.
            return;
        }

        // We need the half to determine which direction to open (bottom = right, top = left).
        Bisected.Half half = trapDoor.getHalf();

        // The open facing will need to be calculated based on the closedFacing and half.
        BlockFace openBlockFace = getOpenFacing(closedBlockFace, half);
        Facing openFacing = Facing.fromBlockFace(openBlockFace);
        if (openFacing == null) { // Something went wrong, bail out.
            return;
        }

        // Store the sideways trapdoor data in the database, so we can properly toggle it based on the data.
        var key = getLocationKey(blockLocation);
        repository.upsert(new BetterTrapdoorsLocationBasedData(key, blockLocation, closedFacing, openFacing));
    }

    private BlockFace getOpenFacing(BlockFace closedFacing, Bisected.Half half) {
        // If the trapdoor is on the bottom, the open facing is to the right.
        if (half == Bisected.Half.BOTTOM) {
            return rotateY(closedFacing);
        }

        // If the trapdoor is on the top, the open facing is to the left.
        return rotateYCounter(closedFacing);
    }

    @EventHandler public void onBlockBreak(BlockBreakEvent event) {
        var block = event.getBlock();
        var blockType = block.getType();
        if (Tag.TRAPDOORS.isTagged(blockType)) {

            var values = repository.streamAllValues().toArray();
            for (var value : values) {
                BetterTrapdoorsLocationBasedData betterTrapdoorsLocationBasedData = (BetterTrapdoorsLocationBasedData) value;
            }

            Location location = block.getLocation();
            String locationKey = getLocationKey(location);

            try {
                repository.delete(locationKey);
            } catch (Exception ignored) {
            }

        }
    }

    private String getLocationKey(Location location) {
        if (location == null) {
            return null;
        }

        var world = location.getWorld();
        if (world == null) {
            return null;
        }

        int x = location.getBlockX();
        int y = location.getBlockY();
        int z = location.getBlockZ();

        return world.getName() + "_" + x + "_" + y + "_" + z;
    }


    private void debugTrapdoorData(Stream<BetterTrapdoorsLocationBasedData> betterTrapdoorsLocationBasedDataStream) {
        var allTrapdoors = betterTrapdoorsLocationBasedDataStream.toList();
        var trapdoorCount = allTrapdoors.size();
        System.out.println("There are " + trapdoorCount + " trapdoors stored in the database.");
        allTrapdoors.forEach((trapdoorLocation) -> System.out.println("Trapdoor Location: " + trapdoorLocation));
    }

    private void setTrapdoorFacing(Block trapdoor, Facing facing) {
        var data = trapdoor.getBlockData();
        TrapDoor trapDoor = (TrapDoor) data;
        BlockFace blockFaceFacing = facing.toBlockFace();
        if (blockFaceFacing == null) {
            return;
        }
        trapDoor.setFacing(blockFaceFacing);
        trapDoor.setOpen(true);

        // Do it after a delay.
//        server.getScheduler().runTaskLater(plugin, () -> {
        trapdoor.setBlockData(trapDoor);
//        }, 1);
    }

    private void setTrapdoorFacing(Block trapdoor, BlockFace blockFace) {
        var data = trapdoor.getBlockData();
        TrapDoor trapDoor = (TrapDoor) data;
        if (blockFace == null) {
            return;
        }
        trapDoor.setFacing(blockFace);
        trapDoor.setOpen(true);

        // Do it after a delay.
//        server.getScheduler().runTaskLater(plugin, () -> {
        trapdoor.setBlockData(trapDoor);
//        }, 1);
    }


    @EventHandler public void onPlayerInteract(PlayerInteractEvent event) {

        // If not a right click, we're done here.
        if (!(event.getAction() == Action.RIGHT_CLICK_BLOCK)) {
            return;
        }

        // Likely trying to place a block.
        var player = event.getPlayer();
        if (player.isSneaking()) {
            return;
        }

        Block block = event.getClickedBlock();
        if (!isValidTrapdoorTarget(block)) {
            return;
        }

        assert block != null;

        // Try to get database entry for the trapdoor.
        Location location = block.getLocation();
        var key = getLocationKey(location);
        BetterTrapdoorsLocationBasedData trapdoorInRepo = repository.get(key);

        if (trapdoorInRepo == null) {
            return;
        }

        // We have the trapdoor in the database. Let's toggle it!
        toggleTrapdoor(block, trapdoorInRepo);
        event.setCancelled(true);
    }

    private void toggleTrapdoor(Block block, BetterTrapdoorsLocationBasedData trapdoorInRepo) {
        String closedFacingString = trapdoorInRepo.closedFacing;
        Facing closedFacing = Facing.fromString(closedFacingString);

        String openFacingString = trapdoorInRepo.openFacing;
        Facing openFacing = Facing.fromString(openFacingString);

        TrapDoor trapdoorData = (TrapDoor) block.getBlockData();

        BlockFace currentBlockFace = trapdoorData.getFacing();
        Facing currentFacing = Facing.fromBlockFace(currentBlockFace);

        Facing newFacing = currentFacing == Facing.fromString(closedFacingString) ? openFacing : closedFacing;
        if (newFacing == null) {
            return;
        }

        // Toggle the current trapdoor
        setTrapdoorFacing(block, newFacing);

        // Check and toggle trapdoors above
        for (int i = 1; i <= 4; i++) {
            Block aboveBlock = block.getRelative(BlockFace.UP, i);
            if (!toggleIfValidTrapdoor(aboveBlock, closedFacing, openFacing, trapdoorData.getHalf())) {
                break;
            }
        }

        // Check and toggle trapdoors below
        for (int i = 1; i <= 4; i++) {
            Block belowBlock = block.getRelative(BlockFace.DOWN, i);
            if (!toggleIfValidTrapdoor(belowBlock, closedFacing, openFacing, trapdoorData.getHalf())) {
                break;
            }
        }
    }

    private boolean toggleIfValidTrapdoor(Block block, Facing closedFacing, Facing openFacing, Bisected.Half half) {
        if (!isValidTrapdoorTarget(block)) {
            return false;
        }

        TrapDoor trapdoorData = (TrapDoor) block.getBlockData();
        if (trapdoorData.getHalf() != half) {
            return false;
        }

        BlockFace currentBlockFace = trapdoorData.getFacing();
        Facing currentFacing = Facing.fromBlockFace(currentBlockFace);

        if (currentFacing != closedFacing && currentFacing != openFacing) {
            return false;
        }

        String key = getLocationKey(block.getLocation());
        BetterTrapdoorsLocationBasedData trapdoorInRepo = repository.get(key);
        if (trapdoorInRepo == null) {
            return false;
        }

        // Check if the closedFacing and openFacing match with the trapdoor in the database
        Facing repoClosedFacing = Facing.fromString(trapdoorInRepo.closedFacing);
        Facing repoOpenFacing = Facing.fromString(trapdoorInRepo.openFacing);
        if (repoClosedFacing != closedFacing || repoOpenFacing != openFacing) {
            return false;
        }

        // All conditions met, toggle the trapdoor
        Facing newFacing = currentFacing == closedFacing ? openFacing : closedFacing;
        setTrapdoorFacing(block, newFacing);

        return true;
    }


    public boolean isValidTrapdoorTarget(Block block) {

        // There needs to be a block target.
        if (block == null) {
            return false;
        }

        // If the block is not a trapdoor, we're done here. Otherwise, return true.
        return Tag.TRAPDOORS.isTagged(block.getType());
    }

    private BlockFace rotateYCounter(BlockFace face) {
        return switch (face) {
            case NORTH -> BlockFace.WEST;
            case WEST -> BlockFace.SOUTH;
            case SOUTH -> BlockFace.EAST;
            case EAST -> BlockFace.NORTH;
            default -> face;
        };
    }

    private BlockFace rotateY(BlockFace face) {
        return switch (face) {
            case NORTH -> BlockFace.EAST;
            case EAST -> BlockFace.SOUTH;
            case SOUTH -> BlockFace.WEST;
            case WEST -> BlockFace.NORTH;
            default -> face;
        };
    }

}
