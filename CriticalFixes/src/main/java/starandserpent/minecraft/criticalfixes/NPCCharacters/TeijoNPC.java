package starandserpent.minecraft.criticalfixes.NPCCharacters;

import net.citizensnpcs.api.npc.NPC;
import net.minso.chathead.API.ChatHeadAPI;
import net.minso.chathead.API.SkinSource;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import starandserpent.minecraft.criticalfixes.NamedLocation;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class TeijoNPC {

    private final NPC teijoNPC;
    private static Random random = new Random();

    private NamedLocation currentTargetLocation;

    private static final List<NamedLocation> hangoutLocations = Arrays.asList(
            new NamedLocation("Pilvisen kotiin", new Location(Bukkit.getWorld("Kuumaa"), -12, 95, 81)),
            new NamedLocation("Temppelille", new Location(Bukkit.getWorld("Kuumaa"), 10, 95, 20)),
            new NamedLocation("Leirinuotiolle", new Location(Bukkit.getWorld("Kuumaa"), -16, 95, 99)),
            new NamedLocation("Pilvisen talon eteen", new Location(Bukkit.getWorld("Kuumaa"), 4, 94, 80)),
            new NamedLocation("Temppelin nuotiolle", new Location(Bukkit.getWorld("Kuumaa"), 9, 94, 37)),
            new NamedLocation("Kasvihuoneelle", new Location(Bukkit.getWorld("Kuumaa"), -23, 97, 55))
    );

    private final List<String> comments = Arrays.asList(
            "Heippa, miten menee?",
            "Kiva päivä kävelylle, vai mitä?",
            "Täällä on aina niin rauhallista.",
            "Mitäs touhuut?",
            "Upea näkymä. Vai mitä?",
            "Mitäs %s?"
    );

    public TeijoNPC(NPC npc) {
        // Assign Teijo class its npc.
        this.teijoNPC = npc;

        // Start walking and talking
        startWalkingAndTalking();
    }

    // Make Teijo walk between locations
    public void startWalkingAndTalking() {
        // Move between locations every 10 seconds
        new BukkitRunnable() {

            int currentLocationIndex = 0;

            @Override
            public void run() {
                if (teijoNPC.isSpawned()) {

                    // Move Teijo to the next hangout location
                    setRandomTargetLocation();

                    // Print next location name.
                    System.out.println("Teijo is moving to: " + currentTargetLocation.getName());

                    // Check if Teijo is near any player and make him say something
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        if (player.getLocation().distance(teijoNPC.getEntity().getLocation()) < 10) { // 10 block radius
                            sayRandomComment(player);
                        }
                    }
                } else {
                    System.out.println("Teijo is not spawned!");
                }
            }
        }.runTaskTimer(Bukkit.getPluginManager().getPlugin("CriticalFixes"), 0, 400);
    }

    // Make Teijo say a random comment to the player
    private void sayRandomComment(Player player) {
        Random random = new Random();
        String randomComment = comments.get(random.nextInt(comments.size()));
        var dateTime = System.currentTimeMillis();
        String timeHHMM = String.format("%tR", dateTime);
        var chatHeadAPI = ChatHeadAPI.getInstance();
        var steveFace = chatHeadAPI.getHeadAsString(UUID.randomUUID(), false, SkinSource.LOCALFILE);
        String formattedComment = String.format(ChatColor.DARK_GRAY + "[" + ChatColor.GRAY + timeHHMM + ChatColor.DARK_GRAY + "] " + steveFace + " " + ChatColor.WHITE + "Teijo" + ChatColor.GRAY + ": " + randomComment, player.getName());
        player.sendMessage(String.format(formattedComment, player.getName())); // This sends message to the NPC (Teijo) entity, but you can make it send a message to the player
    }

    public void setRandomTargetLocation() {
        currentTargetLocation = hangoutLocations.get(random.nextInt(hangoutLocations.size()));
        // Set navigation target to the new location.
        teijoNPC.getNavigator().setTarget(currentTargetLocation.getLocation());
    }

    public NamedLocation getCurrentTargetLocation() {
        return currentTargetLocation;
    }

    public static Location getRandomTargetLocation() {
        return hangoutLocations.get(random.nextInt(hangoutLocations.size())).getLocation();
    }

}
