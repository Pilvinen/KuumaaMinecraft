package starandserpent.minecraft.criticalfixes;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.nivixx.ndatabase.api.annotation.NTable;
import com.nivixx.ndatabase.api.model.NEntity;

// NDatabase class to store data about seasons.
@NTable(name = "seasons_data", schema = "", catalog = "")
public class SeasonsData extends NEntity<String> {

    // When the timer reaches max value, the season changes and /seasons nextseason is called.
    @JsonProperty("season_change_timer") public long seasonChangeTimer;

    // Required for NDatabase. Do not remove.
    public SeasonsData() {}

    public SeasonsData(String key) {
        this.setKey(key);
    }

    @JsonIgnore public long getSeasonTime() {
        return seasonChangeTimer;
    }

    @JsonIgnore public void setSeasonTime(long time) {
        seasonChangeTimer = time;
    }

    @JsonIgnore public void resetSeasonTime() {
        seasonChangeTimer = 0;
    }

}
