package starandserpent.minecraft.criticalfixes;

import org.bukkit.entity.Player;

public class PlayerLogoutEvent {
    private final Player player;
    private final long totalIdleTime;

    public PlayerLogoutEvent(Player player, long totalIdleTime) {
        this.player = player;
        this.totalIdleTime = totalIdleTime;
    }

    public Player getPlayer() {
        return player;
    }

    public long getTotalIdleTime() {
        return totalIdleTime;
    }
}


