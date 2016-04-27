package darius0021.events;

import darius0021.BattlePets;
import darius0021.Database;
import darius0021.Language;
import darius0021.versions.v1_9_1.util1_9;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.*;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.HorseInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.*;

public class PlayerEvents implements Listener {
    public static List<UUID> namechanging = new ArrayList<UUID>();
    public static Map<UUID, UUID> battles = new HashMap<UUID, UUID>();
    public List<UUID> interact = new ArrayList<UUID>();
    BattlePets plugin;

    public PlayerEvents(BattlePets plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void menu(PlayerInteractEntityEvent event) {

        if (interact.contains(event.getPlayer().getUniqueId())) {
            interact.remove(event.getPlayer().getUniqueId());
            return;
        }
        interact.add(event.getPlayer().getUniqueId());
        if (!(event.getRightClicked() instanceof LivingEntity)) return;
        if (event.getRightClicked() instanceof Player) {
            Player p1, p2;
            p1 = event.getPlayer();
            p2 = (Player) event.getRightClicked();
            if (battles.containsKey(p1.getUniqueId())) {
                if (battles.containsKey(p2.getUniqueId())) {

                    if (battles.get(p2.getUniqueId()) == null || !battles.get(p2.getUniqueId()).equals(p1.getUniqueId())) {

                        if (!BattlePets.pets.containsKey(p2.getUniqueId())) {
                            p1.sendMessage(Language.getMessage("battle_no_pet"));
                            return;
                        }
                        battles.put(p1.getUniqueId(), p2.getUniqueId());
                        p1.sendMessage(Language.getMessage("battle_sent"));
                        p2.sendMessage(Language.getMessage("battle_received").replace("{player}", p1.getDisplayName()));
                        return;
                    }
                } else {

                    if (!BattlePets.pets.containsKey(p2.getUniqueId())) {
                        p1.sendMessage(Language.getMessage("battle_no_pet"));
                        return;
                    }
                    battles.put(p1.getUniqueId(), p2.getUniqueId());
                    p1.sendMessage(Language.getMessage("battle_sent"));
                    p2.sendMessage(Language.getMessage("battle_received").replace("{player}", p1.getDisplayName()));
                    return;
                }
            }
            if (battles.containsKey(p2.getUniqueId()))
                if (battles.get(p2.getUniqueId()).equals(p1.getUniqueId())) {
                    if (!p1.hasPermission("battlepets.battle.accept")) {
                        p1.sendMessage(Language.getMessage("no_permission"));
                        return;
                    }
                    //FIGHT.
                    if (BattlePets.pets.containsKey(p1.getUniqueId()) && BattlePets.pets.containsKey(p2.getUniqueId())) {
                        LivingEntity pet1, pet2;
                        pet1 = BattlePets.pets.get(p1.getUniqueId());
                        pet2 = BattlePets.pets.get(p2.getUniqueId());
                        BattlePets.spawning.setTarget(pet1, pet2);
                        BattlePets.spawning.setTarget(pet2, pet1);
                        p1.sendMessage(Language.getMessage("battle_started"));
                        p2.sendMessage(Language.getMessage("battle_started"));
                        battles.remove(p2.getUniqueId());
                    }

                }
            return;
        }
        LivingEntity pet = (LivingEntity) event.getRightClicked();
        if (!pet.hasMetadata("Owner")) return;
        event.setCancelled(true);
        UUID owner = UUID.fromString(pet.getMetadata("Owner").get(0).asString());
        Player p = event.getPlayer();
        if (!p.getUniqueId().equals(owner)) {
            p.sendMessage(Language.getMessage("petmenu_failed"));
            return;
        }

        BattlePets.openmenu(p, pet);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDMGpl(EntityDamageByEntityEvent event) {
        if (event.isCancelled()) return;
        if (!(event.getEntity() instanceof LivingEntity) || (event.getEntity() instanceof ArmorStand))
            return;
        Player p = null;
        if (event.getDamager() instanceof Projectile) {
            Projectile proj = (Projectile) event.getDamager();
            if (proj.getShooter() instanceof Player) {
                p = (Player) proj.getShooter();
            }
        }
        if (event.getDamager() instanceof Player) p = (Player) event.getDamager();
        if (p != null) {
            if (!BattlePets.pets.containsKey(p.getUniqueId())) return;
            LivingEntity pet = BattlePets.pets.get(p.getUniqueId());
            if (event.getEntity() == pet) {
                event.setCancelled(true);
                return;

            }
            BattlePets.spawning.setTarget(pet, (LivingEntity) event.getEntity());
        }
        if (event.getEntity() instanceof Player) {
            p = (Player) event.getEntity();
            if (BattlePets.pets.containsKey(p.getUniqueId())) {
                LivingEntity pet = BattlePets.pets.get(p.getUniqueId());
                LivingEntity attacker = null;
                if (event.getDamager() instanceof Projectile) {
                    Projectile proj = (Projectile) event.getDamager();
                    if (proj.getShooter() instanceof LivingEntity) {
                        attacker = (LivingEntity) proj.getShooter();
                    }
                }
                if (event.getDamager() instanceof LivingEntity) attacker = (LivingEntity) event.getDamager();
                if (attacker != null) BattlePets.spawning.setTarget(pet, attacker);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void interact(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (event.getItem() == null) return;
        ItemStack item = event.getItem();
        if (item.getType() != Material.MONSTER_EGG) return;
        if (!event.getPlayer().getInventory().getItemInHand().equals(item)) return;
        if (!item.hasItemMeta()) return;
        if (!item.getItemMeta().hasLore()) return;
        if (item.getItemMeta().getLore().size() < 6) return;
        if (!item.getItemMeta().getLore().get(2).contains("/")) return;
        if (!item.getItemMeta().getLore().get(3).contains("/")) return;
        if (event.getClickedBlock().getType() == Material.MOB_SPAWNER) {
            event.setCancelled(true);
        }
        if (!BattlePets.AllWorlds && !BattlePets.worlds.contains(event.getPlayer().getWorld().getName())) {
            event.getPlayer().sendMessage(Language.getMessage("disabled_world"));
            return;
        }
        if (BattlePets.wg != null)
            if (!BattlePets.wg.isAllowed(event.getPlayer())) {
                event.getPlayer().sendMessage(Language.getMessage("disabled_zone"));
                return;
            }
        if (BattlePets.pets.containsKey(event.getPlayer().getUniqueId())) {
            event.getPlayer().sendMessage(Language.getMessage("second_pet"));
            return;
        }
        LivingEntity pet = BattlePets.spawning.SpawnCreature(event, plugin);
        if (pet != null)
            BattlePets.pets.put(event.getPlayer().getUniqueId(), pet);

    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        if (namechanging.contains(event.getPlayer().getUniqueId()))
            namechanging.remove(event.getPlayer().getUniqueId());
        if (battles.containsKey(event.getPlayer().getUniqueId()))
            battles.remove(event.getPlayer().getUniqueId());
        if (BattlePets.pets.containsKey(event.getPlayer().getUniqueId()) && Database.enabled) {
            LivingEntity pet = BattlePets.pets.get(event.getPlayer().getUniqueId());
            Database.SavePet(pet);
            BattlePets.spawning.returnPet(pet);
            pet.remove();
            BattlePets.pets.remove(event.getPlayer().getUniqueId());
        } else if (BattlePets.pets.containsKey(event.getPlayer().getUniqueId()))
            BattlePets.return_pet(event.getPlayer());
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if (Database.enabled)
            Database.LoadPet(event.getPlayer());
    }

    @EventHandler
    public void onTP(final PlayerTeleportEvent event) {
        if (event.getCause() == TeleportCause.UNKNOWN) return;
        if (BattlePets.pets.containsKey(event.getPlayer().getUniqueId())) {
            LivingEntity pet = BattlePets.pets.get(event.getPlayer().getUniqueId());
            if (Database.enabled) {
                Database.SavePet(pet);
                BattlePets.spawning.returnPet(pet);
                pet.remove();
                BattlePets.pets.remove(event.getPlayer().getUniqueId());
            } else
                BattlePets.return_pet(event.getPlayer());
        }
        if (Database.enabled) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                @Override
                public void run() {
                    Database.LoadPet(event.getPlayer());
                }
            }, 5L);
        }
    }

    @EventHandler
    public void onChange(final PlayerChangedWorldEvent event) {
        if (BattlePets.pets.containsKey(event.getPlayer().getUniqueId()) && Database.enabled) {
            LivingEntity pet = BattlePets.pets.get(event.getPlayer().getUniqueId());
            Database.SavePet(pet);
            BattlePets.spawning.returnPet(pet);
            pet.remove();
            BattlePets.pets.remove(event.getPlayer().getUniqueId());
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {

                @Override
                public void run() {
                    Database.LoadPet(event.getPlayer());
                }
            }, 5L);
        } else if (BattlePets.pets.containsKey(event.getPlayer().getUniqueId()))
            BattlePets.return_pet(event.getPlayer());
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        if (BattlePets.pets.containsKey(event.getEntity().getUniqueId()) && Database.enabled) {
            LivingEntity pet = BattlePets.pets.get(event.getEntity().getUniqueId());
            Database.SavePet(pet);
            BattlePets.spawning.returnPet(pet);
            pet.remove();
            BattlePets.pets.remove(event.getEntity().getUniqueId());
        } else if (BattlePets.pets.containsKey(event.getEntity().getUniqueId())) {
            ItemStack ite = BattlePets.return_pet(event.getEntity());
            event.getDrops().add(ite);
        }
    }

    @EventHandler
    public void onRespawn(final PlayerRespawnEvent event) {
        if (Database.enabled)
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {

                @Override
                public void run() {
                    Database.LoadPet(event.getPlayer());
                }
            }, 2L);
    }

