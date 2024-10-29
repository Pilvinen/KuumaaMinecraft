package starandserpent.minecraft.criticalfixes;

import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.List;

public class SoundFixesForReplacedBlocks implements Listener {

    private final JavaPlugin plugin;
    private Server server;

    // Sound Fixes for replaced Materials
    // **********************************

    // Glass breaking sounds for minecraft:block.glass.break have been replaced with silence, so we can have
    // proper sound effects for custom blocks. The sounds have moved to kuumaa:block.glass.break and the
    // sounds are in kuumaa/sounds/random/glass1, 2, and 3.
    // Here we replace the glass breaking sound with the custom sound for materials which need it.
    private final String kuumaaBlockGlassBreak = "kuumaa:block.glass.break";
    private final List<Material> glassBreakSoundMaterials = List.of(
            Material.GLASS,
            Material.TINTED_GLASS,
            Material.BLACK_STAINED_GLASS,
            Material.BLUE_STAINED_GLASS,
            Material.BROWN_STAINED_GLASS,
            Material.CYAN_STAINED_GLASS,
//        Material.GRAY_STAINED_GLASS, // Reserved for Altar
            Material.GREEN_STAINED_GLASS,
            Material.LIGHT_BLUE_STAINED_GLASS,
            Material.LIGHT_GRAY_STAINED_GLASS,
            Material.LIME_STAINED_GLASS,
            Material.MAGENTA_STAINED_GLASS,
            Material.ORANGE_STAINED_GLASS,
            Material.PINK_STAINED_GLASS,
            Material.PURPLE_STAINED_GLASS,
            Material.RED_STAINED_GLASS,
            Material.WHITE_STAINED_GLASS,
            Material.YELLOW_STAINED_GLASS,

            Material.GLASS_PANE,
            Material.BLACK_STAINED_GLASS_PANE,
            Material.BLUE_STAINED_GLASS_PANE,
            Material.BROWN_STAINED_GLASS_PANE,
//        Material.CYAN_STAINED_GLASS_PANE, // Reserved for Hook
//        Material.GRAY_STAINED_GLASS_PANE, // Reserved for Chain Harness
            Material.GREEN_STAINED_GLASS_PANE,
            Material.LIGHT_BLUE_STAINED_GLASS_PANE,
            Material.LIGHT_GRAY_STAINED_GLASS_PANE,
            Material.LIME_STAINED_GLASS_PANE,
            Material.MAGENTA_STAINED_GLASS_PANE,
            Material.ORANGE_STAINED_GLASS_PANE,
            Material.PINK_STAINED_GLASS_PANE,
            Material.PURPLE_STAINED_GLASS_PANE,
            Material.RED_STAINED_GLASS_PANE,
            Material.WHITE_STAINED_GLASS_PANE,
            Material.YELLOW_STAINED_GLASS_PANE
    );

    // HashSet of Material to custom sound string.
    private final HashMap<Material, String> customSoundMaterials = new HashMap<>() {{
        put(Material.GRAY_STAINED_GLASS, "minecraft:block.stone.break"); // Altar
        put(Material.GRAY_STAINED_GLASS_PANE, "minecraft:block.chain.break"); // Chain Harness
        put(Material.CYAN_STAINED_GLASS_PANE, "minecraft:block.chain.break"); // Hook
        put(Material.PURPUR_SLAB, "minecraft:block.gravel.break"); // Dirt path slab.
    }};

    // CTOR.
    public SoundFixesForReplacedBlocks(JavaPlugin plugin) {
        this.plugin = plugin;

    }


    @EventHandler public void onBlockBreak(BlockBreakEvent event) {
        var block = event.getBlock();
        var blockType = block.getType();

        // Play the sound for everyone in the area.

        // BREAKING GLASS FIX.
        // Glass sound had to be replaced with silence to allow for custom sounds for glass blocks.
        if (glassBreakSoundMaterials.contains(blockType)) {
            var player = event.getPlayer();
            var location = block.getLocation();
            var world = player.getWorld();
            world.playSound(location, kuumaaBlockGlassBreak, 1.0f, 1.0f);
            return;
        }

        // CUSTOM SOUNDS. Play the custom sound for everyone in the area.
        if (customSoundMaterials.containsKey(blockType)) {
            var soundString = customSoundMaterials.get(blockType);
            var player = event.getPlayer();
            var location = block.getLocation();
            var world = player.getWorld();
            world.playSound(location, soundString, 1.0f, 1.0f);
        }
    }

}
