package me.creuch.dcroles.commands;

import me.creuch.dcroles.DCRoles;
import me.creuch.dcroles.functions.Database;
import me.creuch.dcroles.functions.Messages;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import javax.xml.crypto.Data;
import java.sql.SQLException;

public class DCReload implements CommandExecutor {

    DCRoles DCRoles = new DCRoles();
    Messages Messages = new Messages();
    me.creuch.dcroles.functions.Database Database = new Database();

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if(command.getName().equalsIgnoreCase("dcreload")) {
            DCRoles instance = DCRoles.getInstance();
            if(commandSender.hasPermission("dcr.dcreload")) {
                try {
                    DCRoles.reloadPlugin(commandSender);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            } else {
                try {
                    commandSender.sendMessage(Messages.getMessage(instance.getConfig().getString("messages.noPermission"), (Player) commandSender));
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return true;
    }
}
