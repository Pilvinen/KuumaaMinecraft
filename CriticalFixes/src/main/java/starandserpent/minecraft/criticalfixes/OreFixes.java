package starandserpent.minecraft.criticalfixes;

import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.List;

public class OreFixes implements Listener {

    private final JavaPlugin plugin;
    private Server server;

    public OreFixes(JavaPlugin plugin) {
        this.plugin = plugin;
        this.server = plugin.getServer();
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onBlockDropItemEvent(BlockDropItemEvent event) {

        Material blockType = event.getBlockState().getType();

        final Material materialToReplace;
        final Material materialToReplaceWith;

        if (blockType == Material.IRON_ORE || blockType == Material.DEEPSLATE_IRON_ORE) {
            materialToReplace = Material.RAW_IRON;
            materialToReplaceWith = Material.IRON_NUGGET;
        } else if (blockType == Material.GOLD_ORE || blockType == Material.DEEPSLATE_GOLD_ORE) {
            materialToReplace = Material.RAW_GOLD;
            materialToReplaceWith = Material.GOLD_NUGGET;
        // TODO: Add copper nugget item to the game.
        } else {
            // Nothing to do.
            return;
        }

        // Replace the items.
        List<Item> items = event.getItems();
        for (Item item : items) {
            var itemStack = item.getItemStack();
            var itemType = itemStack.getType();

            if (itemType == materialToReplace) {
                var itemAmount = itemStack.getAmount();
                var newStack = new ItemStack(materialToReplaceWith, itemAmount);
                item.setItemStack(newStack);
            }
        }

    }
}
