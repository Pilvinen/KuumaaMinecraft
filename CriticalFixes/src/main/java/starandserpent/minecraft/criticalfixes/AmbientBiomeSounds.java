package starandserpent.minecraft.criticalfixes;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.List;

// Play custom ambient sounds for the player depending on the biome they are in and the time of the day and/or height.
public class AmbientBiomeSounds implements Listener {

    private final JavaPlugin plugin;
    private Server server;

    // biomeAmbience Map: Maintain a HashMap<Biome, SoundData> where Biome is the biome enum and SoundData
    // is a custom class containing the sound (Sound) and its duration (int). This map stores the sound
    // information for each biome.

    private static HashMap<Biome, SoundData> biomeAmbience = new HashMap<>() {{
        put(Biome.BADLANDS, Sounds.biomeSounds.get(Biome.BADLANDS));
        put(Biome.BAMBOO_JUNGLE, Sounds.biomeSounds.get(Biome.BAMBOO_JUNGLE));
        put(Biome.BASALT_DELTAS, Sounds.biomeSounds.get(Biome.BASALT_DELTAS));
        put(Biome.BEACH, Sounds.biomeSounds.get(Biome.BEACH));
        put(Biome.BIRCH_FOREST, Sounds.biomeSounds.get(Biome.BIRCH_FOREST));
        put(Biome.CHERRY_GROVE, Sounds.biomeSounds.get(Biome.CHERRY_GROVE));
        put(Biome.COLD_OCEAN, Sounds.biomeSounds.get(Biome.COLD_OCEAN));
        put(Biome.CRIMSON_FOREST, Sounds.biomeSounds.get(Biome.CRIMSON_FOREST));
        put(Biome.DARK_FOREST, Sounds.biomeSounds.get(Biome.DARK_FOREST));
        put(Biome.DEEP_COLD_OCEAN, Sounds.biomeSounds.get(Biome.DEEP_COLD_OCEAN));
        put(Biome.DEEP_DARK, Sounds.biomeSounds.get(Biome.DEEP_DARK));
        put(Biome.DEEP_FROZEN_OCEAN, Sounds.biomeSounds.get(Biome.DEEP_FROZEN_OCEAN));
        put(Biome.DEEP_LUKEWARM_OCEAN, Sounds.biomeSounds.get(Biome.DEEP_LUKEWARM_OCEAN));
        put(Biome.DEEP_OCEAN, Sounds.biomeSounds.get(Biome.DEEP_OCEAN));
        put(Biome.DESERT, Sounds.biomeSounds.get(Biome.DESERT));
        put(Biome.DRIPSTONE_CAVES, Sounds.biomeSounds.get(Biome.DRIPSTONE_CAVES));
        put(Biome.END_BARRENS, Sounds.biomeSounds.get(Biome.END_BARRENS));
        put(Biome.END_HIGHLANDS, Sounds.biomeSounds.get(Biome.END_HIGHLANDS));
        put(Biome.END_MIDLANDS, Sounds.biomeSounds.get(Biome.END_MIDLANDS));
        put(Biome.ERODED_BADLANDS, Sounds.biomeSounds.get(Biome.ERODED_BADLANDS));
        put(Biome.FLOWER_FOREST, Sounds.biomeSounds.get(Biome.FLOWER_FOREST));
        put(Biome.FOREST, Sounds.biomeSounds.get(Biome.FOREST));
        put(Biome.FROZEN_OCEAN, Sounds.biomeSounds.get(Biome.FROZEN_OCEAN));
        put(Biome.FROZEN_PEAKS, Sounds.biomeSounds.get(Biome.FROZEN_PEAKS));
        put(Biome.FROZEN_RIVER, Sounds.biomeSounds.get(Biome.FROZEN_RIVER));
        put(Biome.GROVE, Sounds.biomeSounds.get(Biome.GROVE));
        put(Biome.ICE_SPIKES, Sounds.biomeSounds.get(Biome.ICE_SPIKES));
        put(Biome.JAGGED_PEAKS, Sounds.biomeSounds.get(Biome.JAGGED_PEAKS));
        put(Biome.JUNGLE, Sounds.biomeSounds.get(Biome.JUNGLE));
        put(Biome.LUKEWARM_OCEAN, Sounds.biomeSounds.get(Biome.LUKEWARM_OCEAN));
        put(Biome.LUSH_CAVES, Sounds.biomeSounds.get(Biome.LUSH_CAVES));
        put(Biome.MANGROVE_SWAMP, Sounds.biomeSounds.get(Biome.MANGROVE_SWAMP));
        put(Biome.MEADOW, Sounds.biomeSounds.get(Biome.MEADOW));
        put(Biome.MUSHROOM_FIELDS, Sounds.biomeSounds.get(Biome.MUSHROOM_FIELDS));
        put(Biome.NETHER_WASTES, Sounds.biomeSounds.get(Biome.NETHER_WASTES));
        put(Biome.OCEAN, Sounds.biomeSounds.get(Biome.OCEAN));
        put(Biome.OLD_GROWTH_BIRCH_FOREST, Sounds.biomeSounds.get(Biome.OLD_GROWTH_BIRCH_FOREST));
        put(Biome.OLD_GROWTH_PINE_TAIGA, Sounds.biomeSounds.get(Biome.OLD_GROWTH_PINE_TAIGA));
        put(Biome.OLD_GROWTH_SPRUCE_TAIGA, Sounds.biomeSounds.get(Biome.OLD_GROWTH_SPRUCE_TAIGA));
        put(Biome.PLAINS, Sounds.biomeSounds.get(Biome.PLAINS));
        put(Biome.RIVER, Sounds.biomeSounds.get(Biome.RIVER));
        put(Biome.SAVANNA, Sounds.biomeSounds.get(Biome.SAVANNA));
        put(Biome.SAVANNA_PLATEAU, Sounds.biomeSounds.get(Biome.SAVANNA_PLATEAU));
        put(Biome.SMALL_END_ISLANDS, Sounds.biomeSounds.get(Biome.SMALL_END_ISLANDS));
        put(Biome.SNOWY_BEACH, Sounds.biomeSounds.get(Biome.SNOWY_BEACH));
        put(Biome.SNOWY_PLAINS, Sounds.biomeSounds.get(Biome.SNOWY_PLAINS));
        put(Biome.SNOWY_SLOPES, Sounds.biomeSounds.get(Biome.SNOWY_SLOPES));
        put(Biome.SNOWY_TAIGA, Sounds.biomeSounds.get(Biome.SNOWY_TAIGA));
        put(Biome.SOUL_SAND_VALLEY, Sounds.biomeSounds.get(Biome.SOUL_SAND_VALLEY));
        put(Biome.SPARSE_JUNGLE, Sounds.biomeSounds.get(Biome.SPARSE_JUNGLE));
        put(Biome.STONY_PEAKS, Sounds.biomeSounds.get(Biome.STONY_PEAKS));
        put(Biome.STONY_SHORE, Sounds.biomeSounds.get(Biome.STONY_SHORE));
        put(Biome.SUNFLOWER_PLAINS, Sounds.biomeSounds.get(Biome.SUNFLOWER_PLAINS));
        put(Biome.SWAMP, Sounds.biomeSounds.get(Biome.SWAMP));
        put(Biome.TAIGA, Sounds.biomeSounds.get(Biome.TAIGA));
        put(Biome.THE_END, Sounds.biomeSounds.get(Biome.THE_END));
        put(Biome.THE_VOID, Sounds.biomeSounds.get(Biome.THE_VOID));
        put(Biome.WARM_OCEAN, Sounds.biomeSounds.get(Biome.WARM_OCEAN));
        put(Biome.WARPED_FOREST, Sounds.biomeSounds.get(Biome.WARPED_FOREST));
        put(Biome.WINDSWEPT_FOREST, Sounds.biomeSounds.get(Biome.WINDSWEPT_FOREST));
        put(Biome.WINDSWEPT_GRAVELLY_HILLS, Sounds.biomeSounds.get(Biome.WINDSWEPT_GRAVELLY_HILLS));
        put(Biome.WINDSWEPT_HILLS, Sounds.biomeSounds.get(Biome.WINDSWEPT_HILLS));
        put(Biome.WINDSWEPT_SAVANNA, Sounds.biomeSounds.get(Biome.WINDSWEPT_SAVANNA));
        put(Biome.WOODED_BADLANDS, Sounds.biomeSounds.get(Biome.WOODED_BADLANDS));
    }};

