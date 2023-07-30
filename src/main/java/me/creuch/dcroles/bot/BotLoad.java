package me.creuch.dcroles.bot;

import me.creuch.dcroles.DCRoles;
import me.creuch.dcroles.functions.Messages;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.exceptions.InvalidTokenException;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.SQLException;

public class BotLoad {

    public JDA api;
    DCRoles DCRoles = new DCRoles();
    Messages Messages = new Messages();

    public JDA getApi() {
        return api;
    }

    public void login(CommandSender sender) throws SQLException {
        assert DCRoles.checkPluginStatus(sender);
        DCRoles instance = DCRoles.instance;
        String botToken = instance.getConfig().getString("bot.token");
        if(botToken == null || botToken.isEmpty()) {
            DCRoles.pluginEnabled = false;
            DCRoles.disabledReason = "&cBrak tokena bota";
            sender.sendMessage(Messages.getMessage(DCRoles.getDisabledReason(), (Player) sender));
        } else {
            try {
                api = JDABuilder.createDefault(botToken).build();
            } catch (InvalidTokenException e) {
                if(!e.toString().isEmpty()) {
                    DCRoles.pluginEnabled = false;
                    DCRoles.disabledReason = "&cNiepoprawny token bota";
                    sender.sendMessage(Messages.getMessage(DCRoles.getDisabledReason(), (Player) sender));
                    return;
                }
            }
            sender.sendMessage(Messages.getMessage(instance.getConfig().getString("messages.botLoaded").replace("{BOTNAME}", api.getSelfUser().getName()), (Player) sender));

        }
    }
}
