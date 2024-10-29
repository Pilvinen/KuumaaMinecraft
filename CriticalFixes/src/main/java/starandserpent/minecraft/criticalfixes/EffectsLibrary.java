package starandserpent.minecraft.criticalfixes;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.block.data.type.CoralWallFan;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.function.Predicate;

public class EffectsLibrary implements Listener {

    private static Map<Player, BukkitTask> stopShakingTasks = new HashMap<>();

    private static JavaPlugin plugin;
    private static Server server;

    static {
        plugin = JavaPlugin.getProvidingPlugin(EffectsLibrary.class);
        server = plugin.getServer();
    }

//    STOP:
//            /particle minecraft:entity_effect{color: [0.9960784313725490196078431372549, 0.9921568627450980392156862745098, 0.003921568627451, 1.0]} ~ ~ ~ ~ ~ ~ 1 1 force
//
//    START:
//            /particle minecraft:entity_effect{color: [0.9960784313725490196078431372549, 0.9921568627450980392156862745098, 0.2, 1.0]} ~ ~ ~ ~ ~ ~ 1 1 force
//    Frequency of the shake (how wide circles it does) ----^
//
//    ADJUST FREQUENCY:
//            /particle minecraft:entity_effect{color: [0.9960784313725490196078431372549, 0.98823529411764705882352941176471, 0.01, 1.0]} ~ ~ ~ ~ ~ ~ 1 1 force @s
//    Strength (speed) of the shake ----^

    private static double redStop = 0.9960784313725490196078431372549;
    private static double greenStop = 0.9921568627450980392156862745098;
    private static double blueStop = 0.003921568627451;
    // Convert the colors to syntax that spawnParticle understands.
    private static Color stopShakeColorAsRGB = Color.fromRGB((int) (redStop * 255), (int) (greenStop * 255), (int) (blueStop * 255));

    private static double redStart = 0.9960784313725490196078431372549;
    private static double greenStart = 0.9921568627450980392156862745098;
    private static double blueStart_Width = 0.2;
    // Convert the colors to syntax that spawnParticle understands.
    private static Color startShakeColorAsRGB = Color.fromRGB((int) (redStart * 255), (int) (greenStart * 255), (int) (blueStart_Width * 255));

    private static double redFrequency = 0.9960784313725490196078431372549;
    private static double greenFrequency = 0.98823529411764705882352941176471;
    private static double blueFrequency_Speed = 0.01;
    private static double blueFrequency_Speed_Stop = 0.0;

    // Convert the colors to syntax that spawnParticle understands.
    private static Color frequencyShakeColorAsRGB = Color.fromRGB((int) (redFrequency * 255), (int) (greenFrequency * 255), (int) (blueFrequency_Speed * 255));

    private static Color frequencyStopShakeColorAsRGB = Color.fromRGB((int) (redFrequency * 255), (int) (greenFrequency * 255), (int) (blueFrequency_Speed_Stop * 255));

    public EffectsLibrary(JavaPlugin plugin) {
        this.plugin = plugin;
        server = plugin.getServer();
    }

    // On player join. Play a particle effect to stop shake shaker.
    @EventHandler public void onPlayerJoin(PlayerJoinEvent event) { turnOffShake(event.getPlayer()); }
    // On player moving to a different world.
    @EventHandler public void onPlayerChangeWorld(PlayerChangedWorldEvent event)  { turnOffShake(event.getPlayer()); }
    // On player death and respawn.
    @EventHandler public void onPlayerRespawn(PlayerRespawnEvent event) { turnOffShake(event.getPlayer()); }
    // On player being teleported [to a different world].
    @EventHandler public void onPlayerTeleport(PlayerTeleportEvent event)  { turnOffShake(event.getPlayer()); }

