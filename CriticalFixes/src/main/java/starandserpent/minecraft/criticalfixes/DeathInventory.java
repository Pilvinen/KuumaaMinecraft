package starandserpent.minecraft.criticalfixes;

import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.block.ShulkerBox;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class DeathInventory implements Listener {

    private final JavaPlugin plugin;
    private Server server;

    public DeathInventory(JavaPlugin plugin) {
        this.plugin = plugin;
        this.server = plugin.getServer();
    }

    @EventHandler public void onPlayerInteractWithShulkerBox(PlayerInteractEvent event) {
        var action = event.getAction();
        if (action != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        var block = event.getClickedBlock();
        if (block == null) {
            return;
        }

        var blockState = block.getState();

        if ((blockState instanceof ShulkerBox)) {
            System.out.println("This is a shulker box: " + blockState);
            event.setCancelled(true);
            System.out.println("Shulker box interaction cancelled.");
        }

    }

}
