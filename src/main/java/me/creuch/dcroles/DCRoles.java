package me.creuch.dcroles;

/*
ToDo
 - Make all guis customizable in separate file
 - Make custom methods for configs
 - Merge all inventory classes into one
 - Better message methods
 - Automate command requirements checking (if player, permission, arguments)
*/

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.ExtensionMethod;
import lombok.experimental.FieldDefaults;
import me.creuch.dcroles.commands.DCCode;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@ExtensionMethod({java.lang.String.class, TextFormat.class})
@FieldDefaults(level= AccessLevel.PRIVATE)
public final class DCRoles extends JavaPlugin {

    static DCRoles instance;
    @Getter @Setter OfflinePlayer activePlayer;
    @Getter @Setter Inventory inventory;
    @Getter TextFormat textFormat;
    @Getter Database database;
    public static Config config;

    public String generateCode() {
        return String.valueOf(new Random().nextLong(100000000, 999999999));
    }

    public void reloadPlugin() {
        config.loadConfigs();
        instance.getServer().sendMessage("{P}&9 Włączanie pluginu...".getFormatted());
        config.checkForNulls();
        database.createData();
        // Load the bot
        // Initalize database

    }

    @Override
    public void onEnable() {
        instance = this;
        textFormat = new TextFormat(this); database = new Database(this); config = new Config(this);
        // Register Commands and Tab Completions
        this.getServer().getCommandMap().register("dccode", new DCCode(this, "dccode"));
        // Set list of all config files
        List<File> configFiles = new ArrayList<>();
        configFiles.add(new File(getDataFolder(), "config.yml")); configFiles.add(new File(getDataFolder(), "guis.yml")); configFiles.add(new File(getDataFolder(), "lang.yml"));
        config.setConfigFiles(configFiles);
        reloadPlugin();
    }

    @Override
    public void onDisable() {
    }
}

