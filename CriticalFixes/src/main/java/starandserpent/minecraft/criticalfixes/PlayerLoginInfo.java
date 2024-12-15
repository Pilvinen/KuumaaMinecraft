package starandserpent.minecraft.criticalfixes;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nivixx.ndatabase.api.annotation.Indexed;
import com.nivixx.ndatabase.api.annotation.NTable;
import com.nivixx.ndatabase.api.model.NEntity;

import java.util.UUID;

@NTable(name = "player_login_info")
public class PlayerLoginInfo extends NEntity<UUID> {

    @JsonProperty("playerId")
    @Indexed
    private UUID playerId;

    @JsonProperty("playerName")
    private String playerName;

    @JsonProperty("timestamp")
    private long timestamp;

    // Required for NDatabase. Do not remove.
    public PlayerLoginInfo() {}

    public PlayerLoginInfo(UUID playerId, String playerName, long timestamp) {
        this.setKey(UUID.randomUUID());
        this.playerId = playerId;
        this.playerName = playerName;
        this.timestamp = timestamp;
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public String getPlayerName() {
        return playerName;
    }

    public long getTimestamp() {
        return timestamp;
    }
}