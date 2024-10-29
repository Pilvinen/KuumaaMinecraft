package starandserpent.minecraft.criticalfixes;

import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Candle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

// Altars are implemented by modifying cake blocks. There are 42 different cake blocks, which is an insane
// amount of blocks we can modify. The idea is to prevent normal cake functions such as eating, breaking, and
// placing candles.
public class Altars implements Listener {

    private final JavaPlugin plugin;
    private Server server;

    // Define forbidden candles as List.
    private final List<Material> denyCandles = List.of(
//        Material.CANDLE,
//        Material.WHITE_CANDLE,
//        Material.ORANGE_CANDLE,
//        Material.MAGENTA_CANDLE,
//        Material.LIGHT_BLUE_CANDLE,
//        Material.YELLOW_CANDLE,
//        Material.LIME_CANDLE,
//        Material.PINK_CANDLE,
//        Material.GRAY_CANDLE,
//        Material.LIGHT_GRAY_CANDLE,
//        Material.CYAN_CANDLE,
//        Material.BLUE_CANDLE,
//        Material.BROWN_CANDLE,
//        Material.GREEN_CANDLE,
//        Material.RED_CANDLE,
//        Material.BLACK_CANDLE,
        Material.CYAN_CANDLE
    );

    private final List<Material> allCandleMaterials = List.of(
        Material.CANDLE,
        Material.WHITE_CANDLE,
        Material.ORANGE_CANDLE,
        Material.MAGENTA_CANDLE,
        Material.LIGHT_BLUE_CANDLE,
        Material.YELLOW_CANDLE,
        Material.LIME_CANDLE,
        Material.PINK_CANDLE,
        Material.GRAY_CANDLE,
        Material.LIGHT_GRAY_CANDLE,
        Material.CYAN_CANDLE,
        Material.BLUE_CANDLE,
        Material.BROWN_CANDLE,
        Material.GREEN_CANDLE,
        Material.RED_CANDLE,
        Material.BLACK_CANDLE
    );

    private final Material altarBlockType = Material.GRAY_STAINED_GLASS;

    public Altars(JavaPlugin plugin) {
        this.plugin = plugin;
        // Get server.
        this.server = plugin.getServer();
    }

    // On placing candle, check if it's being placed on altar and cancel the event if the candle matches
    // the denyCandles list. Check for placing a candle.
    @EventHandler public void onPlayerInteract(PlayerInteractEvent event) {

        // Check for altar. Cancel interactions with altars that are improper.
        if (isAltar(event)) {
            if(isImproperInteractionWithAltar(event)) {
                event.setCancelled(true);
            }
            handleAltarInteraction(event);
        } else if (isCandlingAltar(event)) {
            event.setCancelled(true);
        }

    }

    private void handleAltarInteraction(PlayerInteractEvent event) {
        // If the interaction is right click.
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {

            // If the block being interacted with is a lit cake, swap it equivalent unlit cake.
            Block block = event.getClickedBlock();
            if (block == null) {
                return;
            }

            // Block above altar.
            Block blockAbove = block.getRelative(0, 1, 0);
            Material blockAboveType = blockAbove.getType();
            boolean candlesOnAltar = allCandleMaterials.contains(blockAboveType);

            // If item in hand being clicked with is flint and steel, allow the interaction.
            if (event.getItem() != null && event.getItem().getType() == Material.FLINT_AND_STEEL && candlesOnAltar) {
                Candle candles = (Candle) blockAbove;
                if (candles.isLit()) {
                    return;
                }
                candles.setLit(true);
                return;
            }

            event.setCancelled(true);
        }
    }

    private boolean isImproperInteractionWithAltar(PlayerInteractEvent event) {

        // Stop right clicks on altars.
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {

            // Lighters are allowed.
            if (event.getItem() != null && event.getItem().getType() == Material.FLINT_AND_STEEL) {
                return false;
            }

            // Prevent all other right click actions.
            return true;
        }

        // Was not improper.
        return false;
    }

    private boolean isCandlingAltar(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) {
            return false; // ignore off-hand events
        }

        // Ignore if there's no item in the hand.
        ItemStack item = event.getItem();
        if (item == null) {
            return false;
        }

        // Ignore if it's not a candle.
        Material itemType = item.getType();
        if (!denyCandles.contains(itemType)) {
            return false; // ignore if it's not a forbidden candle
        }

        Block block = event.getClickedBlock();
        if (block == null) {
            return false;
        }

        Material blockType = block.getType();
        if (blockType != altarBlockType) {
            return false; // ignore if it's not altar block
        }

        return true;
    }

    private boolean isAltar(PlayerInteractEvent event) {
        Block block = event.getClickedBlock();
        if (block == null) {
            return false;
        }

        Material blockType = block.getType();
        if (blockType == altarBlockType) {
            return true;
        }

        return false;
    }

}