    public static void turnOffShake(Player player) {

        // First, delay 1 ticks before running so if this works it's fast.
        server.getScheduler().runTaskLater(plugin, () -> {
            if (!player.isValid()) { return; }
            // Then spawn the ENTITY_EFFECT particle to trigger the shader to stop shaking.
            player.spawnParticle(Particle.ENTITY_EFFECT, player.getLocation(), 1, stopShakeColorAsRGB);
            player.spawnParticle(Particle.ENTITY_EFFECT, player.getLocation(), 1, frequencyStopShakeColorAsRGB);
        }, 1L);

        // Next, delay 2 ticks and run again so if first time failed due to lag, we have a second chance.
        server.getScheduler().runTaskLater(plugin, () -> {
            if (!player.isValid()) { return; }
            // Then spawn the ENTITY_EFFECT particle to trigger the shader to stop shaking.
            player.spawnParticle(Particle.ENTITY_EFFECT, player.getLocation(), 1, stopShakeColorAsRGB);
            player.spawnParticle(Particle.ENTITY_EFFECT, player.getLocation(), 1, frequencyStopShakeColorAsRGB);
        }, 2L);

        // Next, delay 10 ticks and run again, so that we have greater chance of success if there's a failure due to lag.
        server.getScheduler().runTaskLater(plugin, () -> {
            if (!player.isValid()) { return; }
            // Then spawn the ENTITY_EFFECT particle to trigger the shader to stop shaking.
            player.spawnParticle(Particle.ENTITY_EFFECT, player.getLocation(), 1, stopShakeColorAsRGB);
            player.spawnParticle(Particle.ENTITY_EFFECT, player.getLocation(), 1, frequencyStopShakeColorAsRGB);
        }, 10L);

        // Next, delay 20 ticks and run again, so that we have greater chance of success if there's a failure due to lag.
        server.getScheduler().runTaskLater(plugin, () -> {
            if (!player.isValid()) { return; }
            // Then spawn the ENTITY_EFFECT particle to trigger the shader to stop shaking.
            player.spawnParticle(Particle.ENTITY_EFFECT, player.getLocation(), 1, stopShakeColorAsRGB);
            player.spawnParticle(Particle.ENTITY_EFFECT, player.getLocation(), 1, frequencyStopShakeColorAsRGB);
        }, 20L);


        // Next, delay 40 ticks and run again, so that we have greater chance of success if there's a failure due to lag.
        server.getScheduler().runTaskLater(plugin, () -> {
            if (!player.isValid()) { return; }
            // Then spawn the ENTITY_EFFECT particle to trigger the shader to stop shaking.
            player.spawnParticle(Particle.ENTITY_EFFECT, player.getLocation(), 1, stopShakeColorAsRGB);
            player.spawnParticle(Particle.ENTITY_EFFECT, player.getLocation(), 1, frequencyStopShakeColorAsRGB);
        }, 40L);
    }


    public static void showDamageShake(Player player, float widthOfShake, float speedOfShake, float duration) {

        // Cancel any existing stop-shaking task for this player.
        BukkitTask existingTask = stopShakingTasks.get(player);
        if (existingTask != null) {
            existingTask.cancel();
        }

        // Then spawn the ENTITY_EFFECT particle to trigger the shader to start shaking.
        Color damageShakeColorAsRGB = Color.fromRGB((int) (redStart * 255), (int) (greenStart * 255), (int) (widthOfShake * 255));
        Color damageShakeFrequencyAsRGB = Color.fromRGB((int) (redFrequency * 255), (int) (greenFrequency * 255), (int) (speedOfShake * 255));
        player.spawnParticle(Particle.ENTITY_EFFECT, player.getLocation(), 1, damageShakeColorAsRGB);
        player.spawnParticle(Particle.ENTITY_EFFECT, player.getLocation(), 1, damageShakeFrequencyAsRGB);

        // Schedule a task to stop the shaking after the duration.
        var stopShakingTask = server.getScheduler().runTaskLater(plugin, () -> {
            player.spawnParticle(Particle.ENTITY_EFFECT, player.getLocation(), 1, stopShakeColorAsRGB);
            player.spawnParticle(Particle.ENTITY_EFFECT, player.getLocation(), 1, frequencyStopShakeColorAsRGB);

            // Remove the task from the map when it's done.
            stopShakingTasks.remove(player);
        }, (long) (duration * 20));

        // Store the task in the map.
        stopShakingTasks.put(player, stopShakingTask);
    }

    public static void showBloodParticles(LivingEntity livingEntity, float spread, int amount, float speed) {

        // Get the world from the location.
        var location = livingEntity.getLocation();
        var world = location.getWorld();
        if (world == null) {
            return;
        }

        // Center the particles.
        var offsetX = 0.0;
        var offsetY = 1.25;
        var offsetZ = 0.0;

        // Same spread for all directions.
        var spreadX = spread;
        var spreadY = spread;
        var spreadZ = spread;


        var correctedLocation = livingEntity.getLocation().add(offsetX, offsetY, offsetZ);

        // Create the blood particle.
        var bloodParticle = Material.REDSTONE_BLOCK.createBlockData();

        // Spawn the particles.
        world.spawnParticle(Particle.BLOCK, correctedLocation, amount, spreadX, spreadY, spreadZ, speed, bloodParticle);
    }

