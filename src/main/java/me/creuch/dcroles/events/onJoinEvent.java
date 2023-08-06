package me.creuch.dcroles.events;

import me.creuch.dcroles.DCRoles;
import me.creuch.dcroles.Database;
import me.creuch.dcroles.TextHandling;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.HashMap;

public class onJoinEvent implements Listener {

    private final DCRoles instance;

    public onJoinEvent(DCRoles plugin) {
        this.instance = plugin;
    }

    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent e) {
        instance.setPlayer(e.getPlayer());
        Database Database = new Database(instance);
        HashMap<String, String> userData = Database.getUserData();
        if(userData != null && userData.get("exists") == "true") { instance.setPlayer(null); return; }
        String code = instance.getCode();
        userData = new HashMap<>();
        userData.put("exists", "false");
        userData.put("code", code);
        Database.setUserData(userData);
        instance.setPlayer(null);
    }
}
