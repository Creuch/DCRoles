package me.creuch.dcroles.functions;

import me.creuch.dcroles.DCRoles;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GetFunctions {

    DCRoles DCRoles = new DCRoles();

    public List<String> getRanks(CommandSender sender) throws SQLException {
        assert DCRoles.checkPluginStatus(sender);
        List<String> ranks = new ArrayList<>();
        ranks.add("default");
        ranks.addAll(DCRoles.instance.getConfig().getConfigurationSection("ranks").getKeys(false));
        return ranks;
    }


}
