package starandserpent.minecraft.criticalfixes;

import com.nivixx.ndatabase.api.NDatabase;
import com.nivixx.ndatabase.api.repository.Repository;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Snow;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.Objects;

import static net.advancedplugins.seasons.data.StorageHandler.getSeason;

public class SeasonsManagementSystem implements Listener {

    private final JavaPlugin plugin;
    private Server server;
    final Repository<String, SeasonsData> repository;

    private BukkitTask snowRunnable;

    private boolean isRaining = false;
    private boolean isWinter = false;

    // Timer for incrementing the seasonChangeTimer in SeasonsData.
    private BukkitRunnable seasonTimer;

    private long timerIncrement = 10;

    // The value should be 108000 seconds for 30 hours per season transition.
    // With a total of 4 seasons each with 4 transitions this would be 480 IRL hours
    // or 20 IRL days for a full year.
    // Respectively one season will last 120 IRL hours or 5 IRL days.
    private final long maxTimerValueWhenSeasonChanges = 108000;

    // DEBUG: 100 seconds for testing.
//    private long maxTimerValueWhenSeasonChanges = 100;

    // We only need a single entry, so we can use a constant key.
    private final String seasonsDataKey = "seasons_data_key";

    public SeasonsManagementSystem(JavaPlugin plugin) {
        this.plugin = plugin;
        server = plugin.getServer();

        // Get the NDatabase repository.
        repository = NDatabase.api().getOrCreateRepository(SeasonsData.class);

        seasonTimer = new BukkitRunnable() {

            @Override
            public void run() {

                // Get the data.
                repository.getAsync(seasonsDataKey).getResultFuture().thenAccept(seasonsData -> {

                    if (seasonsData == null) {
                        // Create a new entry if it doesn't exist.
                        seasonsData = new SeasonsData(seasonsDataKey);
                        repository.upsert(seasonsData);
                    }

                    // Increment the database timer.
                    seasonsData.setSeasonTime(seasonsData.getSeasonTime() + timerIncrement);

                    // If the timer reaches max value, the season changes.
                    if (seasonsData.getSeasonTime() >= maxTimerValueWhenSeasonChanges) {

                        // Reset the timer.
                        seasonsData.resetSeasonTime();

                        // Schedule the command dispatching to be run on the main server thread
                        plugin.getServer().getScheduler().runTask(plugin, () -> {
                            server.dispatchCommand(new SilentCommandSender(), "seasons nextseason Kuumaa");
                        });
                    }

                    // Update the database entry.
                    repository.upsert(seasonsData);
                });
            }

        };

        // Run the timer every 20 ticks * 10 seconds.
        seasonTimer.runTaskTimer(plugin, 0, 20 * timerIncrement);
    }


