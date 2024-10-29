package starandserpent.minecraft.criticalfixes;

import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class ItemFrameImprovements implements Listener {

    private final JavaPlugin plugin;
    private Server server;

    public ItemFrameImprovements(JavaPlugin plugin) {
        this.plugin = plugin;
        server = plugin.getServer();
    }

    // On thrown invisibility potion hitting item frame
    // Make item frame invisible
    @EventHandler public void onPotionSplash(PotionSplashEvent event) {

        // If the thrown splash potion is a potion of invisibility.
        boolean isInvisibility = isInvisibilityPotion(event);
        if (!isInvisibility) {
            return;
        }

        // Loop entities in radius of potion splash. But do not use affected entities.
        var location = event.getEntity().getLocation();
        var world = location.getWorld();
        if (world == null) {
            return;
        }

        for (Entity entity : world.getNearbyEntities(location, 3, 3, 3)) {

            if (entity instanceof ItemFrame frame) {
                // Item frame must not be empty.
                if (frame.getItem().getType() == Material.AIR) {
                    continue;
                }

                frame.setVisible(false);
            }
        }
    }

    private boolean isInvisibilityPotion(PotionSplashEvent event) {
        for(PotionEffect e : event.getPotion().getEffects()){
            if(e.getType() == PotionEffectType.INVISIBILITY){
                return true;
            }
        }
        return false;
    }

    // On removing item from invisible item frame, make item frame visible.
    @EventHandler(priority = EventPriority.HIGHEST) public void onItemFrameBreak(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof ItemFrame frame) {
            if (frame.isVisible()) {
                return;
            }
            frame.setVisible(true);
        }
    }

}
