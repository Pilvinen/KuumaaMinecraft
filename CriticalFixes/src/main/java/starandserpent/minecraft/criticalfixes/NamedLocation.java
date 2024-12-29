package starandserpent.minecraft.criticalfixes;

import org.bukkit.Location;

public class NamedLocation {
    private final String name;
    private final Location location;

    public NamedLocation(String name, Location location) {
        this.name = name;
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public Location getLocation() {
        return location;
    }

    @Override public String toString() {
        return name + " (" + location.getX() + ", " + location.getY() + ", " + location.getZ() + ")";
    }

}