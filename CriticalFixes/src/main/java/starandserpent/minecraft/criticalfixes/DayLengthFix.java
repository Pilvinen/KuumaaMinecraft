package starandserpent.minecraft.criticalfixes;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.GameMode;
import org.bukkit.GameRule;
import org.bukkit.Server;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Objects;

public class DayLengthFix implements Listener {

    private final JavaPlugin plugin;
    private Server server;

    // Previous known tick count.
    private long previousTickCount = 0;

    // Time advancement rate as ticks. Time advances every X ticks defined here by 1 tick.
    private final int advanceTimeEveryTheseTicks = 4;

    // Time progress in ticks per timeAdvancementRate ticks.
    private final long timeProgressPerStepInTicks = 1;

    public DayLengthFix(JavaPlugin plugin) {
        this.plugin = plugin;

        // Get server.
        server = plugin.getServer();

        // Set day light cycle to false so we can use custom time progression.
        server.getWorlds().forEach(world -> world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false));

        // Register scheduler to run every timeAdvancementRate ticks to progress time by 1 tick for all worlds.
        server.getScheduler().runTaskTimer(plugin, task -> advanceTimeForAllWorlds(), 0, advanceTimeEveryTheseTicks);
    }

    public static void displayTimeWithActionBar(Player player) {

        // DEBUG: Track time progression.
        var server = player.getServer();
        var world = player.getWorld();
        var timeOfDay = getTimeOfDay(world.getName(), server);
        var timeOfDayInHours = ((timeOfDay / 1000) + 6) % 24; // Minecraft day is 24000 ticks, which is 24 hours. So we divide by 1000 to get hours. We add 6 to adjust for Minecraft's time.
        var timeOfDayInMinutes = (timeOfDay % 1000) / 16.66666666; // The remainder of the division by 1000 gives us the fraction of the current hour, which we convert to minutes.
        var timeOfDayInHoursString = String.format("%02d", timeOfDayInHours); // Format the hours as a 2-digit number.
        var timeOfDayInMinutesString = String.format("%02d", (int) Math.floor(timeOfDayInMinutes)); // Format the minutes as a 2-digit number.
        var timeOfDayString = timeOfDayInHoursString + ":" + timeOfDayInMinutesString;
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("Kello on " + timeOfDayString + "."));
    }

    // Loop all worlds and advance time.
    private void advanceTimeForAllWorlds() {
        server.getWorlds().forEach(world -> advanceTime(world.getName(), timeProgressPerStepInTicks));
    }

    // Get the current time of day for the world passed as parameter.
    public static long getTimeOfDay(String worldName, Server server) {
        return Objects.requireNonNull(server.getWorld(worldName)).getTime();
    }

    // Set the current time of day for the world passed as parameter.
    public void setTimeOfDay(String worldName, long time) {
        Objects.requireNonNull(server.getWorld(worldName)).setTime(time);
    }

    public static boolean isDay(String worldName, Server server) {
        long timeOfDay = getTimeOfDay(worldName, server);
        return timeOfDay >= 0 && timeOfDay < 16000;
    }

    // Advance time by the number of ticks passed as parameter.
    public void advanceTime(String worldName, long ticks) {
        long timeOfDay = getTimeOfDay(worldName, server);
        long nextTimeOfDay = timeOfDay + ticks;

        if (wasDayIsNightNow(timeOfDay, nextTimeOfDay)) {
            AmbientBiomeSounds.TurnedIntoNight(server);
        } else if (wasNightIsDayNow(timeOfDay, nextTimeOfDay)) {
            AmbientBiomeSounds.TurnedIntoDay(server);
        }

        setTimeOfDay(worldName, nextTimeOfDay);
    }

    private boolean wasDayIsNightNow(long timeOfDay, long nextTimeOfDay) {
        var wasDay = timeOfDay >= 0 && timeOfDay < 16000;
        var willBeNight = nextTimeOfDay >= 16000;
        return wasDay && willBeNight;
    }

    private boolean wasNightIsDayNow(long timeOfDay, long nextTimeOfDay) {
        var wasNight = timeOfDay >= 16000;
        var willBeDay = nextTimeOfDay > 0 && nextTimeOfDay < 16000;
        return wasNight && willBeDay;
    }

    // On sleep, advance time to morning.
    @EventHandler public void onPlayerBedEnter(PlayerBedEnterEvent event) {
        // Check if all are sleeping.
        new BukkitRunnable() {
            @Override
            public void run() {
                var onlinePlayers = new ArrayList<>(server.getOnlinePlayers());

                // Remove players that are in creative from onlinePlayers.
                onlinePlayers.removeIf(player -> player.getGameMode() == GameMode.CREATIVE);

                if (onlinePlayers.isEmpty()) {
                    setTimeToMorning();
                } else if (onlinePlayers.stream().allMatch(LivingEntity::isSleeping)) {
                    setTimeToMorning();
                }

            }
        }.runTaskLater(plugin, 20L);
    }

    private void setTimeToMorning() {
        server.getWorlds().forEach(world -> setTimeOfDay(world.getName(), 23000));
    }

}
