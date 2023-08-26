package me.creuch.dcroles;

import lombok.AccessLevel;
import lombok.experimental.ExtensionMethod;
import lombok.experimental.FieldDefaults;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
* ToDo
*  - ReplacePlaceholders
*/

@FieldDefaults(level= AccessLevel.PRIVATE)
public class TextFormat {

    public TextFormat(DCRoles instance) {
        this.instance = instance;
        this.database = instance.getDatabase();
        config = DCRoles.config;
    }

    static final MiniMessage miniMessage = MiniMessage.miniMessage();
    Database database;
    static Config config;
    final DCRoles instance;
    

    public static String getAsString(Component component) {
        return miniMessage.serialize(component);
    }

    public static Component getFormatted(String string) {
        return colorString(string);
    }

    public static Component colorString(String msg) {
        msg = convertLegacy(msg);
        return miniMessage.deserialize(msg).decoration(TextDecoration.ITALIC, false);
    }

    public static String convertLegacy(String string) {
        Pattern pattern = Pattern.compile("&#([0-9a-fA-F]{6})");
        Matcher matcher = pattern.matcher(string);
        StringBuffer result = new StringBuffer();
        while (matcher.find()) {
            String replacement = "<#" + matcher.group(1) + ">";
            matcher.appendReplacement(result, replacement);
        }
        matcher.appendTail(result);
        string = result.toString();
        string = string
                .replace("{P}", config.getValue("lang.yml", "replacement.prefix"))
                .replace("&0", "<black>")
                .replace("&1", "<dark_blue>")
                .replace("&2", "<dark_green>")
                .replace("&3", "<dark_aqua>")
                .replace("&4", "<dark_red>")
                .replace("&5", "<dark_purple>")
                .replace("&6", "<gold>")
                .replace("&7", "<gray>")
                .replace("&8", "<dark_gray>")
                .replace("&9", "<blue>")
                .replace("&a", "<green>")
                .replace("&b", "<aqua>")
                .replace("&c", "<red>")
                .replace("&d", "<light_purple>")
                .replace("&e", "<yellow>")
                .replace("&f", "<white>")
                .replace("&n", "<underlined>")
                .replace("&m", "<strikethrough>")
                .replace("&l", "<bold>")
                .replace("&k", "<obfuscated>")
                .replace("&o", "<italic>")
                .replace("&r", "<reset>");
        return string;
    }



}
