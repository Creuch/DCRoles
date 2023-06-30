package me.creuch.dcroles;

import me.creuch.dcroles.bot.BotLoad;
import me.creuch.dcroles.commands.DCCode;
import me.creuch.dcroles.commands.DCMCode;
import me.creuch.dcroles.commands.DCReload;
import me.creuch.dcroles.events.onJoinEvent;
import me.creuch.dcroles.functions.Messages;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.*;
import java.util.List;
import java.util.Random;


public final class DCRoles extends JavaPlugin {

    public static DCRoles instance;
    // Database connection
    public static Connection conn = null;
    // Config file
    public static FileConfiguration config;
    // Plugin status
    public static boolean pluginEnabled = true;
    public static String disabledReason = null;

    public static boolean checkPluginStatus(CommandSender sender) {
        if (!pluginEnabled) {
            sender.sendMessage(Messages.getMessage(instance.getConfig().getString("pluginDisabled")));
            sender.sendMessage(Messages.getMessage("{P} " + disabledReason));
            return false;
        }
        return true;
    }

    public static Connection dbConnect(CommandSender sender) throws SQLException {
        assert checkPluginStatus(sender);
        Connection conn = null;
        if (instance.getConfig().getString("sql.type").equalsIgnoreCase("mysql")) {
            conn = DriverManager.getConnection("jdbc:mysql://" + instance.getConfig().getString("sql.address") + "/" + instance.getConfig().getString("sql.databaseName"), instance.getConfig().getString("sql.user"), instance.getConfig().getString("sql.password"));
        } else if (instance.getConfig().getString("sql.type").equalsIgnoreCase("sqlite")) {
            conn = DriverManager.getConnection("jdbc:sqlite:" + instance.getDataFolder() + ":DCRoles.db");
        } else {
            sender.sendMessage(Messages.getMessage(config.getString("messages.badSQLType")));
            pluginEnabled = false;
            disabledReason = "&cNiepoprawny typ bazy danych";
            return null;
        }
        return conn;
    }

    public static String getCode(Player p) {
        try {
            Connection conn = dbConnect(instance.getServer().getConsoleSender());
            Statement stmt = conn.createStatement();
            ResultSet getPlayer = stmt.executeQuery(String.format("SELECT * FROM discordRoles WHERE username = '%s'", p.getName()));
            String code = instance.getConfig().getString("messages.null");
            if (getPlayer.next()) {
                code = getPlayer.getString("code");
            }
            return code;
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void reloadPlugin(CommandSender sender) {
        instance.saveDefaultConfig();
        instance.reloadConfig();
        config = instance.getConfig();
        sender.sendMessage(Messages.getMessage(config.getString("messages.pluginEnabling")));
        instance.getCommand("dcreload").setExecutor(new DCMCode());
        instance.getCommand("dcreload").setExecutor(new DCReload());
        instance.getCommand("dccode").setExecutor(new DCCode());
        instance.getServer().getPluginManager().registerEvents(new onJoinEvent(), instance);
        BotLoad.login(sender);
        try {
            conn = dbConnect(sender);
            Statement stmt = conn.createStatement();
            Integer createTable = stmt.executeUpdate("CREATE TABLE IF NOT EXISTS discordRoles (username varchar(16), role varchar(16), code varchar(16), used boolean)");
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (pluginEnabled) {
            disabledReason = null;
            sender.sendMessage(Messages.getMessage(config.getString("messages.pluginEnabled").replace("{PLUGIN}", instance.getPluginMeta().getName() + " " + instance.getPluginMeta().getVersion())));
        } else {
            sender.sendMessage(Messages.getMessage(config.getString("messages.pluginDisabled")));
            sender.sendMessage(Messages.getMessage("{P} " + disabledReason));
        }
    }

    public static Long generateCode() {
        Random random = new Random();
        return random.nextLong(100000000, 999999999);
    }

    @Override
    public void onEnable() {
        instance = this;
        reloadPlugin(getServer().getConsoleSender());

    }

    @Override
    public void onDisable() {
        instance.getServer().getConsoleSender().sendMessage(Messages.getMessage(config.getString("messages.pluginDisabling")));
    }
}
