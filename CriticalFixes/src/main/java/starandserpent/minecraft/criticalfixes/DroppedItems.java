package starandserpent.minecraft.criticalfixes;

import org.bukkit.Material;
import org.bukkit.Rotation;
import org.bukkit.Server;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import java.util.UUID;

public class DroppedItems implements Listener {

    private final JavaPlugin plugin;
    private Server server;

    // Constants for the different types of books.
    final int stacked2 = CustomItemsEnum.BOOK_STACK_2.getId();
    final int stacked3 = CustomItemsEnum.BOOK_STACK_3.getId();
    final int stacked4 = CustomItemsEnum.BOOK_STACK_4.getId();
    final int stacked5 = CustomItemsEnum.BOOK_STACK_5.getId();
    final int uprightBack1 = CustomItemsEnum.BOOK_UPRIGHT_BACK_1.getId();
    final int uprightBack2 = CustomItemsEnum.BOOK_UPRIGHT_BACK_2.getId();
    final int uprightBack3 = CustomItemsEnum.BOOK_UPRIGHT_BACK_3.getId();
    final int uprightBack4 = CustomItemsEnum.BOOK_UPRIGHT_BACK_4.getId();
    final int uprightBack5 = CustomItemsEnum.BOOK_UPRIGHT_BACK_5.getId();
    final int uprightMiddle1 = CustomItemsEnum.BOOK_UPRIGHT_MIDDLE_1.getId();
    final int uprightMiddle2 = CustomItemsEnum.BOOK_UPRIGHT_MIDDLE_2.getId();
    final int uprightMiddle3 = CustomItemsEnum.BOOK_UPRIGHT_MIDDLE_3.getId();
    final int uprightMiddle4 = CustomItemsEnum.BOOK_UPRIGHT_MIDDLE_4.getId();
    final int uprightMiddle5 = CustomItemsEnum.BOOK_UPRIGHT_MIDDLE_5.getId();
    final int uprightFront1 = CustomItemsEnum.BOOK_UPRIGHT_FRONT_1.getId();
    final int uprightFront2 = CustomItemsEnum.BOOK_UPRIGHT_FRONT_2.getId();
    final int uprightFront3 = CustomItemsEnum.BOOK_UPRIGHT_FRONT_3.getId();
    final int uprightFront4 = CustomItemsEnum.BOOK_UPRIGHT_FRONT_4.getId();
    final int uprightFront5 = CustomItemsEnum.BOOK_UPRIGHT_FRONT_5.getId();

    public DroppedItems(JavaPlugin plugin) {
        this.plugin = plugin;
        this.server = plugin.getServer();
    }

