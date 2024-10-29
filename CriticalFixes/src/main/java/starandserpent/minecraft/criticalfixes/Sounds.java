package starandserpent.minecraft.criticalfixes;

import org.bukkit.block.Biome;

import java.util.HashMap;
import java.util.Map;

public class Sounds {

    // Real sound effect String mappings for biomeSounds. These are basically the sounds that will be played
    // when the above fake mappings are called. So a "FOREST" above might result on "DECIDUOUS_FOREST" below, but
    // a lot of other biomes might use the same sound effect. These, however, should not be used directly from
    // Minecraft, instead you should use the fake mappings above to always get the correct sound for the biome.
    // If the configuration changes, and you call directly for "DEAD_WIND" you will always get "DEAD_WIND", but
    // if you call for "GROVE" you will get the correct sound for the biome as it is configured at the time, it
    // might not be "DEAD_WIND" if the configuration changes and gets improved over time.

    private static final String DECIDUOUS_FOREST = "kuumaa:ambient.deciduous_forest";
    private static final String DEAD_WIND = "kuumaa:ambient.dead_wind";


    // Fake mappings for biomeSounds, for easy mapping.
    // IMPORTANT NOTE: If you change a biome's sound effect you need to:
    // 1) Change it here.
    // 2) Change it below in biomeSounds.
    // Eg.
    // private static final String FOREST = DECIDUOUS_FOREST; // Previously
    // private static final String FOREST = DEAD_WIND;        // Now
    // And:
    // put(Biome.FOREST, deciduousForest); // Previously
    // put(Biome.FOREST, deadWind);        // Now

