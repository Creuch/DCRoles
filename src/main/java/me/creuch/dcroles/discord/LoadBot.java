package me.creuch.dcroles.discord;

import me.creuch.dcroles.DCRoles;
import me.creuch.dcroles.TextHandling;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;

import javax.security.auth.login.LoginException;
import java.util.ArrayList;
import java.util.List;

public class LoadBot extends ListenerAdapter {

    private final DCRoles instance;
    private TextHandling TextHandling;
    private Command Command;
    private YamlConfiguration config;
    private YamlConfiguration langConfig;
    private static JDA api;
    private final CommandSender commandSender;

    public LoadBot(DCRoles instance, CommandSender commandSender) {
        this.instance = instance;
        this.commandSender = commandSender;
    }

    @Override
    public void onGuildReady(GuildReadyEvent e) {
        Command = new Command(instance);
        List<CommandData> commandDataList = new ArrayList<>();
        commandDataList.add(Command.getCommand());
        e.getGuild().updateCommands().addCommands(commandDataList).queue();
    }

    public JDA getApi() {
        return api;
    }

    public void login() {
        config = instance.getMainConfig();
        langConfig = instance.getLangConfig();
        TextHandling = new TextHandling(instance);
        if (api != null) {
            api.shutdown();
        }
        api = JDABuilder.createDefault(config.getString("botSettings.token")).build();
        api.addEventListener(this);
        api.addEventListener(new Command(instance));
        commandSender.sendMessage(TextHandling.getFormatted(langConfig.getString("minecraft.server.botLoaded").replace("{BOTNAME", api.getSelfUser().getName())));
    }

}
