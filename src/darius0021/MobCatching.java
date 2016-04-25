package darius0021;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.entity.Skeleton.SkeletonType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.Random;


public class MobCatching implements Listener {
    Random rand;
    BattlePets plugin;
    Player player;
    Egg egg;
    LivingEntity creature;
    Entity entity;

    public MobCatching(BattlePets plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
        rand = new Random();
    }

    @EventHandler
    public void hit(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Egg)) return;
        if (!(event.getEntity() instanceof LivingEntity)) return;
        if (!(((Egg) event.getDamager()).getShooter() instanceof Player)) return;
        egg = (Egg) event.getDamager();
        player = (Player) egg.getShooter();
        if (!BattlePets.AllWorlds && !BattlePets.worlds.contains(player.getWorld().getName())) {
            player.sendMessage(Language.getMessage("disabled_world"));
            return;
        }
        creature = (LivingEntity) event.getEntity();
        if (creature.hasMetadata("Owner")) return;
        entity = event.getEntity();
        String typeconf = "";
        if (entity instanceof Ageable)
            if (!((Ageable) entity).isAdult())
                typeconf += "baby-";
        typeconf += entity.getType().toString().toLowerCase();
        //if (typeconf.equalsIgnoreCase("endermite"))
        //	typeconf="block";
        if (!BattlePets.statsai.containsKey(typeconf)) {
            player.sendMessage(Language.getMessage("pet_notcatchable"));
            return;
        }
        if (!player.hasPermission("battlepets.catch.*") && !player.hasPermission("battlepets.catch." + typeconf) && !player.hasPermission("battlepets.catch." + typeconf.replace("baby-", "baby_"))) {
            player.sendMessage(Language.getMessage("pet_noperm_catch"));
            return;
        }
        MobStats statsai = BattlePets.statsai.get(typeconf);
        if (player.getLevel() < statsai.reqlvl) {
            player.sendMessage(Language.getMessage("pet_lowlevel_catch"));
            return;
        }
        if (creature.getHealth() > statsai.HPLessThan) {
            player.sendMessage(Language.getMessage("pet_failed_catch"));
            return;
        }
        if (rand.nextInt(100) + 1 > statsai.Chances) {
            player.sendMessage(Language.getMessage("pet_escaped_catch"));
            return;
        }

        String type = "";
        if (entity instanceof Ageable) {
            type += ((Ageable) entity).isAdult() ? "" : "Baby-";
        }
        if (entity instanceof Horse) {
            type += ((Horse) entity).getColor().toString() + "-";
            type += ((Horse) entity).getStyle().toString() + "-";
            type += ((Horse) entity).getVariant().toString() + "-";
        }
        if (entity instanceof Rabbit)
                type += ((Rabbit) entity).getRabbitType().toString() + "-";
        
        if (entity instanceof Sheep) {
            type += ((Sheep) entity).getColor().toString() + "-";
        } else if (entity instanceof Skeleton) {
            type += ((Skeleton) entity).getSkeletonType() == SkeletonType.NORMAL ? "" : "WITHER-";
        } else if (entity instanceof Villager) {
            type += ((Villager) entity).getProfession().toString() + "-";
        } else if (entity instanceof Zombie) {
            type += ((Zombie) entity).isBaby() ? "Baby-" : "";
            type += ((Zombie) entity).isVillager() ? "Villager-" : "";
        } else if (entity instanceof Slime) {
            type += (((Slime) entity).getSize() + "-");
        }
        type += creature.getType();
        ItemStack item = new ItemStack(Material.MONSTER_EGG, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(Language.defaultas.replace("{type}", type));
        meta.setLore(Arrays.asList(Language.getMessage("type", true) + ": " + type, Language.getMessage("level", true) + ": 1", Language.getMessage("xp", true) + ": 0/" + statsai.XPForLevel, Language.getMessage("hp", true) + ": " + statsai.HP + "/" + statsai.HP, Language.getMessage("skillpoints", true) + ": " + statsai.SkillpointsForLevel, Language.getMessage("vitality", true) + ": 0", Language.getMessage("defense", true) + ": 0", Language.getMessage("strength", true) + ": 0", Language.getMessage("dexterity", true) + ": 0"));
        item.setItemMeta(meta);
        creature.getWorld().dropItemNaturally(creature.getEyeLocation(), item);
        creature.remove();
        player.sendMessage(Language.getMessage("pet_success_catch"));
    }
}
