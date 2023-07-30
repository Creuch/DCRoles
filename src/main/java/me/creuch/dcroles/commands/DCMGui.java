package me.creuch.dcroles.commands;

import me.creuch.dcroles.DCRoles;
import me.creuch.dcroles.functions.Database;
import me.creuch.dcroles.functions.Messages;
import me.creuch.dcroles.inventory.BuilderInventory;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;

public class DCMGui implements CommandExecutor {

    DCRoles DCRoles = new DCRoles();
    Messages Messages = new Messages();
    me.creuch.dcroles.functions.Database Database = new Database();
    BuilderInventory inventory = new BuilderInventory();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (command.getName().equalsIgnoreCase("dcmgui")) {
            DCRoles instance = DCRoles.instance;
            try {
                if (sender.hasPermission("dcr.dcmgui")) {
                    if (sender instanceof HumanEntity) {
                        inventory.setPlayer((Player) sender);
                        ((Player) sender).openInventory(inventory.getInventory());
                    } else {
                        sender.sendMessage(Messages.getMessage(instance.getConfig().getString("messages.executorNotPlayer"), (Player) sender));
                    }
                } else {
                    sender.sendMessage(Messages.getMessage(instance.getConfig().getString("messages.noPermission"), (Player) sender));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return true;
    }


}
