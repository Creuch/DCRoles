package me.creuch.dcroles.discord;

import me.creuch.dcroles.DCRoles;
import me.creuch.dcroles.Database;
import me.creuch.dcroles.TextHandling;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.exceptions.HierarchyException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;

import java.awt.*;
import java.util.HashMap;
import java.util.List;

public class Command extends ListenerAdapter {

    private final DCRoles instance;
    private Database Database;
    private TextHandling TextHandling;
    private YamlConfiguration config;
    private YamlConfiguration langConfig;

    public Command(DCRoles instance) {
        this.instance = instance;
    }

    public CommandData getCommand() {
        config = instance.getMainConfig();
        OptionData name = new OptionData(OptionType.STRING, config.getString("botSettings.options.usernameName"), config.getString("botSettings.options.usernameDesc"), true);
        OptionData code = new OptionData(OptionType.STRING, config.getString("botSettings.options.codeName"), config.getString("botSettings.options.codeDesc"), true);
        return Commands.slash(config.getString("botSettings.dcCommandName"), config.getString("botSettings.dcCommandDesc")).addOptions(name, code);
    }


    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        try {
            Database = new Database(instance);
            TextHandling = new TextHandling(instance);
            config = instance.getMainConfig();
            langConfig = instance.getLangConfig();
            // Get provided data
            String name = event.getOption(config.getString("botSettings.options.usernameName")).getAsString();
            String code = event.getOption(config.getString("botSettings.options.codeName")).getAsString();
            OfflinePlayer p = Bukkit.getOfflinePlayer(name);
            instance.setPlayer(p);
            HashMap<String, String> userData = Database.getUserData();
            Member member = event.getMember();
            if(member == null) {
                event.reply("**Wystąpił błąd!**\nSkontaktuj się z administracją").setEphemeral(true).queue();
                return;
            }
            if (userData.get("exists").equalsIgnoreCase("false")) {
                event.reply(TextHandling.replaceDiscordPlaceholders(p.getName(), event.getMember(), langConfig.getString("discord.invalidUsername"))).setEphemeral(true).queue();
                return;
            }
            if (!userData.get("code").equalsIgnoreCase(code)) {
                event.reply(TextHandling.replaceDiscordPlaceholders(p.getName(), event.getMember(), langConfig.getString("discord.invalidCode"))).setEphemeral(true).queue();
                return;
            }
            if (userData.get("used").equalsIgnoreCase("true") || userData.get("used").equalsIgnoreCase("1") || userData.get("used").equalsIgnoreCase(langConfig.getString("replacement.true"))) {
                event.reply(TextHandling.replaceDiscordPlaceholders(p.getName(), event.getMember(), langConfig.getString("discord.codeUsed"))).setEphemeral(true).queue();
                return;
            }
            if (userData.get("role").equalsIgnoreCase("default")) {
                event.reply(TextHandling.replaceDiscordPlaceholders(p.getName(), event.getMember(), langConfig.getString("discord.defaultRole"))).setEphemeral(true).queue();
                return;
            }
            userData.put("used", "true");
            Database.setUserData(userData);
            event.reply(TextHandling.replaceDiscordPlaceholders(p.getName(), event.getMember(), langConfig.getString("discord.successRedeem"))).setEphemeral(true).queue();
            // Give roles to user
            List<String> discordIDs = config.getStringList("roles." + userData.get("role") + ".discordIDs");
            for (String id : discordIDs) {
                assert member != null;
                Role role = event.getGuild().getRoleById(id);
                if (role == null) {
                    Bukkit.getConsoleSender().sendMessage(TextHandling.getFormatted("{P}&c Invalid role ID\n&8» &7RoleName: " + userData.get("role") + "\n&8» &7ID: " + id));
                } else {
                    event.getGuild().addRoleToMember(member, role).queue();
                }
            }
            EmbedBuilder eb = new EmbedBuilder();
            eb.setTitle(langConfig.getString("discord.logEmbed.title").replace("{SERVERNAME}", langConfig.getString("replacement.serverName")));
            int i = 0;
            for (String descLine : langConfig.getStringList("discord.logEmbed.description")) {
                if (i > 0) {
                    descLine = "\n" + descLine;
                }
                descLine = TextHandling.replaceDiscordPlaceholders(p.getName(), member, descLine);
                eb.appendDescription(descLine);
                i += 1;
            }
            eb.setColor(Color.decode(langConfig.getString("discord.logEmbed.color")));
            event.getGuild().getTextChannelById(langConfig.getString("discord.logEmbed.channelToSend")).sendMessageEmbeds(eb.build()).queue();
        } catch (HierarchyException e) {
            Bukkit.getServer().getConsoleSender().sendMessage(TextHandling.getFormatted("{P} &cRola, którą próbowałem dodać jest za nisko!"));
            event.reply("**Wystąpił błąd!**\nSkontaktuj się z administracją").setEphemeral(true).queue();
        }
    }
}
