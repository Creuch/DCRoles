package me.creuch.dcroles.discord;

import me.creuch.dcroles.DCRoles;
import me.creuch.dcroles.TextHandling;
import me.creuch.dcroles.Config;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.exceptions.InvalidTokenException;
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
    private Config config;
    private JDA api;
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
        try {
            config = new Config(instance);
            TextHandling = new TextHandling(instance);
            if (api != null) {
                api.shutdown();
            }
            api = JDABuilder.createDefault(config.getValue("mainConfig", "botSettings.token")).build();
            api.addEventListener(this);
            api.addEventListener(new Command(instance));
            commandSender.sendMessage(TextHandling.getFormatted(config.getValue("langConfig", "minecraft.server.botLoaded").replace("{BOTNAME", api.getSelfUser().getName())));
        } catch (InvalidTokenException e ) {
            instance.getServer().getConsoleSender().sendMessage(TextHandling.getFormatted("{P}&c Podany token jest niepoprawny"));
            instance.getServer().getPluginManager().disablePlugin(instance);
            api.shutdown();
        } catch (NullPointerException e ) {
            assert (config.getValue("mainConfig", "botSettings.token") == null);
            instance.getServer().getConsoleSender().sendMessage(TextHandling.getFormatted("{P}&c Podany token jest niepoprawny"));
            instance.getServer().getPluginManager().disablePlugin(instance);

        }
    }

}