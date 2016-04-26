package darius0021;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.Arrays;
import java.util.List;

public class Commands {

    public static boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (!cmd.getName().equalsIgnoreCase("battlepets")) return true;

        if (args.length == 0) {
            sender.sendMessage(Language.getMessage("commands"));
            if (sender.hasPermission("battlepets.name"))
                sender.sendMessage(Language.getMessage("cmd_name"));
            if (sender.hasPermission("battlepets.egg")) {
                sender.sendMessage(Language.getMessage("cmd_egg"));
            }
            if (sender.hasPermission("battlepets.shop")) {
                sender.sendMessage(Language.getMessage("cmd_shop"));
            }
            if (sender.hasPermission("battlepets.menu")) {
                sender.sendMessage(Language.getMessage("cmd_menu"));
            }
            if (sender.hasPermission("battlepets.cancel")) {
                sender.sendMessage(Language.getMessage("cmd_cancel"));
            }
            sender.sendMessage(Language.getMessage("cmd_set"));
            return true;
        }
        if (args[0].equalsIgnoreCase("reload")) {
            if (sender.hasPermission("battlepets.reload")) {
                BattlePets.plugin.reloadConfig();
                ((BattlePets) BattlePets.plugin).reload();
                sender.sendMessage(Language.getMessage("reloaded"));
            } else {
                sender.sendMessage(Language.getMessage("no_permission"));
            }
            return true;
        }
        if (args[0].equalsIgnoreCase("removenear")) {
            if (sender.hasPermission("battlepets.removenear")) {
                Player p = (Player) sender;
                int atst = 0;
                try {
                    atst = Integer.parseInt(args[1]);
                } catch (Exception e) {
                    atst = 5;
                }

                List<Entity> ent = p.getNearbyEntities(atst, atst, atst);
                p.sendMessage(Language.getMessage("removal").replace("{number}", "" + ent.size()));
                for (Entity entity : ent) {
                    entity.remove();
                }
            } else {
                sender.sendMessage(Language.getMessage("no_permission"));
            }
            return true;
        }
        if (args[0].equalsIgnoreCase("set") && sender instanceof Player) {
            if (args.length != 3) {
                sender.sendMessage(Language.getMessage("cmd_set_help1"));
                sender.sendMessage(Language.getMessage("cmd_set_help2"));
                sender.sendMessage(Language.getMessage("cmd_set_help3"));
                sender.sendMessage(Language.getMessage("cmd_set_help4"));
                sender.sendMessage(Language.getMessage("cmd_set_help5"));
                sender.sendMessage(Language.getMessage("cmd_set_help6"));
                sender.sendMessage(Language.getMessage("cmd_set_help7"));
                return true;
            }
            args[1] = args[1].toLowerCase();
            if (!BattlePets.pets.containsKey(((Player) sender).getUniqueId())) {
                sender.sendMessage(Language.getMessage("NoPetAlive"));
                return true;
            }
            LivingEntity pet = BattlePets.pets.get(((Player) sender).getUniqueId());
            double kiekis = 0;
            try {
                kiekis = Double.parseDouble(args[2]);
            } catch (Exception e) {
                sender.sendMessage(Language.getMessage("invalidnumber"));
                return true;
            }
            if (!sender.hasPermission("battlepets.set." + args[1])) {
                sender.sendMessage(Language.getMessage("no_permission"));
                return true;
            }
            switch (args[1]) {
                case "skillpoints":
                    pet.setMetadata("Points", new FixedMetadataValue(BattlePets.plugin, (int) kiekis));
                    break;
                case "xp":
                    pet.setMetadata("XP", new FixedMetadataValue(BattlePets.plugin,
                            Math.min(kiekis, pet.getMetadata("XPForLevel").get(0).asDouble() - 0.01)));
                    break;
                case "level":
                    int curr = pet.getMetadata("Level").get(0).asInt();
                    int neww = (int) kiekis;
                    if (curr < neww) {
                        String type = pet.getMetadata("Type").get(0).asString().toLowerCase();
                        String st = "";
                        if (type.contains("baby"))
                            st += "baby-";
                        st += pet.getType().toString().toLowerCase();
                        if (st.equalsIgnoreCase("endermite"))
                            st = "block";
                        MobStats stats = BattlePets.statsai.get(st);
                        pet.setMetadata("Points", new FixedMetadataValue(BattlePets.plugin, pet.getMetadata("Points").get(0).asInt() + stats.SkillpointsForLevel * (neww - curr)));
                    }
                    pet.setMetadata("Level", new FixedMetadataValue(BattlePets.plugin, (int) kiekis));
                    BattlePets.spawning.update(pet, (BattlePets) BattlePets.plugin);
                    pet.setCustomName(ChatColor.translateAlternateColorCodes('&', Language.display.replace("{name}", pet.getMetadata("Name").get(0).asString()).replace("{level}", pet.getMetadata("Level").get(0).asInt() + "")));

                    break;
                case "vitality":
                    pet.setMetadata("Vitality", new FixedMetadataValue(BattlePets.plugin, (int) kiekis));
                    BattlePets.spawning.update(pet, (BattlePets) BattlePets.plugin);
                    break;
                case "dexterity":
                    pet.setMetadata("Dexterity", new FixedMetadataValue(BattlePets.plugin, (int) kiekis));
                    BattlePets.spawning.update(pet, (BattlePets) BattlePets.plugin);
                    break;
                case "defense":
                    pet.setMetadata("Defense", new FixedMetadataValue(BattlePets.plugin, (int) kiekis));
                    BattlePets.spawning.update(pet, (BattlePets) BattlePets.plugin);
                    break;
                case "strength":
                    pet.setMetadata("Strength", new FixedMetadataValue(BattlePets.plugin, (int) kiekis));
                    BattlePets.spawning.update(pet, (BattlePets) BattlePets.plugin);
                    break;
            }
            sender.sendMessage(Language.getMessage("petupdated"));
            return true;
        }
        if (args[0].equalsIgnoreCase("shop") && sender instanceof Player) {
            if (sender.hasPermission("battlepets.shop")) {
                Shop.openshop((Player) sender, "cmd");
                return true;
            } else {
                sender.sendMessage(Language.getMessage("no_permission"));
            }
        }
        if (args[0].equalsIgnoreCase("cancel") && sender instanceof Player) {
            if (sender.hasPermission("battlepets.cancel")) {
                if (BattlePets.pets.containsKey(((Player) sender).getUniqueId())) {
                    BattlePets.spawning.setTarget(BattlePets.pets.get(((Player) sender).getUniqueId()), null);
                }
                return true;
            } else {
                sender.sendMessage(Language.getMessage("no_permission"));
            }
        }
        if (args[0].equalsIgnoreCase("menu") && sender instanceof Player) {
            if (sender.hasPermission("battlepets.menu")) {
                if (BattlePets.pets.containsKey(((Player) sender).getUniqueId())) {
                    BattlePets.openmenu((Player) sender, BattlePets.pets.get(((Player) sender).getUniqueId()));
                } else {
                    sender.sendMessage(Language.getMessage("NoPetAlive"));
                }
                return true;
            } else {
                sender.sendMessage(Language.getMessage("no_permission"));
            }
        }
        if (args[0].equalsIgnoreCase("name") && sender instanceof Player) {
            if (sender.hasPermission("battlepets.name")) {

                if (args.length == 2) {
                    if (!BattlePets.pets.containsKey(((Player) sender).getUniqueId())) {
                        sender.sendMessage(Language.getMessage("NoPetAlive"));
                        return true;
                    }
                    if (args[1].length() > BattlePets.namesize) {
                        sender.sendMessage(Language.getMessage("pet_name_toolong"));
                        return true;
                    }
                    LivingEntity pet = BattlePets.pets.get(((Player) sender).getUniqueId());
                    pet.setMetadata("Name", new FixedMetadataValue(BattlePets.plugin, args[1]));
                    sender.sendMessage(Language.getMessage("pet_renamed"));
                    pet.setCustomName(ChatColor.translateAlternateColorCodes('&', Language.display.replace("{name}", pet.getMetadata("Name").get(0).asString()).replace("{level}", pet.getMetadata("Level").get(0).asString() + "")));
                    //sender.sendMessage(Language.getMessage("renamed_warn"));
                } else {
                    sender.sendMessage(Language.getMessage("cmd_name_usage"));
                }
                return true;
            } else {
                sender.sendMessage(Language.getMessage("no_permission"));
            }
        }
        if (args[0].equalsIgnoreCase("egg")) {
            if (!sender.hasPermission("battlepets.egg")) {
                sender.sendMessage(Language.getMessage("no_permission"));
                return true;
            }
            if (args.length < 2) {
                sender.sendMessage(Language.getMessage("cmd_egg_usage"));
                return true;
            }
            Player p = (Player) (args.length >= 3 ? Bukkit.getPlayer(args[1]) : sender);
            if (p == null) {
                return true;
            }
            String tipas = args.length >= 3 ? args[2] : args[1];
            if (tipas.split(":")[0].equalsIgnoreCase("skull_item")) {
                String name = tipas.split("-")[0].split(":")[1];
                tipas = "SKULL_ITEM:" + name + "-BLOCK";
            } else
                tipas = tipas.toUpperCase();
            String[] argumentai = tipas.split("-");
            String type = "";
            if (argumentai[0].equalsIgnoreCase("baby")) {
                type += "baby-";
            }
            type += argumentai[argumentai.length - 1].toLowerCase();
            if (!BattlePets.statsai.containsKey(type) && !type.equalsIgnoreCase("random")) {
                sender.sendMessage(Language.getMessage("type_unavailable"));
                return true;
            }
            String mob = argumentai[argumentai.length - 1].toLowerCase();
            switch (mob) {
                case "horse":
                    if (argumentai[0].equalsIgnoreCase("baby")) {
                        if (argumentai.length != 5) {
                            sender.sendMessage(Language.getMessage("cmd_horse_usage1"));
                            sender.sendMessage(Language.getMessage("cmd_horse_usage2"));
                            sender.sendMessage(Language.getMessage("cmd_horse_usage3"));
                            sender.sendMessage(Language.getMessage("cmd_horse_usage4"));
                            return true;
                        }
                    } else {
                        if (argumentai.length != 4) {
                            sender.sendMessage(Language.getMessage("cmd_horse_usage1"));
                            sender.sendMessage(Language.getMessage("cmd_horse_usage2"));
                            sender.sendMessage(Language.getMessage("cmd_horse_usage3"));
                            sender.sendMessage(Language.getMessage("cmd_horse_usage4"));
                            return true;
                        }
                    }
                    break;
                case "rabbit":
                    if (argumentai[0].equalsIgnoreCase("baby")) {
                        if (argumentai.length != 3) {
                            sender.sendMessage(Language.getMessage("cmd_rabbit_usage1"));
                            sender.sendMessage(Language.getMessage("cmd_rabbit_usage2"));
                            return true;
                        }
                    } else {
                        if (argumentai.length != 2) {
                            sender.sendMessage(Language.getMessage("cmd_rabbit_usage1"));
                            sender.sendMessage(Language.getMessage("cmd_rabbit_usage2"));
                            return true;
                        }
                    }
                    break;
                case "sheep":
                    if (argumentai[0].equalsIgnoreCase("baby")) {
                        if (argumentai.length != 3) {
                            sender.sendMessage(Language.getMessage("cmd_sheep_usage1"));
                            sender.sendMessage(Language.getMessage("cmd_sheep_usage2"));
                            return true;
                        }
                    } else {
                        if (argumentai.length != 2) {
                            sender.sendMessage(Language.getMessage("cmd_sheep_usage1"));
                            sender.sendMessage(Language.getMessage("cmd_sheep_usage2"));
                            return true;
                        }
                    }
                    break;
                case "villager":
                    if (argumentai[0].equalsIgnoreCase("baby")) {
                        if (argumentai.length != 3) {
                            sender.sendMessage(Language.getMessage("cmd_villager_usage1"));
                            sender.sendMessage(Language.getMessage("cmd_villager_usage2"));
                            return true;
                        }
                    } else {
                        if (argumentai.length != 2) {
                            sender.sendMessage(Language.getMessage("cmd_villager_usage1"));
                            sender.sendMessage(Language.getMessage("cmd_villager_usage2"));
                            return true;
                        }
                    }
                    break;
                case "slime":
                case "magma_cube":
                    if (argumentai.length != 2) {
                        sender.sendMessage(Language.getMessage("cmd_slime_usage1"));
                        sender.sendMessage(Language.getMessage("cmd_slime_usage2"));
                        return true;
                    }

                    break;
            }
            if (type.equalsIgnoreCase("endermite"))
                type = "block";
            MobStats stats = BattlePets.statsai.get(type);
            if (type.equalsIgnoreCase("block") || type.equalsIgnoreCase("baby-wither")) {
            ItemStack item = new ItemStack(Material.MONSTER_EGG, 1);
            ItemMeta meta = item.getItemMeta();
            if (args.length > 3) {
                String name = "";
                for (int i = 3; i < args.length - 1; i++) {
                    name += args[i] + " ";
                }
                name += args[args.length - 1];
                meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
            } else
                meta.setDisplayName(Language.defaultas.replace("{type}", tipas));
            meta.setLore(Arrays.asList(Language.getMessage("type", true) + ": " + tipas, Language.getMessage("level", true) + ": 1", Language.getMessage("xp", true) + ": 0/" + stats.XPForLevel, Language.getMessage("hp", true) + ": " + stats.HP + "/" + stats.HP, Language.getMessage("skillpoints", true) + ": " + stats.SkillpointsForLevel, Language.getMessage("vitality", true) + ": 0", Language.getMessage("defense", true) + ": 0", Language.getMessage("strength", true) + ": 0", Language.getMessage("dexterity", true) + ": 0"));
            item.setItemMeta(meta);
            p.getInventory().addItem(item);
            sender.sendMessage(Language.getMessage("cmd_egg_added"));
            } else {
                ItemStack item = BattlePets.createEgg(type, argumentai);
                ItemMeta meta = item.getItemMeta();
                if (args.length > 3) {
                    String name = "";
                    for (int i = 3; i < args.length - 1; i++) {
                        name += args[i] + " ";
                    }
                    name += args[args.length - 1];
                    meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
                }
                item.setItemMeta(meta);
                p.getInventory().addItem(item);
                sender.sendMessage(Language.getMessage("cmd_egg_added"));
            }
        }
        return true;
    }

}
