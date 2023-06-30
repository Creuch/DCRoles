package me.creuch.dcroles.commands;

import me.creuch.dcroles.DCRoles;
import me.creuch.dcroles.functions.Messages;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class DCMCode implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if(command.getName().equalsIgnoreCase("dcmcode")) {
            DCRoles instance = DCRoles.instance;
            if(commandSender.hasPermission("dcr.dcmcode")) {
                if(strings.length == 0) {
                    List<String> messages = instance.getConfig().getStringList("messages.helpFormat");
                    for (String msg : messages) {
                        commandSender.sendMessage(Messages.getMessage(msg));
                    }
                } else {
                    Player p = Bukkit.getPlayer(strings[0]);
                    if(p == null) {
                        commandSender.sendMessage(Messages.getMessage(instance.getConfig().getString("messages.playerNotFound")));
                        return true;
                    }
                    String code = DCRoles.getCode(p);
                }
            } else {
                commandSender.sendMessage(Messages.getMessage(instance.getConfig().getString("messages.noPermission")));
            }
        }
        return true;
    }
}