    private static final String BADLANDS = "kuumaa:ambient.badlands";
    private static final String BAMBOO_JUNGLE = "kuumaa:ambient.bamboo_jungle";
    private static final String BASALT_DELTAS = "kuumaa:ambient.basalt_deltas";
    private static final String BEACH = "kuumaa:ambient.beach";
    private static final String BIRCH_FOREST = "kuumaa:ambient.birch_forest";
    private static final String CHERRY_GROVE = "kuumaa:ambient.cherry_grove";
    private static final String COLD_OCEAN = "kuumaa:ambient.cold_ocean";
    private static final String CRIMSON_FOREST = "kuumaa:ambient.crimson_forest";
    private static final String CUSTOM = "kuumaa:ambient.custom";
    private static final String DARK_FOREST = "kuumaa:ambient.dark_forest";
    private static final String DEEP_COLD_OCEAN = "kuumaa:ambient.deep_cold_ocean";
    private static final String DEEP_DARK = "kuumaa:ambient.deep_dark";
    private static final String DEEP_FROZEN_OCEAN = "kuumaa:ambient.deep_frozen_ocean";
    private static final String DEEP_LUKEWARM_OCEAN = "kuumaa:ambient.deep_lukewarm_ocean";
    private static final String DEEP_OCEAN = "kuumaa:ambient.deep_ocean";
    private static final String DESERT = "kuumaa:ambient.desert";
    private static final String DRIPSTONE_CAVES = "kuumaa:ambient.dripstone_caves";
    private static final String END_BARRENS = "kuumaa:ambient.end_barrens";
    private static final String END_HIGHLANDS = "kuumaa:ambient.end_highlands";
    private static final String END_MIDLANDS = "kuumaa:ambient.end_midlands";
    private static final String ERODED_BADLANDS = "kuumaa:ambient.eroded_badlands";
    private static final String FLOWER_FOREST = "kuumaa:ambient.flower_forest";
    private static final String FOREST = "kuumaa:ambient.forest";
    private static final String FROZEN_OCEAN = "kuumaa:ambient.frozen_ocean";
    private static final String FROZEN_PEAKS = "kuumaa:ambient.frozen_peaks";
    private static final String FROZEN_RIVER = "kuumaa:ambient.frozen_river";
    private static final String GROVE = DEAD_WIND;
    private static final String ICE_SPIKES = "kuumaa:ambient.ice_spikes";
    private static final String JAGGED_PEAKS = "kuumaa:ambient.jagged_peaks";
    private static final String JUNGLE = "kuumaa:ambient.jungle";
    private static final String LUKEWARM_OCEAN = "kuumaa:ambient.lukewarm_ocean";
    private static final String LUSH_CAVES = "kuumaa:ambient.lush_caves";
    private static final String MANGROVE_SWAMP = "kuumaa:ambient.mangrove_swamp";
    private static final String MEADOW = "kuumaa:ambient.meadow";
    private static final String MUSHROOM_FIELDS = "kuumaa:ambient.mushroom_fields";
    private static final String NETHER_WASTES = "kuumaa:ambient.nether_wastes";
    private static final String OCEAN = "kuumaa:ambient.ocean";
    private static final String OLD_GROWTH_BIRCH_FOREST = "kuumaa:ambient.old_growth_birch_forest";
    private static final String OLD_GROWTH_PINE_TAIGA = "kuumaa:ambient.old_growth_pine_taiga";
    private static final String OLD_GROWTH_SPRUCE_TAIGA = "kuumaa:ambient.old_growth_spruce_taiga";
    private static final String PLAINS = "kuumaa:ambient.plains";
    private static final String RIVER = "kuumaa:ambient.river";
    private static final String SAVANNA = "kuumaa:ambient.savanna";
    private static final String SAVANNA_PLATEAU = "kuumaa:ambient.savanna_plateau";
    private static final String SMALL_END_ISLANDS = "kuumaa:ambient.small_end_islands";
    private static final String SNOWY_BEACH = "kuumaa:ambient.snowy_beach";
    private static final String SNOWY_PLAINS = "kuumaa:ambient.snowy_plains";
    private static final String SNOWY_SLOPES = "kuumaa:ambient.snowy_slopes";
    private static final String SNOWY_TAIGA = "kuumaa:ambient.snowy_taiga";
    private static final String SOUL_SAND_VALLEY = "kuumaa:ambient.soul_sand_valley";
    private static final String SPARSE_JUNGLE = "kuumaa:ambient.sparse_jungle";
    private static final String STONY_PEAKS = "kuumaa:ambient.stony_peaks";
    private static final String STONY_SHORE = "kuumaa:ambient.stony_shore";
    private static final String SUNFLOWER_PLAINS = "kuumaa:ambient.sunflower_plains";
    private static final String SWAMP = "kuumaa:ambient.swamp";
    private static final String TAIGA = "kuumaa:ambient.taiga";
    private static final String THE_END = "kuumaa:ambient.the_end";
    private static final String THE_VOID = "kuumaa:ambient.the_void";
    private static final String WARM_OCEAN = "kuumaa:ambient.warm_ocean";
    private static final String WARPED_FOREST = "kuumaa:ambient.warped_forest";
    private static final String WINDSWEPT_FOREST = "kuumaa:ambient.windswept_forest";
    private static final String WINDSWEPT_GRAVELLY_HILLS = "kuumaa:ambient.windswept_gravelly_hills";
    private static final String WINDSWEPT_HILLS = "kuumaa:ambient.windswept_hills";
    private static final String WINDSWEPT_SAVANNA = "kuumaa:ambient.windswept_savanna";
    private static final String WOODED_BADLANDS = "kuumaa:ambient.wooded_badlands";

    // These are ACTUAL sound effect mappings for biomeSounds. Use these for the biomeSounds below.
    // This is where you define the sound effect lengths. The SoundData should always refer to the real
    // sound effect strings above, not the fake biome mappings. Sound lengths should be configured here only.

    private static final SoundData deciduousForest = new SoundData(Sounds.DECIDUOUS_FOREST, 80.475, 55.949);
    private static final SoundData deadWind = new SoundData(Sounds.DEAD_WIND, 56.608, 65.851);

    // IMPORTANT NOTE: When configuring the resource pack it needs to have TWO entries for each sound effect:
    // "forest.night" and "forest.day", for example. The syntax has to be exactly that, "biome name", "dot", "day/night",
    // and it will be picked by the code.


    // This is the biomeSounds which map the biomes to their sound effects.
    // Do the actual duration configuration ABOVE in ACTUAL sound effects, please.
    // The temporarily defined "new SoundData(etc)" below are here just as placeholders until everything is defined!
    // You can also CHANGE the sounds for biomes below, eg. if you change:
    // put(Biome.FOREST, deciduousForest);
    // to:
    // put(Biome.FOREST, deadWind);
    // then the sound for FOREST will be "DEAD_WIND" instead of "DECIDUOUS_FOREST". Easy-peasy!

