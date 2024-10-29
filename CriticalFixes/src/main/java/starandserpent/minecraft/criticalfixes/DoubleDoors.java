package starandserpent.minecraft.criticalfixes;

import org.bukkit.Server;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Openable;
import org.bukkit.block.data.type.Door;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class DoubleDoors implements Listener {

    private final JavaPlugin plugin;
    private Server server;

    public DoubleDoors(JavaPlugin plugin) {
        this.plugin = plugin;
        this.server = plugin.getServer();
    }

    @EventHandler public void onPlayerInteract(PlayerInteractEvent event) {
        var action = event.getAction();
        if (action != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        Block clickedBlock = event.getClickedBlock();
        if (clickedBlock == null || !(clickedBlock.getBlockData() instanceof Door)) {
            return;
        }

        Block adjacentDoorBlock = getAdjacentDoor(clickedBlock);
        if (adjacentDoorBlock == null) {
            return;
        }

        toggleDoubleDoors(clickedBlock);
    }

    private static Block getAdjacentDoor(Block block) {
        var blockData = block.getBlockData();
        if (!(blockData instanceof Door door)) {
            return null;
        }

        var doorHinge = door.getHinge();
        var doorFacing = door.getFacing();

        BlockFace adjacentBlockFace = getAdjacentBlockFace(doorFacing, doorHinge);
        if (adjacentBlockFace == null) {
            return null;
        }

        var adjacentBlock = block.getRelative(adjacentBlockFace);
        if (!(adjacentBlock.getBlockData() instanceof Door adjacentDoor)) {
            return null;
        }

        // Check if the adjacent door is oriented on the same axis as the clicked door
        if (adjacentDoor.getFacing() != doorFacing) {
            return null;
        }

        return adjacentDoor.getHinge() != doorHinge ? adjacentBlock : null;
    }

    private static BlockFace getAdjacentBlockFace(BlockFace doorFacing, Door.Hinge doorHinge) {
        switch (doorFacing) {
            case NORTH:
                return (doorHinge == Door.Hinge.RIGHT) ? BlockFace.WEST : BlockFace.EAST;
            case SOUTH:
                return (doorHinge == Door.Hinge.RIGHT) ? BlockFace.EAST : BlockFace.WEST;
            case EAST:
                return (doorHinge == Door.Hinge.RIGHT) ? BlockFace.NORTH : BlockFace.SOUTH;
            case WEST:
                return (doorHinge == Door.Hinge.RIGHT) ? BlockFace.SOUTH : BlockFace.NORTH;
            default:
                return null;
        }
    }

    public static void tryOpenDoubleDoors(Block clickedDoorBlock) {
        Block adjacentDoorBlock = getAdjacentDoor(clickedDoorBlock);
        if (adjacentDoorBlock == null) {
            return;
        }

        BlockData clickedDoorData = clickedDoorBlock.getBlockData();
        BlockData adjacentDoorData = adjacentDoorBlock.getBlockData();

        if (!(clickedDoorData instanceof Openable clickedDoor)
                || !(adjacentDoorData instanceof Openable adjacentDoor)) {
            return;
        }

        // Only open the doors if they are not already open
        if (!clickedDoor.isOpen()) {
            clickedDoor.setOpen(true);
            adjacentDoor.setOpen(true);

            clickedDoorBlock.setBlockData(clickedDoor);
            adjacentDoorBlock.setBlockData(adjacentDoor);
        }
    }

    public static void tryCloseDoubleDoors(Block clickedDoorBlock) {
        Block adjacentDoorBlock = getAdjacentDoor(clickedDoorBlock);
        if (adjacentDoorBlock == null) {
            return;
        }

        BlockData clickedDoorData = clickedDoorBlock.getBlockData();
        BlockData adjacentDoorData = adjacentDoorBlock.getBlockData();

        if (!(clickedDoorData instanceof Openable clickedDoor)
                || !(adjacentDoorData instanceof Openable adjacentDoor)) {
            return;
        }

        // Only close the doors if they are not already closed
        if (clickedDoor.isOpen()) {
            clickedDoor.setOpen(false);
            adjacentDoor.setOpen(false);

            clickedDoorBlock.setBlockData(clickedDoor);
            adjacentDoorBlock.setBlockData(adjacentDoor);
        }
    }


    public static void toggleDoubleDoors(Block clickedDoorBlock) {
        BlockData clickedDoorData = clickedDoorBlock.getBlockData();

        if (!(clickedDoorData instanceof Openable clickedDoor)) {
            return;
        }

        // Toggle the state of the doors
        if (clickedDoor.isOpen()) {
            tryCloseDoubleDoors(clickedDoorBlock);
        } else {
            tryOpenDoubleDoors(clickedDoorBlock);
        }
    }

}