    @EventHandler public void onWeatherChange(WeatherChangeEvent event) {

//        System.out.println("SeasonsManagementSystem: WeatherChangeEvent");

        // Only in the Kuumaa world.
        var world = event.getWorld();
        if (!Objects.equals(world.getName().toLowerCase(), "Kuumaa".toLowerCase())) {
//            System.out.println("SeasonsManagementSystem: World is not Kuumaa, it is: " + world.getName() + ".");
            return;
        }

        // If weather is rain.
        if (event.toWeatherState()) {
//            System.out.println("SeasonsManagementSystem: Setting isRaining to true.");
            isRaining = true;
        } else {
//            System.out.println("SeasonsManagementSystem: Setting isRaining to false.");
            isRaining = false;
        }

        // Check if it is winter from AdvancedSeasons API.
        var season = getSeason(world.getName()).getName();
        if (season.equals("Winter")) {
//            System.out.println("SeasonsManagementSystem: Setting isWinter to true.");
            isWinter = true;
        } else {
//            System.out.println("SeasonsManagementSystem: Setting isWinter to false.");
            isWinter = false;
        }

        // If it's winter and it's raining start BukkitRunnable which places snow on the ground
        // in loaded chunks.
        if (isWinter && isRaining) {

//            System.out.println("SeasonsManagementSystem: is winter and is raining.");

            if (snowRunnable != null) {
//                System.out.println("SeasonsManagementSystem: snowRunnable already exists.");
                return;
            }

            var bukkitRunnableSnowRunnable = new BukkitRunnable() {
                @Override
                public void run() {
                    // Place snow on the ground in loaded chunks.
                    var world = server.getWorld("Kuumaa");
                    if (world == null) {
                        return;
                    }

                    // Check if it's raining
                    if (!world.hasStorm()) {
//                        System.out.println("SeasonsManagementSystem: Stopping snowRunnable, it's not raining.");
                        isRaining = false;
                        cancel();
                        return;
                    }

                    Chunk[] loadedChunks = world.getLoadedChunks();

                    // Process all loaded chunks.
                    for (Chunk chunk : loadedChunks) {

                        // Get a single random block from the chunk.
                        var randomX = (int) (Math.random() * 16);
                        var randomZ = (int) (Math.random() * 16);

                        // Convert relative position to absolute position in the world.
                        var worldX = chunk.getX() * 16 + randomX;
                        var worldZ = chunk.getZ() * 16 + randomZ;

                        var highestY = world.getHighestBlockYAt(worldX, worldZ);
                        var randomBlock = world.getBlockAt(worldX, highestY, worldZ);
                        var blockAboveRandomBlock = world.getBlockAt(worldX, highestY + 1, worldZ);
                        boolean isBlockAboveRandomBlockValidTargetForSnow = isHighestBlockValidTargetForSnow(randomBlock, blockAboveRandomBlock);

                        if (isBlockAboveRandomBlockValidTargetForSnow) {
                            var blockDataForSnow = Material.SNOW.createBlockData();
                            // Set snow depth layers to 1.
                            ((Snow) blockDataForSnow).setLayers(1);
                            blockAboveRandomBlock.setBlockData(blockDataForSnow);
//                            System.out.println("SeasonsManagementSystem: Placed snow on the ground successfully!");
                        }

                    }

                }

                private boolean isHighestBlockValidTargetForSnow(Block randomBlock, Block blockAboveRandomBlock) {
                    if (randomBlock == null) {
//                        System.out.println("SeasonsManagementSystem: RandomBlock is null.");
                        return false;
                    }

                    if (blockAboveRandomBlock == null) {
//                        System.out.println("SeasonsManagementSystem: BlockAboveRandomBlock is null.");
                        return false;
                    }

                    if (!randomBlock.getType().isSolid()) {
//                        System.out.println("SeasonsManagementSystem: RandomBlock is not solid, it is: " + randomBlock.getType());
                        return false;
                    }

                    if (!blockAboveRandomBlock.getType().isAir()) {
                        return false;
                    }

                    // Check the temperature of the blockAboveRandomBlock, it needs to be 30 or less.
                    if (blockAboveRandomBlock.getTemperature() > 30) {
//                        System.out.println("SeasonsManagementSystem: BlockAboveRandomBlock temperature is too high: " + blockAboveRandomBlock.getTemperature() + ".");
                        return false;
                    }

//                    System.out.println("SeasonsManagementSystem: Temperature is valid for snow forming.");

                    // Create snow material data.
                    BlockData blockDataForSnow = Material.SNOW.createBlockData();
                    // Set layers to 1
                    Snow snow = (Snow) blockDataForSnow;
                    snow.setLayers(1);

                    if (!blockAboveRandomBlock.canPlace(snow)) {
//                        System.out.println("SeasonsManagementSystem: BlockAboveRandomBlock can't place snow according to MC. Let's see why. The random block is of type: " + randomBlock.getType() + ". And the block above is of type: " + blockAboveRandomBlock.getType() + ".");
                        return false;
                    }

//                    System.out.println("SeasonsManagementSystem: Found valid target for snow.");
                    return true;
                }

            };

            // Run every 1 second.
            bukkitRunnableSnowRunnable.runTaskTimer(plugin, 0, 20);

            return;
        }

        // Stop the snowRunnable if it's not winter or it's not raining.
        if (snowRunnable != null) {
//            System.out.println("SeasonsManagementSystem: Stopping snowRunnable, it's not winter and/or it's not raining.");
            snowRunnable.cancel();
        }


    }
}
