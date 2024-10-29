package starandserpent.minecraft.criticalfixes;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.*;
import com.comphenix.protocol.injector.GamePhase;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class PlayerPoseListener extends PacketAdapter {

    JavaPlugin plugin;
    ProtocolManager protocolManager;

    public PlayerPoseListener(JavaPlugin plugin, ProtocolManager protocolManager) {
        super(plugin, ListenerPriority.NORMAL, PacketType.Play.Client.BLOCK_DIG);
        this.plugin = plugin;
        this.protocolManager = protocolManager;
        System.out.println("PlayerPoseListener: Initialized.");
    }

    @Override public void onPacketSending(PacketEvent packetEvent) {}

    @Override public void onPacketReceiving(PacketEvent packetEvent) {
        var packetType = packetEvent.getPacketType();
        System.out.println("PlayerPoseListener: Received packet of type " + packetType);

        if (packetType == PacketType.Play.Client.BLOCK_DIG) {
            System.out.println("PlayerPoseListener: Accepted " + packetType + " packet.");
            Player player = packetEvent.getPlayer();
            boolean playerIsHooked = Hook.isPlayerHooked(player);
            if (!playerIsHooked) {
                System.out.println("PlayerPoseListener: Player is not hooked, exiting.");
                return;
            }
            System.out.println("PlayerPoseListener: Player is hooked.");
            PacketContainer packet = packetEvent.getPacket();
            System.out.println("Packet integer: " + packet.getIntegers().read(0));
            System.out.println("Player entity ID: " + player.getEntityId());
            System.out.println("Packet content: " + packet.toString());
            System.out.println("Packet type: " + packetEvent.getPacketType());

            var standPosePacket = createStandPosePacket(player, packet);

            try {
                // Send modified packet to player.
                System.out.println("PlayerPoseListener: Sending stand pose packet.");
                packetEvent.setCancelled(true);
                protocolManager.sendServerPacket(player, standPosePacket);
            } catch (Exception e) {
                e.printStackTrace();
                packetEvent.setCancelled(false);
            }

        }
    }

    private PacketContainer createStandPosePacket(Player player, PacketContainer packet) {
        System.out.println("PlayerPoseListener: Creating stand pose packet.");
        if (player == null) {
            System.out.println("PlayerPoseListener: Player is null, cannot create stand pose packet.");
            return null;
        } else {
            System.out.println("PlayerPoseListener: Player is definitely not null.");
        }
        var posePacket = packet.shallowClone();

        // Create a WrappedDataWatcher object
        System.out.println("PlayerPoseListener: Creating watcher. Here's where it usually crashes with NRE.");
        WrappedDataWatcher watcher = WrappedDataWatcher.getEntityWatcher(player);
        System.out.println("PlayerPoseListener: Watcher did not crash yet.");
        // Set the pose to standing
        watcher.setEntity(player); // Set the entity
        System.out.println("PlayerPoseListener: Set entity to watcher.");

        posePacket.getWatchableCollectionModifier().write(0, watcher.getWatchableObjects());
        // DEBUG: Packet data
        System.out.println("PlayerPoseListener: Packet data: " + posePacket.getWatchableCollectionModifier().read(0));
        return posePacket;
    }


    @Override public ListeningWhitelist getSendingWhitelist() {
        System.out.println("PlayerPoseListener: getSendingWhitelist.");
        return ListeningWhitelist.EMPTY_WHITELIST;
    }

    @Override public ListeningWhitelist getReceivingWhitelist() {
        System.out.println("PlayerPoseListener: getReceivingWhitelist.");
        return ListeningWhitelist.newBuilder()
                .types(PacketType.Play.Client.BLOCK_DIG)
                .gamePhase(GamePhase.PLAYING)
                .build();
    }

    @Override public Plugin getPlugin() {
        return this.plugin;
    }

}
