package starandserpent.minecraft.criticalfixes;

import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class PathImprovement implements Listener {

    private final JavaPlugin plugin;
    private Server server;

    public PathImprovement(JavaPlugin plugin) {
        this.plugin = plugin;
        this.server = plugin.getServer();
    }

    // List<T> of tracked item types.
    private final List<Material> allShovels = List.of(
        Material.WOODEN_SHOVEL,
        Material.STONE_SHOVEL,
        Material.IRON_SHOVEL,
        Material.DIAMOND_SHOVEL,
        Material.NETHERITE_SHOVEL,
        Material.GOLDEN_SHOVEL
    );

    private final Material dirtPathSlab = Material.PURPUR_SLAB;
    private final String flattenSound = "minecraft:item.shovel.flatten";

    private final String requiredPermission = "starandserpent.minecraft.criticalfixes.pathimprovement";

    // Create a dirt path slab when right clicking with a shovel on an existing dirt path.
    @EventHandler public void onPlayerInteract(PlayerInteractEvent event) {
        var action = event.getAction();
        var item = event.getItem();

        // Was not a right click, so return.
        if (action != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        // Item is null, so return.
        if (item == null) {
            return;
        }

        // Was not a shovel, so return.
        var itemType = item.getType();
        if (!allShovels.contains(itemType)) {
            return;
        }

        // We have established that player right clicked with a shovel.

        // Get the block clicked.
        var block = event.getClickedBlock();

        // Block is null, so return.
        if (block == null) {
            return;
        }

        // Check if the block clicked was path.
        var blockType = block.getType();
        if (blockType != Material.DIRT_PATH) {
            return;
        }

        // We have established that player is trying to create a lower path with a shovel.

        // Check if player has the required permission.
        var player = event.getPlayer();
        if (!player.isOp()) {
            if (!player.hasPermission(requiredPermission)) {
                return;
            }
        }

        // Set the clicked block to dirtPathSlab.
        block.setType(dirtPathSlab);

        // Reduce shovel durability.
        reduceShovelDurability(item);

        // Play sound.
        block.getWorld().playSound(block.getLocation(), flattenSound, 1, 1);

    }

    private void reduceShovelDurability(ItemStack item) {
        // Reduce durability of the shovel using the modern Damageable.
        var damageable = (Damageable) item.getItemMeta();
        if (damageable == null) {
            return;
        }
        damageable.setDamage(damageable.getDamage() + 1);
    }

    // Handle breaking the dirt path slab.
    @EventHandler public void onBlockBreak(BlockBreakEvent event) {
        var block = event.getBlock();
        var blockType = block.getType();

        // Was not a dirt path slab, so return.
        if (blockType != dirtPathSlab) {
            return;
        }

        // Cancel event.
        event.setCancelled(true);

        // Set block to air.
        block.setType(Material.AIR);

        // Drop a dirt block at the location.
        block.getWorld().dropItemNaturally(block.getLocation(), new ItemStack(Material.DIRT));
    }

    // Prevent explosion from dropping dirt path slabs, but still make sure they are set to air.
    @EventHandler public void onBlockExplode(BlockExplodeEvent event) {
        var blocks = event.blockList();
        blocks.removeIf(block -> block.getType() == dirtPathSlab);
    }

}
