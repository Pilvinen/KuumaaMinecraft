package starandserpent.minecraft.criticalfixes;

import com.comphenix.protocol.ProtocolManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;
import java.util.HashMap;
import java.util.List;

public class Emoticons implements Listener {

    private final JavaPlugin plugin;
    private Server server;
    private final ProtocolManager protocolManager;

    public Emoticons(JavaPlugin plugin, ProtocolManager protocolManager) {
        this.plugin = plugin;
        this.server = plugin.getServer();
        this.protocolManager = protocolManager;
//        addPacketListener();
    }

    // List of enabled players.
    private final List<String> enabledPlayers = List.of(
        "Pilvinen"
    );

    // HashMap of blinking players and their timers.
    private final HashMap<Player, BukkitTask> blinkingPlayers = new HashMap<>();

    private float blinkingMinInterval = 4.3f;
    private float blinkingMaxInterval = 6.8f;

    // List of temporary items.
    private static final List<CustomItemsEnum> temporaryItems = List.of(
        CustomItemsEnum.EYELIDS_2X2,
        CustomItemsEnum.FULL_FACE_TEXTURE_SMILE,
        CustomItemsEnum.FULL_FACE_TEXTURE_SAD,
        CustomItemsEnum.FULL_FACE_TEXTURE_LAUGH,
        CustomItemsEnum.FULL_FACE_TEXTURE_SERIOUS,
        CustomItemsEnum.FULL_FACE_TEXTURE_SHOCK
    );

    public static void iFeel(Player player, Emotion emotion) {

        setHeadGearTo(emotion.getCustomItem(), player);

        // get plugin reference
        CriticalFixes plugin = JavaPlugin.getPlugin(CriticalFixes.class);

        // Remove after bukkit timer delay.
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            deleteTemporaryItemFromHeadGear(player, emotion.getCustomItem());
        }, 20L * 2); // 2 seconds.
    }


