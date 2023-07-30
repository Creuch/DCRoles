package me.creuch.dcroles.bot;

import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class onGuildReadyEvent extends ListenerAdapter {

    GiveRankCommand GiveRankCommand = new GiveRankCommand();

    @Override
    public void onGuildReady(@NotNull GuildReadyEvent event) {
        List<CommandData> commandData = new ArrayList<>();
        commandData.add(GiveRankCommand.loadCommand());
        event.getGuild().updateCommands().addCommands(commandData).queue();
    }

}

