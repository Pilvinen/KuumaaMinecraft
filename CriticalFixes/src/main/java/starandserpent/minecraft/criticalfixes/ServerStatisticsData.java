package starandserpent.minecraft.criticalfixes;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.nivixx.ndatabase.api.annotation.NTable;
import com.nivixx.ndatabase.api.model.NEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@NTable(name = "server_statistics_data", schema = "", catalog = "")
public class ServerStatisticsData extends NEntity<UUID> {

    // Required for NDatabase. Do not remove.
    public ServerStatisticsData() {}

    // It's important to use the setKey method to set the key! Otherwise the data will be lost.
    // The counterpart for this is doing serverStatisticsData = repository.get(serverId);
    // Everything will fail without this.
    public ServerStatisticsData(UUID key) {
        this.setKey(key);
    }

    @JsonProperty("restart_count_tracking_start") public String restartCountTrackingStart = "28.05.2024";
    @JsonProperty("restart_count") public int restartCount;

    @JsonIgnore public int getRestartCount() {
        return restartCount;
    }

    @JsonIgnore public void incrementRestartCount() {
        restartCount++;
    }

    @JsonIgnore public String getRestartCountTrackingStart() {
        return restartCountTrackingStart;
    }

}
