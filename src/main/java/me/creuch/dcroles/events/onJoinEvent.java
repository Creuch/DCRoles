package me.creuch.dcroles.events;

import me.creuch.dcroles.DCRoles;
import me.creuch.dcroles.functions.Database;
import me.creuch.dcroles.functions.Messages;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

public class onJoinEvent implements Listener {
    public static DCRoles instance;

    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent e) throws SQLException {
        instance = DCRoles.instance;
        HashMap<String, String> userProfile = Database.getUserProfile(Bukkit.getOfflinePlayer(e.getPlayer().getName()));
        if(userProfile.get("exists") == "true") { return; }
        Long code = DCRoles.generateCode();
        List<String> messages = instance.getConfig().getStringList("messages.firstJoin");
        for (String s : messages) {
            s = s.replace("{RANK}", instance.getConfig().getString("text.defualtRoleReplace"));
            s = s.replace("{CODE}", code.toString());
            e.getPlayer().sendMessage(Messages.getMessage(s));
        }
        Database.updateUser(e.getPlayer().getName(), "createUser", code.toString(), instance.getServer().getConsoleSender());
    }
}
