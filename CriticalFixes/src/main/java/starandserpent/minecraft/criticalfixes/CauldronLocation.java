package starandserpent.minecraft.criticalfixes;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.nivixx.ndatabase.api.annotation.NTable;
import com.nivixx.ndatabase.api.model.NEntity;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.UUID;

@NTable(name = "cauldron_location", schema = "", catalog = "")
public class CauldronLocation extends NEntity<UUID> {
    @JsonProperty("world") public String world;
    @JsonProperty("x") public float x;
    @JsonProperty("y") public float y;
    @JsonProperty("z") public float z;

    // Required for NDatabase. Do not remove.
    public CauldronLocation() {}

    // CTOR
    public CauldronLocation(Location location) {
        this.setKey(UUID.randomUUID());
        this.world = location.getWorld().getName();
        this.x = location.getBlockX();
        this.y = location.getBlockY();
        this.z = location.getBlockZ();
    }

    @JsonIgnore
    public Location toLocation() {
        return new Location(org.bukkit.Bukkit.getWorld(world), x, y, z);
    }

    @JsonIgnore
    public World getWorld() {
        return Bukkit.getWorld(world);
    }

}
