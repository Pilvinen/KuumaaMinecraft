package starandserpent.minecraft.criticalfixes;

import org.bukkit.Material;
import org.bukkit.Rotation;
import org.bukkit.Server;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.Bed;
import org.bukkit.entity.ItemFrame;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

public class SleepPod implements Listener {

    private final JavaPlugin plugin;
    private Server server;

    public SleepPod(JavaPlugin plugin) {
        this.plugin = plugin;
        this.server = plugin.getServer();
    }

    @EventHandler public void onPlacingSleepPod(BlockPlaceEvent event) {
        var block = event.getBlock();
        var blockType = block.getType();

        // We only handle sleep pods here.
        if (blockType != Material.LIME_BED) {
            return;
        }

        // Get the foot of the bed.
        Block bedFoot = block;

        // Get the next block in the orientation of the bed.
        var bedHeadFacing = ((Bed) block.getBlockData()).getFacing();
        var bedHead = block.getRelative(bedHeadFacing);

        // Check that we have head and foot.
        var bedFootState = bedFoot.getState();
        var bedHeadState = bedFoot.getState();

        BlockFace directionOfTheBed = null;
        if (bedHeadState.getBlockData() instanceof Bed) {
            Bed bedHeadData = (Bed) bedHeadState.getBlockData();
            directionOfTheBed = bedHeadData.getFacing();
        }

        // Switch that goes over facing directions.
        switch (directionOfTheBed) {
            case NORTH:                // foot,    head,    rotation,     footBlockFace,    headBlockFace
                placeInvisibleItemFrame(bedFoot, bedHead, Rotation.NONE, BlockFace.EAST, BlockFace.NORTH);
                break;
            case SOUTH: // Was FLIPPED
                placeInvisibleItemFrame(bedFoot, bedHead, Rotation.NONE, BlockFace.WEST, BlockFace.SOUTH);
                break;
            case EAST:
                placeInvisibleItemFrame(bedFoot, bedHead, Rotation.NONE, BlockFace.SOUTH, BlockFace.EAST);
                break;
            case WEST:
                placeInvisibleItemFrame(bedFoot, bedHead, Rotation.NONE, BlockFace.NORTH, BlockFace.WEST);
                break;
            default:
                placeInvisibleItemFrame(bedFoot, bedHead, Rotation.NONE, BlockFace.NORTH, BlockFace.SOUTH);
                return;
        }
    }

    private void placeInvisibleItemFrame(Block bedFoot, Block bedHead, Rotation frameRotation, BlockFace bedFootBlockFace, BlockFace bedHeadBlockFace) {

        // FEET OF THE BED

        // Instantiate a new item frame above the bedFoot.
        ItemFrame itemFrame = bedFoot.getWorld().spawn(bedFoot.getRelative(bedFootBlockFace).getLocation(), ItemFrame.class);
        itemFrame.setFacingDirection(bedFootBlockFace, true);
        itemFrame.setRotation(frameRotation);
        itemFrame.setInvulnerable(true);
//        itemFrame.setVisible(false);
        itemFrame.setVisible(true);
        itemFrame.setFixed(true);
        itemFrame.setItemDropChance(0);

        // Set the item inside the frame to the pod item.
        ItemStack sleepingPodItem = createSleepingPodItem(CustomItemsEnum.SLEEPING_POD_CLOSED_FEET);
        itemFrame.setItem(sleepingPodItem);

        // HEAD OF THE BED

        // Instantiate a new item frame above the bedHead.
        ItemFrame itemFrame2 = bedHead.getWorld().spawn(bedHead.getRelative(bedHeadBlockFace).getLocation(), ItemFrame.class);
        itemFrame2.setFacingDirection(bedHeadBlockFace, true);
        itemFrame2.setRotation(frameRotation);
        itemFrame2.setInvulnerable(true);
//        itemFrame.setVisible(false);
        itemFrame2.setVisible(true);
        itemFrame2.setFixed(true);
        itemFrame2.setItemDropChance(0);

        // Set the item inside the frame to the pod item.
        ItemStack sleepingPodItem2 = createSleepingPodItem(CustomItemsEnum.SLEEPING_POD_CLOSED_HEAD);
        itemFrame2.setItem(sleepingPodItem2);
    }

    // Create a new item that represents the sleeping pod.
    // This item is used to represent the sleeping pod in the item frame.
    // The item is not a real item that can be picked up.
    // It is a visual representation of the sleeping pod.
    private ItemStack createSleepingPodItem(CustomItemsEnum customItem) {

        ItemStack item = new ItemStack(customItem.getMaterial(), 1);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setCustomModelData(customItem.getId());
            item.setItemMeta(meta);
        }
        return item;
    }

}
