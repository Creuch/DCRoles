package me.creuch.dcroles.papi;

import me.creuch.dcroles.DCRoles;
import me.creuch.dcroles.functions.Database;
import me.creuch.dcroles.functions.Messages;
import org.bukkit.OfflinePlayer;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;

import java.sql.SQLException;
import java.util.HashMap;

public class PlaceholderAPI extends PlaceholderExpansion {

    DCRoles DCRoles = new DCRoles();
    me.creuch.dcroles.functions.Database Database = new Database();
    private DCRoles instance;

    @Override
    public String getAuthor() {
        return "_Creuch";
    }

    @Override
    public String getIdentifier() {
        return "dcr";
    }

    @Override
    public String getVersion() {
        return "1.1";
    }

    @Override
    public boolean persist() {
        return true; // This is required or else PlaceholderAPI will unregister the Expansion on reload
    }

    @Override
    public String onRequest(OfflinePlayer player, String params) {
        instance = DCRoles.getInstance();
        try {
            HashMap<String,String> userProfile = Database.getUserProfile(player);
            if(userProfile.get("exists") == "false") { return null; }
            if (params.equalsIgnoreCase("code")) {
                return userProfile.get("code");
            }
            if (params.equalsIgnoreCase("role")) {
                return userProfile.get("role");
            }
            if (params.equalsIgnoreCase("used")) {
                return userProfile.get("used").replace("true", instance.getConfig().getString("text.true")).replace("false", instance.getConfig().getString("text.false"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
