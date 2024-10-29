package starandserpent.minecraft.criticalfixes;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.type.Leaves;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.world.StructureGrowEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.Random;

public class BirchLeafFix implements Listener {

    private final JavaPlugin plugin;
    private Server server;

    public BirchLeafFix(JavaPlugin plugin) {
        this.plugin = plugin;
        this.server = plugin.getServer();
    }

    // Function to store persistent birch leaf data to chunk.
    private void setBirchLeafData(Location location, PersistentDataContainer dataContainer) {
        String locationKey = "BirchLeaf_" + location.getWorld().getName() + "_"
                + location.getBlockX() + "_"
                + location.getBlockY() + "_"
                + location.getBlockZ();
        dataContainer.set(new NamespacedKey(plugin, locationKey), PersistentDataType.BOOLEAN, true);
    }

    // Function to check for existence of persistent birch leaf data from chunk.
    private boolean hasBirchLeafData(Location location, PersistentDataContainer dataContainer) {
        String locationKey = "BirchLeaf_" + location.getWorld().getName() + "_"
                + location.getBlockX() + "_"
                + location.getBlockY() + "_"
                + location.getBlockZ();
        return dataContainer.has(new NamespacedKey(plugin, locationKey), PersistentDataType.BOOLEAN);
    }

    // Function to delete persistent birch leaf data from chunk.
    private void deleteBirchLeafData(Location location, PersistentDataContainer dataContainer) {
        String locationKey = "BirchLeaf_" + location.getWorld().getName() + "_"
                + location.getBlockX() + "_"
                + location.getBlockY() + "_"
                + location.getBlockZ();
        dataContainer.remove(new NamespacedKey(plugin, locationKey));
    }

    // LeafDecayEvent
    @EventHandler public void onLeavesDecay(LeavesDecayEvent event) {

        var block = event.getBlock();
        var blockType = block.getType();

        // Check if it's acacia leaves.
        if (blockType == Material.ACACIA_LEAVES) {

            // Check for the presence of NBT data in the chunk and match it with the block location. Is it a birch leaf?
            var blockLocation = block.getLocation();
            PersistentDataContainer dataContainer = block.getChunk().getPersistentDataContainer();
            boolean isActuallyFakeBirchLeaf = hasBirchLeafData(blockLocation, dataContainer);
            if (isActuallyFakeBirchLeaf) {

                // Cancel the event.
                event.setCancelled(true);

                // Set block to air.
                block.setType(Material.AIR);

                // Get player's tool for breaking the block.
                ItemStack tool = new ItemStack(Material.AIR);

                // Set drops from custom method: createBirchLeavesDrops
                createBirchLeavesDrops(blockLocation, tool);

                // Delete the persistent data from the chunk for this block.
                deleteBirchLeafData(blockLocation, dataContainer);
            }
        }
    }

    // Handle block break event.
    @EventHandler public void onBlockBreak(BlockBreakEvent event) {

        var block = event.getBlock();
        var blockType = block.getType();

        // Check if it's acacia leaves.
        if (blockType == Material.ACACIA_LEAVES) {

            // Check for the presence of NBT data in the chunk and match it with the block location. Is it a birch leaf?
            var blockLocation = block.getLocation();
            PersistentDataContainer dataContainer = block.getChunk().getPersistentDataContainer();
            boolean isActuallyFakeBirchLeaf = hasBirchLeafData(blockLocation, dataContainer);
            if (isActuallyFakeBirchLeaf) {

                // Cancel the event.
                event.setCancelled(true);

                // Set block to air.
                block.setType(Material.AIR);

                // Get player's tool for breaking the block.
                ItemStack tool = event.getPlayer().getInventory().getItemInMainHand();

                // Set drops from custom method: createBirchLeavesDrops
                createBirchLeavesDrops(blockLocation, tool);

                // Delete the persistent data from the chunk for this block.
                deleteBirchLeafData(blockLocation, dataContainer);
            }

        }
    }

    private void createBirchLeavesDrops(Location location, ItemStack tool) {
        Random random = new Random();

        // Check if the tool is shears or has the silk touch enchantment.
        if (tool.getType() == Material.SHEARS || tool.getEnchantments().containsKey(Enchantment.SILK_TOUCH)) {
            location.getWorld().dropItemNaturally(location, new ItemStack(Material.BIRCH_LEAVES));
        } else {
            // Get the fortune level of the tool.
            int fortuneLevel = tool.getEnchantmentLevel(Enchantment.LOOTING);

            // Define the probabilities for dropping a birch sapling and a stick for each fortune level.
            double[] saplingProbabilities = {0.05, 0.0625, 0.083333336, 0.1};
            double[] stickProbabilities = {0.02, 0.022222223, 0.025, 0.033333335, 0.1};

            // Get the correct probability for the fortune level.
            double saplingProbability = saplingProbabilities[fortuneLevel];
            double stickProbability = stickProbabilities[fortuneLevel];

            // Drop a birch sapling with a certain probability.
            if (random.nextDouble() < saplingProbability) {
                location.getWorld().dropItemNaturally(location, new ItemStack(Material.BIRCH_SAPLING));
            }

            // Drop a stick with a certain probability.
            if (random.nextDouble() < stickProbability) {
                // The number of sticks dropped is a random number between 1 and 2.
                int stickCount = random.nextInt(2) + 1;
                location.getWorld().dropItemNaturally(location, new ItemStack(Material.STICK, stickCount));
            }
        }
    }

    // On block place event.
    @EventHandler public void onBlockPlace(BlockPlaceEvent event) {
        // if we place birch leaves, cancel event and swap them with acacia leaves.

        // Get the block type.
        var block = event.getBlock();
        var blockType = block.getType();
        if (blockType == Material.BIRCH_LEAVES) {

            // Set block on the ground to acacia leaves.
            block.setType(Material.ACACIA_LEAVES);

            // Set the NBT data in the chunk and match it with the block location. This is a birch leaf now.
            PersistentDataContainer dataContainer = block.getChunk().getPersistentDataContainer();
            setBirchLeafData(block.getLocation(), dataContainer);

            // Make the leaves persistent: true so they don't disappear when placed by player.
            Leaves leaves = (Leaves) block.getBlockData();
            leaves.setPersistent(true);
            block.setBlockData(leaves);
        }
    }


    @EventHandler public void onStructureGrow(StructureGrowEvent event) {

        // if we grow birch trees, we have configured them in data pack to grow with acacia leaves.
        // To know that they are birch leaves, we need to set NBT data in the chunk to identify them later
        // to get the correct drops. Don't want to get acacia saplings from chopping down birch trees.
        if (event.getSpecies() == TreeType.BIRCH || event.getSpecies() == TreeType.TALL_BIRCH) {

            // Get the blocks from the event
            List<BlockState> blocks = event.getBlocks();

            // Schedule a delayed task to check the blocks
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                for (BlockState blockState : blocks) {

                    // Get the block at the current position
                    Block block = blockState.getBlock();
                    Material blockType = block.getType();

                    // Check if the block is a birch leaf or log
                    if (blockType == Material.ACACIA_LEAVES) {

                        // Set the NBT data in the chunk and match it with the block location. This is a birch leaf now.
                        PersistentDataContainer dataContainer = block.getChunk().getPersistentDataContainer();
                        setBirchLeafData(block.getLocation(), dataContainer);
                    }
                }
            }, 1L);  // 1 tick delay
        }
    }

}