    private final static HashSet<Material> disallowedMaterialsOnGround = new HashSet<>() {{
        add(Material.DEAD_HORN_CORAL_FAN);
    }};

    public static void bleedOnBlocks(LivingEntity bleedingEntity) {

        var location = bleedingEntity.getLocation();

        // Get blocks in x block radius around the entity.
        var radius = 3;
        var blocks = BlockOperations.getBlocksInRadius(location, radius);

        var candidateBlocks = BlockOperations.getOnlyAirBlocksWithSolidUnderThem(blocks, disallowedMaterialsOnGround);

        var maxRaycastDistance = radius * 2 + 2;
        attemptToPlaceBloodOnRandomBlock(candidateBlocks, bleedingEntity, maxRaycastDistance);
    }

    // HashSet of disallowed materials to bleed on.
    private final static HashSet<Material> disallowedMaterialsOnWalls = new HashSet<>() {{
        add(Material.DEAD_HORN_CORAL_FAN);
        add(Material.DEAD_HORN_CORAL_WALL_FAN);
        add(Material.IRON_BARS);
    }};

    private static void attemptToPlaceBloodOnRandomBlock(HashMap<Block, Block> airBlocksWithSolidUnderThem, LivingEntity livingEntity, int maxRaycastDistance) {

        // If the whole collection is empty we have nothing to do.
        if (airBlocksWithSolidUnderThem.isEmpty()) {
            return;
        }

        var headLocation = livingEntity.getEyeLocation();

        // Pick random key value pair from airBlocksWithSolidUnderThem.
        var randomEntry = airBlocksWithSolidUnderThem.entrySet().stream().skip(new Random().nextInt(airBlocksWithSolidUnderThem.size())).findFirst().get();

        // Get the random block and the block below it.
        Block randomAirBlock = randomEntry.getKey();
        Block solidBlockBelowRandomAirBlock = randomEntry.getValue();

        // Check if the block is air.
        if (randomAirBlock.getType() != Material.AIR) {
            return;
        }

        // Set randomBlock to dead horn coral fan, but remove the water from the block data.
        // Get the BlockData for the dead horn coral fan.

        // Raycast from the head of the entity to the block to check if there's a line of sight.
        var world = headLocation.getWorld();
        if (world == null) {
            return;
        }

        Predicate filterPredicateForRaycast = (Object blockObject) -> {

            // If it's not a block we don't consider anything was hit.
            if (!(blockObject instanceof Block block)) {
                return false;
            }

            // Ignore blood blocks for now, we will verify later whether we can place or not.
            // We don't want the collision shape of the blood blocks to interfere with the raycast.
            if (block.getType() == Material.DEAD_HORN_CORAL_FAN
             || block.getType() == Material.DEAD_HORN_CORAL_WALL_FAN) {
                return false;
            }

            // If we hit a solid block, return true and end raycast.
            if (block.getType().isSolid()) {
                return true;
            }

            // Otherwise we haven't hit anything yet.
            return false;
        };

        var raycastStart = headLocation.getBlock().getLocation().add(0.5, 0.5, 0.5);
        var raycastDirection = solidBlockBelowRandomAirBlock.getLocation().toVector().subtract(raycastStart.toVector()).normalize();
        var rayTrace = world.rayTrace(raycastStart, raycastDirection, maxRaycastDistance, FluidCollisionMode.NEVER, true, 0, filterPredicateForRaycast);

        // DEBUG ************
        // Spawn a red particle at the starting point.
//        Particle.DustOptions dustOptionsRed = new Particle.DustOptions(Color.RED, 1);
//        world.spawnParticle(Particle.DUST, raycastStart, 1, dustOptionsRed);
//        for (double d = 0; d < maxRaycastDistance; d += 0.1) {
//            Location pointOnRaycast = raycastStart.clone().add(raycastDirection.clone().multiply(d));
//            world.spawnParticle(Particle.HAPPY_VILLAGER, pointOnRaycast, 1);
//        }
        // Spawn a green particle at the ending point.
//        Location raycastEnd = raycastStart.clone().add(raycastDirection.clone().multiply(maxRaycastDistance));
//        Particle.DustOptions dustOptionsGreen = new Particle.DustOptions(Color.GREEN, 1);
//        world.spawnParticle(Particle.DUST, raycastEnd, 1, dustOptionsGreen);
        // DEBUG ************

        if (rayTrace == null) {
            return;
        }

        var hitBlock = rayTrace.getHitBlock();
        if (hitBlock == null) {
            return;
        }

        var hitBlockType = hitBlock.getType();

        // Check block below is solid and can be placed.
        if (!hitBlockType.isSolid()) {
            return;
        }

        var faceOfBlockRaycastHit = rayTrace.getHitBlockFace();
        if (faceOfBlockRaycastHit == null) {
            return;
        }

        // If up, not allowed. How did you manage that?
        if (faceOfBlockRaycastHit == BlockFace.DOWN) {
            return;
        }

        // Business as usual.
        if (faceOfBlockRaycastHit == BlockFace.UP) {
            // We found a potentially valid target.
            var blockAbove = hitBlock.getRelative(0, 1, 0);
            boolean canPlaceBlockToBlood = canPlaceBlockToBlood(blockAbove);
            if (!canPlaceBlockToBlood) {
                return;
            }

            setFloorBlockToBlood(blockAbove);
            return;
        }

        // Otherwise it's a wall.

        // These materials being the solid block prevent blood from being placed.
        if (disallowedMaterialsOnWalls.contains(hitBlockType)
            || Tag.WALLS.isTagged(hitBlockType)
            || Tag.FENCES.isTagged(hitBlockType)
            || Tag.FENCE_GATES.isTagged(hitBlockType)
            || Tag.SLABS.isTagged(hitBlockType)
            || Tag.STAIRS.isTagged(hitBlockType)
            || Tag.TRAPDOORS.isTagged(hitBlockType)
            || Tag.DOORS.isTagged(hitBlockType)
        ) {
            return;
        }

        if (faceOfBlockRaycastHit == BlockFace.NORTH) {
            var placementLocation = hitBlock.getRelative(0, 0, -1);
            boolean canPlaceBlockToBlood = canPlaceBlockToBlood(placementLocation);
            if (!canPlaceBlockToBlood) {
                return;
            }
            setWallBlockToBlood(placementLocation, BlockFace.NORTH);
            return;
        }

        if (faceOfBlockRaycastHit == BlockFace.SOUTH) {
            var placementLocation = hitBlock.getRelative(0, 0, 1);
            boolean canPlaceBlockToBlood = canPlaceBlockToBlood(placementLocation);
            if (!canPlaceBlockToBlood) {
                return;
            }
            setWallBlockToBlood(placementLocation, BlockFace.SOUTH);
            return;
        }

        if (faceOfBlockRaycastHit == BlockFace.EAST) {
            var placementLocation = hitBlock.getRelative(1, 0, 0);
            boolean canPlaceBlockToBlood = canPlaceBlockToBlood(placementLocation);
            if (!canPlaceBlockToBlood) {
                return;
            }
            setWallBlockToBlood(placementLocation, BlockFace.EAST);
            return;
        }

        if (faceOfBlockRaycastHit == BlockFace.WEST) {
            var placementLocation = hitBlock.getRelative(-1, 0, 0);
            boolean canPlaceBlockToBlood = canPlaceBlockToBlood(placementLocation);
            if (!canPlaceBlockToBlood) {
                return;
            }
            setWallBlockToBlood(placementLocation, BlockFace.WEST);
            return;
        }

    }

    private static boolean canPlaceBlockToBlood(Block block) {
        return block.getType() == Material.AIR;
    }

    private static void setFloorBlockToBlood(Block randomBlock) {
        BlockData blockData = Bukkit.createBlockData(Material.DEAD_HORN_CORAL_FAN);

        // Cast to Waterlogged to access the waterlogged property.
        Waterlogged waterlogged = (Waterlogged) blockData;

        // Set the waterlogged property to false.
        waterlogged.setWaterlogged(false);

        // Set the block to have the new BlockData.
        randomBlock.setBlockData(blockData);
    }

    private static void setWallBlockToBlood(Block target, BlockFace blockFace) {
        BlockData blockData = Bukkit.createBlockData(Material.DEAD_HORN_CORAL_WALL_FAN);

        // Cast to Waterlogged to access the waterlogged property.
        Waterlogged waterlogged = (Waterlogged) blockData;

        // Set the waterlogged property to false.
        waterlogged.setWaterlogged(false);

        // Set the block to have the new BlockData.
        var bloodBlock = (CoralWallFan) blockData;
        bloodBlock.setFacing(blockFace);

        target.setBlockData(blockData);
    }

}
