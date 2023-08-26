package me.creuch.dcroles;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.ExtensionMethod;
import lombok.experimental.FieldDefaults;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@ExtensionMethod({java.lang.String.class, TextFormat.class})
@FieldDefaults(level=AccessLevel.PRIVATE)
public class Config {

    public Config(DCRoles instance) {
        this.instance = instance;
        this.textFormat = instance.getTextFormat();
    }
    final DCRoles instance;
    TextFormat textFormat;
    @Setter List<File> configFiles = new ArrayList<>();
    @Getter YamlConfiguration mainConfig, guiConfig, langConfig;

    public void loadConfigs() {
        if(configFiles.size() > 0) {
            for(File file : configFiles) {
                if(!file.exists()) {
                    instance.saveResource(file.getName(), true);
                    switch (file.getName()) {
                        case "config.yml":
                            mainConfig = YamlConfiguration.loadConfiguration(new File("config.yml"));
                        case "guis.yml":
                            guiConfig = YamlConfiguration.loadConfiguration(new File("guis.yml"));
                        case "lang.yml":
                            langConfig = YamlConfiguration.loadConfiguration(new File("lang.yml"));
                    }
                    instance.getServer().sendMessage(("{P}&7 Stworzono config&f " + file.getName()).getFormatted());
                }
            }
        }


    }

    public void checkForNulls() {
        for(File file : configFiles) {
            YamlConfiguration originalConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(instance.getResource(file.getName())));
            YamlConfiguration config = YamlConfiguration.loadConfiguration(new File(file.getName()));
            for (String path : originalConfig.getKeys(true)) {
                if (config.get(path) == null && !path.contains("gui")) {
                     Bukkit.getServer().getConsoleSender().sendMessage(("{P}&c Wartość &7" + path + "&c w &7config.yml&c nie istnieje!").getFormatted());
                }
            }

        }
    }

    public void saveConfig(String config) {
        try {
            FileConfiguration fileConfig = YamlConfiguration.loadConfiguration(new File(instance.getDataFolder(), config));
            fileConfig.save(new File(instance.getDataFolder(), config));
            loadConfigs();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ConfigurationSection getSection(String config, String path) {
        FileConfiguration fileConfig = YamlConfiguration.loadConfiguration(new File(instance.getDataFolder(), config));
        ConfigurationSection section = fileConfig.getConfigurationSection(path);
        if(section == null || section.getKeys(true).isEmpty()) {
            section = YamlConfiguration.loadConfiguration(new InputStreamReader(instance.getResource(config))).getConfigurationSection(path);
            // Send null value message (used original config)
            if(section == null || section.getKeys(true).isEmpty()) {
                return null;
            }
        }
        return section;
    }

    public List<String> getList(String config, String path) {
        FileConfiguration fileConfig = YamlConfiguration.loadConfiguration(new File(instance.getDataFolder(), config));
        List<String> list = fileConfig.getStringList(path);
        if(list.isEmpty()) {
            list = YamlConfiguration.loadConfiguration(new InputStreamReader(instance.getResource(config))).getStringList(path);
            // Send null value message (used original config)
            if(list.isEmpty()) {
                list.add("null");
            }
        }
        return list;
    }

    public String getValue(String config, String path) {
        FileConfiguration fileConfig = YamlConfiguration.loadConfiguration(new File(instance.getDataFolder(), config));
        String value = fileConfig.getString(path);
        if(value == null) {
            value = YamlConfiguration.loadConfiguration(new InputStreamReader(instance.getResource(config))).getString(path);
            // Send null value message (used original config)
            if(value == null) {
                value = "null";
            }
        }
        return value;
    }




}
