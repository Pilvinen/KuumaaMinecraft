package starandserpent.minecraft.criticalfixes;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class BlockOperations {

    /**
     * Get a list of blocks in a radius around a location.
     * Radius of 1 will return a cross shape of blocks around the location.
     * Radius of 2 will return a 5x5x5 cube of blocks around the location.
     * Radius of 3 will return a 7x7x7 cube of blocks around the location.
     * And so on. The search is cubic, but the returned shape will be spherical.
     *
     * @param location The center location.
     * @param radius The radius around the location.
     * @return A list of blocks in the radius around the location.
     */
    public static List<Block> getBlocksInRadius(Location location, int radius) {
        List<Block> blocksInRadius = new ArrayList<>();

        var locationX = location.getBlockX();
        var locationY = location.getBlockY();
        var locationZ = location.getBlockZ();

        int cubeLength = radius * 2 + 1;
        int halfCubeLength = cubeLength / 2;

        int startX = locationX - halfCubeLength;
        int endX = startX + cubeLength - 1;

        int startY = locationY - halfCubeLength;
        int endY = startY + cubeLength - 1;

        int startZ = locationZ - halfCubeLength;
        int endZ = startZ + cubeLength - 1;

        var world = location.getWorld();
        if (world == null) {
            return blocksInRadius;
        }

        for (int currentZ = startZ; currentZ <= endZ; currentZ++) {
            for (int currentY = startY; currentY <= endY; currentY++) {
                for (int currentX = startX; currentX <= endX; currentX++) {

                    // Calculate distance of the currently iterated distance from the center.
                    //
                    // This mathematical operation is calculating the Euclidean distance between two points in 3D space.
                    // The two points are (locationX, locationY, locationZ) and (currentX, currentY, currentZ).
                    // The Euclidean distance is the straight-line distance between two points in a space and
                    // is calculated using the Pythagorean theorem. In this case, it's being used to calculate
                    // the distance of the current block from the center location.
                    double distance = Math.sqrt(Math.pow(locationX - currentX, 2) +
                            Math.pow(locationY - currentY, 2) +
                            Math.pow(locationZ - currentZ, 2));

                    // If the distance is greater than the radius, then the block is outside the radius and
                    // should not be included in the list of blocks in the radius.
                    if (distance > radius) {
                        continue;
                    }

                    var block = world.getBlockAt(currentX, currentY, currentZ);

                    blocksInRadius.add(block);
                }
            }
        }

        return blocksInRadius;
    }

        // Order collection by distance from location.
//        blocksInRadius.sort((block1, block2) -> {
//            var block1Location = block1.getLocation();
//            var block2Location = block2.getLocation();
//            var block1Distance = location.distance(block1Location);
//            var block2Distance = location.distance(block2Location);
//            return Double.compare(block1Distance, block2Distance);
//        });

//        return blocksInRadius;
//    }

    /**
     * Get only the air blocks with a solid block under them.
     *
     * @param blocksInRadius The list of blocks in the radius.
     * @return A map of air blocks with a solid block under them. The first block is the air block and
     * the second block is the solid block under it.
     */
    public static HashMap<Block, Block> getOnlyAirBlocksWithSolidUnderThem(List<Block> blocksInRadius, HashSet<Material> ignoreTheseSolidMaterials) {
        HashMap<Block, Block> airBlocksWithSolidUnderThem = new HashMap<>();

        for (var block : blocksInRadius) {
            var blockLocation = block.getLocation();
            var blockX = blockLocation.getBlockX();
            var blockY = blockLocation.getBlockY();
            var blockZ = blockLocation.getBlockZ();
            var world = block.getWorld();
            var blockType = block.getType();
            var blockBelow = world.getBlockAt(blockX, blockY - 1, blockZ);
            var blockBelowType = blockBelow.getType();

            if (blockType.isAir() && blockBelowType.isSolid()) {

                if (ignoreTheseSolidMaterials.contains(blockBelowType)) {
                    continue;
                }

                airBlocksWithSolidUnderThem.put(block, blockBelow);
            }
        }
        return airBlocksWithSolidUnderThem;
    }
}
