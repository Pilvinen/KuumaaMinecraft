package starandserpent.minecraft.criticalfixes;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Server;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class FieldFix implements Listener {

    private final JavaPlugin plugin;
    private Server server;

    private List<Material> hoes = List.of(
            Material.WOODEN_HOE,
            Material.STONE_HOE,
            Material.IRON_HOE,
            Material.GOLDEN_HOE,
            Material.DIAMOND_HOE,
            Material.NETHERITE_HOE
    );

    private List<Material> specialHandlingForHoeUseWith = List.of(
            Material.DIRT,
            Material.GRASS_BLOCK,
            Material.FARMLAND,
            Material.DIRT_PATH,
            Material.COARSE_DIRT,
            Material.ROOTED_DIRT
    );

    // 5 minutes in milliseconds
    private final long timeoutForFarmland = 5 * 60 * 1000;

    public FieldFix(JavaPlugin plugin) {
        this.plugin = plugin;
        server = plugin.getServer();
    }

    // On field trying to turn into dirt by fading, cancel the event.
    @EventHandler public void onFieldTurnToDirt(BlockFadeEvent event) {

        // Get what the block was before turning.
        Material wasPreviously = event.getBlock().getType();
        // Get what the block is trying to turn into.
        Material blockChangedTo = event.getNewState().getType();

        boolean farmlandIsTryingToTurnToDirt = wasPreviously == Material.FARMLAND && blockChangedTo == Material.DIRT;
        if (farmlandIsTryingToTurnToDirt) {

            var block = event.getBlock();
            var blockLocation = block.getLocation();
            var farmlandLocationKey = getFarmlandLocationKey(blockLocation);
            var farmlandTimestamp = getTimestampFromFarmland(blockLocation);

            if (!hasFarmlandPersistentData(block, farmlandLocationKey)) {
                return;
            }

            // Check if timeout has passed.
            var passedTimeSinceTimeStamp = System.currentTimeMillis() - farmlandTimestamp;
            if (passedTimeSinceTimeStamp > timeoutForFarmland) {
                // Delete the persistent data on the farmland.
                deleteFarmlandPersistentData(block, farmlandLocationKey);
                return;
            }

            // We do not allow the farmland to decay at this time.
            event.setCancelled(true);
        }
    }

    // Prevent hoe right click with playerinteract event.
    @EventHandler public void onHoeRightClick(PlayerInteractEvent event) {

        // Only handle right clicks!
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        // Get player item in hand.
        Material itemInHand = event.getPlayer().getInventory().getItemInMainHand().getType();

        // If it's a hoe, prevent its use.
        if (hoes.contains(itemInHand)) {

            // Get target block.
            Material blockType = event.getClickedBlock().getType();

            // If the target block is in the preventHoeUseWith list, cancel the event.
            if (specialHandlingForHoeUseWith.contains(blockType)) {
                event.setCancelled(true);
            }

        }
    }

    // Block break event handling for hoes.
    @EventHandler public void onBlockBreak(BlockBreakEvent event) {

        // Get player and item in hand.
        Player player = event.getPlayer();
        var itemInHand = player.getInventory().getItemInMainHand();
        var itemInHandType = itemInHand.getType();
        var block = event.getBlock();
        var blockType = event.getBlock().getType();

        // First check if farmland is being broken (in general).
        // We need to allow that, but delete any persistent data.
        if (blockType == Material.FARMLAND) {
            // Get the block's location.
            var blockLocation = block.getLocation();

            // Get the chunk's PersistentDataContainer.
            if (!hasFarmlandPersistentData(block, getFarmlandLocationKey(blockLocation))) {
                return;
            }

            // Remove the timestamp on the farmland.
            var farmlandLocationKey = getFarmlandLocationKey(blockLocation);
            deleteFarmlandPersistentData(block, farmlandLocationKey);
            return;
        }

        // Then we can do the hoe specific handling for land types.

        if (hoes.contains(itemInHandType)) {

            // If the block being broken is not one of these, bail out.
            if (!specialHandlingForHoeUseWith.contains(blockType)) {
                return;
            }

            // Damage durability of the hoe by one.
            // Get the item's current ItemMeta
            ItemMeta meta = itemInHand.getItemMeta();

            // The hoe takes damage from use.
            if (meta instanceof Damageable damageable) {
                int currentDamage = damageable.getDamage();
                damageable.setDamage(currentDamage + 1);
                itemInHand.setItemMeta(damageable);

                // Break if max damage is reached.
                if (damageable.getDamage() >= itemInHand.getType().getMaxDurability()) {
                    player.getInventory().setItemInMainHand(null);
                    // Play item break sound for everyone in radius.
                    player.getWorld().playSound(player.getLocation(), "minecraft:entity.item.break", 1, 1);
                }

            }

            // Grass becomes dirt.
            if (blockType == Material.GRASS_BLOCK) {
                // Prevent hoe from breaking grass blocks.
                event.setCancelled(true);

                // Set the grass to dirt with bukkit delay.
                block.setType(Material.DIRT);

            // Dirt becomes farmland.
            } else if (blockType == Material.DIRT) {
                // Prevent hoe from breaking farmland.
                event.setCancelled(true);

                // Set the dirt to farmland.
                block.setType(Material.FARMLAND);

                setPersistentDataOnFarmland(block);

            } else if (blockType == Material.DIRT_PATH) {
                // Prevent hoe from breaking dirt paths.
                event.setCancelled(true);

                // Set the dirt path into dirt.
                block.setType(Material.DIRT);

            } else if (blockType == Material.COARSE_DIRT) {
                // Prevent hoe from breaking coarse dirt.
                event.setCancelled(true);

                // Set the coarse dirt into dirt.
                block.setType(Material.DIRT);

            } else if (blockType == Material.ROOTED_DIRT) {
                // Prevent hoe from breaking rooted dirt.
                event.setCancelled(true);

                // Set the rooted dirt into dirt.
                block.setType(Material.DIRT);

                // Drop hanging roots item above the block location.
                block.getWorld().dropItemNaturally(event.getBlock().getLocation().add(0, 1, 0), new org.bukkit.inventory.ItemStack(Material.HANGING_ROOTS));
            }

            // Everything else is business as usual.

        }
    }

    // On player trampling farmland.
    @EventHandler public void onPlayerTrampleFarmland(PlayerInteractEvent event) {

        // Only handle trampling.
        if (event.getAction() != Action.PHYSICAL) {
            return;
        }

        // Get the block the player is trampling.
        var block = event.getClickedBlock();
        if (block == null) {
            return;
        }

        // If the block is farmland delete the persistent data.
        if (block.getType() == Material.FARMLAND) {
            var playerName = event.getPlayer().getName();
            var blockLocation = block.getLocation();
            var farmlandLocationKey = getFarmlandLocationKey(blockLocation);
            deleteFarmlandPersistentData(block, farmlandLocationKey);
        }
    }

    // Set PersistentData on the farmland indicating a timestamp when it was last tilled.
    // If the timestamp is older than 5 minutes, the farmland is allowed to turn back into dirt.
    private void setPersistentDataOnFarmland(Block block) {
        // Get the block's location.
        var blockLocation = block.getLocation();

        // Get the chunk's PersistentDataContainer.
        var dataContainer = block.getChunk().getPersistentDataContainer();

        // Set the timestamp on the farmland.
        var time = System.currentTimeMillis();
        String timeString = String.valueOf(time);

        // Create a NamespacedKey for the data
        var farmlandLocationKey = getFarmlandLocationKey(blockLocation);
        NamespacedKey key = new NamespacedKey(plugin, farmlandLocationKey);

        // Store the timestamp in the block's PersistentDataContainer
        dataContainer.set(key, PersistentDataType.STRING, timeString);
    }

    // Method which turns string into Location.
    private static String getFarmlandLocationKey(Location location) {

        String locationKey = "Farmland_"
                + location.getWorld().getName() + "_"
                + location.getBlockX() + "_"
                + location.getBlockY() + "_"
                + location.getBlockZ();

        return locationKey;
    }

    public long getTimestampFromFarmland(Location location) {
        // Create the same NamespacedKey
        NamespacedKey key = new NamespacedKey(plugin, getFarmlandLocationKey(location));

        // Get the chunk's PersistentDataContainer
        PersistentDataContainer dataContainer = location.getChunk().getPersistentDataContainer();

        // Retrieve the timestamp from the block's PersistentDataContainer
        String timeString = dataContainer.get(key, PersistentDataType.STRING);
        if (timeString == null) {
            // Return current time if no timestamp is found
            return System.currentTimeMillis();
        }

        // Convert the timestamp string back to a long value
        long time = Long.parseLong(timeString);

        // Return the retrieved timestamp
        return time;
    }

    // Check if persistent data exists for this block.
    private boolean hasFarmlandPersistentData(Block block, String farmlandLocationKey) {
        // Get the chunk's PersistentDataContainer
        PersistentDataContainer dataContainer = block.getChunk().getPersistentDataContainer();

        // Check if the timestamp exists in the block's PersistentDataContainer
        return dataContainer.has(new NamespacedKey(plugin, farmlandLocationKey), PersistentDataType.STRING);
    }


    private void deleteFarmlandPersistentData(Block block, String farmlandLocationKey) {
        // Get the chunk's PersistentDataContainer
        PersistentDataContainer dataContainer = block.getChunk().getPersistentDataContainer();

        // Remove the timestamp from the block's PersistentDataContainer
        dataContainer.remove(new NamespacedKey(plugin, farmlandLocationKey));
    }

}
