package darius0021;

import com.palmergames.bukkit.towny.event.MobRemovalEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.plugin.Plugin;

public class Towny implements Listener {
    boolean allowpetsintowny;
    Plugin plugin;

    public Towny(Plugin plugin, boolean t) {
        allowpetsintowny = t;
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void removing(MobRemovalEvent event) {
        if (allowpetsintowny && event.getEntity().hasMetadata("Owner"))
            event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void spawnilng(EntitySpawnEvent event) {
        if (event.getEntity().hasMetadata("Owner")) {
            event.setCancelled(true);
        }

    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void spawning(EntitySpawnEvent event) {
        if (event.getEntity().hasMetadata("Owner")) {

            event.setCancelled(false);
        }

    }
}
