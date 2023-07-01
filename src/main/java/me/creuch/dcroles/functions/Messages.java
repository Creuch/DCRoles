package me.creuch.dcroles.functions;

import me.creuch.dcroles.DCRoles;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

public class Messages {
    private final static MiniMessage miniMessage = MiniMessage.miniMessage();

    public static Component getMessage(String message) {
        String msg = formatMessage(formatMessage(message));
        return miniMessage.deserialize(msg);
    }

    public static String formatMessage(String msg) {
        DCRoles instance = DCRoles.instance;
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
