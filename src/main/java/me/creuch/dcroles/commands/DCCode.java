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

public class DCCode implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (command.getName().equalsIgnoreCase("dccode")) {
            assert DCRoles.checkPluginStatus(commandSender);
            DCRoles instance = DCRoles.instance;
            if (strings.length == 0) {
                if (commandSender instanceof Player) {
                    if (commandSender.hasPermission("dcr.dccode.self")) {
                        String code = DCRoles.getData((Player) commandSender, "code");
                        List<String> messages = instance.getConfig().getStringList("messages.selfDCCode");
                        for (String msg : messages) {
                            msg = msg.replace("{CODE}", code);
                            commandSender.sendMessage(Messages.getMessage(msg));
                        }
                    } else {
                        commandSender.sendMessage(Messages.getMessage(instance.getConfig().getString("messages.noPermission")));
                    }
                } else {
                    commandSender.sendMessage(Messages.getMessage(instance.getConfig().getString("messages.executorNotPlayer")));
                }
            } else {
                if (commandSender.hasPermission("dcr.dccode.others")) {
                    Player p = Bukkit.getPlayer(strings[0]);
                    if(p == null) {
                        commandSender.sendMessage(Messages.getMessage(instance.getConfig().getString("messages.playerNotFound")));
                        return true;
                    }
                    String code = DCRoles.getData(p, "code");
                    List<String> messages = instance.getConfig().getStringList("messages.argDCCode");
                    for (String msg1 : messages) {
                        msg1 = msg1.replace("{CODE}", code);
                        msg1 = msg1.replace("{USER}", strings[0]);
                        commandSender.sendMessage(Messages.getMessage(msg1));
                    }
                } else {
                    commandSender.sendMessage(Messages.getMessage(instance.getConfig().getString("messages.noPermission")));
                }
            }
        }
        return true;
    }
}
