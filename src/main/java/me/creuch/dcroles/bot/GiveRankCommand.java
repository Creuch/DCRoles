package me.creuch.dcroles.bot;

import me.creuch.dcroles.DCRoles;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class GiveRankCommand {

    public static CommandData loadCommand() {
        DCRoles instance = DCRoles.instance;
        OptionData mcName = new OptionData(OptionType.STRING, "nick", instance.getConfig().getString("bot.mcNameOption"), true);
        OptionData mcCode = new OptionData(OptionType.STRING, "kod", instance.getConfig().getString("bot.codeOption"), true);
        return Commands.slash(instance.getConfig().getString("bot.giveRankName"), instance.getConfig().getString("bot.commandDescription")).addOptions(mcName, mcCode);
    }
}
