package darius0021;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;

import java.io.File;
import java.util.*;
import java.util.logging.Level;

public class Shop implements Listener {
    public static Map<String, Menu> menus = new HashMap<String, Menu>();
    BattlePets plugin;
    Economy eco = null;
    Itemas revive_item;

    public Shop(BattlePets plugin) {
        this.plugin = plugin;
        if (Bukkit.getPluginManager().isPluginEnabled("Vault"))
            eco = Bukkit.getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class).getProvider();
        if (eco == null) {
            plugin.getLogger().info("Economy not found. Disabling BattlePets");
            Bukkit.getPluginManager().disablePlugin(plugin);
        }

        createshop();
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public static void openshop(Player p, String menu) {
        if (!menus.containsKey(menu)) return;
        Menu meniu = menus.get(menu);
        Inventory inv = Bukkit.createInventory(p, meniu.Size, meniu.DisplayName);
        for (Itemas item : meniu.items.values()) {
            if (p.hasPermission(item.perm)) {
                if (BattlePets.pets.containsKey(p.getUniqueId())) {
                    if (BattlePets.pets.get(p.getUniqueId()).getMetadata("Level").get(0).asInt() >= item.level)
                        inv.setItem(item.index, item.item);
                } else
                    inv.setItem(item.index, item.item);
            }
        }
        p.openInventory(inv);
    }

