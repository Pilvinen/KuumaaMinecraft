package starandserpent.minecraft.criticalfixes;

import org.bukkit.Material;
import org.bukkit.Rotation;
import org.bukkit.Server;
import org.bukkit.entity.ItemFrame;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public class Curtains implements Listener {

    private final JavaPlugin plugin;
    private Server server;

    private final Map<Integer, Integer> leftCurtainMap = new HashMap<>();
    private final Map<Integer, Integer> rightCurtainMap = new HashMap<>();

    public Curtains(JavaPlugin plugin) {
        this.plugin = plugin;
        server = plugin.getServer();
        initializeCurtainMaps();
    }

    private void initializeCurtainMaps() {
        leftCurtainMap.put(282042, 282043); // fully_closed -> almost_closed
        leftCurtainMap.put(282043, 282044); // almost_closed -> halfway
        leftCurtainMap.put(282044, 282045); // halfway -> almost_open
        leftCurtainMap.put(282045, 282046); // almost_open -> fully_open
        leftCurtainMap.put(282046, 282047); // fully_open -> almost_open
        leftCurtainMap.put(282047, 282048); // almost_open -> halfway
        leftCurtainMap.put(282048, 282049); // halfway -> almost_closed
        leftCurtainMap.put(282049, 282042); // almost_closed -> fully_closed

        rightCurtainMap.put(282050, 282051); // fully_closed -> almost_closed
        rightCurtainMap.put(282051, 282052); // almost_closed -> halfway
        rightCurtainMap.put(282052, 282053); // halfway -> almost_open
        rightCurtainMap.put(282053, 282054); // almost_open -> fully_open
        rightCurtainMap.put(282054, 282055); // fully_open -> almost_open
        rightCurtainMap.put(282055, 282056); // almost_open -> halfway
        rightCurtainMap.put(282056, 282057); // halfway -> almost_closed
        rightCurtainMap.put(282057, 282050); // almost_closed -> fully_closed
    }


    @EventHandler public void onItemFrameRotate(PlayerInteractEntityEvent event) {

        // This is a right click event on item frame.
        if (!(event.getRightClicked() instanceof ItemFrame itemFrame)) {
            return;
        }

        // The item frame has a shulker shell.
        ItemStack item = itemFrame.getItem();
        if (item.getType() != Material.SHULKER_SHELL) {
            return;
        }

        // The item must have metadata.
        if (!item.hasItemMeta()) {
            return;
        }

        // The item must have custom model data.
        var itemMeta = item.getItemMeta();
        if (itemMeta == null || !itemMeta.hasCustomModelData()) {
            return;
        }

        // The item must be a curtain.
        int customModelData = itemMeta.getCustomModelData();
        if (!isCurtain(customModelData)) {
            return;
        }

        // We have established, we are closing/opening (i.e. "rotating") a curtain.
        int nextCustomModelData = getNextCurtainCustomModelData(customModelData);

        itemMeta.setCustomModelData(nextCustomModelData);
        item.setItemMeta(itemMeta);

        itemFrame.setItem(item);
    }

    private boolean isCurtain(int customModelData) {
        return (customModelData >= 282042 && customModelData <= 282049) ||
                (customModelData >= 282050 && customModelData <= 282057);
    }

    private int getNextCurtainCustomModelData(int customModelData) {
        if (customModelData >= 282042 && customModelData <= 282049) {
            return leftCurtainMap.get(customModelData);
        } else if (customModelData >= 282050 && customModelData <= 282057) {
            return rightCurtainMap.get(customModelData);
        }
        return customModelData;
    }

}
