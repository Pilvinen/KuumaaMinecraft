package starandserpent.minecraft.criticalfixes;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.function.Consumer;

public class FallingBlocksFix implements Listener {

    private final JavaPlugin plugin;
    private Server server;

    // List<T> of materials that have physics.
    private final List<Material> materialsWithPhysics = List.of(
            Material.DIRT,
            Material.GRASS_BLOCK,
            Material.GRAVEL,
            Material.COBBLESTONE,
            Material.COBBLED_DEEPSLATE,
            Material.SNOW_BLOCK,
            Material.POWDER_SNOW,
            Material.HAY_BLOCK,
            Material.PODZOL,
            Material.MYCELIUM,
            Material.COARSE_DIRT
    );

    // List<T> of materials when blocks should fall if they have these materials under them.
    private final List<Material> materialsThatDoNotSupportBlocksWithPhysics = List.of(
            Material.AIR,
            Material.WATER,
            Material.LAVA
    );

    public FallingBlocksFix(JavaPlugin plugin) {
        this.plugin = plugin;

        // Get server.
        server = plugin.getServer();
    }

    @EventHandler public void onBlockBreak(BlockBreakEvent event) {
        var blockType = event.getBlock().getType();
        var breakLocation = event.getBlock().getLocation();

        // Try crumble blocks around the block that was broken.
        if (blockType == Material.STONE || blockType == Material.DEEPSLATE) {
            tryCrumbleNeighboringBlocks(breakLocation, 2);
        }
    }

    @EventHandler public void onBlockPlace(BlockPlaceEvent event) {
        var block = event.getBlock();
        var blocksAroundLocation = getBlocksAroundLocation(block, 2);
        tryMakeBlocksFall(blocksAroundLocation);
    }

    private void tryCrumbleNeighboringBlocks(Location breakLocation, int radius) {
        int maxYToInclude = breakLocation.getBlockY();
        var blocksAroundLocation = getBlocksAroundLocationAboveY(breakLocation.getBlock(), radius, maxYToInclude);
        for (Block block : blocksAroundLocation) {
            var blockType = block.getType();

            // Do not crumble deep blocks immersed in stone or what ever.
            if (blockIsNotExposedToAir(block)) continue;

            // Percentage change to crumble stone.
            if (blockType == Material.STONE) {
                if (Math.random() < 0.015) {
                    block.setType(Material.COBBLESTONE);
                }
            }

            // Percentage change to crumble deep slate.
            if (blockType == Material.DEEPSLATE) {
                if (Math.random() < 0.015) {
                    block.setType(Material.COBBLED_DEEPSLATE);
                }
            }

        }
    }

    private boolean blockIsNotExposedToAir(Block block) {
        var blocksAroundLocation = getNeighboringBlocksOnly(block);
        for (Block blockAround : blocksAroundLocation) {
            if (blockAround.getType() == Material.AIR) {
                return false;
            }
        }
        return true;
    }

    private List<Block> getNeighboringBlocksOnly(Block block) {
        var blocks = new ArrayList<Block>();
        var location = block.getLocation();
        var world = location.getWorld();
        if (world == null) {
            throw new NullPointerException("World cannot be null");
        }
        var x = location.getBlockX();
        var y = location.getBlockY();
        var z = location.getBlockZ();

        blocks.add(world.getBlockAt(x, y + 1, z));
        blocks.add(world.getBlockAt(x, y - 1, z));
        blocks.add(world.getBlockAt(x - 1, y, z));
        blocks.add(world.getBlockAt(x + 1, y, z));
        blocks.add(world.getBlockAt(x, y, z - 1));
        blocks.add(world.getBlockAt(x, y, z + 1));

        return blocks;
    }

//    @EventHandler public void onBlockLand(EntityChangeBlockEvent event) {
        // We don't process blocks starting to fall.
//        Material blockChangedTo = event.getTo();
//        if (blockChangedTo == Material.AIR) {
//            return;
//        }
//    }

    // On fall physics event
    @EventHandler public void onBlockUpdate(BlockPhysicsEvent event) {
        var block = event.getBlock();
        var blocksAroundLocation = getBlocksAroundLocation(block, 1); // was 2
        tryMakeBlocksFall(blocksAroundLocation);
    }

