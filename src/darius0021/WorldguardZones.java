package darius0021;


import com.sk89q.worldguard.bukkit.RegionContainer;
import com.sk89q.worldguard.bukkit.RegionQuery;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;

import java.util.List;

public class WorldguardZones {

    WorldGuardPlugin wg;
    Plugin plugin;
    RegionContainer container;
    RegionQuery query;
    boolean defaultspawning;
    List<String> exceptions;

    public WorldguardZones(Plugin wg, Plugin plugin) {
        this.wg = (WorldGuardPlugin) wg;
        this.plugin = plugin;
        container = this.wg.getRegionContainer();
        query = container.createQuery();
        defaultspawning = plugin.getConfig().getBoolean("WorldGuard.EnableSpawningByDefault");
        exceptions = plugin.getConfig().getStringList("WorldGuard.ExceptionRegions");
    }

    public boolean isAllowed(Entity p) {
        ApplicableRegionSet set = query.getApplicableRegions(p.getLocation());
        for (ProtectedRegion region : set.getRegions()) {
            for (String s : exceptions) {
                if (region.getId().equalsIgnoreCase(s))
                    return !defaultspawning;
            }

        }
        return defaultspawning;
    }
}
