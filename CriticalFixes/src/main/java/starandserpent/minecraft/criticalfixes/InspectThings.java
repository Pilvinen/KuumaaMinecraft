package starandserpent.minecraft.criticalfixes;

import fi.septicuss.tooltips.api.TooltipsAPI;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.TranslatableComponent;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.block.data.Ageable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.HashMap;
import java.util.List;

public class InspectThings implements Listener {

    private final JavaPlugin plugin;
    private Server server;

    // HashSet of inspectable things.
    private final HashMap<Material, String> inspectableThings = new HashMap<>() {{
        // Map different growth stages of potatoes growing in farmland with different texts for each stage.

        put(Material.POTATOES, "{potato} Perunat");
        put(Material.CARROTS, "{carrot} Porkkanat");
        put(Material.TORCHFLOWER_CROP, "{torchflower} Soihtuliljat");
        put(Material.BEETROOTS, "{beetroot} Punajuuret");
        put(Material.MELON_STEM, "{melon} Melonit");
        put(Material.PUMPKIN_STEM, "{pumpkin} Kurpitsat");
        put(Material.WHEAT, "{wheat} Vehnät");
        put(Material.SWEET_BERRY_BUSH, "{sweet_berries} Makeat marjat");
        put(Material.PITCHER_CROP, "{pitcherplant} Sinikannut");
    }};

    public InspectThings(JavaPlugin plugin) {
        this.plugin = plugin;
        this.server = plugin.getServer();
    }

    // Inspect things on right click with empty hand event.
    @EventHandler public void onPlayerInteract(PlayerInteractEvent event) {

        // Check for empty main hand right click only
        if (event.getHand() != EquipmentSlot.HAND ||
                event.getAction() != Action.RIGHT_CLICK_BLOCK ||
                event.getItem() != null) {
            return;
        }

        // Don't process clicking air.
        var player = event.getPlayer();
        var clickedBlock = event.getClickedBlock();
        if (clickedBlock == null) {
            return;
        }

        var targetBlock = clickedBlock;
        var targetBlockType = targetBlock.getType();
        if (targetBlockType == Material.FARMLAND) {
            // Check the block above the farmland.
            targetBlock = clickedBlock.getRelative(0, 1, 0);
            targetBlockType = targetBlock.getType();
        }

        if (inspectableThings.containsKey(targetBlockType)) {
            String inspectionString = inspectableThings.get(targetBlockType);

            // Key was contained, there should be a translation for this, but something went wrong.
            if (inspectionString == null) {
                return;
            }

            // Get the translation string from resource pack.
//            TextComponent translationString = new TextComponent(new TranslatableComponent(translationKey));

            // Check if it can be cast as Ageable.
            if (!(targetBlock.getBlockData() instanceof Ageable ageable)) {
                // Add a full stop since the translation string ended here.
                inspectionString += ". ";

                // Send the message to the player.
                List<String> tooltipText = List.of(inspectionString);
                TooltipsAPI.sendTooltip(player, TooltipsAPI.getTheme("default-two-line"), tooltipText);
//                player.spigot().sendMessage(translationString);
                return;
            }

            // Add a space for ageables.
            inspectionString += " ";
//            translationString.addExtra(new TextComponent(" "));

            // Check for growth stage and add a suffix message.
            String suffixGrowthMessage = getSuffixGrowthMessage(ageable);
            inspectionString += suffixGrowthMessage + ". ";

//            translationString.addExtra(suffixGrowthMessage);
//            translationString.addExtra(new TextComponent("."));

            // Send the message to the player.
            //player.spigot().sendMessage(translationString);
            //player.sendTitle(translationString.getText(), translationString.getText(), 10, 70, 20);
            List<String> tooltipText = List.of(inspectionString);
            TooltipsAPI.sendTooltip(player, TooltipsAPI.getTheme("default-one-line"), tooltipText);
        }
    }

    private static String getSuffixGrowthMessage(Ageable ageable) {
        String suffixGrowthMessage;
        var maxAge = ageable.getMaximumAge();
        var age = ageable.getAge();
        if (age == maxAge) {
            suffixGrowthMessage = "ovat kypsiä korjattavaksi";
        } else if (age == maxAge - 1) {
            suffixGrowthMessage = "ovat melkein kypsiä korjattavaksi";
        } else if (age == 0) {
            suffixGrowthMessage = "on vastikään istutettu";
        } else if (age == 1) {
            suffixGrowthMessage = "ovat alkaneet itää";
        } else {
            suffixGrowthMessage = "eivät ole täysin kypsiä vielä";
        }
        return suffixGrowthMessage;
    }

}
