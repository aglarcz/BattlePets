package darius0021;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class Converter {

    public static void noMoreUnderscore(Plugin plugin) {
        //config.yml
        FileConfiguration config = plugin.getConfig();
        List<String> names = config.getStringList("AllowedPets");
        for (int i = 0; i < names.size(); i++) {
            names.set(i, names.get(i).toLowerCase());
            names.set(i, names.get(i).replace("baby_", "baby-"));
        }
        config.set("AllowedPets", names);
        plugin.saveConfig();
        //pets.yml
        File petfile = new File(plugin.getDataFolder(), "pets.yml");
        if (!petfile.exists())
            plugin.saveResource("pets.yml", false);
        YamlConfiguration configas = YamlConfiguration.loadConfiguration(petfile);
        for (String name : configas.getKeys(false)) {
            if (!name.contains("baby_")) continue;
            String nameold = name;
            String namenew = name.replace("baby_", "baby-");
            configas.set(namenew, configas.get(nameold));
            configas.set(nameold, null);
            //configas.set
        }

        try {
            configas.save(new File(plugin.getDataFolder(), "pets.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