    @EventHandler
    public void onNoPVP(EntityDamageByEntityEvent event) {
        if (!event.getEntity().hasMetadata("Owner")) return;
        Player p = null;
        if (event.getDamager() instanceof Player)
            p = (Player) event.getDamager();
        if (event.getDamager() instanceof Projectile) {
            if (((Projectile) event.getDamager()).getShooter() instanceof Player)
                p = (Player) ((Projectile) event.getDamager()).getShooter();
        }
        if (p == null) return;
        if (BattlePets.PVP) return;
        event.setCancelled(true);

    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        if (namechanging.contains(event.getPlayer().getUniqueId())) {
            if (BattlePets.pets.containsKey(event.getPlayer().getUniqueId())) {
                LivingEntity pet = BattlePets.pets.get(event.getPlayer().getUniqueId());
                if (event.getMessage().length() > BattlePets.namesize) {
                    event.getPlayer().sendMessage(Language.getMessage("pet_name_toolong"));
                    return;
                }
                pet.setMetadata("Name", new FixedMetadataValue(BattlePets.plugin, event.getMessage()));
                event.getPlayer().sendMessage(Language.getMessage("pet_renamed"));
                pet.setCustomName(ChatColor.translateAlternateColorCodes('&', Language.display.replace("{name}", pet.getMetadata("Name").get(0).asString()).replace("{level}", pet.getMetadata("Level").get(0).asString() + "")));
            } else {
                event.getPlayer().sendMessage(Language.getMessage("NoPetAlive"));
                return;
            }
            namechanging.remove(event.getPlayer().getUniqueId());
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        String cmd = event.getMessage().split(" ")[0];
        cmd = cmd.substring(1);
        if (BattlePets.aliases.contains(cmd)) {
            event.setMessage(event.getMessage().replaceFirst(cmd, "battlepets"));
        }
    }

    @EventHandler
    public void onServerCommand(ServerCommandEvent event) {

        String cmd = event.getCommand().split(" ")[0];
        if (BattlePets.aliases.contains(cmd)) {
            event.setCommand(event.getCommand().replaceFirst(cmd, "battlepets"));
        }
    }

    @EventHandler
    public void onAnvil(InventoryClickEvent event) {
        if (event.getInventory() instanceof AnvilInventory) {
            if (event.getSlotType() != SlotType.RESULT) return;
            if (BattlePets.version.equals("v1_9_R1")) {
                if (!util1_9.fromItemStack(event.getCurrentItem()))
                    event.setCancelled(true);
            } else if (event.getCurrentItem().getType() == Material.MONSTER_EGG && event.getCurrentItem().getDurability() == 0) {
                event.setCancelled(true);
            }
        }
        if (event.getClickedInventory() instanceof HorseInventory) {
            if (event.getWhoClicked().isInsideVehicle()) {
                if (event.getWhoClicked().getVehicle().hasMetadata("Owner"))
                    event.setCancelled(true);
            }
        }
    }
}
