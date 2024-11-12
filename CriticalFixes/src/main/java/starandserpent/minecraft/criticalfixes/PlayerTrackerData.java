package starandserpent.minecraft.criticalfixes;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nivixx.ndatabase.api.annotation.NTable;
import com.nivixx.ndatabase.api.model.NEntity;

import java.util.Date;

@NTable(name = "player_tracker_data", schema = "", catalog = "")
public class PlayerTrackerData extends NEntity<String>{

    @JsonProperty("playtime_minutes") long playtimeMinutes;
    @JsonProperty("visits") int visits;
    @JsonProperty("last_seen") Date lastSeen;
    @JsonProperty("last_known_name") String lastKnownName;

    // Required for NDatabase. Do not remove.
    public PlayerTrackerData() {}

    public PlayerTrackerData(String playerUuid) {
        this.setKey(playerUuid);
    }

}
