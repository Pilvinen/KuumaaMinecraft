package starandserpent.minecraft.criticalfixes;

import org.bukkit.Server;
import org.bukkit.entity.ArmorStand;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.UUID;

public class PlayersData implements Listener {

    private final JavaPlugin plugin;
    private Server server;

    public PlayersData(JavaPlugin plugin) {
        this.plugin = plugin;
        this.server = plugin.getServer();
    }

    // HashMap of whether the player has inventory open or not.
    private static final HashMap<UUID, Boolean> playerInventoryOpen = new HashMap<>();

    // HashMap of HeadGear armor stands.
    private static final HashMap<UUID, ArmorStand> playerHeadGear = new HashMap<>();


    public static boolean playerHasInventoryOpen(UUID playerUUID) {
        return playerInventoryOpen.getOrDefault(playerUUID, false);
    }

    public static void setPlayerInventoryOpen(UUID playerUUID, boolean isOpen) {
        playerInventoryOpen.put(playerUUID, isOpen);
    }

    public static boolean playerHasHeadGear(UUID playerUUID) {
        return playerHeadGear.containsKey(playerUUID);
    }

    public static ArmorStand getPlayerHeadGear(UUID playerUUID) {
        return playerHeadGear.get(playerUUID);
    }

    public static void setPlayerHeadGear(UUID playerUUID, ArmorStand armorStand) {
        playerHeadGear.put(playerUUID, armorStand);
    }


    // Even though there's no event for "E" inventory opening we can track it being open from first click.
    @EventHandler(priority = EventPriority.MONITOR) public void onInventoryClick(InventoryClickEvent event) {
        var player = event.getWhoClicked();
        var playerUUID = player.getUniqueId();
        setPlayerInventoryOpen(playerUUID, true);
    }

    // This works for all other inventories except for "E" inventory.
    @EventHandler(priority = EventPriority.MONITOR) public void onInventoryOpen(InventoryOpenEvent event) {
        var player = event.getPlayer();
        var playerUUID = player.getUniqueId();
        setPlayerInventoryOpen(playerUUID, true);
    }

    @EventHandler(priority = EventPriority.MONITOR) public void onInventoryClose(InventoryCloseEvent event) {
        var player = event.getPlayer();
        var playerUUID = player.getUniqueId();
        setPlayerInventoryOpen(playerUUID, false);
    }

    @EventHandler(priority = EventPriority.LOWEST) public void onPlayerQuit(PlayerQuitEvent event) {
        var player = event.getPlayer();
        var playerUUID = player.getUniqueId();
        playerInventoryOpen.remove(playerUUID);
        deleteHeadGear(playerUUID);
    }

    public static void deleteHeadGear(UUID playerUUID) {
        playerHeadGear.remove(playerUUID);
    }

}
