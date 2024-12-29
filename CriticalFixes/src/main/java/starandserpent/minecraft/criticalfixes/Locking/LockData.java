package starandserpent.minecraft.criticalfixes.Locking;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.nivixx.ndatabase.api.annotation.NTable;
import com.nivixx.ndatabase.api.model.NEntity;
import org.bukkit.Location;

import java.util.UUID;

@NTable(name = "lock_data", schema = "", catalog = "")
public class LockData extends NEntity<String> {

    @JsonProperty("password")
    private String password;

    @JsonProperty("is_locked")
    private boolean isLocked;

    @JsonProperty("owner")
    private UUID owner;

    @JsonProperty("last_accessed")
    private long lastAccessed;

    @JsonProperty("last_accessed_by")
    private UUID lastAccessedBy;

    @JsonProperty("container_coordinates")
    private String containerCoordinates;

    @JsonProperty("is_double_chest")
    private boolean isDoubleChest;

    // Required for NDatabase. Do not remove.
    public LockData() {}

    public LockData(String key, String password, boolean isLocked, UUID owner, long lastAccessed, UUID lastAccessedBy, Location location, boolean isDoubleChest) {
        this.password = password;
        this.isLocked = isLocked;
        this.owner = owner;
        this.lastAccessed = lastAccessed;
        this.lastAccessedBy = lastAccessedBy;
        this.containerCoordinates = location.getWorld().getName() + "_" + location.getBlockX() + "_" + location.getBlockY() + "_" + location.getBlockZ();
        this.isDoubleChest = isDoubleChest;
        this.setKey(key);
    }

    // Getters and setters
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isLocked() {
        return isLocked;
    }

    public void setLocked(boolean locked) {
        isLocked = locked;
    }

    public UUID getOwner() {
        return owner;
    }

    public void setOwner(UUID owner) {
        this.owner = owner;
    }

    public long getLastAccessed() {
        return lastAccessed;
    }

    public void setLastAccessed(long lastAccessed) {
        this.lastAccessed = lastAccessed;
    }

    public UUID getLastAccessedBy() {
        return lastAccessedBy;
    }

    public void setLastAccessedBy(UUID lastAccessedBy) {
        this.lastAccessedBy = lastAccessedBy;
    }

    public String getContainerCoordinates() {
        return containerCoordinates;
    }

    public void setContainerCoordinates(String containerCoordinates) {
        this.containerCoordinates = containerCoordinates;
    }

    public boolean isDoubleChest() {
        return isDoubleChest;
    }

    public void setDoubleChest(boolean doubleChest) {
        isDoubleChest = doubleChest;
    }

    @JsonIgnore
    public Location getLocation() {
        String[] parts = containerCoordinates.split("_");
        return new Location(org.bukkit.Bukkit.getWorld(parts[0]), Integer.parseInt(parts[1]), Integer.parseInt(parts[2]), Integer.parseInt(parts[3]));
    }
}