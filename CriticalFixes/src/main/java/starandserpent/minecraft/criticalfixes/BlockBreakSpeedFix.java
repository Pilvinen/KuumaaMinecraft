package starandserpent.minecraft.criticalfixes;

import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDamageAbortEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BlockBreakSpeedFix implements Listener {

    private final JavaPlugin plugin;
    private Server server;

    private List<Material> instantlyBreakingBlocks = new ArrayList<>() {{
        add(Material.COMPARATOR);
        add(Material.REPEATER);
        add(Material.REDSTONE_TORCH);
        add(Material.REDSTONE_WALL_TORCH);
        add(Material.REDSTONE_WIRE);
        add(Material.TRIPWIRE);
        add(Material.TRIPWIRE_HOOK);
        add(Material.DECORATED_POT);
        add(Material.END_ROD);
        add(Material.FIRE);
        add(Material.FLOWER_POT);
        add(Material.FROGSPAWN);
        add(Material.HONEY_BLOCK);
        add(Material.INFESTED_COBBLESTONE);
        add(Material.INFESTED_DEEPSLATE);
        add(Material.INFESTED_STONE);
        add(Material.INFESTED_CHISELED_STONE_BRICKS);
        add(Material.INFESTED_CRACKED_STONE_BRICKS);
        add(Material.INFESTED_MOSSY_STONE_BRICKS);
        add(Material.INFESTED_STONE_BRICKS);
        add(Material.SCAFFOLDING);
        add(Material.SLIME_BLOCK);
        add(Material.SOUL_FIRE);
        add(Material.SOUL_TORCH);
        add(Material.SOUL_WALL_TORCH);
        add(Material.TNT);
        add(Material.TORCH);
        add(Material.WALL_TORCH);

        add(Material.AZALEA);
        add(Material.BEETROOTS);
        add(Material.CARROTS);
        // Corals are handled added as tag because there are so many of them.
        add(Material.DEAD_BUSH);
        add(Material.FERN);
        // Flowers are handled as tag too.
        add(Material.BROWN_MUSHROOM);
        add(Material.RED_MUSHROOM);
        add(Material.CRIMSON_FUNGUS);
        add(Material.WARPED_FUNGUS);
        add(Material.SHORT_GRASS);
        add(Material.TALL_GRASS);
        add(Material.HANGING_ROOTS);
        add(Material.KELP_PLANT);
        add(Material.LILY_PAD);
        add(Material.MANGROVE_PROPAGULE);
        add(Material.MELON_STEM);
        add(Material.NETHER_SPROUTS);
        add(Material.NETHER_WART);
        add(Material.PINK_PETALS);
        add(Material.PITCHER_PLANT);
        add(Material.POTATOES);
        add(Material.PUMPKIN_STEM);
        add(Material.CRIMSON_ROOTS);
        add(Material.MANGROVE_ROOTS);
        add(Material.WARPED_ROOTS);
        add(Material.MUDDY_MANGROVE_ROOTS);
        // Saplings are added as tag too.
        add(Material.SEAGRASS);
        add(Material.SEA_PICKLE);
        add(Material.SPORE_BLOSSOM);
        add(Material.SUGAR_CANE);
        add(Material.SWEET_BERRY_BUSH);
        add(Material.TALL_GRASS);
        add(Material.TALL_SEAGRASS);
        add(Material.LARGE_FERN);
        add(Material.TWISTING_VINES_PLANT);
        add(Material.WEEPING_VINES_PLANT);
        add(Material.WHEAT);

        // Blood.
        add(Material.DEAD_HORN_CORAL_FAN);
        add(Material.DEAD_HORN_CORAL_WALL_FAN);
    }};

    // HashMap of Bukkit timers for blocks.
    private HashMap<Location, BukkitTask> increaseBlockDamageTimers = new HashMap<>();

    // HashMap of block damage for locations.
    private HashMap<Location, Float> blockDamages = new HashMap<>();

    private HashMap<Location, BukkitTask> blockAnimationTimers = new HashMap<>();

    public BlockBreakSpeedFix(JavaPlugin plugin) {
        this.plugin = plugin;
        server = plugin.getServer();
    }

    @EventHandler public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        resetBlockBreakSpeed(player);
    }

    @EventHandler(priority = EventPriority.HIGHEST) public void onBlockDamageStart(BlockDamageEvent event) {

        // If player is holding a tool, increase block break speed.
        var player = event.getPlayer();
        var block = event.getBlock();
        var blockType = block.getType();

//        var toolType = player.getInventory().getItemInMainHand().getType();

        // If not an instantly breaking block, exit early.
        if (!instantlyBreakingBlocks.contains(blockType)
                && !Tag.CORALS.isTagged(blockType)
                && !Tag.FLOWERS.isTagged(blockType)
                && !Tag.SAPLINGS.isTagged(blockType)) {

            allowNormalBreakingSpeed(player);

            // Show breaking speed.
            var breakSpeed = player.getAttribute(Attribute.PLAYER_BLOCK_BREAK_SPEED);
            if (breakSpeed == null) {
                return;
            }
            return;
        }

        var blockLocation = block.getLocation();

        // If there's no blockDamages entry for this location, initialize block damage.
        if (!blockDamages.containsKey(blockLocation)) {
            // Create or refresh a new timer for subtracting any block damage.
            createBlockDamageTimer(blockLocation, player);
        }
    }


    @EventHandler public void onBlockDamageEnd(BlockDamageAbortEvent event) {

        var player = event.getPlayer();

        // If player is holding a tool, increase block break speed.
        var block = event.getBlock();

        // Get block location.
        var blockLocation = block.getLocation();

        // Remove block damage and timer.
        blockDamages.remove(blockLocation);
        var increaseBlockDamageTimer = increaseBlockDamageTimers.get(blockLocation);
        if (increaseBlockDamageTimer != null) {
            increaseBlockDamageTimer.cancel();
        }
        increaseBlockDamageTimers.remove(blockLocation);

        resetBlockBreakSpeed(player);

        // Do a bukkit delay of 2 ticks before resetting sendBlockDamage to 0.0f.
        server.getScheduler().runTaskLater(plugin, () -> {
            player.sendBlockDamage(blockLocation, 0.0f, -2);
        }, 2L);

    }

    private void resetBlockBreakSpeed(Player player) {
        var playerBlockBreakSpeed = player.getAttribute(Attribute.PLAYER_BLOCK_BREAK_SPEED);
        if (playerBlockBreakSpeed == null) {
            return;
        }
        playerBlockBreakSpeed.setBaseValue(0.0);
    }

    private void allowNormalBreakingSpeed(Player player) {
        var playerBlockBreakSpeed = player.getAttribute(Attribute.PLAYER_BLOCK_BREAK_SPEED);
        if (playerBlockBreakSpeed == null) {
            return;
        }
        playerBlockBreakSpeed.setBaseValue(1.0);
    }

    // Create a new timer.
    private void createBlockDamageTimer(Location blockLocation, Player player) {

        BukkitTask increaseBlockDamageTimer = server.getScheduler().runTaskTimer(plugin, () -> {
            // Every time the timer runs out, subtract block damage.
            var blockDamage = blockDamages.get(blockLocation);
            if (blockDamage == null) {
                blockDamage = 0.0f; // Initialize to default value
                blockDamages.put(blockLocation, blockDamage);
            }

            damageBlock(blockLocation, player);
            blockDamage = blockDamages.get(blockLocation);
            // If block damage is zero or less, remove the timer and remove the block damage.
            if (blockDamage == null) {
                return;
            }

            if (blockDamage >= 1.0f) {
                var taskToCancel = increaseBlockDamageTimers.get(blockLocation);
                taskToCancel.cancel();
                increaseBlockDamageTimers.remove(blockLocation);
                blockDamages.remove(blockLocation);
                resetBlockBreakSpeed(player);
            }

        }, 0L, 10L);

        // Create a separate timer that runs every tick
        BukkitTask blockAnimationTimer = server.getScheduler().runTaskTimer(plugin, () -> {
            // Get the current block damage
            var blockDamage = blockDamages.get(blockLocation);
            if (blockDamage != null) {
                // Send the block damage packet to the player

                player.sendBlockDamage(blockLocation, blockDamage, -2);

                if (blockDamage >= 1.0f) {
                    var blockAnimationTimerReference = blockAnimationTimers.get(blockLocation);
                    blockAnimationTimers.remove(blockLocation);
                    blockAnimationTimerReference.cancel();
                }
            }


        }, 0L, 10L);

        // Store the reference to the block animation timer
        blockAnimationTimers.put(blockLocation, blockAnimationTimer);

        // Attempt to get old timer reference.
        var oldBlockDamageTimer = increaseBlockDamageTimers.get(blockLocation);
        if (oldBlockDamageTimer != null) {
            oldBlockDamageTimer.cancel();
        }

        increaseBlockDamageTimers.put(blockLocation, increaseBlockDamageTimer);
    }

    private void damageBlock(Location blockLocation, Player damager) {

        // Get the block damage object.
        var blockDamage = blockDamages.get(blockLocation);

        // Increase block damage.
        blockDamage += 0.25f;

        // If more than 1.0, set to 1.0.
        if (blockDamage > 1.0f) {
            blockDamage = 1.0f;
        }

        blockDamages.put(blockLocation, blockDamage);

        // If block damage is 1.0, break the block.
        if (blockDamage >= 1.0f) {
            breakBlock(blockLocation, damager);
        }
    }

    private void breakBlock(Location blockLocation, Player damager) {
        blockDamages.remove(blockLocation);
        // Get timer
        var reduceBlockDamageTimer = increaseBlockDamageTimers.get(blockLocation);
        if (reduceBlockDamageTimer != null) {
            reduceBlockDamageTimer.cancel();
        }

        increaseBlockDamageTimers.remove(blockLocation);
        var block = blockLocation.getBlock();
        // Get block break sound.
        var soundGroup = block.getBlockData().getSoundGroup();
        var breakSound = soundGroup.getBreakSound();
        var volume = soundGroup.getVolume();
        var pitch = soundGroup.getPitch();
        block.getWorld().playSound(blockLocation, breakSound, volume, pitch);
        // Show block break particles.
        var world = block.getWorld();

        // Get the item from material of the block for particle effects.
        var blockData = block.getBlockData();

        // particle, location, particle amount, ?, offset x, offset y, offset z, data
        world.spawnParticle(Particle.BLOCK, blockLocation.add(0.5,0.5,0.5),10, 1, 0.1,0.1,0.1, blockData);

        block.breakNaturally();
        resetBlockBreakSpeed(damager);
    }

}
