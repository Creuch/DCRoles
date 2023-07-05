package me.creuch.dcroles.functions;

import me.creuch.dcroles.DCRoles;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.sql.*;
import java.util.Objects;

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

    public static void updateUser(String username, String info, String data, CommandSender sender) {
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

    public static String getData(OfflinePlayer p, String type) throws SQLException {
        try {
            if (dbConnect(DCRoles.instance.getServer().getConsoleSender()).isClosed()) {
                dbConnect(DCRoles.instance.getServer().getConsoleSender()).close();
            }
            Connection conn = dbConnect(DCRoles.instance.getServer().getConsoleSender());
            Statement stmt = conn.createStatement();
            ResultSet getPlayer = stmt.executeQuery(String.format("SELECT * FROM discordRoles WHERE username = '%s'", p.getName()));
            String data = DCRoles.instance.getConfig().getString("text.null");
            if (Objects.equals(type, "existence")) {
                ResultSet getAllPlayers = stmt.executeQuery("SELECT username, role, code FROM discordRoles");
                while (getAllPlayers.next()) {
                    if (Objects.equals(getAllPlayers.getString("username"), p.getPlayer().getName())) {
                        conn.close();
                        stmt.close();
                        getAllPlayers.close();
                        getPlayer.close();
                        return "true";
                    }
                }
                conn.close();
                stmt.close();
                getAllPlayers.close();
                getPlayer.close();
                return "false";
            } else if (Objects.equals(type, "code")) {
                if (getPlayer.next()) {
                    data = getPlayer.getString("code");
                }
            } else if (Objects.equals(type, "rank")) {
                if (getPlayer.next()) {
                    data = getPlayer.getString("role");
                }
            } else if (Objects.equals(type, "used")) {
                if (getPlayer.next()) {
                    data = getPlayer.getString("used");
                }
            }
            conn.close();
            stmt.close();
            getPlayer.close();
            return data;
        } catch (SQLException e) {
            e.printStackTrace();
            if (dbConnect(DCRoles.instance.getServer().getConsoleSender()).isClosed()) {
                dbConnect(DCRoles.instance.getServer().getConsoleSender()).close();
            }
        }
        return null;
    }
}
