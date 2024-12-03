package starandserpent.minecraft.criticalfixes;

import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDamageAbortEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public class BlockBreakSpeedFix implements Listener {

    private final JavaPlugin plugin;
    private Server server;

    private final Map<Material, Integer> blocksWithCustomBreakingTime = new HashMap<>() {{
        put(Material.COMPARATOR, DEFAULT_BREAK_TIME);
        put(Material.REPEATER, DEFAULT_BREAK_TIME);
        put(Material.REDSTONE_TORCH, 5);
        put(Material.REDSTONE_WALL_TORCH, 5);
        put(Material.REDSTONE_WIRE, DEFAULT_BREAK_TIME);
        put(Material.TRIPWIRE, DEFAULT_BREAK_TIME);
        put(Material.TRIPWIRE_HOOK, DEFAULT_BREAK_TIME);
        put(Material.DECORATED_POT, DEFAULT_BREAK_TIME);
        put(Material.END_ROD, DEFAULT_BREAK_TIME);
        put(Material.FIRE, 5);
        put(Material.FLOWER_POT, DEFAULT_BREAK_TIME);
        put(Material.FROGSPAWN, DEFAULT_BREAK_TIME);
        put(Material.HONEY_BLOCK, DEFAULT_BREAK_TIME);
        put(Material.INFESTED_COBBLESTONE, DEFAULT_BREAK_TIME);
        put(Material.INFESTED_DEEPSLATE, DEFAULT_BREAK_TIME);
        put(Material.INFESTED_STONE, DEFAULT_BREAK_TIME);
        put(Material.INFESTED_CHISELED_STONE_BRICKS, DEFAULT_BREAK_TIME);
        put(Material.INFESTED_CRACKED_STONE_BRICKS, DEFAULT_BREAK_TIME);
        put(Material.INFESTED_MOSSY_STONE_BRICKS, DEFAULT_BREAK_TIME);
        put(Material.INFESTED_STONE_BRICKS, DEFAULT_BREAK_TIME);
        put(Material.SCAFFOLDING, DEFAULT_BREAK_TIME);
        put(Material.SLIME_BLOCK, DEFAULT_BREAK_TIME);
        put(Material.SOUL_FIRE, DEFAULT_BREAK_TIME);
        put(Material.SOUL_TORCH, DEFAULT_BREAK_TIME);
        put(Material.SOUL_WALL_TORCH, DEFAULT_BREAK_TIME);
        put(Material.TNT, DEFAULT_BREAK_TIME);
        put(Material.TORCH, 10);
        put(Material.WALL_TORCH, 10);

        put(Material.AZALEA, DEFAULT_BREAK_TIME);
        put(Material.BEETROOTS, DEFAULT_BREAK_TIME);
        put(Material.CARROTS, DEFAULT_BREAK_TIME);
        // Corals are handled added as tag because there are so many of them.
        put(Material.DEAD_BUSH, DEFAULT_BREAK_TIME);
        put(Material.FERN, DEFAULT_BREAK_TIME);
        // Flowers are handled as tag too.
        put(Material.BROWN_MUSHROOM, DEFAULT_BREAK_TIME);
        put(Material.RED_MUSHROOM, DEFAULT_BREAK_TIME);
        put(Material.CRIMSON_FUNGUS, DEFAULT_BREAK_TIME);
        put(Material.WARPED_FUNGUS, DEFAULT_BREAK_TIME);
        put(Material.SHORT_GRASS, DEFAULT_BREAK_TIME);
        put(Material.TALL_GRASS, DEFAULT_BREAK_TIME);
        put(Material.HANGING_ROOTS, DEFAULT_BREAK_TIME);
        put(Material.KELP_PLANT, DEFAULT_BREAK_TIME);
        put(Material.LILY_PAD, DEFAULT_BREAK_TIME);
        put(Material.MANGROVE_PROPAGULE, DEFAULT_BREAK_TIME);
        put(Material.MELON_STEM, DEFAULT_BREAK_TIME);
        put(Material.NETHER_SPROUTS, DEFAULT_BREAK_TIME);
        put(Material.NETHER_WART, DEFAULT_BREAK_TIME);
        put(Material.PINK_PETALS, DEFAULT_BREAK_TIME);
        put(Material.PITCHER_PLANT, DEFAULT_BREAK_TIME);
        put(Material.POTATOES, DEFAULT_BREAK_TIME);
        put(Material.PUMPKIN_STEM, DEFAULT_BREAK_TIME);
        put(Material.CRIMSON_ROOTS, DEFAULT_BREAK_TIME);
        put(Material.MANGROVE_ROOTS, DEFAULT_BREAK_TIME);
        put(Material.WARPED_ROOTS, DEFAULT_BREAK_TIME);
        put(Material.MUDDY_MANGROVE_ROOTS, DEFAULT_BREAK_TIME);
        // Saplings are added as tag too.
        put(Material.SEAGRASS, DEFAULT_BREAK_TIME);
        put(Material.SEA_PICKLE, DEFAULT_BREAK_TIME);
        put(Material.SPORE_BLOSSOM, DEFAULT_BREAK_TIME);
        put(Material.SUGAR_CANE, DEFAULT_BREAK_TIME);
        put(Material.SWEET_BERRY_BUSH, DEFAULT_BREAK_TIME);
        put(Material.TALL_SEAGRASS, DEFAULT_BREAK_TIME);
        put(Material.LARGE_FERN, DEFAULT_BREAK_TIME);
        put(Material.TWISTING_VINES_PLANT, DEFAULT_BREAK_TIME);
        put(Material.WEEPING_VINES_PLANT, DEFAULT_BREAK_TIME);
        put(Material.WHEAT, DEFAULT_BREAK_TIME);

        // Blood.
        put(Material.DEAD_HORN_CORAL_FAN, DEFAULT_BREAK_TIME);
        put(Material.DEAD_HORN_CORAL_WALL_FAN, DEFAULT_BREAK_TIME);
    }};

    // Default break time for blocks that break instantly in vanilla Minecraft
    private static final int DEFAULT_BREAK_TIME = 20; // 1 second (20 ticks)

    private Map<Location, BlockBreakTask> activeBreakTasks = new HashMap<>();


    public BlockBreakSpeedFix(JavaPlugin plugin) {
        this.plugin = plugin;
        server = plugin.getServer();
    }

    @EventHandler public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        resetBlockBreakSpeed(player);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockDamageStart(BlockDamageEvent event) {

        resetBlockBreakSpeed(event.getPlayer());

        // No custom speed, use normal vanilla Minecraft speed.
        if (!blocksWithCustomBreakingTime.containsKey(event.getBlock().getType())) {
            allowNormalBreakingSpeed(event.getPlayer());
//            System.out.println("Block (" + event.getBlock() + ") doesn't have custom breaking time.");
            var breakSpeed = event.getPlayer().getAttribute(Attribute.PLAYER_BLOCK_BREAK_SPEED);
//            System.out.println("Player block break speed has been set to: " + breakSpeed);
        // Use custom breaking speed.
        } else {
            event.setCancelled(true);
            Player player = event.getPlayer();
            Location blockLocation = event.getBlock().getLocation();
            int breakTime = blocksWithCustomBreakingTime.getOrDefault(event.getBlock().getType(), DEFAULT_BREAK_TIME);
            startBlockBreaking(blockLocation, player, breakTime);
//            System.out.println("Block (" + event.getBlock() + ") has a custom breaking time: " + breakTime);
            var breakSpeed = event.getPlayer().getAttribute(Attribute.PLAYER_BLOCK_BREAK_SPEED);
//            System.out.println("Player block break speed has been set to: " + breakSpeed);
        }
    }


    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockDamageEnd(BlockDamageAbortEvent event) {
        // Reset player's block break speed
        resetBlockBreakSpeed(event.getPlayer());

        // And clean up any potential tasks.
        cleanupBreakTask(event.getBlock().getLocation());
    }

    private void startBlockBreaking(Location blockLocation, Player player, int breakTime) {

        // Check if the player is already breaking a block, a different block.
        BlockBreakTask existingTask = activeBreakTasks.get(blockLocation);
        if (existingTask != null) {

            // In which case we must cancel the existing task or terrible things will happen.
            cleanupBreakTask(existingTask.BlockLocation);
        }

        BlockBreakTask task = new BlockBreakTask(blockLocation, player, breakTime);

        task.Task = server.getScheduler().runTaskTimer(plugin, () -> {
            updateBlockDamage(task);
        }, 0L, 1L);

        activeBreakTasks.put(blockLocation, task);
    }


    private void updateBlockDamage(BlockBreakTask task) {
        float damageIncrement = 1.0f / task.BreakTime;

        task.CurrentDamage += damageIncrement;

        if (task.CurrentDamage >= 1.0f) {
            task.CurrentDamage = 1.0f;
            breakBlock(task.BlockLocation, task.Player);
        } else {
            task.Player.sendBlockDamage(task.BlockLocation, task.CurrentDamage, -2);
        }
    }


    private void breakBlock(Location blockLocation, Player damager) {
        // Reset player's block break speed
        resetBlockBreakSpeed(damager);

        cleanupBreakTask(blockLocation);

        Block block = blockLocation.getBlock();

        // Get the block and play break sound.
        var soundGroup = block.getBlockData().getSoundGroup();
        var breakSound = soundGroup.getBreakSound();
        var volume = soundGroup.getVolume();
        var pitch = soundGroup.getPitch();
        block.getWorld().playSound(blockLocation, breakSound, volume, pitch);

        // Show block break particles.
        // particle, location, particle amount, ?, offset x, offset y, offset z, data
        var world = block.getWorld();
        // Get the item from material of the block for particle effects.
        var blockData = block.getBlockData();
        world.spawnParticle(Particle.BLOCK, blockLocation.add(0.5,0.5,0.5),10, 1, 0.1,0.1,0.1, blockData);

        // Break the block naturally.
        block.breakNaturally();

        // Reset the block break speed for the player.
        resetBlockBreakSpeed(damager);
    }


    private void cleanupBreakTask(Location blockLocation) {
        BlockBreakTask task = activeBreakTasks.remove(blockLocation);

        if (task != null) {
            if (task.Task != null) {
                task.Task.cancel();
            }

            // Reset damage visual state to no damage.
            server.getScheduler().runTaskLater(plugin, () -> {
                task.Player.sendBlockDamage(blockLocation, 0.0f, -2);
            }, 2L);

            resetBlockBreakSpeed(task.Player);
        }

    }

    // Reset the player's block break speed to default value, which is 0.0, meaning no block breaking.
    private void resetBlockBreakSpeed(Player player) {
        var playerBlockBreakSpeed = player.getAttribute(Attribute.PLAYER_BLOCK_BREAK_SPEED);
        if (playerBlockBreakSpeed == null) {
            return;
        }
        playerBlockBreakSpeed.setBaseValue(0.0);
    }


    // Allow the player to break blocks at normal speed, which is 1.0, meaning no modification to block breaking speed.
    private void allowNormalBreakingSpeed(Player player) {
        var playerBlockBreakSpeed = player.getAttribute(Attribute.PLAYER_BLOCK_BREAK_SPEED);
        if (playerBlockBreakSpeed == null) {
            return;
        }
        playerBlockBreakSpeed.setBaseValue(1.0);
    }


}
