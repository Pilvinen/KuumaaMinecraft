package starandserpent.minecraft.criticalfixes;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nivixx.ndatabase.api.annotation.Indexed;
import com.nivixx.ndatabase.api.annotation.NTable;
import com.nivixx.ndatabase.api.model.NEntity;

import java.util.UUID;

@NTable(name = "player_login_info")
public class PlayerLoginInfo extends NEntity<String> {

    @JsonProperty("playerId")
    @Indexed
    private String playerId;

    @JsonProperty("playerName")
    private String playerName;

    @JsonProperty("timestamp")
    private long timestamp;

    // Required for NDatabase. Do not remove.
    public PlayerLoginInfo() {}

    public PlayerLoginInfo(String playerId, String playerName, long timestamp) {
        this.setKey(playerId);
        this.playerId = playerId;
        this.playerName = playerName;
        this.timestamp = timestamp;
    }

    public String getPlayerId() {
        return playerId;
    }

    public String getPlayerName() {
        return playerName;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long currentTime) {
        this.timestamp = currentTime;
    }

}