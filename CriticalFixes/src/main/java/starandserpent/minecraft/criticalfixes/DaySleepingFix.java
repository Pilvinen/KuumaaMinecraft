package starandserpent.minecraft.criticalfixes;

import dev.geco.gsit.api.GSitAPI;
import dev.geco.gsit.objects.IGPoseSeat;
import org.bukkit.Server;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.Bed;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Pose;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collection;

public class DaySleepingFix implements Listener {

    private final JavaPlugin plugin;
    private Server server;

    public DaySleepingFix(JavaPlugin plugin) {
        this.plugin = plugin;
        this.server = plugin.getServer();
    }

    /*
        GSit API: https://github.com/Gecolay/GSit/blob/main/core/src/main/java/dev/geco/gsit/api/GSitAPI.java
        A list of all GSit-Events:
        PreEntitySitEvent -> Gets called before an entity starts sitting (cancelable)
        EntitySitEvent -> Gets called when an entity starts sitting
        PreEntityGetUpSitEvent -> Gets called before an entity gets up from sitting (cancelable)
        EntityGetUpSitEvent -> Gets called when an entity gets up from sitting
        PrePlayerPoseEvent -> Gets called before a player starts posing (cancelable)
        PlayerPoseEvent -> Gets called when a player starts posing
        PrePlayerGetUpPoseEvent -> Gets called before a player gets up from a Pose (cancelable)
        PlayerGetUpPoseEvent -> Gets called when a player gets up from a Pose
        PrePlayerPlayerSitEvent -> Gets called before a player starts sitting on another Player (cancelable)
        PlayerPlayerSitEvent -> Gets called when a player starts sitting on another Player
        PrePlayerGetUpPlayerSitEvent -> Gets called before a player gets up from another Player (cancelable)
        PlayerGetUpPlayerSitEvent -> Gets called when a player leaves another Player
        PrePlayerCrawlEvent -> Gets called before a player starts crawling (cancelable)
        PlayerCrawlEvent -> Gets called when a player starts crawling
        PrePlayerGetUpCrawlEvent -> Gets called before a player stops crawling (cancelable)
        PlayerGetUpCrawlEvent -> Gets called when a player stops crawling
        GSitReloadEvent -> Gets called when the /gsitreload command is used
    */

    // When player tries to sleep.
    @EventHandler public void onPlayerSleep(PlayerBedEnterEvent event) {
        var player = event.getPlayer();

        // Don't want to get stack overflow errors.
        if (player.isSleeping()) {
            return;
        }

        // Let's get busy. Force sleep! (Without actually sleeping)
        event.setCancelled(true);

        // Get the bed foot block.
        var bedFootBlock = getBedFootBlock(event.getBed());

        Block bedBlock = event.getBed();
        Bed bedData = (Bed) bedBlock.getBlockData();
        var pose = Pose.SLEEPING;
        var yaw = getYawFromBedFoot(player, bedData);

        // Check the bedFootBlock if if contains LivingEntities.
        var world = bedFootBlock.getWorld();
        var entitiesNearBedFoot = world.getNearbyEntities(bedFootBlock.getLocation(), 0.5, 0.5, 0.5);
        if (!entitiesNearBedFoot.isEmpty()) {
            boolean isOccupied = bedPartIsOccupied(player, bedBlock, entitiesNearBedFoot);
            if (isOccupied) {
                // Send message to player using title.
                player.sendTitle("", "Sänky on jo varattu.", 10, 40, 10);
                return;
            }
        }

        // Do the same for the bedBlock.
//        var entitiesNearBedHead = world.getNearbyEntities(bedBlock.getLocation(), 0.5, 0.5, 0.5);
//        if (!entitiesNearBedHead.isEmpty()) {
//
//            boolean isOccupied = bedPartIsOccupied(player, bedBlock, entitiesNearBedHead);
//            if (isOccupied) {
//                player.sendTitle("", "Sänky on jo varattu.", 10, 40, 10);
//                return;
//            }
//        }

        GSitAPI.createPose(bedBlock, player, pose, yaw, true);
    }

    private boolean bedPartIsOccupied(Player you, Block bedBlock, Collection<Entity> entitiesNearBedHead) {
        // Iterate the collection and check if the entity is at the location we're interested in.
        // And if it's you, skip that one.
        for (var entity : entitiesNearBedHead) {

            // Ignore the player if it's you.
            if (entity.getLocation().getBlock().equals(bedBlock) && entity.equals(you)) {
                continue;
            }

            // Check if the entity is sleeping.
            if (entity instanceof LivingEntity livingEntity) {
                if (livingEntity.isSleeping()) {
                    // If it is, we can't sit there.
                    return true;
                }
            }

            // Is it a player then?
            if (entity instanceof Player playerEntity) {
                // Player can "pose" (sleep) using GSit. Check from GSit API if the player is posing.
                if (GSitAPI.isPosing(playerEntity)) {
                    // Check what the pose is.
                    IGPoseSeat poseSeat = GSitAPI.getPose(playerEntity);
                    Pose pose = poseSeat.getPose();

                    // If player is sleeping here, we can't sleep here.
                    if (pose == Pose.SLEEPING) {
                        return true;
                    }
                }
            }
        }

        // Is not occupied, as far as we can tell.
        return false;
    }

    private Block getBedFootBlock(Block bedUnknownPart) {

        // If what we have is head of the bed we can get the facing and get the relative block to get foot of bed.
        var bed = (Bed) bedUnknownPart.getBlockData();
        var partType = bed.getPart();

        if (partType == Bed.Part.HEAD) {
            var facing = bed.getFacing();
            var relativeBlock = bedUnknownPart.getRelative(facing.getOppositeFace());
            return relativeBlock;
        }

        // If we have the foot of the bed that was clicked, we can just return it.
        return bedUnknownPart;
    }

    private float getYawFromBedFoot(Player player, Bed bed) {

        // Get the direction the bed is facing
        // and return the yaw the player should face.

        Bed.Part part = bed.getPart();
        var facing = bed.getFacing();

        float yawDegrees = 0.0f;

        if (part == Bed.Part.HEAD) {
            if (facing == BlockFace.NORTH) {
                yawDegrees = 0.0f;
            } else if (facing == BlockFace.EAST) {
                yawDegrees = 90.0f;
            } else if (facing == BlockFace.SOUTH) {
                yawDegrees = 180.0f;
            } else if (facing == BlockFace.WEST) {
                yawDegrees = -90.0f;
            }
        }

        if (part == Bed.Part.FOOT) {
            if (facing == BlockFace.NORTH) {
                yawDegrees = 180.0f;
            } else if (facing == BlockFace.EAST) {
                yawDegrees = 270.0f;
            } else if (facing == BlockFace.SOUTH) {
                yawDegrees = 0.0f;
            } else if (facing == BlockFace.WEST) {
                yawDegrees = 90.0f;
            }
        }

        return yawDegrees;
    }

}