    // Logged in players ambience play length.
    private static final HashMap<Player, PlayerAmbience> playerAmbienceSubscribers = new HashMap<>();

    // Constructor.
    public AmbientBiomeSounds(JavaPlugin plugin) {
        this.plugin = plugin;
        this.server = plugin.getServer();

        // Start a scheduler to loop all players every X seconds and check if:
        // - Biome has changed.
        // - Ambient sound has stopped.
        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {

                var playerAmbience = playerAmbienceSubscribers.get(player);
                if (playerAmbience == null) {
                    playAmbience(player);
                    continue;
                }

                // Check if biome has changed since last time.
                var lastBiome = playerAmbience.getLastBiome();
                var currentBiome = getBiome(player);
                if (lastBiome != currentBiome) {
                    // Stop playing the old sound and start playing a new sound.
                    if (playerAmbience.isPlaying(player, server)) {
                        // Avoid edge cases with the scheduler by stopping both night and day sounds even if
                        // one of them is not playing.
                        player.stopSound(playerAmbience.soundData.getSound() + ".day");
                        player.stopSound(playerAmbience.soundData.getSound() + ".night");
                        playAmbience(player);
                        continue;
                    // Biome has changed, but the player is no longer playing a sound, play a new sound.
                    } else {
                        playAmbience(player);
                        continue;
                    }
                }

                // The biome is the same as before, check if the sound is still playing
                if (playerAmbience.isPlaying(player, server)) {
                    continue;
                }

                // The player is not playing any sounds, play a new one.
                playAmbience(player);
            }
        }, 0, 60);
    }

    public static void TurnedIntoNight(Server server) {
        // Loop all players and stop their current sounds and start playing night sounds.
        for (Player player : Bukkit.getOnlinePlayers()) {
            var playerAmbience = playerAmbienceSubscribers.get(player);
            if (playerAmbience != null) {
                if (playerAmbience.isPlaying(player, server)) {
                    player.stopSound(playerAmbience.soundData.getSound() + ".day");
                    player.stopSound(playerAmbience.soundData.getSound() + ".night");
                }
                playAmbience(player);
            }
        }
    }

    public static void TurnedIntoDay(Server server) {
        // Loop all players and stop their current sounds and start playing day sounds.
        for (Player player : Bukkit.getOnlinePlayers()) {
            var playerAmbience = playerAmbienceSubscribers.get(player);
            if (playerAmbience != null) {
                if (playerAmbience.isPlaying(player, server)) {
                    player.stopSound(playerAmbience.soundData.getSound() + ".day");
                    player.stopSound(playerAmbience.soundData.getSound() + ".night");
                }
                playAmbience(player);
            }
        }
    }

    // On player joining the server start playing ambience fitting the biome.
    @EventHandler public void onPlayerJoin(PlayerJoinEvent event) {
        var player = event.getPlayer();
        playAmbience(player);
    }

    // On player leaving the server or being disconnected.
    @EventHandler public void onPlayerQuit(PlayerQuitEvent event) {
        var player = event.getPlayer();
        playerAmbienceSubscribers.remove(player);
    }


    // Helper methods
    // **************

    private static String getSoundTimeOfDaySuffix(Player player) {
        return isDay(player) ? ".day" : ".night";
    }

    // Either true for day false for night.
    private static boolean isDay(Player player) {
        var time = player.getWorld().getTime();
        if (time >= 0 && time < 16000) { // 0 ticks = 6:00, 16000 ticks = 22:00
            return true;
        } else {
            return false;
        }
    }

    private static void playAmbience(Player player) {
        Biome lastBiome = getBiome(player);
        SoundData biomeSoundData = getAmbientSoundData(player);
        if (biomeSoundData != null) {
            playerAmbienceSubscribers.put(player, new PlayerAmbience(biomeSoundData, lastBiome));
            player.playSound(player, biomeSoundData.getSound() + getSoundTimeOfDaySuffix(player), 1.0f, 1.0f);
        } else {
            System.out.println("CustomSounds: playAmbience called, but no sound data found for biome "+ lastBiome);
        }
    }

    private static SoundData getAmbientSoundData(Player player) {
        var biomeSoundData = biomeAmbience.get(getBiome(player));
        return biomeSoundData;
    }

    private static Biome getBiome(Player player) {
        var location = player.getLocation();
        var block = location.getBlock();
        var biome = block.getBiome();
        return biome;
    }


}
