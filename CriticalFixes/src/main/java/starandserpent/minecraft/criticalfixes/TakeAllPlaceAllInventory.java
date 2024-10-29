package starandserpent.minecraft.criticalfixes;

import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.block.Container;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class TakeAllPlaceAllInventory implements Listener {

    private final JavaPlugin plugin;
    private Server server;

    public TakeAllPlaceAllInventory(JavaPlugin plugin) {
        this.plugin = plugin;
        this.server = plugin.getServer();
    }

    // On right click player interact event
    @EventHandler public void onPlayerInteract(PlayerInteractEvent event) {

        // Accept only right clicks
        var action = event.getAction();
        if (action == Action.LEFT_CLICK_BLOCK) {
            var blockTarget = event.getClickedBlock();
            if (blockTarget == null) {
                return;
            }

            var player = event.getPlayer();

            // Right clicks that happen while sneaking.
            if (!player.isSneaking()) {
                return;
            }

            var blockTargetType = blockTarget.getType();
            if (blockTargetType == Material.BARREL) {
                player.sendTitle("", Symbols.ARROW_UP.literal,10, 70, 20);
                player.sendMessage("Symbol: " + Symbols.ARROW_UP.literal);
                return;
            }
        }

        if (!(action == Action.RIGHT_CLICK_BLOCK)) {
            return;
        }

        var player = event.getPlayer();

        // Right clicks that happen while sneaking.
        if (!player.isSneaking()) {
            return;
        }

        // Where the target is a container.
        var blockTarget = event.getClickedBlock();
        if (blockTarget == null) {
            return;
        }

        var blockTargetType = blockTarget.getType();
        if (blockTargetType == Material.BARREL) {

            // Cancel event.
            event.setCancelled(true);

            // Get the container inventory.
            var containerState = blockTarget.getState();
            if (containerState instanceof Container container) {

                // Get player inventory.
                var playerInventory = player.getInventory();

                // Get container inventory.
                var containerInventory = container.getInventory();
                var containerContents = containerInventory.getContents();

                // Take all items from container and place them in player inventory.
                for (ItemStack item : containerContents) {

                    if (item != null && item.getType() != Material.AIR) {

                        // Check if there is space for this stack in player's inventory.
                        int slotNumber = getFirstEmptyNonQuickBarInventorySlot(player);
                        if (slotNumber == -1) {
                            // No space in player's inventory.
                            return;
                        }

                        // Add item stack to player's inventory at slotNumber.
                        playerInventory.setItem(slotNumber, item);

                        // Remove item stack from container inventory.
                        containerInventory.removeItem(item);
                    }
                }

                // Bukkit delay and open inventory
                plugin.getServer().getScheduler().runTaskLater(plugin, () -> player.openInventory(containerInventory), 1);
            }
        }
    }

    private int getFirstEmptyNonQuickBarInventorySlot(Player player) {
        var inventory = player.getInventory();
        // Iterate through all the inventory slots, but skip the quick bar and and hand armor slots and such.
        for (int i = 9; i < 36; i++) {
            if (inventory.getItem(i) == null || inventory.getItem(i).getType() == Material.AIR) {
                return i;
            }
        }
        return -1;
    }
}
