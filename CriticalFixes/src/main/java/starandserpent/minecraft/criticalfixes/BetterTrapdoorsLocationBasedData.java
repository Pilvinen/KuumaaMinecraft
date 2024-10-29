package starandserpent.minecraft.criticalfixes;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.nivixx.ndatabase.api.annotation.NTable;
import com.nivixx.ndatabase.api.model.NEntity;
import org.bukkit.Location;

import java.util.Objects;

@NTable(name = "better_trapdoors_location_based_data", schema = "", catalog = "")
public class BetterTrapdoorsLocationBasedData extends NEntity<String> {

    // The data can now be fetched with: repository.findOne(NQuery.predicate("$.locationKey == locationString"));
    @JsonProperty("location_world") public String locationWorld;
    @JsonProperty("location_x") public int locationX;
    @JsonProperty("location_y") public int locationY;
    @JsonProperty("location_z") public int locationZ;
    @JsonProperty("closed_facing") public String closedFacing;
    @JsonProperty("open_facing") public String openFacing;

    // Required for NDatabase. Do not remove.
    public BetterTrapdoorsLocationBasedData() {}

    // It's important to use the setKey method to set the key! Otherwise the data will be lost.
    // The counterpart for this is doing myData = repository.get(serverId);
    // Everything will fail without this.
    @JsonIgnore public BetterTrapdoorsLocationBasedData(String key, Location location, Facing closedFacing, Facing openFacing) {

        // Convert Location to String which can be stored in the database.
        var worldName = Objects.requireNonNull(location.getWorld()).getName();
        var x = location.getBlockX();
        var y = location.getBlockY();
        var z = location.getBlockZ();

        // Store the location coordinates separately for easier querying.
        this.locationWorld = worldName;
        this.locationX = x;
        this.locationY = y;
        this.locationZ = z;

        // Store the facing directions as strings.
        this.closedFacing = closedFacing.toString().toLowerCase();
        this.openFacing = openFacing.toString().toLowerCase();

        // Set the UUID key for the database entry.
        this.setKey(key);
    }

    // Get location.
    @JsonIgnore public Location getLocation() {
        return new Location(org.bukkit.Bukkit.getWorld(locationWorld), locationX, locationY, locationZ);
    }

}
