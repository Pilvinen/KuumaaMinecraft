package starandserpent.minecraft.criticalfixes;

import org.bukkit.Server;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;

public class PlayerAmbience {
    public SoundData soundData;
    public Biome lastBiome;
    // Timestamp when the last sound started playing.
    public final long lastSoundTime;

    public PlayerAmbience(SoundData soundData, Biome lastBiome) {
        this.soundData = soundData;
        this.lastBiome = lastBiome;
        lastSoundTime = System.currentTimeMillis();
    }

    public Biome getLastBiome() {
        return lastBiome;
    }

    public boolean isPlaying(Player player, Server server) {
        // Get world name.
        String worldName = player.getWorld().getName();
        double soundDuration = DayLengthFix.isDay(worldName, server) ? soundData.getDurationDayInMilliseconds() : soundData.getDurationNightInMilliseconds();

        return System.currentTimeMillis() < lastSoundTime + soundDuration;
    }

    // For debugging.
    public long getLastSoundTime() {
        return lastSoundTime;
    }

}
