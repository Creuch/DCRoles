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
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Random;

public final class DCRoles extends JavaPlugin {

    private static DCRoles instance;
    private static OfflinePlayer activePlayer;
    private static Inventory inventory;
    private TextHandling textHandling;
    private Database database;
    private Config config;

    public void setPlayer(OfflinePlayer p) {
        activePlayer = p;
    }

    public void setInventory(Inventory inv) {
        inventory = inv;
    }

    public OfflinePlayer getPlayer() {
        return activePlayer;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public String getCode() {
        return String.valueOf(new Random().nextLong(100000000, 999999999));
    }


    public void reloadPlugin(CommandSender sender) {
        config.loadConfigs();
        sender.sendMessage(textHandling.getFormatted(config.getValue("langConfig", "minecraft.server.pluginEnabling")));
        config.checkNulls();
        HashMap<String, String> info = database.getDBInfo();
        if (info.get("type").equalsIgnoreCase("mysql")) {
            sender.sendMessage(textHandling.getFormatted(config.getValue("langConfig", "minecraft.server.dbConnType").replace("{TYPE}", "MySQL")));
        } else {
            sender.sendMessage(textHandling.getFormatted(config.getValue("langConfig", "minecraft.server.dbConnType").replace("{TYPE}", "SQLite")));
        }
        new LoadBot(instance, Bukkit.getConsoleSender()).login();
        sender.sendMessage(textHandling.getFormatted(config.getValue("langConfig", "minecraft.server.pluginEnabled")));
    }


    @Override
    public void onEnable() {
        instance = this;
        textHandling = new TextHandling(this);
        database = new Database(this);
        config = new Config(this);
        if (Bukkit.getPluginManager().getPlugin("BKCommonLib") == null || !Bukkit.getPluginManager().getPlugin("BKCommonLib").isEnabled()) {
            Bukkit.getConsoleSender().sendMessage(textHandling.getFormatted("{P}&c&l Nie znaleziono pluginu BKCommonLib!"));
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        reloadPlugin(getServer().getConsoleSender());
        // Register commands
        this.getCommand("dcreload").setExecutor(new DCReload(this));
        this.getCommand("dcmgui").setExecutor(new DCMGui(this));
        this.getCommand("dccode").setExecutor(new DCCode(this));
        this.getCommand("dcmcode").setExecutor(new DCMCode(this));
        // Register Tab Completions
        this.getCommand("dccode").setTabCompleter(new DCCode(this));
        this.getCommand("dcmcode").setTabCompleter(new DCMCode(this));
        // Register events
        this.getServer().getPluginManager().registerEvents(new onJoinEvent(this), this);
        this.getServer().getPluginManager().registerEvents(new me.creuch.dcroles.inventory.builderinventory.Events(this), this);
        this.getServer().getPluginManager().registerEvents(new me.creuch.dcroles.inventory.itemsettingsinventory.Events(this), this);
        this.getServer().getPluginManager().registerEvents(new me.creuch.dcroles.inventory.codemanageinventory.Events(this), this);
    }

    @Override
    public void onDisable() {
        Bukkit.getConsoleSender().sendMessage(textHandling.getFormatted(config.getValue("langConfig", "minecraft.server.plguinDisabling")));
        try {
            if (!database.getConnection().isClosed()) {
                database.getConnection().close();
            }
            if (new LoadBot(instance, Bukkit.getConsoleSender()).getApi() != null) {
                new LoadBot(instance, Bukkit.getConsoleSender()).getApi().shutdownNow();
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }
}

