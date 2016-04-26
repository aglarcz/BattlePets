package darius0021.events;

import darius0021.BattlePets;
import darius0021.Language;
import darius0021.MobStats;
import org.bukkit.Bukkit;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.EntityBlockFormEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerEggThrowEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.UUID;

public class EntityEvents implements Listener {
    BattlePets plugin;

    public EntityEvents(BattlePets plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDMGpet(EntityDamageByEntityEvent event) {
        if (!event.getDamager().hasMetadata("Owner")) return;
        if (event.getEntity() instanceof Player) {
            if (event.getDamager().getMetadata("Owner").get(0).asString().equals(((Player) event.getEntity()).getUniqueId())) {
                event.setCancelled(true);
                return;

            }
        }
        if (event.isCancelled() && event.getDamager().getType() != EntityType.WITHER_SKULL) return;
        if (!(event.getEntity() instanceof Damageable)) return;
            if (event.getEntity() instanceof ArmorStand)
                if (event.getEntity().hasMetadata("Owner")) {
                    event.setCancelled(true);
                    BattlePets.pets.get(UUID.fromString(event.getEntity().getMetadata("Owner").get(0).asString())).damage(event.getDamage(), event.getDamager());
                    return;
                }
        if (event.getDamager() instanceof Projectile) {
            if (((Projectile) event.getDamager()).getShooter() instanceof LivingEntity) {
                LivingEntity creature = (LivingEntity) ((Projectile) event.getDamager()).getShooter();
                if (creature.hasMetadata("Owner"))
                    if (!event.getEntity().hasMetadata("spawner")) {
                        ((Damageable) event.getEntity()).damage(creature.getMetadata("Damage").get(0).asDouble(), creature);
                        event.setCancelled(true);
                        return;
                    }
            }
        }
        if (!event.getDamager().hasMetadata("Owner")) return;
        LivingEntity pet = (LivingEntity) event.getDamager();
        BattlePets.AddXP(pet, event.getDamage());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDMGonPet(EntityDamageByEntityEvent event) {
        if (event.isCancelled()) return;
        if (!event.getEntity().hasMetadata("Owner")) return;
        LivingEntity pet = null;
            if (event.getEntity() instanceof ArmorStand) {
                pet = BattlePets.pets.get(UUID.fromString(event.getEntity().getMetadata("Owner").get(0).asString()));
                event.setCancelled(true);
                pet.damage(event.getDamage(), event.getDamager());
                return;
            }
        pet = (LivingEntity) event.getEntity();
        if (pet == null) return;
        String type = pet.getMetadata("Type").get(0).asString().toLowerCase();
        String typeconf = "";
        if (type.contains("baby"))
            typeconf += "baby-";
        typeconf += pet.getType().toString().toLowerCase();
        if (typeconf.equalsIgnoreCase("endermite"))
            typeconf = "block";
        MobStats stats;
        stats = BattlePets.statsai.get(typeconf);
        event.setDamage(Math.max(0, event.getDamage() - stats.Defense - pet.getMetadata("Defense").get(0).asInt() * stats._Defense));

        if (event.getDamager() instanceof LivingEntity) {
            if (event.getDamager() instanceof Player) {
                Player p = (Player) event.getDamager();
                if (!p.getUniqueId().equals(UUID.fromString(event.getEntity().getMetadata("Owner").get(0).asString())))
                    BattlePets.spawning.setTarget(pet, (LivingEntity) event.getDamager());
                return;
            }
            BattlePets.spawning.setTarget(pet, (LivingEntity) event.getDamager());
        }
    }

    @EventHandler
    public void onDeath(EntityDeathEvent event) {
        if (!event.getEntity().hasMetadata("Owner")) return;
        LivingEntity pet = event.getEntity();
        pet.setHealth(0);
        Player p = Bukkit.getPlayer(UUID.fromString(pet.getMetadata("Owner").get(0).asString()));
        p.closeInventory();
        p.sendMessage(Language.getMessage("pet_died"));
        BattlePets.return_pet(p);
        event.getDrops().clear();
        event.setDroppedExp(0);
    }

    @EventHandler
    public void onSpawn(CreatureSpawnEvent event) {
        //Bukkit.broadcastMessage(event.getEntity().getType()+"");
        if (event.getSpawnReason() == SpawnReason.SPAWNER)
            event.getEntity().setMetadata("spawner", new FixedMetadataValue(plugin, ""));
    }

    @EventHandler
    public void onPortal(EntityPortalEvent event) {
        if (event.getEntity().hasMetadata("Owner"))
            event.setCancelled(true);
    }

    @EventHandler
    public void onSnow(EntityBlockFormEvent event) {
        if (event.getEntity().hasMetadata("Owner"))
            event.setCancelled(true);
    }

    @EventHandler
    public void onShear(PlayerShearEntityEvent event) {
        if (event.getEntity().hasMetadata("Owner"))
            event.setCancelled(true);
    }

    @EventHandler
    public void onSunburn(EntityCombustEvent event) {
        if (event.getEntity().hasMetadata("Owner")) event.setCancelled(true);
    }

    @EventHandler
    public void onExplode(EntityExplodeEvent event) {
        if (event.getEntity() instanceof WitherSkull) {
            if (!(((Projectile) event.getEntity()).getShooter() instanceof Wither)) return;
            Wither pet = (Wither) ((Projectile) event.getEntity()).getShooter();
            if (pet.hasMetadata("Owner")) event.setCancelled(true);
        }
    }

    @EventHandler
    public void onRain(EntityDamageEvent event) {

        if (event.getCause() == DamageCause.DROWNING && event.getEntity().hasMetadata("Owner"))
            event.setCancelled(true);

    }

    @EventHandler(priority = EventPriority.LOW)
    public void onDMG(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Egg) {
            event.getDamager().setMetadata("Cancel", new FixedMetadataValue(BattlePets.plugin, " "));
        }
    }

    @EventHandler
    public void onEgg(PlayerEggThrowEvent event) {

        if (event.getEgg().hasMetadata("Cancel")) {
            event.setHatching(false);
            event.setNumHatches((byte) 0);
        }
    }
    @EventHandler
    public void onUnload(ChunkUnloadEvent event) {
    	for (Entity entity : event.getChunk().getEntities()) {
    		if (entity.hasMetadata("Owner")) {
    			BattlePets.return_pet(Bukkit.getPlayer(UUID.fromString(entity.getMetadata("Owner").get(0).asString())));
    		}
    	}
    }
}
