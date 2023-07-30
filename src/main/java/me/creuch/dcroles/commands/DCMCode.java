package me.creuch.dcroles.commands;

import me.creuch.dcroles.DCRoles;
import me.creuch.dcroles.functions.Database;
import me.creuch.dcroles.functions.GUI;
import me.creuch.dcroles.functions.GetFunctions;
import me.creuch.dcroles.functions.Messages;
import me.creuch.dcroles.inventory.BuilderInventory;
import me.creuch.dcroles.inventory.CodeManageInventory;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DCMCode implements CommandExecutor, TabCompleter {

    DCRoles DCRoles = new DCRoles();
    Messages Messages = new Messages();
    me.creuch.dcroles.functions.Database Database = new Database();
    GetFunctions GetFunctions = new GetFunctions();
    CodeManageInventory inventory = new CodeManageInventory();

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (command.getName().equalsIgnoreCase("dcmcode")) {
            try {
                assert DCRoles.checkPluginStatus(commandSender);
                DCRoles instance = DCRoles.instance;
                if (strings.length == 0) {
                    if (commandSender.hasPermission("dcr.dcmcode")) {
                        List<String> messages = instance.getConfig().getStringList("messages.helpFormat");
                        for (String msg : messages) {
                            commandSender.sendMessage(Messages.getMessage(msg, (OfflinePlayer) commandSender));
                        }
                    } else {
                        commandSender.sendMessage(Messages.getMessage(instance.getConfig().getString("messages.noPermission"), (OfflinePlayer) commandSender));
                    }
                } else {
                    if (strings.length >= 2 && strings[1].equalsIgnoreCase("grantRank")) {
                        if (commandSender.hasPermission("dcr.dcmcode")) {
                            List<String> ranks = GetFunctions.getRanks(commandSender);
                            if (strings.length < 3) {
                                commandSender.sendMessage(Messages.getMessage(instance.getConfig().getString("messages.badRank"), (OfflinePlayer) commandSender));
                                return true;
                            }
                            for (String rank : ranks) {
                                if (strings[2].equalsIgnoreCase(rank)) {
                                    try {
                                        commandSender.sendMessage(Messages.getMessage(instance.getConfig().getString("messages.rankGive"), (OfflinePlayer) commandSender));
                                        if (Database.dbConnect(instance.getServer().getConsoleSender()).isClosed()) {
                                            Database.dbConnect(instance.getServer().getConsoleSender()).close();
                                        }
                                        Database.updateUser(strings[0], "setRank", rank, commandSender);
                                        return true;
                                    } catch (SQLException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                            commandSender.sendMessage(Messages.getMessage(instance.getConfig().getString("messages.badRank"), (OfflinePlayer) commandSender));
                        } else {
                            commandSender.sendMessage(Messages.getMessage(instance.getConfig().getString("messages.noPermission"), (OfflinePlayer) commandSender));
                        }
                    } else {
                        if (commandSender.hasPermission("dcr.dcmcode")) {
                            if (commandSender instanceof HumanEntity) {
                                OfflinePlayer p = Bukkit.getPlayer(strings[0]);
                                if (p == null) {
                                    commandSender.sendMessage(Messages.getMessage(instance.getConfig().getString("messages.playerNotFound"), (OfflinePlayer) commandSender));
                                    return true;
                                }
                                Player cs = (Player) commandSender;
                                inventory.setPlayer(cs);
                                cs.openInventory(inventory.getInventory());
                            } else {
                                commandSender.sendMessage(Messages.getMessage(instance.getConfig().getString("messages.executorNotPlayer"), (OfflinePlayer) commandSender));
                            }
                        } else {
                            commandSender.sendMessage(Messages.getMessage(instance.getConfig().getString("messages.noPermission"), (OfflinePlayer) commandSender));
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        List<String> tabArgs = new ArrayList<>();
        if (sender.hasPermission("dcr.dcmcode")) {
            if (args.length == 2) {
                tabArgs.add("grantRank");
                return tabArgs;
            }
            if (args.length == 3) {
                try {
                    return GetFunctions.getRanks(sender);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return null;
    }
}
