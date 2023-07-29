package me.creuch.dcroles.functions;

import me.creuch.dcroles.DCRoles;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.sql.*;
import java.util.HashMap;

public class Database {

    public static Connection dbConnect(CommandSender sender) throws SQLException {
        assert DCRoles.checkPluginStatus(sender);
        Connection conn;
        if (DCRoles.instance.getConfig().getString("sql.type").equalsIgnoreCase("mysql")) {
            conn = DriverManager.getConnection("jdbc:mysql://" + DCRoles.instance.getConfig().getString("sql.dbAddress") + "/" + DCRoles.instance.getConfig().getString("sql.dbName"), DCRoles.instance.getConfig().getString("sql.dbUsername"), DCRoles.instance.getConfig().getString("sql.dbPassword"));
        } else if (DCRoles.instance.getConfig().getString("sql.type").equalsIgnoreCase("sqlite")) {
            conn = DriverManager.getConnection("jdbc:sqlite:" + DCRoles.instance.getDataFolder() + ":DCRoles.db");
        } else {
            sender.sendMessage(Messages.getMessage(DCRoles.config.getString("messages.badSQLType")));
            DCRoles.pluginEnabled = false;
            DCRoles.disabledReason = "&cNiepoprawny typ bazy danych";
            return null;
        }
        return conn;
    }

    public static void updateUser(String username, String info, String data, CommandSender sender) throws SQLException {
        DCRoles instance = DCRoles.instance;
        try {
            if (!dbConnect(instance.getServer().getConsoleSender()).isClosed()) {
                dbConnect(instance.getServer().getConsoleSender()).close();
            }
            Connection conn = dbConnect(sender);
            Statement stmt = conn.createStatement();
            // Create player in database
            if (info.equalsIgnoreCase("createUser")) {
                Integer sql = stmt.executeUpdate(String.format("INSERT INTO discordRoles(username, role, code, used) VALUES('%s', 'default', '%s', false)", username, data));
                conn.close();
                stmt.close();
                // Updates user's code in database
            } else if (info.equalsIgnoreCase("updateCode")) {
                Integer sql = stmt.executeUpdate(String.format("UPDATE discordRoles SET code = '%s' WHERE username = '%s'", data, username));
                conn.close();
                stmt.close();
                // Resets usage of user's code in database
            } else if (info.equalsIgnoreCase("resetUsage")) {
                Integer sql = stmt.executeUpdate(String.format("UPDATE discordRoles SET used = 0 WHERE username = '%s'", username));
                conn.close();
                stmt.close();
                // Removes user's data from database
            } else if (info.equalsIgnoreCase("removeUser")) {
                Integer removeUser = stmt.executeUpdate(String.format("DELETE FROM discordRoles WHERE username = '%s'", username));
                Integer createUser = stmt.executeUpdate(String.format("INSERT INTO discordRoles(username, role, code, used) VALUES('%s', 'default', '%s', false)", username, data));
                conn.close();
                stmt.close();
                // Sets user's rank in database
            } else if (info.equalsIgnoreCase("setRank")) {
                Integer sql = stmt.executeUpdate(String.format("UPDATE discordRoles SET role = '%s' WHERE username = '%s'", data, username));
                conn.close();
                stmt.close();
                // Toggles user's code usage in database
            } else if (info.equalsIgnoreCase("updateUsage")) {
                ResultSet getPlayer = stmt.executeQuery(String.format("SELECT used FROM discordRoles WHERE username = '%s'", username));
                boolean used = false;
                if (getPlayer.next()) {
                    used = getPlayer.getBoolean("used");
                }
                if(String.valueOf(used).equals("true")) {
                    Integer sql = stmt.executeUpdate(String.format("UPDATE discordRoles SET used = 0 WHERE username = '%s'", username));
                } else {
                    Integer sql = stmt.executeUpdate(String.format("UPDATE discordRoles SET used = 1 WHERE username = '%s'", username));
                }
                getPlayer.close();
                conn.close();
                stmt.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static HashMap<String, String> getUserProfile(OfflinePlayer p) throws SQLException {
        try {
            DCRoles instance = DCRoles.instance;
            HashMap<String, String> user = new HashMap<>();
            Connection conn = dbConnect(DCRoles.instance.getServer().getConsoleSender());
            Statement stmt = conn.createStatement();
            ResultSet playerProfile = stmt.executeQuery(String.format("SELECT * FROM discordRoles WHERE username = '%s' limit 0, 1", p.getName()));
            // Check if profile of the player exists
            if(!playerProfile.isClosed()) { user.put("exists", "true"); } else { user.put("exists", "false"); return user; }
            user.put("code", playerProfile.getString("code"));
            user.put("role", playerProfile.getString("role"));
            user.put("used", playerProfile.getString("used").replace("1", instance.getConfig().getString("text.true")).replace("0", instance.getConfig().getString("text.false")));
            // Close all connections
            conn.close(); stmt.close(); playerProfile.close();
            return user;
        } catch (SQLException e) {
            e.printStackTrace();
            if (dbConnect(DCRoles.instance.getServer().getConsoleSender()).isClosed()) {
                dbConnect(DCRoles.instance.getServer().getConsoleSender()).close();
            }
        }
        return null;
    }
}
