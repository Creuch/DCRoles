package me.creuch.dcroles;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class Database {

    public Database(DCRoles plugin) {
        this.instance = plugin;
    }

    private final DCRoles instance;
    private TextHandling textHandling;
    private Config config;


    public Connection getConnection() {
        Connection connection;
        HashMap<String, String> info = getDBInfo();
        try {

            if (info.get("type").equalsIgnoreCase("mysql")) {
                connection = DriverManager.getConnection("jdbc:mysql://" + info.get("address") + "/" + info.get("name"), info.get("username"), info.get("password"));
                Statement stmt = connection.createStatement();
                Integer createTable = stmt.executeUpdate("CREATE TABLE IF NOT EXISTS " + info.get("table") + " (username varchar(16), role varchar(16), code varchar(10), used boolean)");
                return connection;
            }
            connection = DriverManager.getConnection("jdbc:sqlite:" + instance.getDataFolder() + "/" + "DCRoles" + ".db");
            Statement stmt = connection.createStatement();
            Integer createTable = stmt.executeUpdate("CREATE TABLE IF NOT EXISTS " + info.get("table") + " (username varchar(16), role varchar(16), code varchar(10), used boolean)");
            return connection;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public HashMap<String, String> getDBInfo() {
        textHandling = new TextHandling(instance);
        config = new Config(instance);
        HashMap<String, String> info = new HashMap<>();
        info.put("type", config.getValue("mainConfig", "database.type"));
        info.put("table", config.getValue("mainConfig", "database.table"));
        if (info.get("type").equalsIgnoreCase("mysql")) {
            info.put("address", config.getValue("mainConfig", "database.address"));
            info.put("name", config.getValue("mainConfig", "database.name"));
            info.put("username", config.getValue("mainConfig", "database.username"));
            info.put("password", config.getValue("mainConfig", "database.password"));
        }
        boolean isAnyValueNull = false;
        for (Map.Entry<String, String> entry : info.entrySet()) {
            if (entry.getValue() == null || entry.getValue().isEmpty()) {
                isAnyValueNull = true;
                Bukkit.getConsoleSender().sendMessage(textHandling.getFormatted("{P}&c Dane bazy danych są puste &7(" + entry.getKey() + ")"));
            }
        }
        if (isAnyValueNull) {
            instance.getServer().getPluginManager().disablePlugin(instance);
            return null;
        }
        return info;
    }

    public HashMap<String, String> getUserData() {
        config = new Config(instance);
        try {
            HashMap<String, String> userData = new HashMap<>();
            OfflinePlayer p = instance.getPlayer();
            Connection connection = getConnection();
            Statement stmt = connection.createStatement();
            ResultSet userProfile = stmt.executeQuery("SELECT * FROM " + getDBInfo().get("table") + " WHERE username = '" + p.getName() + "'");
            userData.put("exists", "false");
            if (userProfile.next()) {
                if (!userProfile.isClosed() && userProfile.getString("code") != null) {
                    userData.put("exists", "true");
                }
                userData.put("code", userProfile.getString("code"));
                userData.put("role", userProfile.getString("role"));
                userData.put("used", userProfile.getString("used").replace("0", config.getValue("langConfig", "replacement.false")).replace("1", config.getValue("langConfig", "replacement.true")));
                connection.close();
                stmt.close();
            }
            return userData;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void removeUserProfile() {
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    OfflinePlayer p = instance.getPlayer();
                    Connection connection = getConnection();
                    Statement stmt = connection.createStatement();
                    Integer removeProfile = stmt.executeUpdate("DELETE FROM " + getDBInfo().get("table") + " WHERE USERNAME = '" + p.getName() + "'");
                    HashMap<String, String> userData = new HashMap<>();
                    userData.put("exists", "false");
                    userData.put("code", instance.getCode());
                    setUserData(userData);
                    connection.close();
                    stmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }.runTaskAsynchronously(instance);
    }

    public void setUserData(HashMap<String, String> updatedData) {
        config = new Config(instance);
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    TextHandling TextHandling = new TextHandling(instance);
                    OfflinePlayer p = instance.getPlayer();
                    Connection connection = getConnection();
                    Statement stmt = connection.createStatement();
                    if (updatedData.get("exists").equals("false")) {
                        Integer createUser = stmt.executeUpdate(String.format("INSERT INTO " + getDBInfo().get("table") + "(username, role, code, used) VALUES('%s', 'default', '%s', false)", p.getName(), updatedData.get("code")));
                        for (String message : config.getList("langConfig", "minecraft.user.profileCreated")) {
                            if (p.isOnline()) {
                                ((Player) p).sendMessage(TextHandling.getFormatted(message));
                            }
                            Bukkit.getServer().getConsoleSender().sendMessage(TextHandling.getFormatted(message));
                        }
                        Bukkit.getServer().getConsoleSender().sendMessage(TextHandling.getFormatted("&8» &7Nick: &f" + p.getName()));
                    }
                    updatedData.remove("exists");
                    for (Map.Entry<String, String> entry : updatedData.entrySet()) {
                        if (updatedData.get(entry.getKey()) != null) {
                            if (entry.getValue().equalsIgnoreCase("true")) {
                                Integer setValue = stmt.executeUpdate("UPDATE " + getDBInfo().get("table") + " SET " + entry.getKey() + " = true WHERE username = '" + p.getName() + "'");
                            } else if (entry.getValue().equalsIgnoreCase("false")) {
                                Integer setValue = stmt.executeUpdate("UPDATE " + getDBInfo().get("table") + " SET " + entry.getKey() + " = false WHERE username = '" + p.getName() + "'");
                            } else {
                                Integer setValue = stmt.executeUpdate("UPDATE " + getDBInfo().get("table") + " SET " + entry.getKey() + " = '" + entry.getValue() + "' WHERE username = '" + p.getName() + "'");
                            }
                        }
                    }
                    connection.close();
                    stmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }.runTaskAsynchronously(instance);
    }
}