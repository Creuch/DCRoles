package me.creuch.dcroles;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;


public class Database {

    private final DCRoles instance;
    private YamlConfiguration config;
    private YamlConfiguration langConfig;

    public Database(DCRoles plugin) {
        this.instance = plugin;
    }

    public Connection getConnection(CommandSender sender) {
        Connection connection;
        HashMap<String, String> info = getDBInfo();
        for(String s : info.values()) {
            if(s == null || s.isEmpty()) {
                // Plugin disabling mechanic
                return null;
            }
        }
        try {
            if (info.get("type").equalsIgnoreCase("mysql")) {
                connection = DriverManager.getConnection("jdbc:mysql://" + info.get("address") + "/" + info.get("name"), info.get("username"), info.get("password"));
            } else {
                connection = DriverManager.getConnection("jdbc:sqlite:" + instance.getDataFolder() + "/" + "DCRoles" + ".db");
            }
            return connection;
        } catch(SQLException e) {
            // Plugin disabling mechanic
            e.printStackTrace();
        }
        return null;

    }

    public HashMap<String, String> getDBInfo() {
        // Assign variables
        config = instance.getMainConfig();
        HashMap<String, String> info = new HashMap<>();
        info.put("type", "" + config.getString("database.type"));
        info.put("table", "" + config.getString("database.table"));
        if(info.get("type").equalsIgnoreCase("mysql")) {
            info.put("address", "" + config.getString("database.address"));
            info.put("name", "" + config.getString("database.name"));
            info.put("username", "" + config.getString("database.username"));
            info.put("password", "" + config.getString("database.password"));
        }
        return info;
    }

    public void createTable(CommandSender sender) {
        try {
            HashMap<String, String> info = getDBInfo();
            Connection connection = getConnection(sender);
            Statement stmt = connection.createStatement();
            Integer createTable = stmt.executeUpdate("CREATE TABLE IF NOT EXISTS " + info.get("table") + " (username varchar(16), role varchar(16), code varchar(10), used boolean)");
            connection.close();
            stmt.close();
        } catch(SQLException e) {
            // Plugin disabling mechanic
            e.printStackTrace();
        }
    }


    // User data get & set
    public HashMap<String, String> getUserData() {
        try {
            langConfig = instance.getLangConfig();
            HashMap<String, String> userData = new HashMap<>();
            OfflinePlayer p = instance.getPlayer();
            Connection connection = getConnection(Bukkit.getConsoleSender());
            Statement stmt = connection.createStatement();
            // Get data
            ResultSet userProfile = stmt.executeQuery("SELECT * FROM " + getDBInfo().get("table") + " WHERE username = '" + p.getName() + "'");
            if (userProfile.next()) {
                // Check if user's profile exists
                if (!userProfile.isClosed() && userProfile.getString("code") != null) {
                    userData.put("exists", "true");
                } else {
                    userData.put("exists", "false");
                    return userData;
                }
                userData.put("code", userProfile.getString("code"));
                userData.put("role", userProfile.getString("role"));
                userData.put("used", userProfile.getString("used").replace("0", langConfig.getString("replacement.false")).replace("1", langConfig.getString("replacement.true")));
                connection.close();
                stmt.close();
                return userData;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    return null;
    }

    public void removeUserProfile() {
        try {
            OfflinePlayer p = instance.getPlayer();
            Connection connection = getConnection(Bukkit.getConsoleSender());
            Statement stmt = connection.createStatement();
            Integer removeProfile = stmt.executeUpdate("DELETE FROM " + getDBInfo().get("table") + " WHERE USERNAME = '" + p.getName() + "'");
            // Generate new profile
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

    public void setUserData(HashMap<String, String> updatedData) {
        try {
            TextHandling TextHandling = new TextHandling(instance);
            langConfig = instance.getLangConfig();
            OfflinePlayer p = instance.getPlayer();
            Connection connection = getConnection(Bukkit.getConsoleSender());
            Statement stmt = connection.createStatement();
            // Create user if they don't exist
            if(updatedData.get("exists") == "false") {
                Integer createUser = stmt.executeUpdate(String.format("INSERT INTO " + getDBInfo().get("table") + "(username, role, code, used) VALUES('%s', 'default', '%s', false)", p.getName(), updatedData.get("code")));
                for (String message : langConfig.getStringList("minecraft.user.profileCreated")) {
                    if(p.isOnline()) {
                        ((Player) p).sendMessage(TextHandling.getFormatted(message));
                    }
                    Bukkit.getServer().getConsoleSender().sendMessage(TextHandling.getFormatted(message));
                }
                Bukkit.getServer().getConsoleSender().sendMessage(TextHandling.getFormatted("&8» &7Nick: &f" + p.getName()));
            }
            updatedData.remove("exists");
            for(Map.Entry<String, String> entry : updatedData.entrySet()) {
                if(updatedData.get(entry.getKey()) != null) {
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
}
