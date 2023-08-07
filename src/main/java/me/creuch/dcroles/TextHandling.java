package me.creuch.dcroles;

import net.dv8tion.jda.api.entities.Member;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.HashMap;

public class TextHandling {

    private final MiniMessage miniMessage = MiniMessage.miniMessage();
    private final DCRoles instance;
    public TextHandling(DCRoles instance) {
        this.instance = instance;
    }

    public Component getFormatted(String message) {
        try {
            if (instance.getPlayer() != null) {
                message = replaceColors(replaceColors(replacePlaceholders(message)));
            }
            return miniMessage.deserialize(replaceColors(replaceColors(message)));
        } catch (NullPointerException e) {
            return miniMessage.deserialize(replaceColors("{P}&c Pusta wiadomość! Zgłoś to administracji"));
        }
    }

    public String replaceDiscordPlaceholders(String pName, Member member, String message) {
        Database Database = new Database(instance);
        message = message.replace("{USER}", member.getUser().getName());
        message = message.replace("{DNAME}", member.getUser().getEffectiveName());
        message = message.replace("{MCUSER}", pName);
        instance.setPlayer(Bukkit.getOfflinePlayer(pName));
        HashMap<String, String> userData = Database.getUserData();
        instance.setPlayer(null);
        if(userData != null && userData.get("exists").equalsIgnoreCase("true")) {
            message = message.replace("{CODE}", userData.get("code"));
            message = message.replace("{ROLE}", userData.get("role"));
            message = message.replace("{USED}", userData.get("used"));
        }
        return message;
    }

    public String replacePlaceholders(String message) {
        Config config = new Config(instance);
        Database Database = new Database(instance);
        HashMap<String, String> userData = Database.getUserData();
        if(userData != null) {
            message = message.replace("{USER}", "" + instance.getPlayer().getName());
            message = message.replace("{CODE}", "" + userData.get("code"));
            message = message.replace("{ROLE}", "" + userData.get("role").replace("default", "" + config.getValue("langConfig", "replacement.defaultRole")));
            message = message.replace("{USED}", "" + userData.get("used"));
        }
        return message;
    }

    // Replace vanilla coloring to MiniMessage's one
    public String replaceColors(String message) {
        Config config = new Config(instance);
        message = message.replace("§", "&7");
        message = message.replace("&0", "<reset><black>");
        message = message.replace("&1", "<reset><dark_blue>");
        message = message.replace("&2", "<reset><dark_green>");
        message = message.replace("&3", "<reset><dark_aqua>");
        message = message.replace("&4", "<reset><dark_red>");
        message = message.replace("&5", "<reset><dark_purple>");
        message = message.replace("&6", "<reset><gold>");
        message = message.replace("&7", "<reset><gray>");
        message = message.replace("&8", "<reset><dark_gray>");
        message = message.replace("&9", "<reset><blue>");
        message = message.replace("&a", "<reset><green>");
        message = message.replace("&b", "<reset><aqua>");
        message = message.replace("&c", "<reset><red>");
        message = message.replace("&d", "<reset><light_purple>");
        message = message.replace("&e", "<reset><yellow>");
        message = message.replace("&f", "<reset><white>");
        message = message.replace("&l", "<bold>");
        message = message.replace("&m", "<st>");
        message = message.replace("&n", "<u>");
        message = message.replace("&o", "<italic>");
        message = message.replace("&r", "<reset>");
        message = message.replace("{P}", config.getValue("langConfig", "prefix"));
        return message;
    }

}