    // Get blocks around location including the block in the location in a cube of size you can define.
    private List<Block> getBlocksAroundLocation(Block block, int radius) {
        var blocks = new ArrayList<Block>();
        var location = block.getLocation();
        var world = location.getWorld();
        if (world == null) {
            throw new NullPointerException("World cannot be null");
        }
        var x = location.getBlockX();
        var y = location.getBlockY();
        var z = location.getBlockZ();

        int startX = x - (radius - 1) / 2;
        int endX = x + radius / 2;
        int startY = y - (radius - 1) / 2;
        int endY = y + radius / 2;
        int startZ = z - (radius - 1) / 2;
        int endZ = z + radius / 2;

        for (int i = startX; i <= endX; i++) {
            for (int j = startY; j <= endY; j++) {
                for (int k = startZ; k <= endZ; k++) {
                    blocks.add(world.getBlockAt(i, j, k));
                }
            }
        }
        // Sort blocks by y coordinate.
        return sortBlocksByY(blocks);
    }

    private List<Block> getBlocksAroundLocationAboveY(Block block, int radius, int maxYToInclude) {
        var blocks = new ArrayList<Block>();
        var location = block.getLocation();
        var world = location.getWorld();
        if (world == null) {
            throw new NullPointerException("World cannot be null");
        }
        var x = location.getBlockX();
        var y = location.getBlockY();
        var z = location.getBlockZ();

        int startX = x - (radius - 1) / 2;
        int endX = x + radius / 2;
        int startY = y - (radius - 1) / 2;
        int endY = y + radius / 2;
        int startZ = z - (radius - 1) / 2;
        int endZ = z + radius / 2;

        for (int i = startX; i <= endX; i++) {
            for (int j = Math.max(startY, maxYToInclude); j <= endY; j++) {
                for (int k = startZ; k <= endZ; k++) {
                    blocks.add(world.getBlockAt(i, j, k));
                }
            }
        }

        return sortBlocksByY(blocks);
    }

    // Sort list of blocks by y coordinate, making the lowest y coordinate block the first in the list.
    private List<Block> sortBlocksByY(List<Block> blocks) {
        blocks.sort((block1, block2) -> {
            var y1 = block1.getY();
            var y2 = block2.getY();
            return Integer.compare(y1, y2);
        });

        return blocks;
    }

    // Try make block fall if it's a block with physics and has air under it. Take in a list of blocks.
    private void tryMakeBlocksFall(List<Block> blocks) {
        Set<Block> checkedBlocks = new LinkedHashSet<>();

        for (Block block : blocks) {
            var blockType = block.getType();
            var location = block.getLocation();
            var blockBelow = location.clone().add(0, -1, 0).getBlock();
            var blockBelowType = blockBelow.getType();

            if (materialsWithPhysics.contains(blockType)
                    && materialsThatDoNotSupportBlocksWithPhysics.contains(blockBelowType)) {

                if (!checkedBlocks.add(block)) {
                    continue; // Skip if the block has already been checked
                }

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        block.setType(Material.AIR);

                        // Manually create and configure the FallingBlock entity
                        var world = location.getWorld();
                        if (world != null) {
                            var fallingBlock = world.spawnFallingBlock(location, blockType.createBlockData());
                            fallingBlock.setHurtEntities(true);
                            fallingBlock.setDamagePerBlock(4);
                            fallingBlock.setDropItem(false);
                            //System.out.println("Spawned new FallingBlock entity at: " + location);
                        }

                    }
                }.runTaskLater(plugin, 1);
            }
        }
    }

    // We are essentially looking for floating blocks.
    private boolean isFloatingBlock(Block block) {

        // Create blocks to check in a list to loop through them.
        List<Block> blocksAroundLocation = List.of(
            block.getLocation().clone().add(0, 1, 0).getBlock(),
            block.getLocation().clone().add(-1, 0, 0).getBlock(),
            block.getLocation().clone().add(1, 0, 0).getBlock(),
            block.getLocation().clone().add(0, 0, -1).getBlock(),
            block.getLocation().clone().add(0, 0, 1).getBlock()
        );

        for (Block potentialSupportingBlock : blocksAroundLocation) {

            // Other materials can support when in direct contact.
            boolean canSupport = !(materialsThatDoNotSupportBlocksWithPhysics.contains(potentialSupportingBlock.getType()));
            if (canSupport) {
                return false;
            }

        }

        // We have a floater!
        return true;
    }



}
