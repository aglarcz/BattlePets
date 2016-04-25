package darius0021.events;

import darius0021.BattlePets;
import darius0021.Language;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;

import java.util.UUID;

public class ArmorStandEvent implements Listener {
    BattlePets plugin;

    public ArmorStandEvent(BattlePets plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onClick(PlayerArmorStandManipulateEvent event) {
        LivingEntity pett = event.getRightClicked();
        if (!pett.hasMetadata("Owner")) return;
        event.setCancelled(true);
        UUID owner = UUID.fromString(pett.getMetadata("Owner").get(0).asString());
        Player p = event.getPlayer();
        if (!p.getUniqueId().equals(owner)) {
            p.sendMessage(Language.getMessage("petmenu_failed"));
            return;
        }
        LivingEntity pet = BattlePets.pets.get(owner);
        BattlePets.openmenu(p, pet);
    }
}
