package me.creuch.dcroles.commands;

import me.creuch.dcroles.DCRoles;
import me.creuch.dcroles.functions.Database;
import me.creuch.dcroles.functions.Messages;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;


public class DCCode implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (command.getName().equalsIgnoreCase("dccode")) {
            assert DCRoles.checkPluginStatus(commandSender);
            DCRoles instance = DCRoles.instance;
            HashMap<String, String> user;
            String code;
            try {
                if (strings.length == 0) {
                    if (commandSender instanceof Player) {
                        if (commandSender.hasPermission("dcr.dccode.self")) {
                            user = Database.getUserProfile((OfflinePlayer) commandSender);
                            if(user.get("exists").equalsIgnoreCase("false")) {
                                commandSender.sendMessage(Messages.getMessage(instance.getConfig().getString("messages.playerNotFound")));
                                return true;
                            }
                            code = user.get("code");
                            List<String> messages = instance.getConfig().getStringList("messages.userCodeCommandSelf");
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
                        user = Database.getUserProfile(Bukkit.getOfflinePlayer(strings[0]));
                        if(user.get("exists").equalsIgnoreCase("false")) {
                            commandSender.sendMessage(Messages.getMessage(instance.getConfig().getString("messages.playerNotFound")));
                            return true;
                        }
                        code = user.get("code");
                        List<String> messages = instance.getConfig().getStringList("messages.userCodeCommandOther");
                        for (String msg1 : messages) {
                            msg1 = msg1.replace("{CODE}", code);
                            msg1 = msg1.replace("{USER}", strings[0]);
                            commandSender.sendMessage(Messages.getMessage(msg1));
                        }
                    } else {
                        commandSender.sendMessage(Messages.getMessage(instance.getConfig().getString("messages.noPermission")));
                    }
                }
            } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        }
        return true;
    }
}

