package me.creuch.dcroles;

import me.creuch.dcroles.commands.DCCode;
import me.creuch.dcroles.commands.DCMCode;
import me.creuch.dcroles.commands.DCMGui;
import me.creuch.dcroles.commands.DCReload;
import me.creuch.dcroles.discord.LoadBot;
import me.creuch.dcroles.events.onJoinEvent;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Random;

public final class DCRoles extends JavaPlugin {

    private static DCRoles instance;
    Database Database;
    TextHandling TextHandling;
    LoadBot LoadBot;
    // Config files
    private YamlConfiguration mainConfig;
    private YamlConfiguration langConfig;
    // Get player that all functions use at the moment
    private static OfflinePlayer activePlayer;
    // Get active inventory
    private static Inventory inventory;

    // Setters
    public void setPlayer(OfflinePlayer p) {
        activePlayer = p;
    }
    public void setInventory(Inventory inv) {
        inventory = inv;
    }

    // Getters
    public OfflinePlayer getPlayer() {
        return activePlayer;
    }
    public Inventory getInventory() {
        return inventory;
    }
    public DCRoles getInstance() {
        return instance;
    }
    public YamlConfiguration getMainConfig() {
        return mainConfig;
    }
    public YamlConfiguration getLangConfig() {
        return langConfig;
    }
    public String getCode() { return String.valueOf(new Random().nextLong(100000000, 999999999)); }


    // Plugin toggling methods
    public void reloadPlugin(CommandSender sender) {
        try {
            LoadBot = new LoadBot(this, sender);
            // Handle config files
            if(!new File(getDataFolder(), "config.yml").exists()) { saveResource("config.yml", true); }
            if(!new File(getDataFolder(), "lang.yml").exists()) { saveResource("lang.yml", true); }
            mainConfig = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "config.yml"));
            langConfig = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "lang.yml"));
            mainConfig.save(new File(getDataFolder(), "config.yml"));
            langConfig.save(new File(getDataFolder(), "lang.yml"));
            sender.sendMessage(TextHandling.getFormatted(langConfig.getString("minecraft.server.pluginEnabling")));
            Database.createTable(sender);
            // Send info about conn type
            HashMap<String, String> info = Database.getDBInfo();
            if (info.get("type").equalsIgnoreCase("mysql")) {
                sender.sendMessage(TextHandling.getFormatted(langConfig.getString("minecraft.server.dbConnType").replace("{TYPE}", "MySQL")));
            } else {
                sender.sendMessage(TextHandling.getFormatted(langConfig.getString("minecraft.server.dbConnType").replace("{TYPE}", "SQLite")));
            }
            LoadBot.login();
            sender.sendMessage(TextHandling.getFormatted(langConfig.getString("minecraft.server.pluginEnabled")));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onEnable() {
        instance = this;
        mainConfig = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "config.yml"));
        langConfig = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "lang.yml"));
        TextHandling = new TextHandling(this);
        // Check if BKCommonLib is installed
        if(Bukkit.getPluginManager().getPlugin("BKCommonLib") == null || !Bukkit.getPluginManager().getPlugin("BKCommonLib").isEnabled()){
            Bukkit.getConsoleSender().sendMessage(TextHandling.getFormatted("{P}&c&l Nie znaleziono pluginu BKCommonLib!"));
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        Database = new Database(this);
        // Load config files
        reloadPlugin(getServer().getConsoleSender());
        // Declare Commands
        this.getCommand("dcreload").setExecutor(new DCReload(this));
        this.getCommand("dcmgui").setExecutor(new DCMGui(this));
        this.getCommand("dccode").setExecutor(new DCCode(this));
        this.getCommand("dcmcode").setExecutor(new DCMCode(this));
        // Declare Tab Completion
        this.getCommand("dccode").setTabCompleter(new DCCode(this));
        this.getCommand("dcmcode").setTabCompleter(new DCMCode(this));
        // Declare Events
        this.getServer().getPluginManager().registerEvents(new onJoinEvent(this), this);
        this.getServer().getPluginManager().registerEvents(new me.creuch.dcroles.inventory.builderinventory.Events(this), this);
        this.getServer().getPluginManager().registerEvents(new me.creuch.dcroles.inventory.itemsettingsinventory.Events(this), this);
        this.getServer().getPluginManager().registerEvents(new me.creuch.dcroles.inventory.codemanageinventory.Events(this), this);

    }

    @Override
    public void onDisable() {
        Bukkit.getConsoleSender().sendMessage(TextHandling.getFormatted(langConfig.getString("minecraft.server.pluginDisabling")));
        try {
            if(!Database.getConnection(Bukkit.getConsoleSender()).isClosed()) {
                Database.getConnection(Bukkit.getConsoleSender()).close();
            }
            if(LoadBot.getApi() != null) {
                LoadBot.getApi().shutdownNow();
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }
}

