package starandserpent.minecraft.criticalfixes;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.Openable;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DangerousMobs implements Listener {

    private final JavaPlugin plugin;
    private Server server;

    // List of dangerous mobs.
    private final List<EntityType> dangerousMobs = List.of(
        EntityType.ZOMBIE,
        EntityType.SKELETON,
        EntityType.SPIDER,
        EntityType.WITCH,
        EntityType.PILLAGER,
        EntityType.RAVAGER,
        EntityType.VINDICATOR,
        EntityType.ILLUSIONER,
        EntityType.EVOKER,
        EntityType.HUSK,
        EntityType.STRAY,
        EntityType.WITHER_SKELETON,
        EntityType.HOGLIN,
        EntityType.PIGLIN,
        EntityType.ZOMBIFIED_PIGLIN,
        EntityType.PIGLIN_BRUTE,
        EntityType.ZOGLIN
    );

    // List of all carpets.
    private final List<Material> carpets = List.of(
        Material.WHITE_CARPET,
        Material.ORANGE_CARPET,
        Material.MAGENTA_CARPET,
        Material.LIGHT_BLUE_CARPET,
        Material.YELLOW_CARPET,
        Material.LIME_CARPET,
        Material.PINK_CARPET,
        Material.GRAY_CARPET,
        Material.LIGHT_GRAY_CARPET,
        Material.CYAN_CARPET,
        Material.PURPLE_CARPET,
        Material.BLUE_CARPET,
        Material.BROWN_CARPET,
        Material.GREEN_CARPET,
        Material.RED_CARPET,
        Material.BLACK_CARPET
    );

    // HaspMap of BukkitTask timers for keeping track of mobs targeting players.
    private HashMap<Monster, BukkitTask> mobsTargetingPlayers = new HashMap<>();

    public DangerousMobs(JavaPlugin plugin) {
        this.plugin = plugin;
        this.server = plugin.getServer();
    }

    // On mob targeting player event
    @EventHandler public void onMobTargetingPlayer(EntityTargetLivingEntityEvent event) {

        // Is a living entity
        var attackingEntity = event.getEntity();
        if (!(attackingEntity instanceof Monster attackingLivingEntity)) {
            return;
        }

        // If living entity is of type player, cast it
        LivingEntity targetEntity = event.getTarget();
        if (!(targetEntity instanceof Player player)) {
            return;
        }

        // Don't chase creative players please.
        if (player.getGameMode() == GameMode.CREATIVE) {
            return;
        }

        var entity = event.getEntity();
        var entityType = entity.getType();

        if (!dangerousMobs.contains(entityType)) {
            return;
        }

        // We're good to go!

        // If entity is already targeting player, return.
        if (mobsTargetingPlayers.containsKey(attackingLivingEntity)) {
            return;
        }

        // Create bukkit timer.
        // ********************
        // Create bukkit timer.
        BukkitTask task = server.getScheduler().runTaskTimer(plugin, () -> {

            // If the monster is no longer valid, stop the task and remove it from the HashMap
            if (!attackingLivingEntity.isValid()) {
                stopTask(attackingLivingEntity);
                return;
            }

            var isDead = attackingLivingEntity.isDead();
            if (isDead) {
                stopTask(attackingLivingEntity);
            }

            // Check if mob is still targeting player.
            Monster monster = attackingLivingEntity;
            var monsterTarget = monster.getTarget();
            if (monsterTarget == null || monsterTarget.isEmpty()) {

                // Pathfind to nearest player except if the player is in creative mode.
                monster.getNearbyEntities(5, 5, 5).stream()
                        .filter(someEntity -> someEntity instanceof Player && ((Player) someEntity).getGameMode() != GameMode.CREATIVE)
                        .findFirst()
                        .ifPresent(somePlayer -> {
                            monster.setTarget((LivingEntity) somePlayer);

                            // Apply speed effect.
                            monster.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 3, 1));
                        });
            }

            // It's fine. Do your thing.
            tryOpenNearbyDoors(attackingEntity);
            tryDestroyNearbyCarpets(attackingEntity);

        }, 20L,20L);

        // Add task to HashMap
        mobsTargetingPlayers.put(attackingLivingEntity, task);
    }

    private void tryDestroyNearbyCarpets(Entity attackingEntity) {
        // Get block at location
        var blockAttackingMobIsAt = attackingEntity.getLocation().getBlock();

        // Get list of carpets in radius
        var carpetList = getBlocksInRadius(carpets, blockAttackingMobIsAt, 1);
        if (carpetList.isEmpty()) {
            return;
        }

        // Break first carpet in list
        tryDestroyCarpet(carpetList.get(0));
    }

    private void tryDestroyCarpet(Block carpet) {

        // Show particle break effect
        var material = carpet.getType();
        carpet.getWorld().spawnParticle(Particle.BLOCK, carpet.getLocation(), 10, 0.5, 0.5, 0.5, 0.1, material.createBlockData());
        // Play carpet material break sound.
        carpet.getWorld().playSound(carpet.getLocation(), "minecraft:block.wool.break", 1.0f, 1.0f);
        // Break.
        carpet.breakNaturally();
    }

    private List<Block> getBlocksInRadius(List<Material> targetTypes, Block targetBlock, int radius) {
        List<Block> blocks = new ArrayList<>();
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {

                    var relativeBlock = targetBlock.getRelative(x, y, z);
                    // If block is of target type, add it to the list.
                    if (targetTypes.contains(relativeBlock.getType())){
                        blocks.add(relativeBlock);
                    }
                }
            }
        }
        return blocks;
    }

    private void stopTask(Monster attackingLivingEntity) {
        if (attackingLivingEntity == null) {
            return;
        }

        BukkitTask task = mobsTargetingPlayers.get(attackingLivingEntity);
        if (task != null) {
            task.cancel();
        }
        mobsTargetingPlayers.remove(attackingLivingEntity);
    }

    private void tryOpenNearbyDoors(Entity attackingEntity) {
        // Get block at location
        var blockAttackingMobIsAt = attackingEntity.getLocation().getBlock();

        // Get list of doors in radius
        var doorList = getDoorsInRadius(blockAttackingMobIsAt, 1);
        if (doorList.isEmpty()) {
            return;
        }

        // Iterate through doors
        for (Block door : doorList) {
            tryOpenDoor(door);
        }
    }

    private void tryOpenDoor(Block door) {

        var openable = (Openable) door.getBlockData();
        if (openable.isOpen()) {
            return;
        }

        openOpenable(door);
        playDoorOpenSound(door);
    }

    // Play door open sound at door location.
    private void playDoorOpenSound(Block door) {
        var world = door.getWorld();
        var location = door.getLocation();
        world.playSound(location, "minecraft:block.wooden_door.open", 1.0f, 1.0f);
    }

    private void openOpenable(Block openableBlock) {
        var openable = (Openable) openableBlock.getBlockData();
        openable.setOpen(true);
        openableBlock.setBlockData(openable);

        // If it's a double door, open the other door too.
        DoubleDoors.tryOpenDoubleDoors(openableBlock);
    }

    private List<Block> getDoorsInRadius(Block block, int radius) {
        List<Block> blocks = new ArrayList<>();
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {

                    var relativeBlock = block.getRelative(x, y, z);
                    // If block is door, add it to the list.
                    if (Tag.DOORS.isTagged(relativeBlock.getType())) {
                        blocks.add(relativeBlock);
                    }

                }
            }
        }
        return blocks;
    }

    // On mob spawn tune up the enemies, make them better and stronger and more resilient.
    @EventHandler public void onMobSpawnEvent(CreatureSpawnEvent event) {
        var entity = event.getEntity();
        var entityType = entity.getType();

        // Zombies
        if (entityType == EntityType.ZOMBIE) {
            var zombie = (Zombie) entity;

            // Can always break doors.
            zombie.setCanBreakDoors(true);

            // Can always pick up items.
            zombie.setCanPickupItems(true);
        }
    }

    // Throw the players a bone and kill the mobs around their spawn point.
    @EventHandler private void makeNastyRespawnsEasier(PlayerRespawnEvent event) {
        var player = event.getPlayer();

        // Apply invulnerability for a short time.
        player.setInvulnerable(true);
        server.getScheduler().runTaskLater(plugin, () -> player.setInvulnerable(false), 80);

        var world = player.getWorld();
        var spawnLocation = player.getLocation();
        var entities = world.getNearbyEntities(spawnLocation, 2, 2, 2);

        for (Entity entity : entities) {
            if (entity instanceof Monster monster) {
                // get direction vector between player and monster
                Location entityLocation = entity.getLocation();
                Vector entityVector = entityLocation.toVector();
                Vector pushVelocity = entityVector.subtract(spawnLocation.toVector());
                // Make it a unit vector
                pushVelocity.normalize();
                // Set it in reverse
                pushVelocity.multiply(-1);
                // Push it up a bit.
                pushVelocity.setY(0.2);
                // Multiply by force, adjust for more/less.
                float force = 1.0f;
                pushVelocity.multiply(force);

                // Push the monster away from the player, hard.
                entity.setVelocity(pushVelocity);

                // Damage the monster.
                monster.damage(2);
                monster.playHurtAnimation(2);
                var hurtSound = monster.getHurtSound();
                assert hurtSound != null;
                world.playSound(entityLocation, hurtSound, 1.0f, 1.0f);
            }
        }
    }

    // On player join event.
    @EventHandler public void onPlayerJoin(PlayerJoinEvent event) {
        var player = event.getPlayer();
        // Check if player is invulnerable and remove it. Just to make sure the state doesn't persist.
        if (player.isInvulnerable()) {
            player.setInvulnerable(false);
        }
    }

}
