package darius0021;

import darius0021.events.PlayerEvents;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.sql.*;
import java.util.Arrays;
import java.util.UUID;

public class Database {
    public static boolean enabled = false;
    public static String url, user, password, table, port;

    public static void Enable() {
        table = "pets";
        enabled = BattlePets.plugin.getConfig().getBoolean("MySQL.Enabled");
        if (!enabled) return;
        Connection con = null;
        Statement st = null;
        ResultSet rs = null;
        port = BattlePets.plugin.getConfig().getString("MySQL.Port");
        url = "jdbc:mysql://" + BattlePets.plugin.getConfig().getString("MySQL.IP") + ":" + port + "/" + BattlePets.plugin.getConfig().getString("MySQL.Database") + "?useUnicode=true&characterEncoding=utf-8";
        user = BattlePets.plugin.getConfig().getString("MySQL.Username");
        password = BattlePets.plugin.getConfig().getString("MySQL.Password");

        //bando prisijungti.
        try {
            con = DriverManager.getConnection(url, user, password);
            st = con.createStatement();
        } catch (SQLException ex) {
            BattlePets.plugin.getLogger().info("Couldn't connect to MySQL.");
            enabled = false;
            return;
        }
        //owner,name,type,level,xp,HP,skillpoints,vitality,strength,defense,dexterity.
        try {
            DatabaseMetaData dbmeta = con.getMetaData();
            rs = dbmeta.getTables(null, null, table, null);
            if (!rs.next()) {
                // There's no table in MySQL. Creating one.
                String creator = "CREATE TABLE " + table + " (" +
                        "owner VARCHAR(45) PRIMARY KEY, "
                        + "name VARCHAR(45) NOT NULL default '', "
                        + "type VARCHAR(45) NOT NULL default '', "
                        + "level INT NOT NULL default 1, "
                        + "xp DOUBLE NOT NULL default 0, "
                        + "hp DOUBLE NOT NULL default 0, "
                        + "skillpoints INT NOT NULL default 1, "
                        + "vitality INT NOT NULL default 1, "
                        + "strength INT NOT NULL default 1, "
                        + "defense INT NOT NULL default 1, "
                        + "dexterity INT NOT NULL default 1, "
                        + "saved INT NOT NULL default 0)";

                st.executeUpdate(creator);
            }
            rs.close();
            st.close();
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
            enabled = false;
            return;
        }


    }

