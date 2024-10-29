package starandserpent.minecraft.criticalfixes;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListeningWhitelist;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.events.PacketListener;
import com.comphenix.protocol.injector.GamePhase;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class TabKeyListener implements PacketListener {

    JavaPlugin plugin;
    ProtocolManager protocolManager;

    // Constructor.
    public TabKeyListener(JavaPlugin plugin, ProtocolManager protocolManager) {
        this.plugin = plugin;
        this.protocolManager = protocolManager;
    }

    @Override public void onPacketSending(PacketEvent packetEvent) {

    }

    @Override public void onPacketReceiving(PacketEvent packetEvent) {
        // Intercept TAB_COMPLETE packets.
        var packetType = packetEvent.getPacketType();
        if (packetType == PacketType.Play.Client.TAB_COMPLETE) {
            // Cancel the packet.
            packetEvent.setCancelled(true);
        }
    }


    @Override public ListeningWhitelist getSendingWhitelist() {
        return ListeningWhitelist.EMPTY_WHITELIST;
    }

    @Override public ListeningWhitelist getReceivingWhitelist() {
        return ListeningWhitelist.newBuilder()
                .types(PacketType.Play.Client.TAB_COMPLETE)
                .gamePhase(GamePhase.PLAYING)
                .build();
    }

    @Override public Plugin getPlugin() {
        return this.plugin;
    }

}