    // On item being dropped.
    // This is called when an item is dropped.
    @EventHandler public void onItemDrop(ItemSpawnEvent event) {
        var item = event.getEntity();
        var itemStack = item.getItemStack();

        // Check if item was dropped by a player.
        UUID itemDropper = item.getThrower();
        if (itemDropper == null) {
//            System.out.println("Item was not dropped by a player.");
            return;
        }

        // Get player by UUID.
        var player = server.getPlayer(itemDropper);
        if (player == null) {
            System.out.println("Player was not found.");
            return;
        }

        // Item was not book, written book, or writable book.
        var itemType = itemStack.getType();
        if (itemType != Material.WRITABLE_BOOK
            && itemType != Material.WRITTEN_BOOK
            && itemType != Material.BOOK) {
            return;
        }

        // Now we know a player dropped the item.

        // Bukkit task wait for 3 seconds.
        var task = new BukkitRunnable() {
            int count = 0;

            @Override
            public void run() {

                // Increment the count. We try 5 times to place the item on the ground every half seconds.
                count++;

                // We ultimately failed to place the item on the ground.
                if (count > 6) {
                    System.out.println("Item was not placed on the ground successfully in time.");
                    this.cancel();
                    return;
                }

                // Check if the item is still on the ground.
                if (item.isDead()) {
                    System.out.println("Item is dead.");
                    this.cancel();
                    return;
                }

                // Check if the item is still the same item.
                if (!item.getItemStack().equals(itemStack)) {
                    System.out.println("Item is not the same item.");
                    this.cancel();
                    return;
                }

                // Check if it's a single item. We don't want to deal with stacks.
                if (itemStack.getAmount() != 1) {
                    System.out.println("Item is not a single item.");
                    this.cancel();
                    return;
                }

                // Check if the item is still in the same location.
                var itemLocationNow = item.getLocation();
                var blockAtItemLocation = itemLocationNow.getBlock();
                var blockTypeAtItemLocation = blockAtItemLocation.getType();
                var blockBelowItemLocation = blockAtItemLocation.getRelative(BlockFace.DOWN);
                var blockTypeBelowItemLocation = blockBelowItemLocation.getType();
                if (blockTypeAtItemLocation != Material.AIR) {
                    System.out.println("Item is not on the ground.");
                    return;
                }

                // DEBUG: Check the type of block at and below.
                System.out.println("Block at item location: " + blockTypeAtItemLocation);
                System.out.println("Block below item location: " + blockTypeBelowItemLocation);

                // Only accepted surfaces are allowed.
                if (!Tag.PLANKS.isTagged(blockTypeBelowItemLocation)
                    && blockTypeBelowItemLocation != Material.CHISELED_BOOKSHELF
                    && !Tag.SLABS.isTagged(blockTypeBelowItemLocation)
                    && blockTypeBelowItemLocation != Material.BOOKSHELF) {
                    System.out.println("Item is not on a proper surface.");
                    return;
                }

                // Now we need to check if there's already an item frame there.
                var itemFrameAtLocation = blockAtItemLocation.getWorld().getNearbyEntities(itemLocationNow, 0.5, 0.5, 0.5).stream()
                    .filter(entity -> entity instanceof ItemFrame)
                    .map(entity -> (ItemFrame) entity)
                    .findFirst()
                    .orElse(null);

                boolean successfullyPlacedItem = false;
                // If there's no item frame there, we can just place one with no worries.
                if (itemFrameAtLocation == null) {
                    System.out.println("No item frame at location.");
                    placeItemOnTheGround(blockAtItemLocation, itemStack, this);
                    successfullyPlacedItem = true;
                } else {
                    System.out.println("Item frame at location.");
                    if (weCanAddMoreItemsToItemFrame(itemFrameAtLocation, itemStack)) {
                        System.out.println("Yes! We can add more items to the item frame.");
                        successfullyPlacedItem = true;
                    }
                    tryToPlaceMoreItemsInItemFrame(itemFrameAtLocation, itemStack, this);
                }

                // Remove the dropped item.
                if (successfullyPlacedItem) {
                    item.remove();
                    this.cancel();
                }
            }
        };

        // Schedule the task to run every 500 milliseconds (10 ticks) for 6 times
        task.runTaskTimer(plugin, 0L, 10L);

    }

    private boolean weCanAddMoreItemsToItemFrame(ItemFrame itemFrameAtLocation, ItemStack itemStack) {
        // Get the item in the item frame.
        var itemInItemFrame = itemFrameAtLocation.getItem();
        var itemInItemFrameType = itemInItemFrame.getType();

        // If the item in the item frame is not a book, we don't want to deal with it.
        if (itemInItemFrameType != Material.BOOK) {
            System.out.println("Item in item frame is not a book.");
            return false;
        }

        // We can add to max 5 books in a stack.
        var metaData = itemInItemFrame.getItemMeta();
        if (metaData == null) {
            System.out.println("Item in item frame has no item meta.");
            return false;
        }

        boolean hasCustomModelData = metaData.hasCustomModelData();
        int customModelData = -1;
        if (!hasCustomModelData) {
            return true;
        }

        customModelData = metaData.getCustomModelData();

        if (customModelData == stacked5) {
            return false;
        } else if (customModelData == uprightBack5) {
            return false;
        } else if (customModelData == uprightMiddle5) {
            return false;
        } else if (customModelData == uprightFront5) {
            return false;
        }

        return true;
    }

