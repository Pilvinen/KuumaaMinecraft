package starandserpent.minecraft.criticalfixes;

import org.bukkit.*;
import org.bukkit.entity.ItemFrame;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

import static org.bukkit.Bukkit.getServer;

public class ImprovedClock implements Listener {

    private final JavaPlugin plugin;
    private Server server;

    private List<Location> clockLocations = new ArrayList<>(); // Stores locations of all clocks found

    private long checkInterval = 100L; // Check for clocks every 5 seconds (100 ticks)
    private long soundInterval = 20L; // Play clock sounds every 1 second (20 ticks)

    private final float volume = 0.2F;
    private boolean playHighPitch = true;

    public ImprovedClock(JavaPlugin plugin) {
        this.plugin = plugin;
        server = plugin.getServer();

        getServer().getScheduler().runTaskTimer(plugin, this::updateClockLocations, 0L, checkInterval);
        getServer().getScheduler().runTaskTimer(plugin, this::playSounds, soundInterval, soundInterval);
    }

    private void updateClockLocations() {
        clockLocations.clear(); // Clear previous list before searching
        for (World world : getServer().getWorlds()) {
            Collection<ItemFrame> clockFrames = world.getEntitiesByClass(ItemFrame.class);
            for (ItemFrame frame : clockFrames) {
                Location location = frame.getLocation();
                if (isClockInFrame(frame)) {
                    clockLocations.add(location);
                }
            }
        }
    }

    private void playSounds() {
        for (Location location : clockLocations) {
            var world = location.getWorld();
            if (world == null) {
                continue;
            }

            playHighPitch = !playHighPitch;
            float pitch = playHighPitch ? 0.9F : 0.8F;
            world.playSound(location, Sound.BLOCK_NOTE_BLOCK_HAT, volume, pitch);
        }
    }

    private boolean isClockInFrame(ItemFrame frame) {
        // Implement your logic to check if the item frame contains a clock
        // This example assumes there's a method on ItemFrame to check the displayed item
        return frame.getItem().getType() == Material.CLOCK;
    }


    @EventHandler public void onPlayerClickOnClickInItemFrame(PlayerInteractEntityEvent event) {

        // Not an item frame.
        if (!(event.getRightClicked() instanceof ItemFrame itemFrame)) {
            return;
        }

        // Not a click in item frame.
        if (!isClockInFrame(itemFrame)) {
            return;
        }

        // It is? Well don't rotate it, you dingus!
        event.setCancelled(true);

        // Tell the time.
        DayLengthFix.displayTimeWithActionBar(event.getPlayer());
    }

}
