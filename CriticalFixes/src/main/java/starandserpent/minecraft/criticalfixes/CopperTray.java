package starandserpent.minecraft.criticalfixes;

import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.GlowItemFrame;
import org.bukkit.entity.ItemFrame;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class CopperTray implements Listener {

    private final JavaPlugin plugin;
    private Server server;

    public CopperTray(JavaPlugin plugin) {
        this.plugin = plugin;

        // Get server.
        server = plugin.getServer();
    }

    // On placing item frame, check if it's being placed on wall or ceiling and cancel the event.
    // This is to prevent players from placing item frames on walls and ceilings.

    @EventHandler void onFramePlace(HangingPlaceEvent event) {

        var entity = event.getEntity();

        // If it's not an item frame, return, do nothing.
        if (!(entity instanceof ItemFrame)) {
            return;
        }

        if (entity instanceof GlowItemFrame) {
            return;
        }

        BlockFace blockFace = event.getBlockFace();
        // BlockFace.DOWN, ie. when you're aiming up at the ceiling it's the block face which is facing down.
        if (blockFace == BlockFace.DOWN || blockFace == BlockFace.NORTH || blockFace == BlockFace.SOUTH || blockFace == BlockFace.EAST || blockFace == BlockFace.WEST) {
            event.setCancelled(true);

            // If not creative, give the person back their item frame which was consumed.
            if (event.getPlayer().getGameMode() == org.bukkit.GameMode.CREATIVE) {
                return;
            }

            event.getPlayer().getInventory().removeItem(new org.bukkit.inventory.ItemStack(Material.ITEM_FRAME, 1));
            event.getPlayer().getInventory().addItem(new org.bukkit.inventory.ItemStack(Material.ITEM_FRAME, 1));
        }
    }

}
