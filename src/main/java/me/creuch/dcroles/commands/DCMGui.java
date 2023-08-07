package me.creuch.dcroles.commands;

import me.creuch.dcroles.DCRoles;
import me.creuch.dcroles.TextHandling;
import me.creuch.dcroles.inventory.builderinventory.BInventory;
import me.creuch.dcroles.Config;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.HumanEntity;
import org.jetbrains.annotations.NotNull;

public class DCMGui implements CommandExecutor {

    private final DCRoles instance;
    private Config config;

    public DCMGui(DCRoles plugin) {
        this.instance = plugin;
        this.config = new Config(instance);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        TextHandling TextHandling = new TextHandling(instance);
        if(command.getName().equalsIgnoreCase("dcmgui")) {
            if(commandSender.hasPermission("dcr.dcmgui")) {
                if(commandSender instanceof HumanEntity) {
                    ((HumanEntity) commandSender).openInventory(new BInventory(instance).getInventory());
                } else {
                    commandSender.sendMessage(TextHandling.getFormatted(config.getValue("langConfig", "minecraft.user.onlyPlayerCommand")));
                }
            } else {
                commandSender.sendMessage(TextHandling.getFormatted(config.getValue("langConfig", "minecraft.user.noPermission")));
            }
        }
        return true;
    }
}