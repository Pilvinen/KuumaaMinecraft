package starandserpent.minecraft.criticalfixes;

import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;

public class Hats implements Listener {

    private final JavaPlugin plugin;
    private Server server;
    private PluginManager pluginManager;

    // Permission to use the plugin.
    private static final Permission canUsePlugin = new Permission("hats.use");

    // Permission for all hats.
    private static final Permission permissionAllHats = new Permission("hats.wear.all_hats");

    // HashMap of hats you're allowed to wear.
    private static final Permission permissionAllFlowerHats = new Permission("hats.wear.all_flower_hats");
    private static final HashMap<Material, Permission> flowerHats = new HashMap<>() {{
        // All flowers.
        put(Material.DANDELION, new Permission("hats.wear.flower_hats.dandelion"));
        put(Material.POPPY, new Permission("hats.wear.flower_hats.poppy"));
        put(Material.BLUE_ORCHID, new Permission("hats.wear.flower_hats.blue_orchid"));
        put(Material.ALLIUM, new Permission("hats.wear.flower_hats.allium"));
        put(Material.AZURE_BLUET, new Permission("hats.wear.flower_hats.azure_bluet"));
        put(Material.RED_TULIP, new Permission("hats.wear.flower_hats.red_tulip"));
        put(Material.ORANGE_TULIP, new Permission("hats.wear.flower_hats.orange_tulip"));
        put(Material.WHITE_TULIP, new Permission("hats.wear.flower_hats.white_tulip"));
        put(Material.PINK_TULIP, new Permission("hats.wear.flower_hats.pink_tulip"));
        put(Material.OXEYE_DAISY, new Permission("hats.wear.flower_hats.oxeye_daisy"));
        put(Material.CORNFLOWER, new Permission("hats.wear.flower_hats.cornflower"));
        put(Material.LILY_OF_THE_VALLEY, new Permission("hats.wear.flower_hats.lily_of_the_valley"));
        put(Material.WITHER_ROSE, new Permission("hats.wear.flower_hats.wither_rose"));
        put(Material.SUNFLOWER, new Permission("hats.wear.flower_hats.sunflower"));
        put(Material.LILAC, new Permission("hats.wear.flower_hats.lilac"));
        put(Material.ROSE_BUSH, new Permission("hats.wear.flower_hats.rose_bush"));
        put(Material.PEONY, new Permission("hats.wear.flower_hats.peony"));
    }};

    // HashMap of hats all special hats.
    private static final Permission permissionAllSpecialHats = new Permission("hats.wear.all_special_hats");
    private static final HashMap<Material, Permission> specialHats = new HashMap<>() {{
        // Glass block.
        put(Material.GLASS, new Permission("hats.wear.special_hats.space_helmet"));
        put(Material.WHITE_BANNER, new Permission("hats.wear.special_hats.white_banner"));
        put(Material.RED_BANNER, new Permission("hats.wear.special_hats.red_banner"));
    }};

    public Hats(JavaPlugin plugin) {
        this.plugin = plugin;
        server = plugin.getServer();
        pluginManager = server.getPluginManager();

        registerHatPermissions();
    }

    private void registerHatPermissions() {
        // Register the main permission.
        pluginManager.addPermission(canUsePlugin);
        // Register all hats permission.
        pluginManager.addPermission(permissionAllHats);
        // Register all flower hats permission.
        pluginManager.addPermission(permissionAllFlowerHats);
        // Register all special hats permission.
        pluginManager.addPermission(permissionAllSpecialHats);

        // Iterate all individual flower hats and register their permissions.
        for (Permission permission : flowerHats.values()) {
            pluginManager.addPermission(permission);
        }

        // Iterate all individual special hats and register their permissions.
        for (Permission permission : specialHats.values()) {
            pluginManager.addPermission(permission);
        }
    }

