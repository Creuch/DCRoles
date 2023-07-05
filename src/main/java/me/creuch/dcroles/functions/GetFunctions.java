package me.creuch.dcroles.functions;

import me.creuch.dcroles.DCRoles;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class GetFunctions {
    public static List<String> getRanks(CommandSender sender) {
        assert DCRoles.checkPluginStatus(sender);
        List<String> ranks = new ArrayList<>();
        ranks.add("default");
        ranks.addAll(DCRoles.instance.getConfig().getConfigurationSection("ranks").getKeys(false));
        return ranks;
    }

}
