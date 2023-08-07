package me.creuch.dcroles;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.InputStreamReader;
import java.util.List;

public class Config {

    public Config(DCRoles instance) {
        this.instance = instance;
    }

    private final DCRoles instance;
    private TextHandling textHandling;
    // Config files
    private static YamlConfiguration mainConfig;
    private static YamlConfiguration langConfig;

    public YamlConfiguration getMainConfig() {
        return mainConfig;
    }

    public YamlConfiguration getLangConfig() {
        return langConfig;
    }

    public String getValue(String configType, String path) {
        textHandling = new TextHandling(instance);
        String value = null;
        if(configType.equalsIgnoreCase("mainConfig")) {
            value = mainConfig.getString(path);
            if(value == null) {
                Bukkit.getConsoleSender().sendMessage(textHandling.getFormatted("{P}&c Wysłano &c&lpustą&c wartość &7(" + path + ")"));
                YamlConfiguration originalMainConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(instance.getResource("config.yml")));
                value = originalMainConfig.getString(path);
                if(value == null) {
                    value = "null";
                }
            }
        } else if(configType.equalsIgnoreCase("langConfig")) {
            value = langConfig.getString(path);
            if(value == null) {
                Bukkit.getConsoleSender().sendMessage(textHandling.getFormatted("{P}&c Wysłano &c&lpustą&c wartość &7(" + path + ")"));
                YamlConfiguration originalLangConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(instance.getResource("lang.yml")));
                value = originalLangConfig.getString(path);
                if(value == null) {
                    value = "null";
                }
            }
        }
        return value;
    }

    public List<String> getList(String configType, String path) {
        textHandling = new TextHandling(instance);
        List<String> value = null;
        if(configType.equalsIgnoreCase("mainConfig")) {
            value = mainConfig.getStringList(path);
            if(value.isEmpty()) {
                Bukkit.getConsoleSender().sendMessage(textHandling.getFormatted("{P}&c Wysłano &c&lpustą&c wartość &7(" + path + ")"));
                YamlConfiguration originalMainConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(instance.getResource("config.yml")));
                value = originalMainConfig.getStringList(path);
                if(value.isEmpty()) {
                    value.add("null");
                }
            }
        } else if(configType.equalsIgnoreCase("langConfig")) {
            value = langConfig.getStringList(path);
            if(value.isEmpty()) {
                Bukkit.getConsoleSender().sendMessage(textHandling.getFormatted("{P}&c Wysłano &c&lpustą&c wartość &7(" + path + ")"));
                YamlConfiguration originalLangConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(instance.getResource("lang.yml")));
                value = originalLangConfig.getStringList(path);
                if(value.isEmpty()) {
                    value.add("null");
                }
            }
        }
        return value;
    }

    public void loadConfigs() {
        textHandling = new TextHandling(instance);
        if (!new File(instance.getDataFolder(), "config.yml").exists()) {
            instance.saveResource("config.yml", true);
        }
        if (!new File(instance.getDataFolder(), "lang.yml").exists()) {
            instance.saveResource("lang.yml", true);
        }
        mainConfig = YamlConfiguration.loadConfiguration(new File(instance.getDataFolder(), "config.yml"));
        langConfig = YamlConfiguration.loadConfiguration(new File(instance.getDataFolder(), "lang.yml"));
    }

    public void checkNulls() {
        YamlConfiguration originalMainConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(instance.getResource("config.yml")));
        YamlConfiguration originalLangConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(instance.getResource("lang.yml")));
        for (String path : originalMainConfig.getKeys(true)) {
            if (mainConfig.get(path) == null && !path.contains("gui.items")) {
                Bukkit.getServer().getConsoleSender().sendMessage(textHandling.getFormatted("{P}&c Wartość &7" + path + "&c w &7config.yml&c nie istnieje!"));
            }
        }
        for (String path : originalLangConfig.getKeys(true)) {
            if (langConfig.get(path) == null) {
                Bukkit.getServer().getConsoleSender().sendMessage(textHandling.getFormatted("{P}&c Wartość &7" + path + "&c w &7lang.yml&c nie istnieje!"));
            }
        }
    }

}
