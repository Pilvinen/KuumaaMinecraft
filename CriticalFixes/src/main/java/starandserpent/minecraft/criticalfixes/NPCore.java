package starandserpent.minecraft.criticalfixes;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPCRegistry;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.event.Listener;
import starandserpent.minecraft.criticalfixes.NPCCharacters.TeijoNPC;

public class NPCore implements Listener {
    private final NPCRegistry npcRegistry;

    private TeijoNPC teijoNPC;

    public NPCore() {
        this.npcRegistry = CitizensAPI.getNPCRegistry();
    }

    public void initialize() {
        System.out.println("NPCore initializing...");

        if (npcRegistry == null) {
            System.out.println("NPCore: NPCRegistry is null.");
            return;
        }

        System.out.println("NPCore: NPCRegistry is ready.");

        instantiateExistingNpcsFromRegistry();

    }

    private void instantiateExistingNpcsFromRegistry() {
        boolean teijoExists = false;

        // Check if Teijo NPC exists in the registry.
        for (net.citizensnpcs.api.npc.NPC npc : npcRegistry) {
            if (npc.getName().equals("Teijo")) {
                // Assign existing Teijo NPC to the class so we can control it.
                this.teijoNPC = new TeijoNPC(npc);
                System.out.println("NPCore: Teijo already exists.");

                // Check if the NPC is spawned, if not, spawn it at a default location
                if (!npc.isSpawned()) {
                    Location defaultLocation = TeijoNPC.getRandomTargetLocation();
                    npc.spawn(defaultLocation);
                    System.out.println("NPCore: Teijo spawned at default location: " + defaultLocation);
                } else {
                    System.out.println("NPCore: Teijo is hanging out at location: " + teijoNPC.getCurrentTargetLocation());
                }

                teijoExists = true;
                break;
            }
        }

        if (!teijoExists) {
            createTeijoNPC();
        }
    }

    // Create a new Teijo NPC
    private void createTeijoNPC() {
        Location spawnLocation = TeijoNPC.getRandomTargetLocation(); // Set a default location
        net.citizensnpcs.api.npc.NPC npc = npcRegistry.createNPC(EntityType.PLAYER, "Teijo", spawnLocation);
        npc.spawn(spawnLocation);
        this.teijoNPC = new TeijoNPC(npc);

        System.out.println("NPCore: Teijo was created to location: " + spawnLocation);
    }

}
