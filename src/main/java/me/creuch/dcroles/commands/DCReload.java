package me.creuch.dcroles.commands;

import me.creuch.dcroles.DCRoles;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

public class DCReload implements CommandExecutor {

    private final DCRoles instance;

    public DCReload(DCRoles plugin) {
        this.instance = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        YamlConfiguration langConfig = instance.getLangConfig();
        if(command.getName().equalsIgnoreCase("dcreload")) {
            if(commandSender.hasPermission("dcr.reload")) {
                instance.reloadPlugin(commandSender);
            } else {
                commandSender.sendMessage(langConfig.getString("messages.user.noPermission"));
            }
        }
        return true;
    }
}
