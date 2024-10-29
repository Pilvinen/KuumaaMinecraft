package starandserpent.minecraft.criticalfixes;

// Represents a warp location the players can be teleported to. The warp location is stored in a yaml file.
// The yaml file is stored in the plugin data folder Warps/. The yaml file is named warps.yml
// The warp location is stored as a world name, x, y, z, yaw, pitch.

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

import static org.bukkit.Bukkit.getServer;

public class WarpLocation {

    // Warp name.
    public String warpName;

    // World name where the warp location resides
    public final String worldName;

    // X, Y, Z coordinates of the location
    public final double x, y, z;

    // Yaw and pitch rotation of the player at the location
    public final float yaw, pitch;

    public WarpLocation(String warpName, String worldName, double x, double y, double z, float yaw, float pitch) {
        this.warpName = warpName;
        this.worldName = worldName;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public void save(String warpName) {
        var fileName = new File("plugins/CriticalFixes/Warps/"+ warpName +".yml");
        YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(fileName);
        yamlConfiguration.set(".warpName", warpName);
        yamlConfiguration.set(".worldName", worldName);
        yamlConfiguration.set(".x", x);
        yamlConfiguration.set(".y", y);
        yamlConfiguration.set(".z", z);
        yamlConfiguration.set(".yaw", yaw);
        yamlConfiguration.set(".pitch", pitch);
        try {
            yamlConfiguration.save(fileName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static WarpLocation load(File fileName) {
        var yamlConfiguration = YamlConfiguration.loadConfiguration(fileName);
        var warpName = yamlConfiguration.getString("warpName");
        var worldName = yamlConfiguration.getString("worldName");
        assert worldName != null;
        var x = yamlConfiguration.getDouble("x");
        var y = yamlConfiguration.getDouble("y");
        var z = yamlConfiguration.getDouble("z");
        var yaw = (float) yamlConfiguration.getDouble("yaw");
        var pitch = (float) yamlConfiguration.getDouble("pitch");
        var warpLocation = new WarpLocation(warpName, worldName, x, y, z, yaw, pitch);
        return warpLocation;
    }

    public String getWarpName() {
        return warpName;
    }

    public Location getLocation() {
        World world = getServer().getWorld(worldName);
        return new Location(world, x, y, z, yaw, pitch);
    }

    public void delete(String warpName) {
        File file = new File("plugins/CriticalFixes/Warps/"+ warpName +".yml");
        file.delete();
    }

}
