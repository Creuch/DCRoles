package me.creuch.dcroles.bot;

import me.creuch.dcroles.DCRoles;
import me.creuch.dcroles.functions.Messages;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.exceptions.InvalidTokenException;
import org.bukkit.command.CommandSender;

public class BotLoad {

    public static JDA api;

    public static void login(CommandSender sender) {
        assert DCRoles.checkPluginStatus(sender);
        DCRoles instance = DCRoles.instance;
        String botToken = instance.getConfig().getString("bot.token");
        if(botToken == null || botToken.isEmpty()) {
            DCRoles.pluginEnabled = false;
            DCRoles.disabledReason = "&cBrak tokena bota";
            sender.sendMessage(Messages.getMessage(DCRoles.disabledReason));
        } else {
            try {
                api = JDABuilder.createDefault(botToken).build();
                api.addEventListener(new onGuildReadyEvent());
            } catch (InvalidTokenException e) {
                if(!e.toString().isEmpty()) {
                    DCRoles.pluginEnabled = false;
                    DCRoles.disabledReason = "&cNiepoprawny token bota";
                    sender.sendMessage(Messages.getMessage(DCRoles.disabledReason));
                    return;
                }
            }
            sender.sendMessage(Messages.getMessage(instance.getConfig().getString("messages.botLoaded").replace("{BOTNAME}", api.getSelfUser().getName())));

        }
    }
}
