package me.creuch.dcroles.commands;

import me.creuch.dcroles.DCRoles;
import me.creuch.dcroles.functions.Database;
import me.creuch.dcroles.functions.GUI;
import me.creuch.dcroles.functions.GetFunctions;
import me.creuch.dcroles.functions.Messages;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static me.creuch.dcroles.functions.Database.dbConnect;

public class DCMCode implements CommandExecutor, TabCompleter {
    public static Inventory getManageGUI(String pName, OfflinePlayer p) throws SQLException {
        DCRoles instance = DCRoles.instance;
        Inventory inv = Bukkit.createInventory(null, instance.getConfig().getInt("gui.size"), Messages.getMessage(instance.getConfig().getString("text.codeManageInvName")));
        ItemStack is;
        for(String s : instance.getConfig().getConfigurationSection("gui.items").getKeys(false)) {
            is = GUI.getConfigItem("gui.items." + s, pName, "normal");
            if(Objects.equals(instance.getConfig().getString("gui.items." + s + ".type"), "INFO_ITEM")) {
                SkullMeta skullMeta = (SkullMeta) is.getItemMeta();
                skullMeta.setOwningPlayer(p);
                is.setItemMeta(skullMeta);
            }
            String permission = instance.getConfig().getString("gui.items." + s + ".permission");
            if(!permission.equalsIgnoreCase("NONE") && ((Player) p).hasPermission(permission)) {
                inv.setItem(Integer.parseInt(s), is);
            }

        }
        for(int i = 0; i < inv.getSize(); i++) {
            if(inv.getItem(i) == null || inv.getItem(i).getType().equals(Material.AIR)) {
                ItemStack itemStack = new ItemStack(Material.getMaterial(instance.getConfig().getString("gui.filler.material")));
                if(itemStack.getType() != Material.AIR) {
                    ItemMeta itemMeta = itemStack.getItemMeta();
                    itemMeta.displayName(Messages.getMessage("&7 ").decoration(TextDecoration.ITALIC, false));
                    itemStack.setItemMeta(itemMeta);
                    inv.setItem(i, itemStack);
                }
            }
        }
        return inv;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (command.getName().equalsIgnoreCase("dcmcode")) {
            assert DCRoles.checkPluginStatus(commandSender);
            DCRoles instance = DCRoles.instance;
            if (strings.length == 0) {
                if (commandSender.hasPermission("dcr.dcmcode")) {
                    List<String> messages = instance.getConfig().getStringList("messages.helpFormat");
                    for (String msg : messages) {
                        commandSender.sendMessage(Messages.getMessage(msg));
                    }
                } else {
                    commandSender.sendMessage(Messages.getMessage(instance.getConfig().getString("messages.noPermission")));
                }
            } else {
                if (strings.length >= 2 && strings[1].equalsIgnoreCase("grantRank")) {
                    if (commandSender.hasPermission("dcr.dcmcode")) {
                        List<String> ranks = GetFunctions.getRanks(commandSender);
                        if (strings.length < 3) {
                            commandSender.sendMessage(Messages.getMessage(instance.getConfig().getString("messages.badRank")));
                            return true;
                        }
                        for (String rank : ranks) {
                            if (strings[2].equalsIgnoreCase(rank)) {
                                try {
                                    commandSender.sendMessage(Messages.getMessage(instance.getConfig().getString("messages.rankGive").replace("{USER}", strings[0]).replace("{RANK}", rank)));
                                    if (dbConnect(instance.getServer().getConsoleSender()).isClosed()) {
                                        dbConnect(instance.getServer().getConsoleSender()).close();
                                    }
                                    Database.updateUser(strings[0], "setRank", rank, commandSender);
                                    return true;
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        commandSender.sendMessage(Messages.getMessage(instance.getConfig().getString("messages.badRank")));
                    } else {
                        commandSender.sendMessage(Messages.getMessage(instance.getConfig().getString("messages.noPermission")));
                    }
                } else {
                    if (commandSender.hasPermission("dcr.dcmcode")) {
                        if(commandSender instanceof HumanEntity) {
                            OfflinePlayer p = Bukkit.getPlayer(strings[0]);
                            if (p == null) {
                                commandSender.sendMessage(Messages.getMessage(instance.getConfig().getString("messages.playerNotFound")));
                                return true;
                            }
                            Player cs = (Player) commandSender;
                            try {
                                cs.openInventory(GUI.getGui(p.getName(), p, "normal", "gui.items."));
                            } catch (SQLException e) {
                                throw new RuntimeException(e);
                            }
                        } else {
                            commandSender.sendMessage(Messages.getMessage(instance.getConfig().getString("messages.executorNotPlayer")));
                        }
                    } else {
                        commandSender.sendMessage(Messages.getMessage(instance.getConfig().getString("messages.noPermission")));
                    }
                }
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
                return GetFunctions.getRanks(sender);
            }
        }
        return null;
    }
}
