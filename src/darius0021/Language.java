package darius0021;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class Language {
    public static Map<String, String> messages = new HashMap<String, String>();
    public static String display, defaultas;
    static String prefix;
    Plugin plugin;
    File file;

    public Language(Plugin plugin) {
        this.plugin = plugin;
        update();
    }

    public static String getMessage(String index) {
        if (messages.containsKey(index))
            return prefix + messages.get(index);
        else
            return "";
    }

    public static String getMessage(String index, boolean nk) {
        if (messages.containsKey(index))
            return messages.get(index);
        else
            return "";
    }

    public void update() {
        file = new File(plugin.getDataFolder(), "lang.yml");
        if (!file.exists())
            plugin.saveResource("lang.yml", false);
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

        Reader stream;
        stream = new InputStreamReader(plugin.getResource("lang.yml"), StandardCharsets.UTF_8);
        YamlConfiguration conf = YamlConfiguration.loadConfiguration(stream);
        for (String s : conf.getKeys(false)) {
            if (!config.contains(s))
                config.set(s, conf.get(s));
        }
        try {
            config.save(new File(plugin.getDataFolder(), "lang.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (String s : config.getKeys(false)) {
            messages.put(s, ChatColor.translateAlternateColorCodes('&', config.getString(s)));
        }
        display = ChatColor.translateAlternateColorCodes('&', config.getString("displayname"));
        defaultas = ChatColor.translateAlternateColorCodes('&', config.getString("defaultname"));
        prefix = ChatColor.translateAlternateColorCodes('&', config.getString("prefix"));
    }
}
