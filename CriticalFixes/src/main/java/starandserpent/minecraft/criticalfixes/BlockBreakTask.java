package starandserpent.minecraft.criticalfixes;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

public class BlockBreakTask {
    public Location BlockLocation;
    public org.bukkit.entity.Player Player;
    public BukkitTask Task;
    public float CurrentDamage;
    public int BreakTime;

    public BlockBreakTask(Location blockLocation, Player player, int breakTime) {
        BlockLocation = blockLocation;
        Player = player;
        CurrentDamage = 0.0f;
        BreakTime = breakTime;
    }
}