    public static void SavePet(LivingEntity pet) {
        Player p = Bukkit.getPlayer(UUID.fromString(pet.getMetadata("Owner").get(0).asString()));
        if (PlayerEvents.namechanging.contains(p.getUniqueId()))
            PlayerEvents.namechanging.remove(p.getUniqueId());
        if (PlayerEvents.battles.containsKey(p.getUniqueId()))
            PlayerEvents.battles.remove(p.getUniqueId());
        Connection con = null;
        Statement st = null;
        ResultSet rs = null;
        String owner = pet.getMetadata("Owner").get(0).asString();
        String name = pet.getMetadata("Name").get(0).asString();
        String type = pet.getMetadata("Type").get(0).asString();
        int level = pet.getMetadata("Level").get(0).asInt();
        double xp = pet.getMetadata("XP").get(0).asDouble();
        double hp = pet.getHealth();
        int skillpoints = pet.getMetadata("Points").get(0).asInt();
        int vitality = pet.getMetadata("Vitality").get(0).asInt();
        int strength = pet.getMetadata("Strength").get(0).asInt();
        int defense = pet.getMetadata("Defense").get(0).asInt();
        int dexterity = pet.getMetadata("Dexterity").get(0).asInt();

        try {
            con = DriverManager.getConnection(url, user, password);
            st = con.createStatement();
            rs = st.executeQuery("SELECT * FROM " + table + " WHERE owner='" + owner + "'");

            if (!rs.next()) {
                // Adding player to MySQL.
                st.executeUpdate("INSERT INTO " + table + " ("
                        + "owner, name, type, level, xp, hp, skillpoints, vitality, strength, defense, dexterity, saved) "
                        + "VALUES ("
                        + "'" + owner + "', "
                        + "'" + name + "', "
                        + "'" + type + "', "
                        + "'" + level + "', "
                        + "'" + xp + "', "
                        + "'" + hp + "', "
                        + "'" + skillpoints + "', "
                        + "'" + vitality + "', "
                        + "'" + strength + "', "
                        + "'" + defense + "', "
                        + "'" + dexterity + "', "
                        + "'" + 1 + "'"
                        + ")");
            } else {
                st.executeUpdate("UPDATE " + table + " SET "
                        + "name='" + name + "', "
                        + "type='" + type + "', "
                        + "level='" + level + "', "
                        + "xp='" + xp + "', "
                        + "hp='" + hp + "', "
                        + "skillpoints='" + skillpoints + "', "
                        + "vitality='" + vitality + "', "
                        + "strength='" + strength + "', "
                        + "defense='" + defense + "', "
                        + "dexterity='" + dexterity + "', "
                        + "saved='" + 1 + "'"
                        + " WHERE owner='" + owner + "'");
            }
            rs.close();
            st.close();
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static LivingEntity LoadPet(Player p) {
        if (!BattlePets.AllWorlds && !BattlePets.worlds.contains(p.getWorld().getName()))
            return null;

        Connection con = null;
        Statement st = null;
        ResultSet rs = null;
        String owner = p.getUniqueId().toString();
        String name = "";
        String type = "";
        int level = 1;
        double xp = 0;
        double hp = 0;
        int skillpoints = 0;
        int vitality = 0;
        int strength = 0;
        int defense = 0;
        int dexterity = 0;
        int saved = 0;
        LivingEntity pet = null;
        try {
            con = DriverManager.getConnection(url, user, password);
            st = con.createStatement();
            rs = st.executeQuery("SELECT * FROM " + table + " WHERE owner='" + owner + "'");

            if (!rs.next()) {
                return null;
            } else {
                name = rs.getString(2);
                type = rs.getString(3);
                level = rs.getInt(4);
                xp = rs.getDouble(5);
                hp = rs.getDouble(6);
                skillpoints = rs.getInt(7);
                vitality = rs.getInt(8);
                strength = rs.getInt(9);
                defense = rs.getInt(10);
                dexterity = rs.getInt(11);
                saved = rs.getInt(12);
            }
            if (saved == 0) return null;
            st.executeUpdate("UPDATE " + table + " SET "
                    + "saved='" + 0 + "'"
                    + " WHERE owner='" + owner + "'");
            rs.close();
            st.close();
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        String[] argumentai = type.split("-");
        ItemStack item = new ItemStack(Material.MONSTER_EGG, 1);
        ItemMeta meta = item.getItemMeta();
        String tipas = "";
        if (argumentai[0].equalsIgnoreCase("baby")) {
            tipas += "baby-";
        }
        tipas += argumentai[argumentai.length - 1].toLowerCase();
        MobStats stats = BattlePets.statsai.get(tipas);
        meta.setDisplayName(name);
        meta.setLore(Arrays.asList(Language.getMessage("type", true) + ": " + type, Language.getMessage("level", true) + ": " + level, Language.getMessage("xp", true) + ": " + xp + "/" + stats.XPForLevel, Language.getMessage("hp", true) + ": " + hp + "/" + stats.HP, Language.getMessage("skillpoints", true) + ": " + skillpoints, Language.getMessage("vitality", true) + ": " + vitality, Language.getMessage("defense", true) + ": " + defense, Language.getMessage("strength", true) + ": " + strength, Language.getMessage("dexterity", true) + ": " + dexterity));
        item.setItemMeta(meta);
        pet = BattlePets.spawning.SpawnCreature(new PlayerInteractEvent(p, Action.RIGHT_CLICK_BLOCK, item, p.getLocation().getBlock(), BlockFace.DOWN), (BattlePets) BattlePets.plugin);
        if (pet != null)
            BattlePets.pets.put(p.getUniqueId(), pet);
        return pet;
    }
}