    // Allow players to wear hats.
    @EventHandler public static void onInvInteract(InventoryClickEvent event) {

        // Check if the player is trying to remove a hat. We ALWAYS allow that, permissions or not.
        boolean isTryingToRemoveHat = isTryingToRemoveHat(event);
        if (isTryingToRemoveHat) {
            tryToRemoveHat(event);
            return;
        }

        // Now that the special case is handled, it's business as usual. Check if the action is valid.
        boolean isValidAction = isValidAction(event);
        if (!isValidAction) {
            return;
        }

        // Check permissions.
        ItemStack cursor = event.getCursor();
        HumanEntity player = event.getWhoClicked();
        boolean isAllowedToWearAsHat = isAllowedToWearAsHat(player, cursor);
        if (!isAllowedToWearAsHat) {
            return;
        }

        // Everything seems legit, let's handle different click types.
        switch (event.getClick()) {
            // When player right clicks the helmet slot with an item, transfer the items.
            case RIGHT:
                transferItems(event, player, cursor);
                break;
            // When player left clicks the helmet slot, swap the items.
            case LEFT:
                swapItems(event, player, cursor);
                break;
            // When player shift left/right clicks the helmet slot, cancel the event.
            case SHIFT_LEFT:
            case SHIFT_RIGHT:
            default:
                // No specific action for other click types
                break;
        }

    }

    private static boolean isTryingToRemoveHat(InventoryClickEvent event) {

        // Check if the click is a left click. That's the only way to remove a hat.
        var click = event.getClick();
        if (!click.equals(ClickType.LEFT)) {
            return false;
        }

        // Must be clicking the helmet slot.
        var rawSlot = event.getRawSlot();
        if (rawSlot != 5) {
            return false;
        }

        // Check if the player is trying to remove a hat based on the cursor being empty.
        boolean cursorIsNullOrEmpty = true;
        var cursor = event.getCursor();
        if (cursor != null) {
            var cursorType = cursor.getType();
            cursorIsNullOrEmpty = cursorType.equals(Material.AIR);
        }

        // If the cursor is not null or empty we're most definitely not trying to remove a hat.
        if (!cursorIsNullOrEmpty) {
            return false;
        }

        // Next check if the hat slot contains something to remove.
        var inv = event.getWhoClicked().getInventory();
        var helmet = inv.getItem(EquipmentSlot.HEAD);
        if (helmet == null) {
            return false;
        }

        // Check against air too.
        var helmetType = helmet.getType();
        if (helmetType.equals(Material.AIR)) {
            return false;
        }

        // Well. It seems like the player is really trying to remove a hat.
        return true;
    }

    private static void tryToRemoveHat(InventoryClickEvent event) {
        // Cancel the event to prevent default behavior
        event.setCancelled(true);

        // Get the player's inventory.
        PlayerInventory inv = event.getWhoClicked().getInventory();

        // Get the helmet item.
        ItemStack helmet = inv.getHelmet();

        // Set the helmet slot to empty.
        inv.setHelmet(null);

        // Set the helmet slot to empty.
        event.getWhoClicked().setItemOnCursor(helmet);
    }

    private static boolean isValidAction(InventoryClickEvent event) {

        // Get the raw slot number.
        var rawSlot = event.getRawSlot();

        // If the raw slot is not the helmet slot, return we have nothing to do.
        if (rawSlot != 5) {
            return false;
        }

        // Get the slot type being interacted with.
        var slotType = event.getSlotType();

        // If the slot type interacted with is not armor, return we have nothing to do.
        if (!slotType.equals(InventoryType.SlotType.ARMOR)) {
            return false;
        }

        // Cursor represents the item that the player is holding in the mouse cursor.
        var cursor = event.getCursor();

        // If the cursor is null we should always allow the interaction. Might be trying to take off a hat.
        if (cursor == null) {
            return true;
        }

        // Now that we've established that the cursor is not null, we can check the type of the item.
        var cursorType = cursor.getType();

        // If the cursor is air, we should always allow the interaction. Might be trying to take off a hat.
        if (cursorType.equals(Material.AIR)) {
            return true;
        }

        // Default is to allow.
        return true;
    }

