package me.creuch.dcroles.commands;

import me.creuch.dcroles.DCRoles;
import me.creuch.dcroles.functions.Messages;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class DCReload implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if(command.getName().equalsIgnoreCase("dcreload")) {
            DCRoles instance = DCRoles.instance;
            if(commandSender.hasPermission("dcr.dcreload")) {
                DCRoles.reloadPlugin(commandSender);
            } else {
                commandSender.sendMessage(Messages.getMessage(instance.getConfig().getString("messages.noPermission")));
            }
        }
        return true;
    }
}
