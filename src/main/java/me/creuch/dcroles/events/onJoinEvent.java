package me.creuch.dcroles.events;

import me.creuch.dcroles.DCRoles;
import me.creuch.dcroles.functions.Messages;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class onJoinEvent implements Listener {
    public static DCRoles instance;

    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent e) throws SQLException {
        instance = DCRoles.instance;
        Connection conn = DCRoles.dbConnect(instance.getServer().getConsoleSender());
        Statement stmt = conn.createStatement();
        ResultSet checkIfPlayerExist = stmt.executeQuery("SELECT username, role, code FROM discordRoles");
        boolean playerExist = false;
        while (checkIfPlayerExist.next()) {
            if (checkIfPlayerExist.getString("username").equalsIgnoreCase(e.getPlayer().getName())) {
                playerExist = true;
                break;
            }
        }
        if(!playerExist) {
            Long code = DCRoles.generateCode();
            List<String> messages = instance.getConfig().getStringList("messages.firstJoin");
            for(String s : messages) {
                s = s.replace("{RANK}", instance.getConfig().getString("messages.defualtRoleReplace"));
                s = s.replace("{CODE}", code.toString());
                e.getPlayer().sendMessage(Messages.getMessage(s));
            }
            Integer createPlayerRank = stmt.executeUpdate(String.format("INSERT INTO discordRoles(username, role, code, used) VALUES('%s', 'default', '%s', false)", e.getPlayer().getName(), code));
        }
    conn.close();
    }
}
