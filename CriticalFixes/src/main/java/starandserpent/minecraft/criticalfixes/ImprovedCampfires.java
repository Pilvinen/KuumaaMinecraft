package starandserpent.minecraft.criticalfixes;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Campfire;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ImprovedCampfires implements Listener {

    private final JavaPlugin plugin;
    private Server server;

    private Map<String, BukkitTask> campfireTasks = new HashMap<>();

    // HashMap of campfires int key and rotation and Material.
    private HashMap<Integer, CampfireData> campfires = new HashMap<>() {{
        // Cold coals only.
        put(0, new CampfireData(0, Material.SOUL_CAMPFIRE, BlockFace.NORTH, false));
        // Hot coals only.
        put(1, new CampfireData(1, Material.SOUL_CAMPFIRE, BlockFace.EAST, false));
        // Burning fire with 2 logs.
        put(2, new CampfireData(2, Material.SOUL_CAMPFIRE, BlockFace.NORTH, true));
        // Burning fire with 4 logs.
        put(3, new CampfireData(3, Material.CAMPFIRE, BlockFace.EAST, true));
        // Burning fire with 6 logs.
        put(4, new CampfireData(4, Material.CAMPFIRE, BlockFace.SOUTH, true));
        // Burning fire with 8 logs.
        put(5, new CampfireData(5, Material.SOUL_CAMPFIRE, BlockFace.WEST, true));
        // Fresh lit campfire with kindling.
        put(6, new CampfireData(6, Material.SOUL_CAMPFIRE, BlockFace.SOUTH, false));
        // Fresh unlit campfire with kindling.
        put(7, new CampfireData(7, Material.SOUL_CAMPFIRE, BlockFace.WEST, false));
    }};

    private HashMap<Integer, CampfireData> campfiresWithGrill = new HashMap<>() {{
        // Same as above, but with grill also.
        put(0, new CampfireData(0, Material.CAMPFIRE, BlockFace.NORTH, false));
        put(1, new CampfireData(1, Material.CAMPFIRE, BlockFace.EAST, false));
        put(2, new CampfireData(2, Material.SOUL_CAMPFIRE, BlockFace.SOUTH, true));
        put(3, new CampfireData(3, Material.CAMPFIRE, BlockFace.WEST, true));
        put(4, new CampfireData(4, Material.CAMPFIRE, BlockFace.NORTH, true));
        put(5, new CampfireData(5, Material.SOUL_CAMPFIRE, BlockFace.EAST, true));
        put(6, new CampfireData(6, Material.CAMPFIRE, BlockFace.SOUTH, false));
        put(7, new CampfireData(7, Material.CAMPFIRE, BlockFace.WEST, false));
    }};

    private final HashMap<Integer, Long> campfireBurnTimes = new HashMap<>() {{
        put(0, 20L * 15);      // 15 seconds
        put(1, 20L * 60);      // 1 minutes

        put(2, 20L * 60 * 15); // 15 minutes
        put(3, 20L * 60 * 15); // 15 minutes
        put(4, 20L * 60 * 15); // 15 minutes
        put(5, 20L * 60 * 15); // 15 minutes

// DEBUG speed.
//        put(2, 20L * 30); // 15 minutes
//        put(3, 20L * 30); // 15 minutes
//        put(4, 20L * 30); // 15 minutes
//        put(5, 20L * 30); // 15 minutes

        put(6, 20L * 15);      // 15 seconds
        put(7, 20L * 15);      // 15 seconds
    }};

    private final Material grillBlockOnly = Material.WAXED_WEATHERED_COPPER_GRATE;

    public ImprovedCampfires(JavaPlugin plugin) {
        this.plugin = plugin;
        this.server = plugin.getServer();
    }


    @EventHandler public void onPlacingGrill(PlayerInteractEvent event) {

        var itemInHand = event.getPlayer().getInventory().getItemInMainHand();

        boolean isTryingToPlaceGrill = isTryingToPlaceGrill(event, itemInHand);
        if (!isTryingToPlaceGrill) {
            return;
        }

        // We've established that the player is holding a grill and is trying to place it.
//        System.out.println("Player is trying to place a grill.");

        // Get the block that was replaced. This is the block that was clicked on, but we treat it as the block below.
        var clickedBlock = event.getClickedBlock();
        if (clickedBlock == null) {
            return;
        }

        var isCampfireWithGrill = isCampfireWithGrill(clickedBlock);
        if (isCampfireWithGrill) {
//            System.out.println("ERROR: Trying to place grill on campfire with grill.");
            return;
        }

        // Place it on existing campfire.
        if (isCampfire(clickedBlock)) {

            // Subtract 1 item from the player's hand.
            itemInHand.setAmount(itemInHand.getAmount() - 1);
            placeGrillOnExistingCampfire(clickedBlock);
//            System.out.println("Placing grill on existing campfire.");
            return;
        }

        // Place it on ground or what ever - no pre-existing campfire involved.
//        System.out.println("Placing grill on ground, no campfire.");

        var replacedBlock = clickedBlock.getRelative(BlockFace.UP);
        var blockBelowReplacedBlock = clickedBlock;

        // The replacedBlock needs to be air.
        if (replacedBlock.getType() != Material.AIR) {
//            System.out.println("ERROR: Replaced block is not air, it is: " + replacedBlock.getType().name());
            return;
        }

        // Don't allow any of this.
        if (!blockBelowReplacedBlock.getType().isSolid() // Must be solid below.
            || blockBelowReplacedBlock.getType() == Material.AIR // Can't be air below.
            || blockBelowReplacedBlock.getType() == Material.WATER // Can't be water below.
            || blockBelowReplacedBlock.getType() == Material.LAVA // Can't be lava below.
            || blockBelowReplacedBlock.getType() == Material.CAMPFIRE // Can't be campfire below.
            || blockBelowReplacedBlock.getType() == Material.SOUL_CAMPFIRE // Can't be soul campfire below.
            || blockBelowReplacedBlock.getType() == grillBlockOnly // Can't be placed on another grill.
            || Tag.FENCES.isTagged(blockBelowReplacedBlock.getType()) // Can't be fence below.
            || Tag.FENCE_GATES.isTagged(blockBelowReplacedBlock.getType()) // Can't be fence gate below.
            || Tag.WALLS.isTagged(blockBelowReplacedBlock.getType())) { // Can't be wall below.

//            System.out.println("ERROR: Block below is not solid or allowed, it is: "
//                    + blockBelowReplacedBlock.getType().name());
            return;
        }

        // Additionally, test if MC thinks grillBlockOnly can be placed here.
        BlockData placementTesterGrillBlock = grillBlockOnly.createBlockData();
        if (!replacedBlock.canPlace(placementTesterGrillBlock)) {
//            System.out.println("ERROR: MC thinks grillBlockOnly can't be placed here.");
            return;
        }

        // Subtract 1 item from the player's hand.
        itemInHand.setAmount(itemInHand.getAmount() - 1);

        placeGrillOnEmptyBlock(replacedBlock);
    }

    private boolean isTryingToPlaceGrill(PlayerInteractEvent event, ItemStack itemInHand) {
        // Was right click action.
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return false;
        }

        // Tool of player should be a shulker shell (grill).
        var player = event.getPlayer();
        var itemInHandType = itemInHand.getType();
        if (itemInHandType != Material.SHULKER_SHELL) {
            return false;
        }

        // No meta? Can't do anything.
        var itemMeta = itemInHand.getItemMeta();
        if (itemMeta == null) {
            return false;
        }

        // No custom model data? Can't do anything. It's not our item.
        if (!itemMeta.hasCustomModelData()) {
            return false;
        }

        // Is it a campfire surrogate item?
        int customModelData = itemMeta.getCustomModelData();
        if (customModelData != CustomItemsEnum.GRILL.getId()) {
            return false;
        }

        return true;
    }

    private void placeGrillOnExistingCampfire(Block replacedBlock) {

        // Get the campfire data.
        var campfireId = getCampfireId(replacedBlock);
//        System.out.println("Placing Campfire with Grill ID: " + campfireId);

        // Set the block to campfire with grill.
        setBlockToCampfireWithGrill(replacedBlock, campfireId);

        // Play grill placing sound
        Location location = replacedBlock.getLocation();
        playGrillPlacingSound(location);
    }

    private void placeGrillOnEmptyBlock(Block replacedBlock) {
        // Set the block to grill.
        // TODO: Need to add grill block.
        Location location = replacedBlock.getLocation();
        playGrillPlacingSound(location);

        // Set to waxed_weathered_copper_grate, eg. grill without any campfire.
        replacedBlock.setType(grillBlockOnly);
    }

    private void playGrillPlacingSound(Location location) {
        // Play sound wood placing sound.
        Sound sound = Sound.BLOCK_COPPER_GRATE_PLACE;
        float pitch = 1.0f;
        float volume = 1.0f;
        var world = location.getWorld();
        if (world == null) {
            return;
        }
        world.playSound(location, sound, volume, pitch);
    }

    @EventHandler public void preventCampfirePlacing(BlockPlaceEvent event) {
        // Get the block that was placed.
        var placedBlock = event.getBlockPlaced();
        var placedBlockType = placedBlock.getType();
        if (placedBlockType == Material.CAMPFIRE
            || placedBlockType == Material.SOUL_CAMPFIRE) {
            // Cancel placing all campfires. They are placed now via surrogate item (see below).
            event.setCancelled(true);
        }
    }

    @EventHandler public void onPlacingCampfire(PlayerInteractEvent event) {

        ItemStack itemInHand = event.getPlayer().getInventory().getItemInMainHand();
        boolean isTryingToPlaceCampfireSurrogateItem = isTryingToPlaceCampfireSurrogateItem(event, itemInHand);
        if (!isTryingToPlaceCampfireSurrogateItem) {
            return;
        }

        // We've established that the player is holding the campfire surrogate item and trying to place it.

        // Get the block that was replaced. This is the block that was clicked on, but we treat it as the block below.
        var clickedBlock = event.getClickedBlock();
        if (clickedBlock == null) {
            return;
        }

        // First we shall test if the player is trying to place the campfire on an empty grill.
        var clickedBlockType = clickedBlock.getType();
        if (clickedBlockType == grillBlockOnly) {
            // Remove 1 item from the player's hand.
            itemInHand.setAmount(itemInHand.getAmount() - 1);

            // Set the block to campfire with grill.
            setBlockToCampfireWithGrill(clickedBlock, 7);

            // Play sound wood placing sound.
            Sound sound = Sound.BLOCK_WOOD_PLACE;
            float pitch = 1.0f;
            float volume = 1.0f;
            var world = clickedBlock.getWorld();
            world.playSound(clickedBlock.getLocation(), sound, volume, pitch);

            return;
        }

        // Otherwise we shall test if the player is trying to place the campfire on the ground.

        var blockBelow = clickedBlock;
        var blockBelowType = clickedBlockType;

        // Get the block that will be replaced.
        Block replacedBlock = blockBelow.getRelative(BlockFace.UP);
        Material replacedBlockType = replacedBlock.getType();

        // Don't allow any of this.
        if (!blockBelowType.isSolid()
            || blockBelowType == Material.AIR
            || blockBelowType == Material.WATER
            || blockBelowType == Material.LAVA
            || blockBelowType == Material.CAMPFIRE
            || blockBelowType == Material.SOUL_CAMPFIRE
            || Tag.FENCES.isTagged(blockBelowType)
            || Tag.FENCE_GATES.isTagged(blockBelowType)
            || Tag.WALLS.isTagged(blockBelowType)) {

            return;
        }

        // If the block was not air likely player was doing stuff with shovel or flint and steel.
        // All campfire changes create block placed events. But we don't handle all that here.
        if (replacedBlockType != Material.AIR) {
            return;
        }

        // Remove 1 item from the player's hand.
        itemInHand.setAmount(itemInHand.getAmount() - 1);

        // Play sound wood placing sound.
        Sound sound = Sound.BLOCK_WOOD_PLACE;
        float pitch = 1.0f;
        float volume = 1.0f;
        var world = replacedBlock.getWorld();
        world.playSound(replacedBlock.getLocation(), sound, volume, pitch);

        // Bukkit delay of 1 tick.
        server.getScheduler().runTask(plugin, () -> {
            setBlockToCampfire(replacedBlock, 7);
        });
    }

    private boolean isTryingToPlaceCampfireSurrogateItem(PlayerInteractEvent event, ItemStack itemInHand) {
        // Was right click action.
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return false;
        }

        // Tool of player should be campfire or soul campfire.
        var itemInHandType = itemInHand.getType();
        if (itemInHandType != Material.SHULKER_SHELL) {
            return false;
        }

        // No meta? Can't do anything.
        var itemMeta = itemInHand.getItemMeta();
        if (itemMeta == null) {
            return false;
        }

        // No custom model data? Can't do anything. It's not out item.
        if (!itemMeta.hasCustomModelData()) {
            return false;
        }

        // Is it a campfire surrogate item?
        int customModelData = itemMeta.getCustomModelData();
        if (customModelData != CustomItemsEnum.CAMPFIRE_SURROGATE_ITEM.getId()) {
            return false;
        }

        return true;
    }

    // On lighting campfire with flint and steel
    @EventHandler public void onInteractingWithCampfire(PlayerInteractEvent event) {

        // Must be interacting with a block.
        var block = event.getClickedBlock();
        if (block == null) {
            return;
        }

        var blockType = block.getType();

        // Needs to be campfire.
        if (blockType != Material.CAMPFIRE
            && blockType != Material.SOUL_CAMPFIRE) {
            return;
        }

        // Must be right click.
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        var eventItemType = event.getMaterial();

        // Prevent shoveling action. If you want to extinguish, you should break it like a normal person.
        if (Tag.ITEMS_SHOVELS.isTagged(eventItemType)) {
            event.setCancelled(true);
            return;
        }

        // Other than that, if it's not flint and steel or fire charge, we really don't care.
        if (eventItemType != Material.FLINT_AND_STEEL
        && eventItemType != Material.FIRE_CHARGE
        && !Tag.LOGS_THAT_BURN.isTagged(eventItemType)) {
            return;
        }

        // So it's something which is trying to light a fire (or trying to add wood to the fire).

        // First of all, we cancel anything from happening.
        event.setCancelled(true);

        // Let's see if player is trying to add firewood to the campfire.
//        if (Tag.LOGS_THAT_BURN.isTagged(eventItemType)) {

            // Check if more firewood can be added to the fire.
//            boolean firewoodCanBeAdded = canAddFirewoodToCampfire(block);
//            if (!firewoodCanBeAdded) {
//                return;
//            }
//
//            addFirewoodToCampfire(block);

            // Subtract 1 firewood from tool.
//            var player = event.getPlayer();
//            var itemInHand = player.getInventory().getItemInMainHand();
//            itemInHand.setAmount(itemInHand.getAmount() - 1);
//
//            return;
//        }

        // Only light unlit campfires.
        boolean isFreshUnlit = isFreshUnlit(block);
        if (!isFreshUnlit) {
            return;
        }

        // With grill or without.
        if (isCampfireWithGrill(block)) {
            showCampfireTransitionEffects(block);

            setBlockToCampfireWithGrill(block, 6);
        } else {
            // Light the campfire.
            showCampfireTransitionEffects(block);

            setBlockToCampfire(block, 6);
        }

        // Repeating bukkit timer running every X ticks.
        startCampfireTask(block,6);

    }

    // On player dropping log into campfire.
    // On item being dropped.
    // This is called when an item is dropped.
    @EventHandler public void onItemDrop(ItemSpawnEvent event) {
        var item = event.getEntity();
        var itemStack = item.getItemStack();

        // Check if item was dropped by a player.
        UUID itemDropper = item.getThrower();
        if (itemDropper == null) {
            return;
        }

        // Get player by UUID.
        var player = server.getPlayer(itemDropper);
        if (player == null) {
            return;
        }

        // Item was not book, written book, or writable book.
        var itemType = itemStack.getType();
        if (!Tag.LOGS_THAT_BURN.isTagged(itemType)) {
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
                    this.cancel();
                    return;
                }

                // Check if the item is still on the ground.
                if (item.isDead()) {
                    this.cancel();
                    return;
                }

                // Check if the item is still the same item.
                if (!item.getItemStack().equals(itemStack)) {
                    this.cancel();
                    return;
                }

                // Check if it's a single item. We don't want to deal with stacks.
                if (itemStack.getAmount() != 1) {
                    this.cancel();
                    return;
                }

                // Check if the item is still in the same location.
                var itemLocationNow = item.getLocation();
                var blockAtItemLocation = itemLocationNow.getBlock();
                var blockTypeAtItemLocation = blockAtItemLocation.getType();
                if (blockTypeAtItemLocation != Material.CAMPFIRE
                        && blockTypeAtItemLocation != Material.SOUL_CAMPFIRE) {
                    return;
                }

                // Check if more firewood can be added to the fire.
                boolean firewoodCanBeAdded = canAddFirewoodToCampfire(blockAtItemLocation);
                if (!firewoodCanBeAdded) {
                    return;
                }

                addFirewoodToCampfire(blockAtItemLocation);

                // Remove the item from the ground.
                item.remove();

                // Cancel the task.
                this.cancel();
            }
        };

        // Schedule the task to run every 500 milliseconds (10 ticks) for 6 times
        task.runTaskTimer(plugin, 0L, 10L);
    }

    private void addFirewoodToCampfire(Block block) {

        // It's not even any kind of campfire. What are you doing?
        if (!isAnyCampfireType(block)) {
            return;
        }

        boolean isCampfire = isCampfire(block);

        int campfireId = getAnyTypeCampfireId(block);

        if (isCampfire) {
            // Add firewood to the campfire.
            setBlockToCampfire(block, campfireId + 1);
        } else {
            setBlockToCampfireWithGrill(block, campfireId + 1);
        }

        // Play sound wood placing sound.
        Sound sound = Sound.BLOCK_WOOD_PLACE;
        float pitch = 1.0f;
        float volume = 1.0f;
        var world = block.getWorld();

        Sound sound2 = Sound.ITEM_FIRECHARGE_USE;
        float volume2 = 0.5f;

        world.playSound(block.getLocation(), sound, volume, pitch);
        world.playSound(block.getLocation(), sound2, volume2, pitch);

        // Show particle effects from rousing the fire.
        showCampfireTransitionEffects(block);
    }

    private boolean canAddFirewoodToCampfire(Block block) {

        // It's not even any kind of campfire. What are you doing?
        if (!isAnyCampfireType(block)) {
            return false;
        }

        int campfireId = getAnyTypeCampfireId(block);

        // Can't add firewood to a campfire that's already at max.
        boolean canAddFirewood = campfireId < 5 && campfireId > 0;
        return canAddFirewood;
    }


    private void startCampfireTask(Block block, int campfireState) {

        String blockKey = block.getLocation().toString();
        // Cancel existing task if present.
        if (campfireTasks.containsKey(blockKey)) {
            campfireTasks.get(blockKey).cancel();
        }

        long burnTime = campfireBurnTimes.get(campfireState);
        var campfireTask = new BukkitRunnable() {
            @Override
            public void run() {

                // Before consuming the campfire, check if it's a valid campfire block.
                if (!isAnyCampfireType(block)) {
                    cancelCampfireTask(block.getLocation()); // Cancel the task if the block is not a campfire.
                    return;
                }

                // Consume campfire.
                int newState = consumeCampfire(block);

                // We received error state or end state. The campfire is gone. Cancel the task.
                if (newState <= 0) {
                    cancelCampfireTask(block.getLocation());
                    return;
                }

                // Show transition effects.
                showCampfireTransitionEffects(block);

                // Cancel the BukkitRunnable and start a new one with the burn time for the new campfire state.
                if (newState != campfireState) {
                    cancelCampfireTask(block.getLocation()); // Cancel the campfire task also.
                    startCampfireTask(block, newState);
                }
            }
        }.runTaskTimer(plugin, burnTime, burnTime);

        campfireTasks.put(blockKey, campfireTask);

        // If the campfire is in stage 6, start a separate BukkitRunnable that creates black smoke particles
        // every second.
        int smokingFire = 6;
        if (campfireState == smokingFire) {
            new BukkitRunnable() {
                @Override
                public void run() {

                    // Check if the block is a campfire before proceeding.
                    if (!isAnyCampfireType(block)) {
                        cancelCampfireTask(block.getLocation()); // Cancel the campfire task also.
                        this.cancel(); // Cancel the task if the block is not a campfire.
                        return;
                    }

                    // Check if it's raining and the campfire is exposed to the sky.
                    boolean isRaining = !block.getWorld().isClearWeather();
                    boolean isExposedToSky = block.getWorld().getHighestBlockYAt(block.getLocation().add(0,1,0)) <= block.getY();

                    // Define the base chance for the fire to go out.
                    double baseChanceToExtinguish = 1.0;
                    double changeToExtinguishWhenWet = 6.0;

                    double changeToExtinguish = baseChanceToExtinguish;
                    // If it's raining and the campfire is exposed to the sky, increase the chance for the fire to go out.
                    if (isRaining && isExposedToSky) {
                        changeToExtinguish = changeToExtinguishWhenWet;
//                        System.out.println("Campfire is exposed to the sky and it's raining. Trying to extinguish it...");
                    }

                    // Random chance for the fire to go out.
                    double random = Math.random() * 100;
//                    System.out.println("Rolled a random number: " + random + ", needs to be less than: " + changeToExtinguish);
                    if (random < changeToExtinguish) {
                        setCampfireStateToExtinguishedFresh(block);

                        // The fire has gone out, cancel all tasks.
                        cancelCampfireTask(block.getLocation()); // Cancel the campfire task also.
                        this.cancel();
                        return;
                    }

                    // Create black smoke particles.
                    var blockLocation = block.getLocation();
                    showParticle(blockLocation, Particle.SMOKE, 10);

                    // If the campfire state has changed, cancel this BukkitRunnable.
                    int campfireId = getAnyTypeCampfireId(block);
                    if (campfireId != smokingFire) {
                        this.cancel();
                    }
                }

                private void setCampfireStateToExtinguishedFresh(Block block) {
                    // Create black smoke particles.
                    var blockLocation = block.getLocation();
                    showParticle(blockLocation, Particle.SMOKE, 10);
                    showParticle(blockLocation, Particle.WHITE_SMOKE, 10);

                    // Play extinguish sound for campfire.
                    Sound extinguishSound = Sound.BLOCK_FIRE_EXTINGUISH;
                    var pitch = 1.0f;
                    var volume = 1.0f;
                    var world = block.getWorld();
                    world.playSound(block.getLocation(), extinguishSound, volume, pitch);

                    // Something went wrong.
                    if (!isAnyCampfireType(block)) {
                        return;
                    }

                    // Set the state back to unlit fresh state.
                    boolean isCampfire = isCampfire(block);
                    int extinguishedFreshCampfireId = 7;

                    if (isCampfire) {
                        setBlockToCampfire(block, extinguishedFreshCampfireId);
                    } else {
                        setBlockToCampfireWithGrill(block, extinguishedFreshCampfireId);
                    }
                }

            }.runTaskTimer(plugin, 0L, 10L); // 20 ticks = 1 second
        }
    }

    private void cancelCampfireTask(Location location) {
        String blockKey = location.toString();
        if (campfireTasks.containsKey(blockKey)) {
            campfireTasks.get(blockKey).cancel();
            campfireTasks.remove(blockKey);
        }
    }

    // On breaking a campfire.
    @EventHandler public void onBreakingCampfire(BlockBreakEvent event) {

        // Get the block that was placed.
        var block = event.getBlock();
        var blockType = block.getType();

        // We only deal with campfires.

        if (!isAnyCampfireType(event.getBlock()) && blockType != grillBlockOnly) {
            return;
        }

        // Handle grillBlockOnly first.
        if (blockType == grillBlockOnly) {

            // Cancel all grill events.
            event.setCancelled(true);

            // Set the block to air.
            block.setType(Material.AIR);

            // Play break sound for grill.
            Sound sound = Sound.BLOCK_COPPER_GRATE_BREAK;
            float pitch = 1.0f;
            float volume = 1.0f;
            var world = block.getWorld();
            world.playSound(block.getLocation(), sound, volume, pitch);

            // particle, location, particle amount, ?, offset x, offset y, offset z, data
            showBlockBreakParticle(block.getLocation(), block.getBlockData());

            // Cancel tasks.
            cancelCampfireTask(block.getLocation());

            // Drop the grill.
            dropGrill(block.getLocation(), 1);

            return;
        }

        // Handle breaking of campfires of various types.

        // Play break sound for campfire.
        BlockData blockData = block.getBlockData();
        var soundGroup = blockData.getSoundGroup();
        var breakSound = soundGroup.getBreakSound();
        var pitch = soundGroup.getPitch();
        var volume = soundGroup.getVolume();
        var world = block.getWorld();
        world.playSound(block.getLocation(), breakSound, volume, pitch);

        int campfireId = getAnyTypeCampfireId(block);

        // Play particle break effect from item.
        var blockLocation = block.getLocation();

        // Cancel all campfire events.
        event.setCancelled(true);

        boolean isCampfireWithGrill = isCampfireWithGrill(block);
        if (isCampfireWithGrill) {
            // Break the fire and set the block to only grill.
            block.setType(grillBlockOnly);
            // Cancel tasks.
            cancelCampfireTask(block.getLocation());
        } else {
            // There's no grill, just break the fire. Set the block to air.
            block.setType(Material.AIR);
            // Cancel tasks.
            cancelCampfireTask(block.getLocation());
        }

        // particle, location, particle amount, ?, offset x, offset y, offset z, data
        showBlockBreakParticle(blockLocation, blockData);

        switch (campfireId) {
            case 0: // Cold coals only.
                showParticle(blockLocation, Particle.WHITE_ASH, 5);
                dropCharcoal(blockLocation, 1);
                break;
            case 1: // Hot coals only.
                showParticle(blockLocation, Particle.WHITE_ASH, 5);
                showParticle(blockLocation, Particle.LAVA, 10);
                dropCharcoal(blockLocation, 1);
                break;
            case 2: // Burning fire with 2 logs.
                showParticle(blockLocation, Particle.LAVA, 10);
                showParticle(blockLocation, Particle.SMOKE, 10);
                dropCharcoal(blockLocation, 1);
                break;
            case 3: // Burning fire with 4 logs.
                showParticle(blockLocation, Particle.LAVA, 10);
                showParticle(blockLocation, Particle.SMOKE, 10);
                dropCharcoal(blockLocation, 1);
                break;
            case 4: // Burning fire with 6 logs.
                showParticle(blockLocation, Particle.LAVA, 10);
                showParticle(blockLocation, Particle.SMOKE, 10);
                dropCharcoal(blockLocation, 1);
                dropFirewood(blockLocation, 1);
                break;
            case 5: // Burning fire with 8 logs.
                showParticle(blockLocation, Particle.LAVA, 10);
                showParticle(blockLocation, Particle.SMOKE, 10);
                dropCharcoal(blockLocation, 1);
                dropFirewood(blockLocation, 2);
                break;
            case 6: // Fresh lit campfire with kindling.
                showParticle(blockLocation, Particle.SMOKE, 10);
                dropCharcoal(blockLocation, 1);
                dropFirewood(blockLocation, 3);
                break;
            case 7: // Fresh unlit campfire with kindling.
                dropKindling(blockLocation);
                dropCharcoal(blockLocation, 1);
                dropKindling(blockLocation);
                dropFirewood(blockLocation, 3);
                break;
        }

    }

    private void dropGrill(Location location, int amount) {

        var world = location.getWorld();
        if (world == null) {
            return;
        }

        var grillItem = CustomItemsEnum.GRILL.getItem(amount);
        if (grillItem == null) {
            return;
        }
        world.dropItemNaturally(location, grillItem);
    }

    private void showCampfireTransitionEffects(Block block) {

        // Check if the block is a campfire.
        if (!isAnyCampfireType(block)) {
            return;
        }

        int campfireId = getAnyTypeCampfireId(block);

        // Play particle break effect from item.
        var blockLocation = block.getLocation().add(0.5,0.5,0.5);
        var spreadX = 0.2f;
        var spreadY = 0.2f;
        var spreadZ = 0.2f;

        switch (campfireId) {
            case 0: // Cold coals only.

                // Play extinguish sound for campfire.
                Sound extinguishSound = Sound.BLOCK_FIRE_EXTINGUISH;
                float pitch = 2.0f;
                float volume = 0.2f;
                var world = block.getWorld();
                world.playSound(block.getLocation(), extinguishSound, volume, pitch);

                showParticle(blockLocation, Particle.SMOKE, 50, spreadX, spreadY, spreadZ, 0.01f);
                showParticle(blockLocation, Particle.WHITE_SMOKE, 25, spreadX, spreadY, spreadZ, 0.01f);
                break;
            case 1: // Hot coals only.

                // Play extinguish sound for campfire.
                Sound extinguishSound2 = Sound.BLOCK_FIRE_EXTINGUISH;
                float pitch2 = 2.0f;
                float volume2 = 0.2f;
                var world2 = block.getWorld();
                world2.playSound(block.getLocation(), extinguishSound2, volume2, pitch2);

                showParticle(blockLocation, Particle.LAVA, 2, spreadX, spreadY, spreadZ, 0.1f);
                showParticle(blockLocation, Particle.SMOKE, 50, spreadX, spreadY, spreadZ, 0.01f);
                showParticle(blockLocation, Particle.WHITE_SMOKE, 25, spreadX, spreadY, spreadZ, 0.01f);
                break;
            case 2: // Burning fire with 2 logs.
                showParticle(blockLocation, Particle.LAVA, 3, spreadX, spreadY, spreadZ, 0.1f);
                showParticle(blockLocation, Particle.SMOKE, 50, spreadX, spreadY, spreadZ, 0.01f);
                showParticle(blockLocation, Particle.WHITE_SMOKE, 25, spreadX, spreadY, spreadZ, 0.01f);
                break;
            case 3: // Burning fire with 4 logs.
                showParticle(blockLocation, Particle.LAVA, 3, spreadX, spreadY, spreadZ, 0.1f);
                showParticle(blockLocation, Particle.SMOKE, 50, spreadX, spreadY, spreadZ, 0.01f);
                showParticle(blockLocation, Particle.WHITE_SMOKE, 25, spreadX, spreadY, spreadZ, 0.01f);
                break;
            case 4: // Burning fire with 6 logs.
                showParticle(blockLocation, Particle.LAVA, 3, spreadX, spreadY, spreadZ, 0.1f);
                showParticle(blockLocation, Particle.SMOKE, 50, spreadX, spreadY, spreadZ, 0.01f);
                showParticle(blockLocation, Particle.WHITE_SMOKE, 25, spreadX, spreadY, spreadZ, 0.01f);
                break;
            case 5: // Burning fire with 8 logs.
                showParticle(blockLocation, Particle.LAVA, 3, spreadX, spreadY, spreadZ, 0.1f);
                showParticle(blockLocation, Particle.SMOKE, 50, spreadX, spreadY, spreadZ, 0.01f);
                showParticle(blockLocation, Particle.WHITE_SMOKE, 25, spreadX, spreadY, spreadZ, 0.01f);
                break;
            case 6: // Fresh lit campfire with kindling.

                // Play  for campfire.
                Sound burnSound2 = Sound.BLOCK_FIRE_AMBIENT;
                float burnPitch2 = 1.0f;
                float burnVolume2 = 1.0f;
                var burnWorld2 = block.getWorld();
                burnWorld2.playSound(block.getLocation(), burnSound2, burnVolume2, burnPitch2);

                showParticle(blockLocation, Particle.SMOKE, 150, spreadX, spreadY, spreadZ, 0.01f);
                showParticle(blockLocation, Particle.WHITE_SMOKE, 75, spreadX, spreadY, spreadZ, 0.01f);
                break;
            case 7: // Fresh unlit campfire with kindling.

                // Play  for campfire.
                Sound burnSound = Sound.BLOCK_FIRE_AMBIENT;
                float burnPitch = 1.0f;
                float burnVolume = 1.0f;
                var burnWorld = block.getWorld();
                burnWorld.playSound(block.getLocation(), burnSound, burnVolume, burnPitch);

                showParticle(blockLocation, Particle.LAVA, 3, spreadX, spreadY, spreadZ, 0.1f);
                showParticle(blockLocation, Particle.SMOKE, 100, spreadX, spreadY, spreadZ, 0.01f);
                showParticle(blockLocation, Particle.WHITE_SMOKE, 50, spreadX, spreadY, spreadZ, 0.01f);
                break;
        }

    }


    private int consumeCampfire(Block block) {

        // Return error state.
        if (!isAnyCampfireType(block)) {
            return -111;
        }

        boolean isCampfire = isCampfire(block);

        int campfireId = 7;
        int newCampfireId = 7;

        if (isCampfire) {
            campfireId = getCampfireId(block);
            newCampfireId = campfireId - 1;

            setBlockToCampfire(block, newCampfireId);
            return newCampfireId;

        } else {
            campfireId = getCampfireWithGrillId(block);
            newCampfireId = campfireId - 1;

            setBlockToCampfireWithGrill(block, newCampfireId);
            return newCampfireId;
        }

    }

    private void showParticle(Location location, Particle particle, int amount,
                              float spreadX, float spreadY,
                              float spreadZ, float speed) {
        var world = location.getWorld();
        if (world == null) {
            return;
        }
        world.spawnParticle(particle, location, amount, spreadX, spreadY, spreadZ, speed);
    }


    private void showParticle(Location location, Particle particle, int amount) {
        var world = location.getWorld();
        if (world == null) {
            return;
        }
        final double offsetX = 0.5;
        final double offsetY = 0.5;
        final double offsetZ = 0.5;
        final double spreadX = 0.1;
        final double spreadY = 0.1;
        final double spreadZ = 0.1;
        final double speed = 0.01;

        world.spawnParticle(particle, location.add(offsetX,offsetY, offsetZ), amount, spreadX,spreadY,spreadZ, speed);
    }

    private void showBlockBreakParticle(Location location, BlockData blockData) {
        var world = location.getWorld();
        if (world == null) {
            return;
        }

        final double offsetX = 0.5;
        final double offsetY = 0.5;
        final double offsetZ = 0.5;
        final double spreadX = 0.1;
        final double spreadY = 0.1;
        final double spreadZ = 0.1;
        final double speed = 0.01;
        final int amount = 10;

        world.spawnParticle(Particle.BLOCK, location.add(offsetX, offsetY, offsetZ), amount,
                spreadX, spreadY, spreadZ, speed, blockData);
    }

    private void dropCharcoal(Location location, int amount) {
        var world = location.getWorld();
        if (world == null) {
            return;
        }
        ItemStack item = new ItemStack(Material.CHARCOAL, amount);
        world.dropItemNaturally(location, item);
    }

    private void dropFirewood(Location location, int amount) {
        var world = location.getWorld();
        if (world == null) {
            return;
        }
        ItemStack item = new ItemStack(Material.OAK_LOG, amount);
        world.dropItemNaturally(location, item);
    }

    private void dropKindling(Location blockLocation) {
        var world = blockLocation.getWorld();
        if (world == null) {
            return;
        }
        ItemStack item = new ItemStack(Material.STICK, 3);
        world.dropItemNaturally(blockLocation, item);
    }

    // *****************
    // Helper functions.
    // *****************

    private int getAnyTypeCampfireId(Block block) {
        if (!isAnyCampfireType(block)) {
            throw new IllegalArgumentException("Block is not a campfire. You should check for this with " +
                    "isAnyCampfireType(block) before calling getAnyTypeCampfireId().");
        }

        boolean isCampfire = isCampfire(block);
        if (isCampfire) {
            return getCampfireId(block);
        }

        return getCampfireWithGrillId(block);
    }

    private int getCampfireId(Block block) {

        boolean isCampfire = isCampfire(block);
        if (!isCampfire) {
            throw new IllegalArgumentException("Block is not a campfire. You should check for this with " +
                    "isCampfire(block) before calling getCampfireId.");
        }

        var blockAtLocationOfPlacedBlock = block.getLocation().getBlock();
        Material material = blockAtLocationOfPlacedBlock.getType();
        Campfire campfire = (Campfire) blockAtLocationOfPlacedBlock.getBlockData();
        BlockFace facing = campfire.getFacing();
        boolean isLit = campfire.isLit();

        // Find a match from the campfires.
        for (var campfireEntry : campfires.entrySet()) {
            var campfireData = campfireEntry.getValue();
            if (campfireData.getMaterial() == material
                && campfireData.getFacing() == facing
                && campfireData.isLit() == isLit) {
                return campfireEntry.getKey();
            }
        }
        return 7;
    }

    private int getCampfireWithGrillId(Block block) {

        boolean isCampfireWithGrill = isCampfireWithGrill(block);
        if (!isCampfireWithGrill) {
            throw new IllegalArgumentException("Block is not a campfire with grill. You should check for this with " +
                    "isCampfireWithGrill(block) before calling getCampfireWithGrillId.");
        }

        var blockAtLocationOfPlacedBlock = block.getLocation().getBlock();
        Material material = blockAtLocationOfPlacedBlock.getType();
        Campfire campfire = (Campfire) blockAtLocationOfPlacedBlock.getBlockData();
        BlockFace facing = campfire.getFacing();
        boolean isLit = campfire.isLit();

        // Find a match from the campfires.
        for (var campfireEntry : campfiresWithGrill.entrySet()) {
            var campfireData = campfireEntry.getValue();
            if (campfireData.getMaterial() == material
                    && campfireData.getFacing() == facing
                    && campfireData.isLit() == isLit) {
                return campfireEntry.getKey();
            }
        }
        return 7;
    }

    private boolean isCampfireWithGrill(Block block) {
        BlockData blockData = block.getBlockData();
        if (!(blockData instanceof Campfire campfire)) {
            return false;
        }

        Material material = blockData.getMaterial();
        BlockFace facing = campfire.getFacing();
        boolean isLit = campfire.isLit();

        // Find a match from the campfires.
        for (var campfireEntry : campfiresWithGrill.entrySet()) {
            var campfireData = campfireEntry.getValue();
            if (campfireData.getMaterial() == material
                    && campfireData.getFacing() == facing
                    && campfireData.isLit() == isLit) {
                return true;
            }
        }
        return false;
    }

    private boolean isAnyCampfireType(Block block) {
        var blockType = block.getType();

        // Check if the block is a campfire of any type.
        if (blockType == Material.CAMPFIRE
            || blockType == Material.SOUL_CAMPFIRE) {
            return true;
        }

        // It is NOT a campfire of any type.
        return false;

        // Check if the block is a regular campfire.
//        boolean isRegularCampfire = isCampfire(block);

        // Check if the block is a campfire with a grill.
//        boolean isGrillCampfire = isCampfireWithGrill(block);

        // Return true if either condition is true.
//        return isRegularCampfire || isGrillCampfire;
    }

    private boolean isCampfire(Block block) {
        BlockData blockData = block.getBlockData();
        if (!(blockData instanceof Campfire campfire)) {
            return false;
        }

        Material material = blockData.getMaterial();
        BlockFace facing = campfire.getFacing();
        boolean isLit = campfire.isLit();

        // Find a match from the campfires.
        for (var campfireEntry : campfires.entrySet()) {
            var campfireData = campfireEntry.getValue();
            if (campfireData.getMaterial() == material
                    && campfireData.getFacing() == facing
                    && campfireData.isLit() == isLit) {
                return true;
            }
        }
        return false;
    }

    private void setBlockToCampfire(Block block, int campfireId) {
        var campfireData = campfires.get(campfireId);
        if (campfireData == null) {
            return;
        }
        var material = campfireData.getMaterial();
        var facing = campfireData.getFacing();
        var isLit = campfireData.isLit();

        block.setType(material);
        var campfire = (org.bukkit.block.data.type.Campfire) block.getBlockData();
        campfire.setFacing(facing);
        campfire.setLit(isLit);
        block.setBlockData(campfire);
    }

    private void setBlockToCampfireWithGrill(Block block, int campfireId) {
        var campfireData = campfiresWithGrill.get(campfireId);
        if (campfireData == null) {
            return;
        }
        var material = campfireData.getMaterial();
        var facing = campfireData.getFacing();
        var isLit = campfireData.isLit();

        block.setType(material);
        var campfire = (Campfire) block.getBlockData();
        campfire.setFacing(facing);
        campfire.setLit(isLit);
        block.setBlockData(campfire);
    }

    private boolean isFreshUnlit(Block block) {

        // It's not even any kind of campfire. What are you doing?
        if (!isAnyCampfireType(block)) {
            return false;
        }

        int freshUnlitCampfireId = 7;
        return getAnyTypeCampfireId(block) == freshUnlitCampfireId;
    }

}