    public void createshop() {
        if (!new File(plugin.getDataFolder(), "shop.yml").exists())
            plugin.saveResource("shop.yml", false);
        FileConfiguration config = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "shop.yml"));
        Map<Integer, Itemas> items = new HashMap<Integer, Itemas>();
        int size;
        String DisplayName;
        size = config.getInt("Size");
        DisplayName = ChatColor.translateAlternateColorCodes('&', config.getString("Name"));
        //MAINMENU
        for (String s : config.getConfigurationSection("Items").getKeys(false)) {
            ItemStack item = new ItemStack(Material.valueOf(config.getString("Items." + s + ".Material")), 1, (short) config.getInt("Items." + s + ".Data"));
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', config.getString("Items." + s + ".DisplayName")));
            List<String> lore = new ArrayList<String>();
            for (String lor : config.getStringList("Items." + s + ".Lore")) {
                lore.add(ChatColor.translateAlternateColorCodes('&', lor));
            }
            meta.setLore(lore);
            item.setItemMeta(meta);
            Itemas itemas = new Itemas(
                    config.contains("Items." + s + ".Index") ?
                            config.getInt("Items." + s + ".Index") : 0,
                    item,
                    config.contains("Items." + s + ".Action") ?
                            config.getString("Items." + s + ".Action") : "",
                    config.contains("Items." + s + ".Price") ?
                            config.getDouble("Items." + s + ".Price") : 0,
                    config.contains("Items." + s + ".Perm") ?
                            config.getString("Items." + s + ".Perm") : "",
                    config.contains("Items." + s + ".Level") ?
                            config.getInt("Items." + s + ".Level") : 0);
            items.put(itemas.index, itemas);
        }
        menus.put("main_menu", new Menu("main_menu", items, size, DisplayName));
        //SECONDARY MENUS.
        if (config.getConfigurationSection("SecondaryMenus") != null)
            for (String sec : config.getConfigurationSection("SecondaryMenus").getKeys(false)) {
                items = new HashMap<Integer, Itemas>();
                size = config.getInt("SecondaryMenus." + sec + ".Size");
                DisplayName = ChatColor.translateAlternateColorCodes('&', config.getString("SecondaryMenus." + sec + ".DisplayName"));
                for (String s : config.getConfigurationSection("SecondaryMenus." + sec + ".Items").getKeys(false)) {
                    ItemStack item = new ItemStack(Material.valueOf(config.getString("SecondaryMenus." + sec + ".Items." + s + ".Material")), 1, (short) config.getInt("SecondaryMenus." + sec + ".Items." + s + ".Data"));
                    ItemMeta meta = item.getItemMeta();
                    meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', config.getString("SecondaryMenus." + sec + ".Items." + s + ".DisplayName")));
                    List<String> lore = new ArrayList<String>();
                    for (String lor : config.getStringList("SecondaryMenus." + sec + ".Items." + s + ".Lore")) {
                        lore.add(ChatColor.translateAlternateColorCodes('&', lor));
                    }
                    meta.setLore(lore);
                    item.setItemMeta(meta);
                    Itemas itemas = new Itemas(
                            config.contains("SecondaryMenus." + sec + ".Items." + s + ".Index") ?
                                    config.getInt("SecondaryMenus." + sec + ".Items." + s + ".Index") : 0,
                            item,
                            config.contains("SecondaryMenus." + sec + ".Items." + s + ".Action") ?
                                    config.getString("SecondaryMenus." + sec + ".Items." + s + ".Action") : "",
                            config.contains("SecondaryMenus." + sec + ".Items." + s + ".Price") ?
                                    config.getDouble("SecondaryMenus." + sec + ".Items." + s + ".Price") : 0,
                            config.contains("SecondaryMenus." + sec + ".Items." + s + ".Perm") ?
                                    config.getString("SecondaryMenus." + sec + ".Items." + s + ".Perm") : "",
                            config.contains("SecondaryMenus." + sec + ".Items." + s + ".Level") ?
                                    config.getInt("SecondaryMenus." + sec + ".Items." + s + ".Level") : 0);
                    items.put(itemas.index, itemas);
                }
                menus.put(sec, new Menu(sec, items, size, DisplayName));
            }
        //REVIVE ITEM
        ItemStack item2 = new ItemStack(Material.valueOf(config.getString("Revive.Material")), 1, (short) config.getInt("Revive.Data"));
        ItemMeta meta = item2.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', config.getString("Revive.DisplayName")));
        List<String> lore = new ArrayList<String>();
        for (String lor : config.getStringList("Revive.Lore")) {
            lore.add(ChatColor.translateAlternateColorCodes('&', lor));
        }
        meta.setLore(lore);
        item2.setItemMeta(meta);
        Itemas itemas = new Itemas(0, item2, "Revive", config.getDouble("Revive.Price"), "", 0);
        revive_item = itemas;
    }

    public void openrevive(Player p) {
        Inventory inv = Bukkit.createInventory(p, InventoryType.DISPENSER, ChatColor.RED + "Revive");
        inv.setItem(4, revive_item.item);
        p.openInventory(inv);
    }

    @EventHandler
    public void click(InventoryClickEvent event) {
        if (event.getClickedInventory() == null) return;
        if (event.getCurrentItem() == null) return;
        if (event.getCurrentItem().getType() == Material.AIR) return;
        if (ChatColor.stripColor(event.getClickedInventory().getName()).equalsIgnoreCase("revive")) {
            event.setCancelled(true);
            if (eco.getBalance((Player) event.getWhoClicked()) < revive_item.price) {
                ((Player) event.getWhoClicked()).sendMessage(Language.getMessage("shop_money_failed"));
                return;
            }
            eco.withdrawPlayer((Player) event.getWhoClicked(), revive_item.price);
            DoTheJob((Player) event.getWhoClicked(), "revive");
            return;
        }
        Menu meniu = null;
        String invname = event.getClickedInventory().getName();
        for (Menu men : menus.values()) {
            if (invname == men.DisplayName) {
                meniu = men;
                break;
            }
        }
        if (meniu == null) return;
        event.setCancelled(true);
        int slot = event.getRawSlot();
        Itemas itemas = meniu.items.get(slot);
        if (eco.getBalance((Player) event.getWhoClicked()) < itemas.price) {
            ((Player) event.getWhoClicked()).sendMessage(Language.getMessage("shop_money_failed"));
            return;
        }
        eco.withdrawPlayer((Player) event.getWhoClicked(), itemas.price);
        DoTheJob((Player) event.getWhoClicked(), itemas.ItemSpec);

    }

    //JOBS:
    //Heal:10
    //XP:25
    // Skillpoints:5
    // Egg:MightyZombie:ZOMBIE
    // Openmenu:VIP
    void DoTheJob(Player p, String fulljob) {
        String[] jobargs = fulljob.split(":");
        String partjob = jobargs[0].toLowerCase();
        LivingEntity pet = BattlePets.pets.get(p.getUniqueId());
        switch (partjob) {
            case "revive":
                ItemStack item2 = p.getInventory().getItemInHand();
                ItemMeta meta1 = item2.getItemMeta();
                List<String> lore = meta1.getLore();
                double hp = Double.valueOf(lore.get(3).substring(lore.get(3).indexOf("/") + 1));
                String line = lore.get(3);
                line = line.substring(0, line.lastIndexOf(":") + 2) + hp + "/" + hp;
                lore.set(3, line);
                meta1.setLore(lore);
                item2.setItemMeta(meta1);
                p.getInventory().setItem(p.getInventory().getHeldItemSlot(), item2);

                p.closeInventory();
                p.sendMessage(Language.getMessage("pet_revived"));
                break;
            case "heal":
                pet.setHealth(Math.min(pet.getMaxHealth(), pet.getHealth() + Double.parseDouble(jobargs[1])));
                p.sendMessage(Language.getMessage("pet_healed"));
                break;
            case "xp":
                BattlePets.AddXP(pet, Double.parseDouble(jobargs[1]));
                p.sendMessage(Language.getMessage("pet_xpbought"));
                break;
            case "skillpoints":
                pet.setMetadata("Points", new FixedMetadataValue(plugin, pet.getMetadata("Points").get(0).asInt() + Integer.parseInt(jobargs[1])));
                p.sendMessage(Language.getMessage("pet_spbought"));
                break;
            case "egg":
                if (jobargs.length > 3)
                    jobargs[2] += ":" + jobargs[3];
                String type = jobargs[2].split("-")[jobargs[2].split("-").length - 1].toLowerCase();
                if (!type.equalsIgnoreCase("block") && !type.equalsIgnoreCase("baby-wither")) {
                ItemStack ite = BattlePets.createEgg(type, jobargs[2].split("-"));
                ItemMeta meta = ite.getItemMeta();
                meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', jobargs[1]));
                type = "";
                jobargs[2] = jobargs[2].toUpperCase();
                if (jobargs[2].contains("BABY")) {
                    type += "baby-";
                }
                jobargs[2].split("-")[jobargs[2].split("-").length - 1].toLowerCase();
                ite.setItemMeta(meta);
                p.closeInventory();
                if (p.getInventory().firstEmpty() == -1) {
                    if (p.getEnderChest().firstEmpty() != -1)
                        p.getEnderChest().addItem(ite);
                    else
                        p.getWorld().dropItemNaturally(p.getEyeLocation(), ite);
                } else {
                    p.getInventory().addItem(ite);
                }
                p.sendMessage(Language.getMessage("egg_bought"));
                } else {
                ItemStack ite = new ItemStack(Material.MONSTER_EGG, 1);
                ItemMeta meta = ite.getItemMeta();
                meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', jobargs[1]));
                type = "";
                jobargs[2] = jobargs[2].toUpperCase();
                if (jobargs.length > 3)
                    jobargs[2] += ":" + jobargs[3];
                if (jobargs[2].contains("BABY")) {
                    type += "baby-";
                }
                type += jobargs[2].split("-")[jobargs[2].split("-").length - 1].toLowerCase();
                MobStats statsai = BattlePets.statsai.get(type);
                if (statsai == null) {
                    Bukkit.getLogger().log(Level.WARNING, "Egg type: '" + jobargs[2] + "' not found!");
                    return;
                }
                meta.setLore(Arrays.asList(Language.getMessage("type", true) + ": " + jobargs[2], Language.getMessage("level", true) + ": 1", Language.getMessage("xp", true) + ": " + "0/" + statsai.XPForLevel, Language.getMessage("hp", true) + ": " + statsai.HP + "/" + statsai.HP, Language.getMessage("skillpoints", true) + ": " + statsai.SkillpointsForLevel, Language.getMessage("vitality", true) + ": 0", Language.getMessage("defense", true) + ": 0", Language.getMessage("strength", true) + ": 0", Language.getMessage("dexterity", true) + ": 0"));
                ite.setItemMeta(meta);
                p.closeInventory();
                if (p.getInventory().firstEmpty() == -1) {
                    if (p.getEnderChest().firstEmpty() != -1)
                        p.getEnderChest().addItem(ite);
                    else
                        p.getWorld().dropItemNaturally(p.getEyeLocation(), ite);
                } else {
                    p.getInventory().addItem(ite);
                }
                p.sendMessage(Language.getMessage("egg_bought"));
                }
                break;
            case "pointsreset":
                int total = 0;
                total += pet.getMetadata("Vitality").get(0).asInt();
                total += pet.getMetadata("Defense").get(0).asInt();
                total += pet.getMetadata("Strength").get(0).asInt();
                total += pet.getMetadata("Dexterity").get(0).asInt();
                total += pet.getMetadata("Points").get(0).asInt();

                pet.setMetadata("Vitality", new FixedMetadataValue(plugin, 0));
                pet.setMetadata("Defense", new FixedMetadataValue(plugin, 0));
                pet.setMetadata("Strength", new FixedMetadataValue(plugin, 0));
                pet.setMetadata("Dexterity", new FixedMetadataValue(plugin, 0));

                pet.setMetadata("Points", new FixedMetadataValue(plugin, total));
                BattlePets.spawning.update(pet, plugin);
                p.sendMessage(Language.getMessage("reset_bought"));
                break;
            case "openmenu":
                if (jobargs[1].equalsIgnoreCase("return")) {
                    BattlePets.openmenu(p, BattlePets.pets.get(p.getUniqueId()));
                    return;
                }
                openshop(p, jobargs[1]);
                break;
        }
    }

    public class Itemas {
        int index;
        ItemStack item;
        String ItemSpec;
        double price;
        String perm;
        int level;

        public Itemas
                (int index,
                 ItemStack item,
                 String ItemSpec,
                 double price,
                 String perm,
                 int level) {
            this.index = index;
            this.item = item;
            this.ItemSpec = ItemSpec;
            this.price = price;
            this.perm = perm;
            this.level = level;
        }
    }

    public class Menu {
        int Size;
        String DisplayName;
        String name;
        Map<Integer, Itemas> items;

        public Menu(String name, Map<Integer, Itemas> items, int Size, String DisplayName) {
            this.name = name;
            this.items = items;
            this.Size = Size;
            this.DisplayName = DisplayName;
        }
    }
}