//    private void addPacketListener() {
//        protocolManager.addPacketListener(new PacketAdapter(plugin, PacketType.Play.Server.ENTITY_LOOK, PacketType.Play.Server.REL_ENTITY_MOVE_LOOK) {
//
//            @Override
//            public void onPacketSending(PacketEvent event) {
//                Player player = event.getPlayer();
//                ArmorStand armorStand = getArmorStandPassenger(player);
//
//                if (armorStand != null) {
//                     Modify the packet to reflect new armor stand position
//                    adjustArmorStandPosition(armorStand, player);
//                }
//            }
//        });
//    }

    // Start blinking on join.
    @EventHandler public void onPlayerJoin(PlayerJoinEvent event) {
        var player = event.getPlayer();
        var playerName = player.getName();

        if (enabledPlayers.contains(playerName)) {

            // Register a timer for blinking player.
            var ref = new Object() {
                long randomBlinkDelay = (long) (blinkingMinInterval + Math.random() * (blinkingMaxInterval - blinkingMinInterval)) * 20;
            };

            var task = Bukkit.getScheduler().runTaskTimer(plugin, () -> {

                // Set a random blink delay to the timer will keep going.
                ref.randomBlinkDelay = (long) (blinkingMinInterval + Math.random() * (blinkingMaxInterval - blinkingMinInterval)) * 20;

                // Check if the player can blink.
                boolean canBlink = canBlink(player);
                if (!canBlink) {
                    return;
                }

                // If the player doesn't have a helmet, give them eyelids.
                setEyelidsToHeadGear(player);

                // Remove the helmet after a random time between 0.1 and 0.4 seconds.
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    deleteEyelidsFromHeadGear(player);
                }, (long) (Math.random() * 5 + 2)); // Ie. 0.1 to 0.35 seconds (in ticks).

            }, 0, ref.randomBlinkDelay);

            blinkingPlayers.put(player, task);
        }
    }


    private boolean canBlink(Player player) {

        // Check if inventory is open. We don't want to eyelids to be grabbed.
        var isNotAllowedToBlinkRightNow = PlayersData.playerHasInventoryOpen(player.getUniqueId());
        if (isNotAllowedToBlinkRightNow) {
            return false;
        }

        boolean canBlink = false;

        var helmetSlot = player.getInventory().getHelmet();
        if (helmetSlot == null) {
            canBlink = true;
        } else if (helmetSlot.getType() == Material.AIR) {
            canBlink = true;
        }

        if (!canBlink) {
            return false;
        }
        return true;
    }

    // Prevent removing the eyelids helmet from the player's inventory via inventory interaction.
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInventoryClick(InventoryClickEvent event) {
        Inventory clickedInventory = event.getClickedInventory();

        // Check if the clicked inventory is null
        if (clickedInventory == null) {
            return;
        }

        if (clickedInventory.getType() == InventoryType.PLAYER) {
//            System.out.println("Player inventory click event.");
            if (event.getSlotType() == InventoryType.SlotType.ARMOR) {
//                System.out.println("Player armor slot click event.");

                var cursor = event.getCurrentItem();
                if (cursor == null) {
//                    System.out.println("Cursor is null.");
                    return;
                }

                ItemMeta itemMeta = cursor.getItemMeta();
                if (itemMeta == null) {
//                    System.out.println("Item meta is null.");
                    return;
                }

//                System.out.println("Item meta is not null: " + itemMeta.getDisplayName());


                if (isEyelids(cursor)) {
//                    System.out.println("Player armor slot click event. Eyelids found!!!");
                    event.setCurrentItem(new ItemStack(Material.AIR));
                }
            }
        }
    }

    @EventHandler public void onPlayerInventoryPickup(InventoryPickupItemEvent event) {
        ItemStack item = event.getItem().getItemStack();
        if (isEyelids(item)) {
            event.setCancelled(true);
            item.setType(Material.AIR);
        }
    }

    // On player death event, remove the eyelids helmet.
    @EventHandler public void onPlayerDeath(PlayerDeathEvent event) {
        var player = event.getEntity();
        deleteEyelidsFromHeadGear(player);
    }

    @EventHandler public void onItemDrop(PlayerDropItemEvent event) {
        ItemStack item = event.getItemDrop().getItemStack();
        if (isEyelids(item)) {
            event.setCancelled(true);
            item.setType(Material.AIR);
        }
    }


    private void toggleEyelidsInHeadGear(Player player, boolean visible) {
        if (visible) {
            setEyelidsToHeadGear(player);
        } else {
            deleteEyelidsFromHeadGear(player);
        }
    }

    private static void setHeadGearTo(CustomItemsEnum customItem, Player player) {

        // Get the helmet from the player's inventory
        ItemStack helmet = player.getInventory().getHelmet();

        // Check if the helmet is null or if its type is not AIR
        if (helmet != null && helmet.getType() != Material.AIR) {
            return;
        }

        // Create item stack.
        var itemStack = new ItemStack(customItem.getMaterial());
        var itemMetaData = itemStack.getItemMeta();
        itemMetaData.setCustomModelData(customItem.getId());
        itemStack.setItemMeta(itemMetaData);

        // Set the item to the player's helmet slot.
        player.getInventory().setHelmet(itemStack);
    }

    private void setEyelidsToHeadGear(Player player) {

        // Get the helmet from the player's inventory
        ItemStack helmet = player.getInventory().getHelmet();

        // Check if the helmet is null or if its type is not AIR
        if (helmet != null && helmet.getType() != Material.AIR) {
            return;
        }

        // Create eyelids item stack.
        var eyelids = new ItemStack(CustomItemsEnum.EYELIDS_2X2.getMaterial());
        var eyelidsMetaData = eyelids.getItemMeta();
        eyelidsMetaData.setCustomModelData(CustomItemsEnum.EYELIDS_2X2.getId());
        eyelidsMetaData.setItemName("Silmäluomet");
        eyelidsMetaData.setLore(List.of("Hyi olkoon, silmäluomet! Miksi sinulla on nämä??"));
        eyelids.setItemMeta(eyelidsMetaData);

        // Set the eyelids to the player's helmet slot.
        player.getInventory().setHelmet(eyelids);
    }

    private static void deleteTemporaryItemFromHeadGear(Player player, CustomItemsEnum customItem) {
        ItemStack helmet = player.getInventory().getHelmet();
        if (helmet == null) {
            return;
        }

        Material helmetType = helmet.getType();
        if (helmetType != customItem.getMaterial()) {
            return;
        }

        ItemMeta helmetMetaData = helmet.getItemMeta();
        if (helmetMetaData == null) {
            return;
        }

        if (helmetMetaData.getCustomModelData() != customItem.getId()) {
            return;
        }

        // Check if the item is a temporary item.
        if (!temporaryItems.contains(customItem)) {
            return;
        }

        // Remove the helmet from the player.
        player.getInventory().setHelmet(null);
    }

    private void deleteEyelidsFromHeadGear(Player player) {
        ItemStack helmet = player.getInventory().getHelmet();
        if (helmet == null) {
            return;
        }

        Material helmetType = helmet.getType();
        if (helmetType != CustomItemsEnum.EYELIDS_2X2.getMaterial()) {
            return;
        }

        ItemMeta helmetMetaData = helmet.getItemMeta();
        if (helmetMetaData == null) {
            return;
        }

        if (helmetMetaData.getCustomModelData() != CustomItemsEnum.EYELIDS_2X2.getId()) {
            return;
        }

        // Remove the helmet from the player.
        player.getInventory().setHelmet(null);
    }

    private boolean isEyelids(ItemStack itemStack) {
        if (itemStack == null) {
//            System.out.println("Item stack is null.");
            return false;
        }

        if (itemStack.getType() != CustomItemsEnum.EYELIDS_2X2.getMaterial()) {
//            System.out.println("Item stack is not eyelids material.");
            return false;
        }

        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) {
//            System.out.println("Item meta is null.");
            return false;
        }

        boolean hasEyelidData = itemMeta.getCustomModelData() == CustomItemsEnum.EYELIDS_2X2.getId();
//        System.out.println("Item has eyelid data: " + hasEyelidData);
        return hasEyelidData;
    }

    // Delete headGear on quit.
    @EventHandler(priority = EventPriority.HIGHEST) public void onPlayerQuit(PlayerQuitEvent event) {
        var player = event.getPlayer();

        // Remove the eyelid helmet on player quit event.
        deleteEyelidsFromHeadGear(player);
    }


    private static EulerAngle convertVectorToEulerAngle(Vector vec) {

        double x = vec.getX();
        double y = vec.getY();
        double z = vec.getZ();

        double xz = Math.sqrt(x*x + z*z);

        double eulX;
        if(x < 0) {
            if(y == 0) {
                eulX = Math.PI*0.5;
            } else {
                eulX = Math.atan(xz/y)+Math.PI;
            }
        } else {
            eulX = Math.atan(y/xz)+Math.PI*0.5;
        }

        double eulY;
        if(x == 0) {
            if(z > 0) {
                eulY = Math.PI;
            } else {
                eulY = 0;
            }
        } else {
            eulY = Math.atan(z/x)+Math.PI*0.5;
        }

        return new EulerAngle(eulX, eulY, 0);

    }

}
