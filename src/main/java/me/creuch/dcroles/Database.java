package me.creuch.dcroles;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.ExtensionMethod;
import lombok.experimental.FieldDefaults;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

@ExtensionMethod({java.lang.String.class, TextFormat.class})
@FieldDefaults(level=AccessLevel.PRIVATE)
public class Database {

    public Database(DCRoles instance) {
        this.instance = instance;
        this.config = DCRoles.config;
        this.textFormat = instance.getTextFormat();
    }

    final DCRoles instance;
    final Config config;
    final TextFormat textFormat;
    @Getter HashMap<String, String> dbData;
    Connection connection;

    public Connection getConneciton() {
        try {
            if (connection != null && !connection.isClosed()) connection.close();
            String dbType = config.getValue("config.yml", "database.type");
            HashMap<String, String> data = dbData;
            if(dbType.equalsIgnoreCase("mysql")) {
                connection = DriverManager.getConnection("jdbc:mysql://" + data.get("address") + "/" + data.get("name"), data.get("username"), data.get("password"));
            } else {
                connection = DriverManager.getConnection("jdbc:sqlite:" + instance.getDataFolder() + "/DCRoles.db");
            }
            if(connection != null && !connection.isClosed() && connection.isValid(3)) {
                Statement stmt = connection.createStatement();
                Integer createTable = stmt.executeUpdate("CREATE TABLE IF NOT EXISTS " + data.get("table") + " (username varchar(16), role varchar(16), code varchar(10), used boolean)");
                stmt.close();
                return connection;
            } else {
                Bukkit.getServer().sendMessage("{P}&c Połączenie z bazą danych nie powiodło się!".getFormatted());
            }
        } catch (SQLException e) {
            Bukkit.getServer().sendMessage("{P}&c Połączenie z bazą danych nie powiodło się!".getFormatted());
            e.printStackTrace();
        }
        return null;
    }

    public void createData() {
        HashMap<String, String> data = new HashMap<>();
        data.put("type", config.getValue("config.yml", "database.type"));
        data.put("table", config.getValue("config.yml", "database.table"));
        if (data.get("type").equalsIgnoreCase("mysql")) {
            data.put("address", config.getValue("config.yml", "database.address"));
            data.put("name", config.getValue("config.yml", "database.name"));
            data.put("username", config.getValue("config.yml", "database.username"));
            data.put("password", config.getValue("config.yml", "database.password"));
        }
        dbData = data;
    }

}