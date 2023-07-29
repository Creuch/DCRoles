package me.creuch.dcroles.commands;

import me.creuch.dcroles.DCRoles;
import me.creuch.dcroles.functions.Database;
import me.creuch.dcroles.functions.GUI;
import me.creuch.dcroles.functions.Messages;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DCMGui implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(command.getName().equalsIgnoreCase("dcmgui")) {
            DCRoles instance = DCRoles.instance;
            if(sender.hasPermission("dcr.dcmgui")) {
                if(sender instanceof HumanEntity) {
                    try {
                        ((HumanEntity) sender).openInventory(GUI.getGui(sender.getName(), (Player) sender, "test", "gui.items."));
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    sender.sendMessage(Messages.getMessage(instance.getConfig().getString("messages.executorNotPlayer")));
                }
            } else {
                sender.sendMessage(Messages.getMessage(instance.getConfig().getString("messages.noPermission")));
            }
        }
        return true;
    }


}
