package darius0021;

import darius0021.events.ArmorStandEvent;
import darius0021.events.EntityEvents;
import darius0021.events.InventoryEvents;
import darius0021.events.PlayerEvents;
import darius0021.reflection.v1_8_1.Spawning_v1_8_R1;
import darius0021.reflection.v1_8_2.Spawning_v1_8_R2;
import darius0021.reflection.v1_8_3.Spawning_v1_8_R3;
import darius0021.reflection.v1_9_1.Spawning_v1_9_R1;
import darius0021.versions.Spawning;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.*;

public class BattleMobs extends JavaPlugin implements Listener {
    public static HashMap<String, MobStats> statsai = new HashMap<String, MobStats>();
    public static Map<UUID, LivingEntity> pets = new HashMap<UUID, LivingEntity>();
    public static Plugin plugin;
    public static int namesize;
    public static boolean PVP, AllWorlds;
    public static Spawning spawning;
    public static String version;
    public static List<String> worlds = new ArrayList<String>();
    public static List<String> aliases = new ArrayList<String>();
    public static WorldguardZones wg = null;
    public Shop shop;
    public Language lang;
    public int radius1, radius2;
    boolean allowpetsintowny;
    List<String> allowed = new ArrayList<String>();
    MobCatching catcher;
    ArmorStandEvent armorstandevent;
    EntityEvents entityevents;
    InventoryEvents inventoryevents;
    PlayerEvents playerevents;

    public static ItemStack return_pet(Player p) {
        if (PlayerEvents.namechanging.contains(p.getUniqueId()))
            PlayerEvents.namechanging.remove(p.getUniqueId());
        if (PlayerEvents.battles.containsKey(p.getUniqueId()))
            PlayerEvents.battles.remove(p.getUniqueId());

        LivingEntity pet = pets.get(p.getUniqueId());
        spawning.returnas(pet);
        ItemStack ite = new ItemStack(Material.MONSTER_EGG, 1);
        ItemMeta meta = ite.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', pet.getMetadata("Name").get(0).asString()));
        meta.setLore(Arrays.asList(Language.getMessage("type", true) + ": " + pet.getMetadata("Type").get(0).asString(), Language.getMessage("level", true) + ": " + pet.getMetadata("Level").get(0).asString(), Language.getMessage("xp", true) + ": " + pet.getMetadata("XP").get(0).asDouble() + "/" + pet.getMetadata("XPForLevel").get(0).asDouble(), Language.getMessage("hp", true) + ": " + new DecimalFormat("0.##").format(pet.getHealth()).replace(',', '.') + "/" + pet.getMaxHealth(), Language.getMessage("skillpoints", true) + ": " + pet.getMetadata("Points").get(0).asString(), Language.getMessage("vitality", true) + ": " + pet.getMetadata("Vitality").get(0).asString(), Language.getMessage("defense", true) + ": " + pet.getMetadata("Defense").get(0).asString(), Language.getMessage("strength", true) + ": " + pet.getMetadata("Strength").get(0).asString(), Language.getMessage("dexterity", true) + ": " + pet.getMetadata("Dexterity").get(0).asString()));
        ite.setItemMeta(meta);
        if (p.getInventory().firstEmpty() == -1) {
            p.getWorld().dropItemNaturally(p.getEyeLocation(), ite);
        } else {
            p.getInventory().addItem(ite);
        }
        pet.remove();

        pets.remove(p.getUniqueId());
        return ite;
    }

    public static void AddXP(LivingEntity pet, double amount) {
        double current = pet.getMetadata("XP").get(0).asDouble();
        double req = pet.getMetadata("XPForLevel").get(0).asDouble();
        current += amount;
        String type = pet.getMetadata("Type").get(0).asString().toLowerCase();
        String st = "";
        if (type.contains("baby"))
            st += "baby-";
        st += pet.getType().toString().toLowerCase();
        if (st.equalsIgnoreCase("endermite"))
            st = "block";
        MobStats stats = statsai.get(st);
        int level = pet.getMetadata("Level").get(0).asInt();
        int max = stats.MaxLevel;
        if (level >= max) return;
        if (current >= req) {
            //LEVEL UP!
            current -= req;
            level++;
            double xpforlevel = stats.XPForLevel * level;
            pet.setMetadata("Level", new FixedMetadataValue(plugin, level));
            pet.setMetadata("XPForLevel", new FixedMetadataValue(plugin, xpforlevel));
            pet.setMetadata("XP", new FixedMetadataValue(plugin, current));
            int points = pet.getMetadata("Points").get(0).asInt();
            pet.setMetadata("Points", new FixedMetadataValue(plugin, points + stats.SkillpointsForLevel));
            Bukkit.getPlayer(UUID.fromString(pet.getMetadata("Owner").get(0).asString())).sendMessage(Language.getMessage("pet_levelup").replace("{level}", "" + level));
            pet.setCustomName(Language.display.replace("{name}", pet.getMetadata("Name").get(0).asString()).replace("{level}", level + ""));
            spawning.nameupdate(pet);
            AddXP(pet, 0);
        } else
            pet.setMetadata("XP", new FixedMetadataValue(plugin, current));
    }

