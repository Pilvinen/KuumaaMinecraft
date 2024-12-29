package starandserpent.minecraft.criticalfixes;

import net.citizensnpcs.api.event.CitizensEnableEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class CitizensListener implements Listener {

    private NPCore npCore;

    @EventHandler public void onCitizensEnable(CitizensEnableEvent event) {
        System.out.println("CitizensListener: Citizens is enabled. Initializing NPCore...");
        npCore = new NPCore();
        npCore.initialize();
    }

    public NPCore getNpCore() {
        return npCore;
    }

}
