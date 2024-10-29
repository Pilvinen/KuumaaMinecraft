package starandserpent.minecraft.criticalfixes;

import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.Tag;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;

public class CopperFixes implements Listener {

    private final JavaPlugin plugin;
    private Server server;

    private HashSet<Material> materialsThatCanOxidize = new HashSet<Material>() {{
        add(Material.COPPER_BLOCK);
        add(Material.CHISELED_COPPER);
        add(Material.COPPER_GRATE);
        add(Material.CUT_COPPER);
        add(Material.CUT_COPPER_STAIRS);
        add(Material.CUT_COPPER_SLAB);
        add(Material.COPPER_DOOR);
        add(Material.COPPER_TRAPDOOR);
        add(Material.COPPER_BULB);

        add(Material.EXPOSED_COPPER);
        add(Material.EXPOSED_CHISELED_COPPER);
        add(Material.EXPOSED_COPPER_GRATE);
        add(Material.EXPOSED_CUT_COPPER);
        add(Material.EXPOSED_CUT_COPPER_STAIRS);
        add(Material.EXPOSED_CUT_COPPER_SLAB);
        add(Material.EXPOSED_COPPER_DOOR);
        add(Material.EXPOSED_COPPER_TRAPDOOR);
        add(Material.EXPOSED_COPPER_BULB);

        add(Material.WEATHERED_COPPER);
        add(Material.WEATHERED_CHISELED_COPPER);
        add(Material.WEATHERED_COPPER_GRATE);
        add(Material.WEATHERED_CUT_COPPER);
        add(Material.WEATHERED_CUT_COPPER_STAIRS);
        add(Material.WEATHERED_CUT_COPPER_SLAB);
        add(Material.WEATHERED_COPPER_DOOR);
        add(Material.WEATHERED_COPPER_TRAPDOOR);
        add(Material.WEATHERED_COPPER_BULB);

        add(Material.OXIDIZED_COPPER);
        add(Material.OXIDIZED_CHISELED_COPPER);
        add(Material.OXIDIZED_COPPER_GRATE);
        add(Material.OXIDIZED_CUT_COPPER);
        add(Material.OXIDIZED_CUT_COPPER_STAIRS);
        add(Material.OXIDIZED_CUT_COPPER_SLAB);
        add(Material.OXIDIZED_COPPER_DOOR);
        add(Material.OXIDIZED_COPPER_TRAPDOOR);
        add(Material.OXIDIZED_COPPER_BULB);

        add(Material.WAXED_COPPER_BLOCK);
        add(Material.WAXED_CHISELED_COPPER);
        add(Material.WAXED_COPPER_GRATE);
        add(Material.WAXED_CUT_COPPER);
        add(Material.WAXED_CUT_COPPER_STAIRS);
        add(Material.WAXED_CUT_COPPER_SLAB);
        add(Material.WAXED_COPPER_DOOR);
        add(Material.WAXED_COPPER_TRAPDOOR);
        add(Material.WAXED_COPPER_BULB);

        add(Material.WAXED_EXPOSED_COPPER);
        add(Material.WAXED_EXPOSED_CHISELED_COPPER);
        add(Material.WAXED_EXPOSED_COPPER_GRATE);
        add(Material.WAXED_EXPOSED_CUT_COPPER);
        add(Material.WAXED_EXPOSED_CUT_COPPER_STAIRS);
        add(Material.WAXED_EXPOSED_CUT_COPPER_SLAB);
        add(Material.WAXED_EXPOSED_COPPER_DOOR);
        add(Material.WAXED_EXPOSED_COPPER_TRAPDOOR);
        add(Material.WAXED_EXPOSED_COPPER_BULB);

        add(Material.WAXED_WEATHERED_COPPER);
        add(Material.WAXED_WEATHERED_CHISELED_COPPER);
        add(Material.WAXED_WEATHERED_COPPER_GRATE);
        add(Material.WAXED_WEATHERED_CUT_COPPER);
        add(Material.WAXED_WEATHERED_CUT_COPPER_STAIRS);
        add(Material.WAXED_WEATHERED_CUT_COPPER_SLAB);
        add(Material.WAXED_WEATHERED_COPPER_DOOR);
        add(Material.WAXED_WEATHERED_COPPER_TRAPDOOR);
        add(Material.WAXED_WEATHERED_COPPER_BULB);

        add(Material.WAXED_OXIDIZED_COPPER);
        add(Material.WAXED_OXIDIZED_CHISELED_COPPER);
        add(Material.WAXED_OXIDIZED_COPPER_GRATE);
        add(Material.WAXED_OXIDIZED_CUT_COPPER);
        add(Material.WAXED_OXIDIZED_CUT_COPPER_STAIRS);
        add(Material.WAXED_OXIDIZED_CUT_COPPER_SLAB);
        add(Material.WAXED_OXIDIZED_COPPER_DOOR);
        add(Material.WAXED_OXIDIZED_COPPER_TRAPDOOR);
        add(Material.WAXED_OXIDIZED_COPPER_BULB);
    }};

    public CopperFixes(JavaPlugin plugin) {
        this.plugin = plugin;
        this.server = plugin.getServer();
    }

    // Prevent oxidation of copper blocks.
    @EventHandler public void onOxidation(BlockFormEvent event) {
        var block = event.getBlock();
        var blockType = block.getType();
        if (materialsThatCanOxidize.contains(blockType)) {
            event.setCancelled(true);
        }
    }

    @EventHandler public void onLightningRemovingOxidation(EntityChangeBlockEvent event) {

        // If it's not a lightning, we don't care.
        var entityType = event.getEntity().getType();
        if (!(entityType == EntityType.LIGHTNING_BOLT)) {
            return;
        }

        var block = event.getBlock();
        var blockType = block.getType();

        // Event entity is a lightning.
        if (materialsThatCanOxidize.contains(blockType)) {

            // Prevent oxidation removal by lightning.
            event.setCancelled(true);
        }
    }

    // Prevent waxing of copper blocks with honeycomb.
    // Prevent removal of wax with axes.
    @EventHandler public void onWaxInteractions(PlayerInteractEvent event) {

        var block = event.getClickedBlock();

        if (block == null) {
            return;
        }

        var blockType = block.getType();
        if (!materialsThatCanOxidize.contains(blockType)) {
            return;
        }

        var item = event.getItem();
        if (item == null) {
            return;
        }

        var itemType = item.getType();

        // Prevent waxing of copper blocks with honeycomb.
        if (itemType == Material.HONEYCOMB) {
            event.setCancelled(true);

        // Prevent removal of wax with axes.
        } else if (Tag.ITEMS_AXES.isTagged(itemType)) {
            event.setCancelled(true);
        }

    }

}
