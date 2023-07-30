package me.creuch.dcroles.functions;

import me.clip.placeholderapi.PlaceholderAPI;
import me.creuch.dcroles.DCRoles;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.HashMap;

public class Messages {
    private final MiniMessage miniMessage = MiniMessage.miniMessage();
    DCRoles DCRoles = new DCRoles();
    me.creuch.dcroles.functions.Database Database = new Database();

    public Component getMessage(String message, OfflinePlayer p) throws SQLException {
        String msg = formatMessage(formatMessage(message, p), p);
        return miniMessage.deserialize(msg);
    }

    public String replaceMinecraftText(String msg, OfflinePlayer p) throws SQLException {
        String replacedText = msg;
        HashMap<String, String> userProfile = Database.getUserProfile(p);
        replacedText = replacedText.replace("{USER}", p.getName());
        replacedText = replacedText.replace("{CODE}", userProfile.get("code"));
        replacedText = replacedText.replace("{ROLE}", userProfile.get("role"));
        replacedText = replacedText.replace("{USED}", userProfile.get("used"));
        return replacedText;
    }

    public String formatMessage(String msg, OfflinePlayer p) throws SQLException {
        DCRoles instance = DCRoles.getInstance();
        if (p != null) {
            msg = replaceMinecraftText(msg, p);
            msg = PlaceholderAPI.setPlaceholders(p, msg);
        }
        msg = msg.replace("§", "&7");
        msg = msg.replace("&0", "<reset><black>");
        msg = msg.replace("&1", "<reset><dark_blue>");
        msg = msg.replace("&2", "<reset><dark_green>");
        msg = msg.replace("&3", "<reset><dark_aqua>");
        msg = msg.replace("&4", "<reset><dark_red>");
        msg = msg.replace("&5", "<reset><dark_purple>");
        msg = msg.replace("&6", "<reset><gold>");
        msg = msg.replace("&7", "<reset><gray>");
        msg = msg.replace("&8", "<reset><dark_gray>");
        msg = msg.replace("&9", "<reset><blue>");
        msg = msg.replace("&a", "<reset><green>");
        msg = msg.replace("&b", "<reset><aqua>");
        msg = msg.replace("&c", "<reset><red>");
        msg = msg.replace("&d", "<reset><light_purple>");
        msg = msg.replace("&e", "<reset><yellow>");
        msg = msg.replace("&f", "<reset><white>");
        msg = msg.replace("&l", "<bold>");
        msg = msg.replace("&m", "<st>");
        msg = msg.replace("&n", "<u>");
        msg = msg.replace("&o", "<italic>");
        msg = msg.replace("&r", "<reset>");
        msg = msg.replace("{P}", instance.getConfig().getString("text.prefix"));
        return msg;
    }
}
