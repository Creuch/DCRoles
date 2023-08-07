package me.creuch.dcroles.discord;

import me.creuch.dcroles.DCRoles;
import me.creuch.dcroles.Database;
import me.creuch.dcroles.TextHandling;
import me.creuch.dcroles.Config;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.exceptions.ContextException;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.exceptions.HierarchyException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;

import javax.management.relation.RoleNotFoundException;
import java.awt.*;
import java.util.HashMap;
import java.util.List;

public class Command extends ListenerAdapter {

    private final DCRoles instance;
    private Database Database;
    private TextHandling TextHandling;
    private Config config;

    public Command(DCRoles instance) {
        this.instance = instance;
        this.config = new Config(instance);
    }

    public CommandData getCommand() {
        OptionData name = new OptionData(OptionType.STRING, config.getValue("mainConfig", "botSettings.options.usernameName"), config.getValue("mainConfig", "botSettings.options.usernameDesc"), true);
        OptionData code = new OptionData(OptionType.STRING, config.getValue("mainConfig", "botSettings.options.codeName"), config.getValue("mainConfig", "botSettings.options.codeDesc"), true);
        return Commands.slash(config.getValue("mainConfig", "botSettings.dcCommandName"), config.getValue("mainConfig", "botSettings.dcCommandDesc")).addOptions(name, code);
    }


    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        Role role = null;
        try {
            Database = new Database(instance);
            TextHandling = new TextHandling(instance);
            // Get provided data
            String name = event.getOption(config.getValue("mainConfig", "botSettings.options.usernameName")).getAsString();
            String code = event.getOption(config.getValue("mainConfig", "botSettings.options.codeName")).getAsString();
            OfflinePlayer p = Bukkit.getOfflinePlayer(name);
            instance.setPlayer(p);
            HashMap<String, String> userData = Database.getUserData();
            Member member = event.getMember();
            if(member == null) {
                event.reply("**Wystąpił błąd!**\nSkontaktuj się z administracją").setEphemeral(true).queue();
                return;
            }
            if (userData.get("exists").equalsIgnoreCase("false")) {
                event.reply(TextHandling.replaceDiscordPlaceholders(p.getName(), event.getMember(), config.getValue("langConfig", "discord.invalidUsername"))).setEphemeral(true).queue();
                return;
            }
            if (!userData.get("code").equalsIgnoreCase(code)) {
                event.reply(TextHandling.replaceDiscordPlaceholders(p.getName(), event.getMember(), config.getValue("langConfig", "discord.invalidCode"))).setEphemeral(true).queue();
                return;
            }
            if (userData.get("used").equalsIgnoreCase("true") || userData.get("used").equalsIgnoreCase("1") || userData.get("used").equalsIgnoreCase(config.getValue("langConfig", "replacement.true"))) {
                event.reply(TextHandling.replaceDiscordPlaceholders(p.getName(), event.getMember(), config.getValue("langConfig", "discord.codeUsed"))).setEphemeral(true).queue();
                return;
            }
            if (userData.get("role").equalsIgnoreCase("default")) {
                event.reply(TextHandling.replaceDiscordPlaceholders(p.getName(), event.getMember(), config.getValue("langConfig", "discord.defaultRole"))).setEphemeral(true).queue();
                return;
            }
            userData.put("used", "true");
            Database.setUserData(userData);
            event.reply(TextHandling.replaceDiscordPlaceholders(p.getName(), event.getMember(), config.getValue("langConfig", "discord.successRedeem"))).setEphemeral(true).queue();
            // Give roles to user
            List<String> discordIDs = config.getList("mainConfig", "roles." + userData.get("role") + ".discordIDs");
            for (String id : discordIDs) {
                assert member != null;
                role = event.getGuild().getRoleById(id);
                if (role == null) {
                    Bukkit.getConsoleSender().sendMessage(TextHandling.getFormatted("{P}&c Invalid role ID\n&8» &7RoleName: " + userData.get("role") + "\n&8» &7ID: " + id));
                } else {
                    event.getGuild().addRoleToMember(member, role).queue();
                }
            }
            EmbedBuilder eb = new EmbedBuilder();
            eb.setTitle(config.getValue("langConfig", "discord.logEmbed.title").replace("{SERVERNAME}", config.getValue("langConfig", "replacement.serverName")));
            int i = 0;
            for (String descLine : config.getList("langConfig", "discord.logEmbed.description")) {
                if (i > 0) {
                    descLine = "\n" + descLine;
                }
                descLine = TextHandling.replaceDiscordPlaceholders(p.getName(), member, descLine);
                eb.appendDescription(descLine);
                i += 1;
            }
            eb.setColor(Color.decode(config.getValue("langConfig", "discord.logEmbed.color")));
            event.getGuild().getTextChannelById(config.getValue("langConfig", "discord.logEmbed.channelToSend")).sendMessageEmbeds(eb.build()).queue();
        } catch (HierarchyException e) {
            Bukkit.getServer().getConsoleSender().sendMessage(TextHandling.getFormatted("{P} &cRola, którą próbowałem dodać jest za nisko!"));
            event.reply("**Wystąpił błąd!**\nSkontaktuj się z administracją").setEphemeral(true).queue();
        } catch (NumberFormatException | NullPointerException e) {
            if(role == null) {
                Bukkit.getServer().getConsoleSender().sendMessage(TextHandling.getFormatted("{P} &cRola, którą próbowałem dodać nie istnieje!"));
                event.reply("**Wystąpił błąd!**\nSkontaktuj się z administracją").setEphemeral(true).queue();
            }
        } catch (ErrorResponseException e) {
            event.reply("**Wystąpił błąd!**\nSkontaktuj się z administracją").setEphemeral(true).queue();
        }
    }
}