    private void tryToPlaceMoreItemsInItemFrame(ItemFrame itemFrameAtLocation, ItemStack itemStack, BukkitRunnable task) {
        // Get the item in the item frame.
        var itemInItemFrame = itemFrameAtLocation.getItem();

        System.out.println("Attempting heroically to place more items in the item frame.");
        // Get the custom model data of the item in the item frame.
        var itemMeta = itemInItemFrame.getItemMeta();
        if (itemMeta == null) {
            System.out.println("Item in item frame has no item meta.");
            task.cancel();
            return;
        }

        int customModelData = -1;
        boolean hasCustomModelData = itemMeta.hasCustomModelData();
        if (hasCustomModelData) {
            System.out.println("Item in item frame has custom model data!");
            customModelData = itemMeta.getCustomModelData();
            System.out.println("And the custom model data is: " + customModelData);
        } else { // DEBUG
            System.out.println("Item in item frame has NO custom model data.");
        }

        var nextCustomItemId = getNextCustomItemId(customModelData);

        if (nextCustomItemId == -1) {
            System.out.println("Next custom item id is -1.");
            task.cancel();
            return;
        }

        System.out.println("Next custom item id will be: " + nextCustomItemId);

        // Set the item to the next custom item. We're stacking it up.
        itemMeta.setCustomModelData(nextCustomItemId);
        itemInItemFrame.setItemMeta(itemMeta);

        // Update the item in the item frame.
        itemFrameAtLocation.setItem(itemInItemFrame);

        System.out.println("Item was placed in the item frame successfully.");

    }

    private int getNextCustomItemId(int customModelData) {

        // Get the current custom model data.
        var currentCustomItemId = customModelData;

        // Use switch to go over the different types of books.
        var nextCustomItemId = -1;

        if (currentCustomItemId == -1) {
            nextCustomItemId = stacked2;
        } else if (currentCustomItemId == stacked2) {
            nextCustomItemId = stacked3;
        } else if (currentCustomItemId == stacked3) {
            nextCustomItemId = stacked4;
        } else if (currentCustomItemId == stacked4) {
            nextCustomItemId = stacked5;
        } else if (currentCustomItemId == uprightBack1) {
            nextCustomItemId = uprightBack2;
        } else if (currentCustomItemId == uprightBack2) {
            nextCustomItemId = uprightBack3;
        } else if (currentCustomItemId == uprightBack3) {
            nextCustomItemId = uprightBack4;
        } else if (currentCustomItemId == uprightBack4) {
            nextCustomItemId = uprightBack5;
        } else if (currentCustomItemId == uprightMiddle1) {
            nextCustomItemId = uprightMiddle2;
        } else if (currentCustomItemId == uprightMiddle2) {
            nextCustomItemId = uprightMiddle3;
        } else if (currentCustomItemId == uprightMiddle3) {
            nextCustomItemId = uprightMiddle4;
        } else if (currentCustomItemId == uprightMiddle4) {
            nextCustomItemId = uprightMiddle5;
        } else if (currentCustomItemId == uprightFront1) {
            nextCustomItemId = uprightFront2;
        } else if (currentCustomItemId == uprightFront2) {
            nextCustomItemId = uprightFront3;
        } else if (currentCustomItemId == uprightFront3) {
            nextCustomItemId = uprightFront4;
        } else if (currentCustomItemId == uprightFront4) {
            nextCustomItemId = uprightFront5;
        }

        return nextCustomItemId;
    }

    private void placeItemOnTheGround(Block blockAtItemLocation, ItemStack itemStack, BukkitRunnable task) {
        // Place invisible item frame.
        var location = blockAtItemLocation.getLocation();
        var world = location.getWorld();
        if (world == null) {
            System.out.println("World is null.");
            task.cancel();
            return;
        }

        ItemFrame itemFrame = (ItemFrame) world.spawnEntity(location, EntityType.ITEM_FRAME);
        itemFrame.setVisible(false);
        itemFrame.setFacingDirection(BlockFace.UP, true);
        itemFrame.setItem(itemStack);

        Rotation randomRotation = Rotation.values()[(int) (Math.random() * Rotation.values().length)];
        itemFrame.setRotation(randomRotation);

        System.out.println("Item was placed on the ground successfully.");
    }