    // Check permissions.
    public static boolean isAllowedToWearAsHat(HumanEntity player, ItemStack item) {

        // First off we need to have the permission to use the plugin.
        boolean hasRightToUse = player.hasPermission(canUsePlugin);
        System.out.println("hasRightToUse: " + hasRightToUse);
        boolean hasSpaceHelmetPermission = player.hasPermission(specialHats.get(Material.GLASS));
        System.out.println("hasSpaceHelmetPermission: " + hasSpaceHelmetPermission);

        if (!player.hasPermission(canUsePlugin)) {
            return false;
        }

        // Get the item type.
        Material itemType = item.getType();

        // Next we check if the item is a hat.
        // And whether the player has permission to wear that specific hat.
        // We do that by checking all the hat collections.
        boolean canWearAsHat = false;
        boolean permissionCorrect = false;

        // Player has permission to wear all hats.
        if (player.hasPermission(permissionAllHats)) {
            permissionCorrect = true;
        }

        // Flower hats
        System.out.println("flowerHats.containsKey(item.getType()): " + flowerHats.containsKey(item.getType()) + ", ie. " + item.getType());
        System.out.println("specialHats.containsKey(item.getType()): " + specialHats.containsKey(item.getType()) + ", ie. " + item.getType());
        if (flowerHats.containsKey(item.getType())) {
            canWearAsHat = true;

            permissionCorrect = player.hasPermission(permissionAllFlowerHats);

            if (player.hasPermission(flowerHats.get(itemType))) {
                permissionCorrect = true;
            }

        // Special hats
        } else if (specialHats.containsKey(item.getType())) {
            canWearAsHat = true;

            permissionCorrect = player.hasPermission(permissionAllSpecialHats);

            if (player.hasPermission(specialHats.get(itemType))) {
                permissionCorrect = true;
            }
        }

        // Both conditions must be true:
        // - Can we worn (ie. is a hat).
        // - Has permission.
        return canWearAsHat && permissionCorrect;
    }

    private static void transferItems(InventoryClickEvent event, HumanEntity player, ItemStack cursor) {

        // Cancel the event to prevent default behavior
        event.setCancelled(true);

        PlayerInventory inv = player.getInventory();
        ItemStack helmet = inv.getHelmet();

        // Check if cursor has more than one item. We do not allow it.
        if (cursor.getAmount() > 1) {
            return;
        }

        // Transfer a single item from cursor to helmet (if possible)
        // If the hat slot was not empty we do not allow adding more items.
        boolean notWearingHelmet = helmet == null || helmet.getType() == Material.AIR;
        if (notWearingHelmet) {
            helmet = new ItemStack(cursor.getType(), 1);
            inv.setHelmet(helmet);

            // If the cursor had more than one item, decrease the amount by one.
            // Otherwise, set the cursor to an empty item stack.
            if (cursor.getAmount() > 1) {
                cursor.setAmount(cursor.getAmount() - 1);
            } else {
                event.getWhoClicked().setItemOnCursor(new ItemStack(Material.AIR));
            }

        }
    }

    private static void swapItems(InventoryClickEvent event, HumanEntity player, ItemStack cursor) {

        // Cancel the event to prevent default behavior
        event.setCancelled(true);

        // Get the player's inventory.
        PlayerInventory inv = event.getWhoClicked().getInventory();

        ItemStack helmet = inv.getHelmet();

        // Stack size on cursor must be exactly 1 or we do not allow this.
        if (cursor.getAmount() != 1) {
            return;
        }

        // If cursor and helmet are different, swap them completely
        if (!cursor.equals(helmet)) {

            // Set the helmet slot to the cursor item.
            inv.setHelmet(cursor);

            // Set the cursor to the helmet item.
            event.getWhoClicked().setItemOnCursor(helmet);

            // If cursor and helmet are the same, combine them at cursor, ie. remove hat.
        } else {
            // Handle potential overflow if combining stacks by not allowing the action.
            int combinedAmount = cursor.getAmount() + helmet.getAmount();
            if (combinedAmount > helmet.getMaxStackSize()) {
                return;
            }

            // Set the helmet slot to empty.
            inv.setHelmet(null);

            // Set the cursor to the combined item stack.
            cursor.setAmount(combinedAmount);
            event.getWhoClicked().setItemOnCursor(cursor);
        }
    }

}

