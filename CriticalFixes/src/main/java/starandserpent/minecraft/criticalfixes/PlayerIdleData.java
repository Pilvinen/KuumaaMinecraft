package starandserpent.minecraft.criticalfixes;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nivixx.ndatabase.api.annotation.NTable;
import com.nivixx.ndatabase.api.model.NEntity;

@NTable(name = "player_idle_data", schema = "", catalog = "")
public class PlayerIdleData extends NEntity<String>{

    @JsonProperty("idletime_minutes") long playtimeMinutes;

    // Required for NDatabase. Do not remove.
    public PlayerIdleData() {}

    public PlayerIdleData(String playerUuid) {
        this.setKey(playerUuid);
    }

}