    // On removing an item from item frame.
    @EventHandler public void onItemFrameRemove(EntityDamageByEntityEvent event) {
        var entity = event.getEntity();
        if (!(entity instanceof ItemFrame itemFrame)) {
            System.out.println("Entity is not an item frame.");
            return;
        }

        ItemStack item = itemFrame.getItem();
        var itemType = item.getType();

        // If item frame is invisible, remove it.
        if (itemFrame.isVisible()) {
            System.out.println("Item frame is visible.");
            return;
        }

        // But only if the item is a book of some sort.
        if (itemType != Material.WRITABLE_BOOK
            && itemType != Material.WRITTEN_BOOK
            && itemType != Material.BOOK) {
            System.out.println("Item is not a writable book.");
            return;
        }

        System.out.println("Item frame was removed.");
        // Drop the item in the item frame.
//        itemFrame.getWorld().dropItemNaturally(itemFrame.getLocation(), item);
        itemFrame.remove();
    }

    // When rotating an item in item frame which is a book/custom book stack, go through the custom states.
    @EventHandler public void onItemFrameRotate(PlayerInteractEntityEvent event) {

        var entity = event.getRightClicked();
        if (!(entity instanceof ItemFrame itemFrame)) {
            System.out.println("Entity is not an item frame.");
            return;
        }

        ItemStack item = itemFrame.getItem();
        var itemType = item.getType();

        // If item frame is visible, it's not one of ours.
        if (itemFrame.isVisible()) {
            System.out.println("Item frame is visible.");
            return;
        }

        // But only if the item is a book of some sort.
        if (itemType != Material.BOOK) {
            System.out.println("Item is not a book.");
            return;
        }

        // Get the custom model data of the item in the item frame.
        var itemMeta = item.getItemMeta();
        if (itemMeta == null) {
            System.out.println("Item in item frame has no item meta.");
            return;
        }

        int customModelData = -1;
        boolean hasCustomModelData = itemMeta.hasCustomModelData();
        if (hasCustomModelData) {
            System.out.println("Item in item frame has custom model data!");
            customModelData = itemMeta.getCustomModelData();
            System.out.println("And the custom model data is: " + customModelData);
        } else { // DEBUG
            System.out.println("Item in item frame has NO custom model data.");
        }

        var rotation = itemFrame.getRotation();
        var nextCustomItemId = getNextCustomItemIdRotation(rotation, customModelData);

        System.out.println("Next custom item id will be: " + nextCustomItemId);

        // If -100 is returned, we don't need to do anything, it's not final rotation.
        if (nextCustomItemId == -100) {
            System.out.println("Next custom item id is -100.");
            return;
        }

        itemMeta.setCustomModelData(nextCustomItemId);
        item.setItemMeta(itemMeta);

        // Set rotation to NONE
        if (rotation != Rotation.COUNTER_CLOCKWISE_45) {
            itemFrame.setRotation(Rotation.CLOCKWISE_45);
        }

        // Update the item in the item frame.
        itemFrame.setItem(item);

        System.out.println("Item was rotated in the item frame successfully.");
    }