    public static final Map<Biome, SoundData> biomeSounds = new HashMap<>() {{
        put(Biome.BADLANDS, new SoundData(Sounds.BADLANDS, 10.0, 10.0));
        put(Biome.BAMBOO_JUNGLE, new SoundData(Sounds.BAMBOO_JUNGLE, 10.0, 10.0));
        put(Biome.BASALT_DELTAS, new SoundData(Sounds.BASALT_DELTAS, 10.0, 10.0));
        put(Biome.BEACH, new SoundData(Sounds.BEACH, 10.0, 10.0));
        put(Biome.BIRCH_FOREST, new SoundData(Sounds.BIRCH_FOREST, 10.0, 10.0));
        put(Biome.CHERRY_GROVE, new SoundData(Sounds.CHERRY_GROVE, 10.0, 10.0));
        put(Biome.COLD_OCEAN, new SoundData(Sounds.COLD_OCEAN, 10.0, 10.0));
        put(Biome.CRIMSON_FOREST, new SoundData(Sounds.CRIMSON_FOREST, 10.0, 10.0));
        put(Biome.CUSTOM, new SoundData(Sounds.CUSTOM,10.0, 10.0));
        put(Biome.DARK_FOREST, new SoundData(Sounds.DARK_FOREST, 10.0, 10.0));
        put(Biome.DEEP_COLD_OCEAN, new SoundData(Sounds.DEEP_COLD_OCEAN, 10.0, 10.0));
        put(Biome.DEEP_DARK, new SoundData(Sounds.DEEP_DARK, 10.0, 10.0));
        put(Biome.DEEP_FROZEN_OCEAN, new SoundData(Sounds.DEEP_FROZEN_OCEAN, 10.0, 10.0));
        put(Biome.DEEP_LUKEWARM_OCEAN, new SoundData(Sounds.DEEP_LUKEWARM_OCEAN, 10.0, 10.0));
        put(Biome.DEEP_OCEAN, new SoundData(Sounds.DEEP_OCEAN, 10.0, 10.0));
        put(Biome.DESERT, new SoundData(Sounds.DESERT, 10.0, 10.0));
        put(Biome.DRIPSTONE_CAVES, new SoundData(Sounds.DRIPSTONE_CAVES, 10.0, 10.0));
        put(Biome.END_BARRENS, new SoundData(Sounds.END_BARRENS, 10.0, 10.0));
        put(Biome.END_HIGHLANDS, new SoundData(Sounds.END_HIGHLANDS, 10.0, 10.0));
        put(Biome.END_MIDLANDS, new SoundData(Sounds.END_MIDLANDS, 10.0, 10.0));
        put(Biome.ERODED_BADLANDS, new SoundData(Sounds.ERODED_BADLANDS, 10.0, 10.0));
        put(Biome.FLOWER_FOREST, new SoundData(Sounds.FLOWER_FOREST, 10.0, 10.0));
        put(Biome.FOREST, deciduousForest);
        put(Biome.FROZEN_OCEAN, new SoundData(Sounds.FROZEN_OCEAN, 10.0, 10.0));
        put(Biome.FROZEN_PEAKS, new SoundData(Sounds.FROZEN_PEAKS, 10.0, 10.0));
        put(Biome.FROZEN_RIVER, new SoundData(Sounds.FROZEN_RIVER, 10.0, 10.0));
        put(Biome.GROVE, deadWind);
        put(Biome.ICE_SPIKES, new SoundData(Sounds.ICE_SPIKES, 10.0, 10.0));
        put(Biome.JAGGED_PEAKS, new SoundData(Sounds.JAGGED_PEAKS, 10.0, 10.0));
        put(Biome.JUNGLE, new SoundData(Sounds.JUNGLE, 10.0, 10.0));
        put(Biome.LUKEWARM_OCEAN, new SoundData(Sounds.LUKEWARM_OCEAN, 10.0, 10.0));
        put(Biome.LUSH_CAVES, new SoundData(Sounds.LUSH_CAVES, 10.0, 10.0));
        put(Biome.MANGROVE_SWAMP, new SoundData(Sounds.MANGROVE_SWAMP, 10.0, 10.0));
        put(Biome.MEADOW, new SoundData(Sounds.MEADOW, 10.0, 10.0));
        put(Biome.MUSHROOM_FIELDS, new SoundData(Sounds.MUSHROOM_FIELDS, 10.0, 10.0));
        put(Biome.NETHER_WASTES, new SoundData(Sounds.NETHER_WASTES, 10.0, 10.0));
        put(Biome.OCEAN, new SoundData(Sounds.OCEAN, 10.0, 10.0));
        put(Biome.OLD_GROWTH_BIRCH_FOREST, new SoundData(Sounds.OLD_GROWTH_BIRCH_FOREST, 10.0, 10.0));
        put(Biome.OLD_GROWTH_PINE_TAIGA, new SoundData(Sounds.OLD_GROWTH_PINE_TAIGA, 10.0, 10.0));
        put(Biome.OLD_GROWTH_SPRUCE_TAIGA, new SoundData(Sounds.OLD_GROWTH_SPRUCE_TAIGA, 10.0, 10.0));
        put(Biome.PLAINS, new SoundData(Sounds.PLAINS, 10.0, 10.0));
        put(Biome.RIVER, new SoundData(Sounds.RIVER, 10.0, 10.0));
        put(Biome.SAVANNA, new SoundData(Sounds.SAVANNA, 10.0, 10.0));
        put(Biome.SAVANNA_PLATEAU, new SoundData(Sounds.SAVANNA_PLATEAU, 10.0, 10.0));
        put(Biome.SMALL_END_ISLANDS, new SoundData(Sounds.SMALL_END_ISLANDS, 10.0, 10.0));
        put(Biome.SNOWY_BEACH, new SoundData(Sounds.SNOWY_BEACH, 10.0, 10.0));
        put(Biome.SNOWY_PLAINS, new SoundData(Sounds.SNOWY_PLAINS, 10.0, 10.0));
        put(Biome.SNOWY_SLOPES, new SoundData(Sounds.SNOWY_SLOPES, 10.0, 10.0));
        put(Biome.SNOWY_TAIGA, new SoundData(Sounds.SNOWY_TAIGA, 10.0, 10.0));
        put(Biome.SOUL_SAND_VALLEY, new SoundData(Sounds.SOUL_SAND_VALLEY, 10.0, 10.0));
        put(Biome.SPARSE_JUNGLE, new SoundData(Sounds.SPARSE_JUNGLE, 10.0, 10.0));
        put(Biome.STONY_PEAKS, new SoundData(Sounds.STONY_PEAKS, 10.0, 10.0));
        put(Biome.STONY_SHORE, new SoundData(Sounds.STONY_SHORE, 10.0, 10.0));
        put(Biome.SUNFLOWER_PLAINS, new SoundData(Sounds.SUNFLOWER_PLAINS, 10.0, 10.0));
        put(Biome.SWAMP, new SoundData(Sounds.SWAMP, 10.0, 10.0));
        put(Biome.TAIGA, new SoundData(Sounds.TAIGA, 10.0, 10.0));
        put(Biome.THE_END, new SoundData(Sounds.THE_END, 10.0, 10.0));
        put(Biome.THE_VOID, new SoundData(Sounds.THE_VOID, 10.0, 10.0));
        put(Biome.WARM_OCEAN, new SoundData(Sounds.WARM_OCEAN, 10.0, 10.0));
        put(Biome.WARPED_FOREST, new SoundData(Sounds.WARPED_FOREST, 10.0, 10.0));
        put(Biome.WINDSWEPT_FOREST, new SoundData(Sounds.WINDSWEPT_FOREST, 10.0, 10.0));
        put(Biome.WINDSWEPT_GRAVELLY_HILLS, new SoundData(Sounds.WINDSWEPT_GRAVELLY_HILLS, 10.0, 10.0));
        put(Biome.WINDSWEPT_HILLS, new SoundData(Sounds.WINDSWEPT_HILLS, 10.0, 10.0));
        put(Biome.WINDSWEPT_SAVANNA, new SoundData(Sounds.WINDSWEPT_SAVANNA, 10.0, 10.0));
        put(Biome.WOODED_BADLANDS, new SoundData(Sounds.WOODED_BADLANDS, 10.0, 10.0));
    }};

}
