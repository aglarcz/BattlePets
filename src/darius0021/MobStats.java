package darius0021;

import darius0021.versions.Util;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.HashMap;

public class MobStats {
    //-------------- CUSTOM
    public boolean custom;
    public double offX, offY, offZ;
    public ItemStack texture;
    //---------------
    public boolean ridable;
    public int reqlvl;
    public int HPLessThan;
    public int Chances;
    public int MaxLevel;
    public double XPForLevel;
    public int SkillpointsForLevel;
    public double HPPerSecPercent;
    public double HP, Damage, Defense, Speed;
    public double _HP, _Damage, _Defense, _Speed;
    public int maxvit, maxstr, maxdef, maxdex;
    public HashMap<Integer, Integer> maxes = new HashMap<Integer, Integer>();
    Plugin plugin;
    String type;

    public MobStats(Plugin plugin, String type, boolean custom) {
        this.type = type;
        this.plugin = plugin;
        this.custom = custom;
        build();
    }

    private void build() {
        FileConfiguration config;
        if (!custom)
            config = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "pets.yml"));
        else {
            config = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "custompets.yml"));
            texture = Util.CreateItem(config.getString(type + ".Texture"), 1);
            offX = config.getDouble(type + ".Offset.X");
            offY = config.getDouble(type + ".Offset.Y");
            offZ = config.getDouble(type + ".Offset.Z");
        }
        HPLessThan = config.getInt(type + ".HPLessThan");
        Chances = config.getInt(type + ".Chances");
        MaxLevel = config.getInt(type + ".MaxLevel");
        XPForLevel = config.getInt(type + ".XPForLevel");
        HP = config.getDouble(type + ".BaseStats.HP");
        Damage = config.getDouble(type + ".BaseStats.Damage");
        Defense = config.getDouble(type + ".BaseStats.Defense");
        Speed = config.getDouble(type + ".BaseStats.Speed");
        _HP = config.getDouble(type + ".AddPerStat.HP");
        _Damage = config.getDouble(type + ".AddPerStat.Damage");
        _Defense = config.getDouble(type + ".AddPerStat.Defense");
        _Speed = config.getDouble(type + ".AddPerStat.Speed");
        SkillpointsForLevel = config.getInt(type + ".SkillpointsForLevel");
        HPPerSecPercent = config.getDouble(type + ".HPPerSecPercent");
        reqlvl = config.getInt(type + ".RequiredLevel");
        maxvit = 0;
        maxvit = config.getInt(type + ".MaxPoints.Vitality");
        maxstr = config.getInt(type + ".MaxPoints.Strength");
        maxdef = config.getInt(type + ".MaxPoints.Defense");
        maxdex = config.getInt(type + ".MaxPoints.Dexterity");
        maxes.put(11, maxvit);
        maxes.put(12, maxstr);
        maxes.put(13, maxdef);
        maxes.put(14, maxdex);
        if (config.contains(type + ".Ridable"))
            ridable = config.getBoolean(type + ".Ridable");
        else
            ridable = false;
    }
}
