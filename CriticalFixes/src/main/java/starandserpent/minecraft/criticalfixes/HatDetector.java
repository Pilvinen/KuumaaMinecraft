package starandserpent.minecraft.criticalfixes;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.*;
import com.comphenix.protocol.injector.GamePhase;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class HatDetector extends PacketAdapter {

    JavaPlugin plugin;
    ProtocolManager protocolManager;

    public HatDetector(JavaPlugin plugin, ProtocolManager protocolManager) {
        super(plugin, ListenerPriority.HIGHEST, PacketType.Play.Server.LOGIN);
        this.plugin = plugin;
        this.protocolManager = protocolManager;
    }

    @Override public void onPacketSending(PacketEvent packetEvent) {}

    @Override public void onPacketReceiving(PacketEvent packetEvent) {

        System.out.println("HatDetector: Received packet of type " + packetEvent.getPacketType());
        System.out.println("HatDetector: Here's the contents of the packet: " + packetEvent.getPacket().toString());

        if (packetEvent.getPacketType() == PacketType.Play.Server.PLAYER_INFO) {
            System.out.println("HatDetector: Received packet of type " + packetEvent.getPacketType());

            WrappedDataWatcher watcher = new WrappedDataWatcher(packetEvent.getPacket().getWatchableCollectionModifier().read(0));

            if (watcher.hasIndex(16)) {
                byte flags = watcher.getByte(16);
                boolean hasHatLayer = (flags & 0x02) != 0;
                System.out.println("Has hat layer: " + hasHatLayer);
            }
        }

    }


    @Override public ListeningWhitelist getSendingWhitelist() {
        return ListeningWhitelist.EMPTY_WHITELIST;
    }

    @Override public ListeningWhitelist getReceivingWhitelist() {
        return ListeningWhitelist.newBuilder()
                .types(PacketType.Play.Server.LOGIN)
                .gamePhase(GamePhase.PLAYING)
                .build();
    }

    @Override public Plugin getPlugin() {
        return this.plugin;
    }


}
