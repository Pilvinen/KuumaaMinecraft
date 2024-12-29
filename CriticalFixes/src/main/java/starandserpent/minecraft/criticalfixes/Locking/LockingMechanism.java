package starandserpent.minecraft.criticalfixes.Locking;

import com.nivixx.ndatabase.api.NDatabase;
import com.nivixx.ndatabase.api.repository.Repository;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.block.data.type.Door;
import org.bukkit.block.Barrel;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class LockingMechanism implements Listener {
    private final JavaPlugin plugin;
    private final Repository<String, LockData> repository;

    public LockingMechanism(JavaPlugin plugin) {
        this.plugin = plugin;
        this.repository = NDatabase.api().getOrCreateRepository(LockData.class);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Block block = event.getBlock();
        if (block.getState() instanceof Chest) {
            Chest chest = (Chest) block.getState();
            if (chest.getCustomName() != null) {
                String chestName = chest.getCustomName();
                boolean isDoubleChest = chest.getInventory().getHolder() instanceof DoubleChest;
                LockData lockData = new LockData(getLocationKey(block.getLocation()), "", false, null, 0, null, block.getLocation(), isDoubleChest);
                repository.upsert(lockData);
            }
            handleDoubleChest(chest);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        if (block.getState() instanceof Chest) {
            if (isLocked(block)) {
                event.setCancelled(true);
                event.getPlayer().sendMessage("You cannot break a locked chest.");
            } else {
                repository.delete(getLocationKey(block.getLocation()));
            }
        }
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        if (event.getInventory().getHolder() instanceof Chest) {
            Chest chest = (Chest) event.getInventory().getHolder();
            if (isLocked(chest.getBlock())) {
                event.setCancelled(true);
                event.getPlayer().sendMessage("The chest is locked.");
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Block block = event.getClickedBlock();
        if (block != null && block.getState() instanceof Chest) {
            Player player = event.getPlayer();
            ItemStack key = player.getInventory().getItemInMainHand();
            if (key.hasItemMeta() && key.getItemMeta().hasDisplayName()) {
                String keyName = key.getItemMeta().getDisplayName();
                Chest chest = (Chest) block.getState();
                if (chest.getCustomName() != null && chest.getCustomName().equals(keyName)) {
                    if (isLocked(block)) {
                        unlockContainer(block, player);
                    } else {
                        lockContainer(block, player);
                    }
                    event.setCancelled(true);
                }
            } else if (isLocked(block)) {
                event.setCancelled(true);
                player.sendMessage("The chest is locked.");
            }
        }
    }

    private void lockContainer(Block block, Player owner) {
        ItemStack key = owner.getInventory().getItemInMainHand();
        String keyName = key.getItemMeta().getDisplayName();
        boolean isDoubleChest = ((Chest) block.getState()).getInventory().getHolder() instanceof DoubleChest;
        LockData lockData = new LockData(getLocationKey(block.getLocation()), keyName, true, owner.getUniqueId(), System.currentTimeMillis(), owner.getUniqueId(), block.getLocation(), isDoubleChest);
        repository.upsert(lockData);
        playLockSound(owner);
        owner.sendMessage("Container locked successfully.");
    }

    private void unlockContainer(Block block, Player player) {
        LockData lockData = repository.get(getLocationKey(block.getLocation()));
        if (lockData != null) {
            lockData.setLocked(false);
            repository.upsert(lockData);
            playUnlockSound(player);
            player.sendMessage("Container unlocked successfully.");
        }
    }

    private boolean isLocked(Block block) {
        LockData lockData = repository.get(getLocationKey(block.getLocation()));
        return lockData != null && lockData.isLocked();
    }

    private void playLockSound(Player player) {
        player.playSound(player.getLocation(), "minecraft:block.anvil.use", 1.0f, 1.0f);
    }

    private void playUnlockSound(Player player) {
        player.playSound(player.getLocation(), "minecraft:block.anvil.break", 1.0f, 1.0f);
    }

    private void handleDoubleChest(Chest chest) {
        if (chest.getInventory().getHolder() instanceof DoubleChest) {
            DoubleChest doubleChest = (DoubleChest) chest.getInventory().getHolder();
            Chest leftChest = (Chest) doubleChest.getLeftSide();
            Chest rightChest = (Chest) doubleChest.getRightSide();
            if (isLocked(leftChest.getBlock()) || isLocked(rightChest.getBlock())) {
                LockData lockData = isLocked(leftChest.getBlock()) ? repository.get(getLocationKey(leftChest.getBlock().getLocation())) : repository.get(getLocationKey(rightChest.getBlock().getLocation()));
                lockData.setDoubleChest(true);
                repository.upsert(new LockData(getLocationKey(leftChest.getBlock().getLocation()), lockData.getPassword(), lockData.isLocked(), lockData.getOwner(), lockData.getLastAccessed(), lockData.getLastAccessedBy(), leftChest.getBlock().getLocation(), true));
                repository.upsert(new LockData(getLocationKey(rightChest.getBlock().getLocation()), lockData.getPassword(), lockData.isLocked(), lockData.getOwner(), lockData.getLastAccessed(), lockData.getLastAccessedBy(), rightChest.getBlock().getLocation(), true));
            }
        }
    }

    private String getLocationKey(Location location) {
        return location.getWorld().getName() + "_" + location.getBlockX() + "_" + location.getBlockY() + "_" + location.getBlockZ();
    }
}