    public static void openmenu(Player p, LivingEntity livingEntity) {
        LivingEntity pet = livingEntity;
        String type = pet.getMetadata("Type").get(0).asString().toLowerCase();
        String st = "";
        if (type.contains("baby"))
            st += "baby-";
        st += pet.getType().toString().toLowerCase();
        if (st.equalsIgnoreCase("endermite"))
            st = "block";
        //---------------------------
        MobStats petstats = statsai.get(st);
        String mobtype;
        int level, points;
        int Vitality, Defense, Strength, Dexterity;
        double xp, xpforlevel;
        mobtype = livingEntity.getMetadata("Type").get(0).asString();
        level = livingEntity.getMetadata("Level").get(0).asInt();
        points = livingEntity.getMetadata("Points").get(0).asInt();
        Vitality = livingEntity.getMetadata("Vitality").get(0).asInt();
        Defense = livingEntity.getMetadata("Defense").get(0).asInt();
        Strength = livingEntity.getMetadata("Strength").get(0).asInt();
        Dexterity = livingEntity.getMetadata("Dexterity").get(0).asInt();
        xp = livingEntity.getMetadata("XP").get(0).asDouble();
        xpforlevel = livingEntity.getMetadata("XPForLevel").get(0).asDouble();
        //-------------------------------
        Inventory inv = Bukkit.createInventory(p, 18, Language.getMessage("pet_stats", true));
        ItemStack item = new ItemStack(Material.DIAMOND);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.BLUE + livingEntity.getMetadata("Name").get(0).asString());
        meta.setLore(Arrays.asList(
                Language.getMessage("stats_type", true) + mobtype,
                Language.getMessage("stats_level", true) + level,
                Language.getMessage("stats_xp", true) + xp + "/" + xpforlevel,
                Language.getMessage("stats_rename", true)
        ));
        item.setItemMeta(meta);
        inv.setItem(0, item);
        item = new ItemStack(Material.GOLD_INGOT);
        meta = item.getItemMeta();
        meta.setDisplayName(Language.getMessage("menu_stats", true));
        meta.setLore(Arrays.asList(
                Language.getMessage("menu_hp", true) + new DecimalFormat("0.##").format(livingEntity.getHealth()).replace(',', '.') + "/" + new DecimalFormat("0.##").format(livingEntity.getMaxHealth()).replace(',', '.'),
                Language.getMessage("menu_dmg", true) + petstats.Damage + "+" + new DecimalFormat("0.##").format((Strength * petstats._Damage)).replace(',', '.'),
                Language.getMessage("menu_defense", true) + petstats.Defense + "+" + new DecimalFormat("0.##").format((Defense * petstats._Defense)).replace(',', '.'),
                Language.getMessage("menu_speed", true) + petstats.Speed + "+" + new DecimalFormat("0.##").format((Dexterity * petstats._Speed)).replace(',', '.')
        ));
        item.setItemMeta(meta);
        inv.setItem(1, item);
        item = new ItemStack(Material.ANVIL);
        meta = item.getItemMeta();
        meta.setDisplayName(Language.getMessage("menu_skillpoints", true));
        meta.setLore(Arrays.asList(
                Language.getMessage("menu_skillpoint", true) + " " + points,
                Language.getMessage("menu_vit", true) + " " + Vitality,
                Language.getMessage("menu_str", true) + " " + Strength,
                Language.getMessage("menu_def", true) + " " + Defense,
                Language.getMessage("menu_dex", true) + " " + Dexterity,
                Language.getMessage("menu_managepoints", true)
        ));
        item.setItemMeta(meta);
        inv.setItem(2, item);
        item = new ItemStack(Material.ENCHANTMENT_TABLE);
        meta = item.getItemMeta();
        meta.setDisplayName(Language.getMessage("pet_shop", true));
        meta.setLore(Arrays.asList(
                Language.getMessage("pet_shop_lore", true)
        ));
        item.setItemMeta(meta);
        inv.setItem(8, item);
        if (petstats.ridable) {
            item = new ItemStack(Material.SADDLE);
            meta = item.getItemMeta();
            meta.setDisplayName(Language.getMessage("menu_ride", true));
            meta.setLore(Arrays.asList(
                    Language.getMessage("menu_ride_lore", true)
            ));
            item.setItemMeta(meta);
            inv.setItem(9, item);
        }
        if (p.hasPermission("battlepets.battle.request")) {
            item = new ItemStack(Material.GOLD_SWORD);
            meta = item.getItemMeta();
            meta.setDisplayName(Language.getMessage("menu_battle", true));
            meta.setLore(Arrays.asList(
                    Language.getMessage("menu_battle_lore", true)
            ));
            item.setItemMeta(meta);
            inv.setItem(10, item);
        }
        item = new ItemStack(Material.MONSTER_EGG);
        meta = item.getItemMeta();
        meta.setDisplayName(Language.getMessage("egg_return", true));
        meta.setLore(Arrays.asList(
                Language.getMessage("egg_return_lore", true)
        ));
        item.setItemMeta(meta);
        inv.setItem(17, item);
        p.openInventory(inv);
    }

    public static void skillpointsmenu(Player p) {
        LivingEntity pet = BattleMobs.pets.get(p.getUniqueId());
        int points = pet.getMetadata("Points").get(0).asInt();
        int Vitality = pet.getMetadata("Vitality").get(0).asInt();
        int Strength = pet.getMetadata("Strength").get(0).asInt();
        int Defense = pet.getMetadata("Defense").get(0).asInt();
        int Dexterity = pet.getMetadata("Dexterity").get(0).asInt();
        String type = pet.getMetadata("Type").get(0).asString().toLowerCase();
        String st = "";
        if (type.contains("baby"))
            st += "baby-";
        st += pet.getType().toString().toLowerCase();
        if (st.equalsIgnoreCase("endermite"))
            st = "block";
        MobStats stats = BattleMobs.statsai.get(st);
        Inventory inv = null;
        if (points > 0) {
            inv = Bukkit.createInventory(p, 18, Language.getMessage("menu_skillpoints", true));
        } else {
            inv = Bukkit.createInventory(p, 9, Language.getMessage("menu_skillpoints", true));
        }
        ItemStack item;
        item = new ItemStack(Material.REDSTONE);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(Language.getMessage("menu_vit", true));
        meta.setLore(
                Arrays.asList(Language.getMessage("menu_points", true) + ": " + Vitality,
                        Language.getMessage("menu_maxpoints", true) + ": " + stats.maxvit));
        item.setItemMeta(meta);
        inv.setItem(2, item);
        item = new ItemStack(Material.DIAMOND_SWORD);
        meta = item.getItemMeta();
        meta.setDisplayName(Language.getMessage("menu_str", true));
        meta.setLore(Arrays.asList(Language.getMessage("menu_points", true) + ": " + Strength,
                Language.getMessage("menu_maxpoints", true) + ": " + stats.maxstr));
        item.setItemMeta(meta);
        inv.setItem(3, item);
        item = new ItemStack(Material.IRON_CHESTPLATE);
        meta = item.getItemMeta();
        meta.setDisplayName(Language.getMessage("menu_def", true));
        meta.setLore(Arrays.asList(Language.getMessage("menu_points", true) + ": " + Defense,
                Language.getMessage("menu_maxpoints", true) + ": " + stats.maxdef));
        item.setItemMeta(meta);
        inv.setItem(4, item);
        item = new ItemStack(Material.GOLD_BOOTS);
        meta = item.getItemMeta();
        meta.setDisplayName(Language.getMessage("menu_dex", true));
        meta.setLore(Arrays.asList(Language.getMessage("menu_points", true) + ": " + Dexterity,
                Language.getMessage("menu_maxpoints", true) + ": " + stats.maxdex));
        item.setItemMeta(meta);
        inv.setItem(5, item);

        item = new ItemStack(Material.GOLD_INGOT);
        meta = item.getItemMeta();
        meta.setDisplayName(Language.getMessage("menu_skillpoint", true));
        meta.setLore(Arrays.asList(Language.getMessage("menu_available", true) + ": " + points));
        item.setItemMeta(meta);
        inv.setItem(0, item);
        item = new ItemStack(Material.ENDER_PEARL);
        meta = item.getItemMeta();
        meta.setDisplayName(Language.getMessage("menu_return", true));
        meta.setLore(Arrays.asList(Language.getMessage("return_lore", true)));
        item.setItemMeta(meta);
        inv.setItem(8, item);
        if (points > 0) {
            item = new ItemStack(Material.EMERALD);
            meta = item.getItemMeta();
            meta.setDisplayName(Language.getMessage("menu_vit", true) + " +0");
            meta.setLore(Arrays.asList(Language.getMessage("add_points", true), Language.getMessage("remove_points", true)));
            item.setItemMeta(meta);
            inv.setItem(11, item);
            item = new ItemStack(Material.EMERALD);
            meta = item.getItemMeta();
            meta.setDisplayName(Language.getMessage("menu_str", true) + " +0");
            meta.setLore(Arrays.asList(Language.getMessage("add_points", true), Language.getMessage("remove_points", true)));
            item.setItemMeta(meta);
            inv.setItem(12, item);
            item = new ItemStack(Material.EMERALD);
            meta = item.getItemMeta();
            meta.setDisplayName(Language.getMessage("menu_def", true) + " +0");
            meta.setLore(Arrays.asList(Language.getMessage("add_points", true), Language.getMessage("remove_points", true)));
            item.setItemMeta(meta);
            inv.setItem(13, item);
            item = new ItemStack(Material.EMERALD);
            meta = item.getItemMeta();
            meta.setDisplayName(Language.getMessage("menu_dex", true) + " +0");
            meta.setLore(Arrays.asList(Language.getMessage("add_points", true), Language.getMessage("remove_points", true)));
            item.setItemMeta(meta);
            inv.setItem(14, item);
            item = new ItemStack(Material.DIAMOND);
            meta = item.getItemMeta();
            meta.setDisplayName(Language.getMessage("save_changes", true));
            meta.setLore(Arrays.asList(Language.getMessage("save_lor1", true), Language.getMessage("save_lor2", true)));
            item.setItemMeta(meta);
            inv.setItem(17, item);
        }
        p.openInventory(inv);
    }

    public static ItemStack createEgg(String type, String args[]) {
        ItemStack item = new ItemStack(Material.MONSTER_EGG, 1);
        ItemMeta meta = item.getItemMeta();
        Random rand = new Random();
        if (type.equalsIgnoreCase("random")) {
            type = (String) statsai.keySet().toArray()[rand.nextInt(statsai.size())];
            args = new String[1];
            args[0] = "random";
        }

        return null;
    }

    @Override
    public void onEnable() {
        plugin = this;
        loadConfig();

        setupVersion();
        if (spawning == null) {
            getLogger().info("Incompatible version! Please upgrade your server version or wait for plugin update.");
            Bukkit.getPluginManager().disablePlugin(this);
        }
        Converter.noMoreUnderscore(this);
        Database.Enable();
        spawning.load();
        Bukkit.getPluginManager().registerEvents(this, this);
        entityevents = new EntityEvents(this);
        inventoryevents = new InventoryEvents(this);
        playerevents = new PlayerEvents(this);
        if (version.contains("1_8"))
            armorstandevent = new ArmorStandEvent(this);

        catcher = new MobCatching(this);
        shop = new Shop(this);
        lang = new Language(this);
        loadConfigs();

        @SuppressWarnings("unused")
        Towny towny;
        if (Bukkit.getPluginManager().isPluginEnabled("Towny")) {
            towny = new Towny(this, allowpetsintowny);
        }

        Plugin worldguard = getServer().getPluginManager().getPlugin("WorldGuard");
        if (worldguard != null && getConfig().getBoolean("WorldGuard.Enabled")) {
            wg = new WorldguardZones(worldguard, this);
        }
        if (wg != null) {
            Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
                @Override
                public void run() {
                    if (!pets.isEmpty()) {
                        List<UUID> ids = new ArrayList<UUID>();
                        for (UUID val : pets.keySet()) {
                            ids.add(val);
                        }
                        for (UUID val : ids) {
                            if (!wg.isAllowed(pets.get(val))) {
                                try {
                                    Bukkit.getPlayer(val).sendMessage(Language.getMessage("disabled_zone_return"));
                                    return_pet(Bukkit.getPlayer(val));
                                } catch (Exception e) {
                                }
                            }
                        }

                    }
                }
            }, 0, 15);
        }

        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
                for (LivingEntity pet : pets.values()) {
                    if (pet.getHealth() < pet.getMaxHealth()) {
                        pet.setHealth(Math.min(pet.getHealth() + pet.getMetadata("Regen").get(0).asDouble() * pet.getMaxHealth(), pet.getMaxHealth()));
                    }
                }
            }
        }, 0, 20);

        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
                if (!pets.isEmpty()) {
                    List<UUID> ids = new ArrayList<UUID>();
                    for (UUID val : pets.keySet()) {
                        ids.add(val);
                    }
                    for (UUID val : ids) {
                        if (!pets.get(val).isDead()) continue;
                        try {
                            Bukkit.getPlayer(val).sendMessage(Language.getMessage("disabled_zone_return"));
                            return_pet(Bukkit.getPlayer(val));
                        } catch (Exception e) {
                        }

                    }

                }
            }
        }, 0, 40);

    }

    public void loadConfig() {
        getConfig().options().copyDefaults(true);
        saveConfig();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        return Commands.onCommand(sender, cmd, label, args);
    }

    public void loadConfigs() {
        aliases = getConfig().getStringList("CommandAliases");
        allowed = getConfig().getStringList("AllowedPets");
        radius2 = getConfig().getInt("MaxRadius");
        radius1 = getConfig().getInt("RadiusFromWhereToFollow");
        namesize = getConfig().getInt("PetNameSize");
        PVP = getConfig().getBoolean("PVPEnabled");
        AllWorlds = getConfig().getBoolean("AllWorldsAllowed");
        if (!AllWorlds) {
            worlds = getConfig().getStringList("AllowedWorlds");
        }
        File custompetfile = new File(getDataFolder(), "custompets.yml");
        if (!custompetfile.exists())
            saveResource("custompets.yml", false);

        File petfile = new File(getDataFolder(), "pets.yml");
        if (!petfile.exists())
            saveResource("pets.yml", false);
        YamlConfiguration config = YamlConfiguration.loadConfiguration(petfile);

        Reader stream;
        stream = new InputStreamReader(getResource("pets.yml"), StandardCharsets.UTF_8);
        YamlConfiguration conf = YamlConfiguration.loadConfiguration(stream);
        for (String s : conf.getKeys(false)) {
            if (!config.contains(s))
                config.set(s, conf.get(s));
        }
        try {
            config.save(new File(getDataFolder(), "pets.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        config = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "pets.yml"));
        for (String s : config.getKeys(false)) {
            if (allowed.contains(s))
                statsai.put(s, new MobStats(this, s, false));
        }

        YamlConfiguration config2 = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "custompets.yml"));

        for (String s : config2.getKeys(false)) {
            if (allowed.contains(s)) {
                statsai.put(s, new MobStats(this, s, true));
                //Bukkit.getLogger().info(s);
            }

        }
        allowpetsintowny = getConfig().getBoolean("AllowPetsInTowny");
    }

    @Override
    public void onDisable() {
        if (!pets.isEmpty()) {
            List<UUID> ids = new ArrayList<UUID>();
            for (UUID val : pets.keySet()) {
                ids.add(val);
            }
            for (UUID val : ids) {
                try {
                    return_pet(Bukkit.getPlayer(val));
                } catch (Exception e) {
                }
            }

        }
        pets.clear();

        Bukkit.getScheduler().cancelTasks(this);
    }

    public void reload() {
        if (!pets.isEmpty()) {
            List<UUID> ids = new ArrayList<UUID>();
            for (UUID val : pets.keySet()) {
                ids.add(val);
            }
            for (UUID val : ids) {
                try {
                    return_pet(Bukkit.getPlayer(val));
                } catch (Exception e) {
                }
            }

        }
        pets.clear();

        statsai.clear();
        ((BattleMobs) BattleMobs.plugin).allowed.clear();
        Shop.menus.clear();
        shop.createshop();
        Language.messages.clear();
        lang.update();
        loadConfigs();

    }

    private boolean setupVersion() {
        try {
            version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
        } catch (ArrayIndexOutOfBoundsException whatVersionAreYouUsingException) {
            return false;
        }
        getLogger().info("Your server is running version " + version);
        switch (version) {
            case "v1_8_R3":
                spawning = new Spawning_v1_8_R3();
                break;
            case "v1_8_R2":
                spawning = new Spawning_v1_8_R2();
                break;
            case "v1_8_R1":
                spawning = new Spawning_v1_8_R1();
                break;
            case "v1_9_R1":
                spawning = new Spawning_v1_9_R1();
        }

        return spawning != null;
    }
}
