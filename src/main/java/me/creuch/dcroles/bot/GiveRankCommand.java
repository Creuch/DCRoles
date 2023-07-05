package me.creuch.dcroles.bot;

import me.creuch.dcroles.DCRoles;
import me.creuch.dcroles.functions.Database;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.bukkit.configuration.file.FileConfiguration;

import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

public class GiveRankCommand extends ListenerAdapter {

    public static CommandData loadCommand() {
        DCRoles instance = DCRoles.instance;
        OptionData mcName = new OptionData(OptionType.STRING, "nick", instance.getConfig().getString("bot.mcNameOption"), true);
        OptionData mcCode = new OptionData(OptionType.STRING, "kod", instance.getConfig().getString("bot.codeOption"), true);
        return Commands.slash(instance.getConfig().getString("bot.giveRankName"), instance.getConfig().getString("bot.commandDescription")).addOptions(mcName, mcCode);
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        DCRoles instance = DCRoles.instance;
        instance.getServer().getLogger().finest(event.getName());
        FileConfiguration config = instance.getConfig();
        String cmd = event.getCommandString();
        User user = event.getUser();
        Member member = event.getGuild().getMember(user);
        assert cmd.equalsIgnoreCase(config.getString("bot.giveRankName"));
        // Provided options
        String usernameOption = event.getOption("nick").getAsString();
        String codeOption = event.getOption("kod").getAsString();
        // Valid options
        String codeValue = "";
        String rankValue = "";
        String usedValue = "";
        try {
            codeValue = Database.getData(instance.getServer().getOfflinePlayer(usernameOption), "code");
            rankValue = Database.getData(instance.getServer().getOfflinePlayer(usernameOption), "rank");
            usedValue = Database.getData(instance.getServer().getOfflinePlayer(usernameOption), "used");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        if (!codeOption.equals(codeValue)) {
            event.reply("" + config.getString("messages.discord.invalidCode")).setEphemeral(true).queue();
        } else if (Objects.equals(rankValue, "default")) {
            event.reply("" + config.getString("messages.discord.defaultRole")).setEphemeral(true).queue();
        } else if (Objects.equals(usedValue, "1")) {
            event.reply("" + config.getString("messages.discord.codeAlreadyUsed")).setEphemeral(true).queue();
        }
        if (Objects.equals(usedValue, "0") && !Objects.equals(rankValue, "default") && codeOption.equals(codeValue)) {
            Database.updateUser(usernameOption, "updateUsage", null, instance.getServer().getConsoleSender());
            event.reply("" + config.getString("messages.discord.succesRedeem").replace("{RANK}", rankValue)).setEphemeral(true).queue();
            List<String> discordIDs = config.getStringList("ranks." + rankValue + ".discordIDs");
            for (String roleID : discordIDs) {
                assert member != null;
                if (!member.getRoles().contains(event.getGuild().getRoleById(roleID))) {
                    event.getGuild().addRoleToMember(member, event.getGuild().getRoleById(roleID)).queue();
                }
            }
            EmbedBuilder eb = new EmbedBuilder();
            eb.setTitle(config.getString("messages.discord.logEmbed.title").replace("{SERVERNAME}", config.getString("text.serverName")));
            List<String> description = config.getStringList("messages.discord.logEmbed.description");
            eb.setColor(Color.getColor(config.getString("messages.discord.logEmbed.color")));
            for (String descLine : description) {
                descLine = descLine.replace("{USER}", usernameOption);
                descLine = descLine.replace("{RANK}", rankValue);
                descLine = descLine.replace("{DISCORD-EXECUTOR}", event.getUser().getName());
                eb.appendDescription(descLine);
            }
            event.getGuild().getTextChannelById(config.getString("messages.discord.logEmbed.channelToSend")).sendMessageEmbeds(eb.build()).queue();
        }
    }
}
