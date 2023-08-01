package me.creuch.dcroles.commands;

import kotlin.collections.ArrayDeque;
import me.creuch.dcroles.DCRoles;
import me.creuch.dcroles.Database;
import me.creuch.dcroles.TextHandling;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.HumanEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class DCCode implements CommandExecutor, TabCompleter {

    private final DCRoles instance;

    public DCCode(DCRoles plugin) {
        this.instance = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (command.getName().equalsIgnoreCase("dccode")) {
            Database Database = new Database(instance);
            TextHandling TextHandling = new TextHandling(instance);
            YamlConfiguration langConfig = instance.getLangConfig();
            // Get self code
            if (strings.length == 0 || strings[0].equalsIgnoreCase(commandSender.getName())) {
                if (commandSender instanceof HumanEntity) {
                    if (commandSender.hasPermission("dcr.dccode.self")) {
                        instance.setPlayer((OfflinePlayer) commandSender);
                        for (String message : langConfig.getStringList("minecraft.user.codeMessage")) {
                            commandSender.sendMessage(TextHandling.getFormatted(message));
                        }
                    } else {
                        commandSender.sendMessage(TextHandling.getFormatted(langConfig.getString("minecraft.user.noPermission")));
                    }
                } else {
                    commandSender.sendMessage(TextHandling.getFormatted(langConfig.getString("minecraft.user.onlyPlayerCommand")));
                }
            } else {
                if (commandSender.hasPermission("dcr.dccode.others")) {
                    instance.setPlayer(Bukkit.getOfflinePlayer(strings[0]));
                    if (Database.getUserData().get("exists") == "true") {
                        for (String message : langConfig.getStringList("minecraft.user.codeMessageOther")) {
                            commandSender.sendMessage(TextHandling.getFormatted(message));
                        }
                    } else {
                        commandSender.sendMessage(TextHandling.getFormatted(langConfig.getString("minecraft.user.playerNotFound")));
                    }
                } else {
                    commandSender.sendMessage(TextHandling.getFormatted(langConfig.getString("minecraft.user.noPermission")));
                }
            }
        }
        instance.setPlayer(null);
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (command.getName().equalsIgnoreCase("dccode")) {
            if (commandSender.hasPermission("dcr.dccode.others")) {
                List<String> tabArgs = new ArrayDeque<>();
                for (OfflinePlayer op : Bukkit.getOfflinePlayers()) {
                    tabArgs.add(op.getName());
                }
            }
        }
        return null;
    }
}
