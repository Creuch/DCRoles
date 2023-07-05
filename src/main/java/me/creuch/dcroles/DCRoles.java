package me.creuch.dcroles;

import me.creuch.dcroles.bot.BotLoad;
import me.creuch.dcroles.bot.GiveRankCommand;
import me.creuch.dcroles.bot.onGuildReadyEvent;
import me.creuch.dcroles.commands.DCCode;
import me.creuch.dcroles.commands.DCMCode;
import me.creuch.dcroles.commands.DCReload;
import me.creuch.dcroles.events.onInventoryClick;
import me.creuch.dcroles.events.onJoinEvent;
import me.creuch.dcroles.functions.Database;
import me.creuch.dcroles.functions.Messages;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import java.sql.*;
import java.util.Random;

public final class DCRoles extends JavaPlugin {

    public static DCRoles instance;
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

    public static void reloadPlugin(CommandSender sender) {
        instance.saveDefaultConfig();
        instance.reloadConfig();
        config = instance.getConfig();
        sender.sendMessage(Messages.getMessage(config.getString("messages.pluginEnabling")));
        BotLoad.login(sender);
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
        try {
            if(!Database.dbConnect(instance.getServer().getConsoleSender()).isClosed()) {
                Database.dbConnect(instance.getServer().getConsoleSender()).close();
            }
            Connection conn = Database.dbConnect(instance.getServer().getConsoleSender());
            Statement stmt = conn.createStatement();
            Integer createTable = stmt.executeUpdate("CREATE TABLE IF NOT EXISTS discordRoles (username varchar(16), role varchar(16), code varchar(16), used boolean)");
            conn.close();
            stmt.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        instance.getCommand("dcmcode").setExecutor(new DCMCode());
        instance.getCommand("dcmcode").setTabCompleter(new DCMCode());
        instance.getCommand("dcreload").setExecutor(new DCReload());
        instance.getCommand("dccode").setExecutor(new DCCode());
        instance.getServer().getPluginManager().registerEvents(new onJoinEvent(), instance);
        instance.getServer().getPluginManager().registerEvents(new onInventoryClick(), instance);
        reloadPlugin(getServer().getConsoleSender());
        BotLoad.api.addEventListener(new onGuildReadyEvent());
        BotLoad.api.addEventListener(new GiveRankCommand());
    }

    @Override
    public void onDisable() {
        instance.getServer().getConsoleSender().sendMessage(Messages.getMessage(config.getString("messages.pluginDisabling")));
        try {
            if(!Database.dbConnect(instance.getServer().getConsoleSender()).isClosed()) {
                Database.dbConnect(instance.getServer().getConsoleSender()).close();
                BotLoad.api.shutdown();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}