    private int getNextCustomItemIdRotation(Rotation rotation, int customModelData) {

            // Get the current custom model data.
            var currentCustomItemId = customModelData;

            // Use switch to go over the different types of books.
            var nextCustomItemId = -100;

            if (rotation == Rotation.COUNTER_CLOCKWISE_45) {

                if (currentCustomItemId == -1) {
                    nextCustomItemId = uprightBack1;
                } else if (currentCustomItemId == uprightBack1) {
                    nextCustomItemId = uprightMiddle1;
                } else if (currentCustomItemId == uprightMiddle1) {
                    nextCustomItemId = uprightFront1;
                } else if (currentCustomItemId == uprightFront1) {
                    nextCustomItemId = -1;

                } else if (currentCustomItemId == stacked2) {
                    nextCustomItemId = uprightBack2;
                } else if (currentCustomItemId == uprightBack2) {
                    nextCustomItemId = uprightMiddle2;
                } else if (currentCustomItemId == uprightMiddle2) {
                    nextCustomItemId = uprightFront2;
                } else if (currentCustomItemId == uprightFront2) {
                    nextCustomItemId = stacked2;

                } else if (currentCustomItemId == stacked3) {
                    nextCustomItemId = uprightBack3;
                } else if (currentCustomItemId == uprightBack3) {
                    nextCustomItemId = uprightMiddle3;
                } else if (currentCustomItemId == uprightMiddle3) {
                    nextCustomItemId = uprightFront3;
                } else if (currentCustomItemId == uprightFront3) {
                    nextCustomItemId = stacked3;

                } else if (currentCustomItemId == stacked4) {
                    nextCustomItemId = uprightBack4;
                } else if (currentCustomItemId == uprightBack4) {
                    nextCustomItemId = uprightMiddle4;
                } else if (currentCustomItemId == uprightMiddle4) {
                    nextCustomItemId = uprightFront4;
                } else if (currentCustomItemId == uprightFront4) {
                    nextCustomItemId = stacked4;

                } else if (currentCustomItemId == stacked5) {
                    nextCustomItemId = uprightBack5;
                } else if (currentCustomItemId == uprightBack5) {
                    nextCustomItemId = uprightMiddle5;
                } else if (currentCustomItemId == uprightMiddle5) {
                    nextCustomItemId = uprightFront5;
                } else if (currentCustomItemId == uprightFront5) {
                    nextCustomItemId = stacked5;
                }
            }

            return nextCustomItemId;
    }

    // On entity damage by entity. If it's item frame with custom books, cancel event and drop book items.
    // And remove the item frame.
    @EventHandler public void onItemFrameDamage(EntityDamageByEntityEvent event) {
        var entity = event.getEntity();
        if (!(entity instanceof ItemFrame itemFrame)) {
            System.out.println("Entity is not an item frame.");
            return;
        }

        ItemStack item = itemFrame.getItem();
        var itemType = item.getType();

        // If item frame is visible, skip.
        if (itemFrame.isVisible()) {
            System.out.println("Item frame is visible.");
            return;
        }

        // Must be a book.
        if (itemType != Material.BOOK) {
            System.out.println("Item is not a book.");
            return;
        }

        // Drop the item in the item frame.
        var dropLocation = itemFrame.getLocation();
        var world = dropLocation.getWorld();
        if (world == null) {
            System.out.println("World is null.");
            return;
        }
        int count = getBookCountFromCustomModelData(itemFrame);

        // Cancel event.
        event.setCancelled(true);

        ItemStack droppingItemStack = new ItemStack(Material.BOOK, count);
        world.dropItemNaturally(dropLocation, droppingItemStack);
        itemFrame.remove();
    }

    private int getBookCountFromCustomModelData(ItemFrame itemFrame) {
        var item = itemFrame.getItem();
        var itemMeta = item.getItemMeta();
        if (itemMeta == null) {
            return 1;
        }

        int customModelData = -1;
        boolean hasCustomModelData = itemMeta.hasCustomModelData();
        if (hasCustomModelData) {
            customModelData = itemMeta.getCustomModelData();
        }

        if (customModelData == -1) {
            return 1;
        } else if (customModelData == stacked2) {
            return 2;
        } else if (customModelData == stacked3) {
            return 3;
        } else if (customModelData == stacked4) {
            return 4;
        } else if (customModelData == stacked5) {
            return 5;
        } else if (customModelData == uprightBack1) {
            return 1;
        } else if (customModelData == uprightBack2) {
            return 2;
        } else if (customModelData == uprightBack3) {
            return 3;
        } else if (customModelData == uprightBack4) {
            return 4;
        } else if (customModelData == uprightBack5) {
            return 5;
        } else if (customModelData == uprightMiddle1) {
            return 1;
        } else if (customModelData == uprightMiddle2) {
            return 2;
        } else if (customModelData == uprightMiddle3) {
            return 3;
        } else if (customModelData == uprightMiddle4) {
            return 4;
        } else if (customModelData == uprightMiddle5) {
            return 5;
        } else if (customModelData == uprightFront1) {
            return 1;
        } else if (customModelData == uprightFront2) {
            return 2;
        } else if (customModelData == uprightFront3) {
            return 3;
        } else if (customModelData == uprightFront4) {
            return 4;
        } else if (customModelData == uprightFront5) {
            return 5;
        }

        return 1;
    }


}
