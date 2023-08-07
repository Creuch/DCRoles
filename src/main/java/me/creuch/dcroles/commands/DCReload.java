package me.creuch.dcroles.commands;

import me.creuch.dcroles.DCRoles;
import me.creuch.dcroles.TextHandling;
import me.creuch.dcroles.Config;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

public class DCReload implements CommandExecutor {

    private final DCRoles instance;
    private Config config;

    public DCReload(DCRoles plugin) {
        this.instance = plugin;
        this.config = new Config(instance);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        TextHandling TextHandling = new TextHandling(instance);
        if(command.getName().equalsIgnoreCase("dcreload")) {
            if(commandSender.hasPermission("dcr.reload")) {
                instance.reloadPlugin(commandSender);
            } else {
                commandSender.sendMessage(TextHandling.getFormatted(config.getValue("langConfig", "minecraft.user.noPermission")));
            }
        }
        return true;
    }
}