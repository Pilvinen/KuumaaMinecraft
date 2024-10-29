package starandserpent.minecraft.criticalfixes;

import com.nivixx.ndatabase.api.NDatabase;
import com.nivixx.ndatabase.api.query.NQuery;
import com.nivixx.ndatabase.api.repository.Repository;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;


public class CauldronImprovements implements Listener {

    private final JavaPlugin plugin;
    private Server server;

    Repository<UUID, CauldronLocation> repository;

    public CauldronImprovements(JavaPlugin plugin) {
        this.plugin = plugin;
        this.server = plugin.getServer();

        // Get the NDatabase repository.
        repository = NDatabase.api().getOrCreateRepository(CauldronLocation.class);


//        System.out.println("!!! Running CauldronImprovements test !!!");
        // Print debug information.
//        debugCauldrons(repository.streamAllValues());

        // Try to add a cauldron.
//        var testCauldronLocation = new Location(Bukkit.getWorld("Kuumaa"), 1, 2, 3);
//        repository.insert(new CauldronLocation(testCauldronLocation));

        // Print debug information again after adding the info.
//        debugCauldrons(repository.streamAllValues());

//        System.out.println("!!! CauldronImprovements is finished !!!");
    }

    // Print out all cauldrons for debug purposes.
    public void debugCauldrons(Stream<CauldronLocation> cauldrons) {

        // Convert the stream to a collection.
        var allCauldrons = cauldrons.toList();
        var cauldronCount = allCauldrons.size();

        System.out.println("There were " + cauldronCount + " cauldrons stored in the database.");

        allCauldrons.forEach((cauldronLocation) -> System.out.println("Cauldron Location: " + cauldronLocation));
    }

    // TODO: Make cauldron great again.

    // On placing a cauldron.
    @EventHandler public void onBlockPlace(BlockPlaceEvent event) {

        Block placedBlock = event.getBlock();
        Material placedBlockType = placedBlock.getType();
        if (placedBlockType == Material.CAULDRON) {

            // Get world of the clicked block.
            var blockLocation = placedBlock.getLocation();

            repository.insert(new CauldronLocation(blockLocation));
        }
    }

    @EventHandler public void onBlockBreak(BlockBreakEvent event) {
        var block = event.getBlock();
        var blockType = block.getType();
        if (blockType == Material.CAULDRON) {
            System.out.println("Cauldron broken at " + block.getLocation());
            System.out.println("Data before removal:");
            debugCauldrons(repository.streamAllValues());

            String world = block.getWorld().getName();
            Location location = block.getLocation();

            Optional<CauldronLocation> cauldronInRepo = repository.findOne(NQuery.predicate("$.world == '" + world + "' && $.x == " + location.getBlockX() + " && $.y == " + location.getBlockY() + " && $.z == " + location.getBlockZ()));
            cauldronInRepo.ifPresent(cauldronLocation -> repository.delete(cauldronLocation));
            System.out.println("Data after removal:");
            debugCauldrons(repository.streamAllValues());
        }
    }

}
