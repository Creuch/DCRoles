package me.creuch.dcroles;

import me.creuch.dcroles.bot.BotLoad;
import me.creuch.dcroles.bot.GiveRankCommand;
import me.creuch.dcroles.bot.onGuildReadyEvent;
import me.creuch.dcroles.commands.DCCode;
import me.creuch.dcroles.commands.DCMCode;
import me.creuch.dcroles.commands.DCMGui;
import me.creuch.dcroles.commands.DCReload;
import me.creuch.dcroles.events.onInventoryEvent;
import me.creuch.dcroles.events.onJoinEvent;
import me.creuch.dcroles.functions.Database;
import me.creuch.dcroles.functions.Messages;
import me.creuch.dcroles.papi.PlaceholderAPI;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public final class DCRoles extends JavaPlugin implements @NotNull Listener {

    public DCRoles instance;
    // Config file
    public FileConfiguration config;
    // Plugin status
    public boolean pluginEnabled = true;
    public String disabledReason = null;
    // Other Class's instances
    Messages Messages = new Messages();
    BotLoad BotLoad = new BotLoad();
    Database Database = new Database();

    public boolean checkPluginStatus(CommandSender sender) throws SQLException {
        if (!pluginEnabled) {
            sender.sendMessage(Messages.getMessage(instance.getConfig().getString("pluginDisabled"), (Player) sender));
            sender.sendMessage(Messages.getMessage("{P} " + disabledReason, (Player) sender));
            return false;
        }
        return true;
    }

    public void reloadPlugin(CommandSender sender) throws SQLException {
        try {
            instance.saveDefaultConfig();
            instance.reloadConfig();
            config = instance.getConfig();
        } catch (Exception e) {
            if (e.getMessage().contains("InvalidConfigurationException")) {
                pluginEnabled = false;
                disabledReason = "&cBłąd w configu!";
                sender.sendMessage(Messages.getMessage(getDisabledReason(), (Player) sender));
                e.printStackTrace();
                return;
            } else {
                e.printStackTrace();
            }
        }
        sender.sendMessage(Messages.getMessage(config.getString("messages.pluginEnabling"), (Player) sender));
        BotLoad.login(sender);
        List<String> addedPerms = new ArrayList<>();
        for (String s : Objects.requireNonNull(instance.getConfig().getConfigurationSection("gui.items")).getKeys(false)) {
            String permissionName = instance.getConfig().getString("gui.items." + s + ".permission");
            if (!permissionName.equalsIgnoreCase("NONE")) {
                Permission perm = new Permission(permissionName, PermissionDefault.OP);
                perm.addParent("dcroles.*", true);
                for (Permission permission : instance.getServer().getPluginManager().getPermissions()) {
                    if (permission.getName().equalsIgnoreCase(permissionName) || instance.getServer().getPluginManager().getPermissions().contains(perm) && addedPerms.size() > 0 && !addedPerms.contains(permissionName)) {
                        break;
                    }
                    instance.getServer().getPluginManager().addPermission(perm);
                    addedPerms.add(permissionName);
                }
            }
        }
        if (pluginEnabled) {
            disabledReason = null;
            sender.sendMessage(Messages.getMessage(config.getString("messages.pluginEnabled").replace("{PLUGIN}", instance.getDescription().getName() + " " + instance.getDescription().getVersion()), (Player) sender));
        } else {
            sender.sendMessage(Messages.getMessage(config.getString("messages.pluginDisabled"), (Player) sender));
            sender.sendMessage(Messages.getMessage("{P} " + disabledReason, (Player) sender));
        }

    }

    public DCRoles getInstance() {
        return instance;
    }

    public String getDisabledReason() {
        return disabledReason;
    }

    public Long generateCode() {
        Random random = new Random();
        return random.nextLong(100000000, 999999999);
    }

    @Override
    public void onEnable() {
        instance = this;
        try {
            if (instance.getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
                instance.getServer().getPluginManager().registerEvents(this, this);
            }
            if (!Database.dbConnect(instance.getServer().getConsoleSender()).isClosed()) {
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
        instance.getCommand("dcmgui").setExecutor(new DCMGui());
        instance.getCommand("dcreload").setExecutor(new DCReload());
        instance.getCommand("dccode").setExecutor(new DCCode());
        instance.getServer().getPluginManager().registerEvents(new onJoinEvent(), instance);
        instance.getServer().getPluginManager().registerEvents(new onInventoryEvent(), instance);
        new PlaceholderAPI().register();
        try {
            reloadPlugin(getServer().getConsoleSender());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        BotLoad.api.addEventListener(new onGuildReadyEvent());
        BotLoad.api.addEventListener(new GiveRankCommand());

    }

    @Override
    public void onDisable() {
        try {
            instance.getServer().getConsoleSender().sendMessage(Messages.getMessage(config.getString("messages.pluginDisabling"), null));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        try {
            if (!Database.dbConnect(instance.getServer().getConsoleSender()).isClosed()) {
                Database.dbConnect(instance.getServer().getConsoleSender()).close();
                BotLoad.api.shutdown();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}

