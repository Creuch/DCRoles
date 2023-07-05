package me.creuch.dcroles.commands;

import me.creuch.dcroles.DCRoles;
import me.creuch.dcroles.functions.Database;
import me.creuch.dcroles.functions.GetFunctions;
import me.creuch.dcroles.functions.Messages;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
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

import static me.creuch.dcroles.functions.Database.dbConnect;

public class DCMCode implements CommandExecutor, TabCompleter {
    public static Inventory getManageGUI(String pName, OfflinePlayer p) throws SQLException {
        DCRoles instance = DCRoles.instance;
        String code = Database.getData(p, "code");
        String rank = Database.getData(p, "rank");
        String used = Database.getData(p, "used").replace("1", instance.getConfig().getString("text.true").replace("0", instance.getConfig().getString("text.false")));
        Inventory inv = Bukkit.createInventory(null, 36, Messages.getMessage(instance.getConfig().getString("text.codeManageInvName").replace("{USER}", pName)));
        // Background of the inventory
        for (int i = 0; i < inv.getSize(); i++) {
            ItemStack item = new ItemStack(Material.GRAY_STAINED_GLASS_PANE, 1);
            ItemMeta itemMeta = item.getItemMeta();
            itemMeta.displayName(Messages.getMessage("&7 "));
            item.setItemMeta(itemMeta);
            inv.setItem(i, item);
        }
        // Skull of the provided player
        ItemStack item = new ItemStack(Material.PLAYER_HEAD, 1);
        SkullMeta skullMeta = (SkullMeta) item.getItemMeta();
        skullMeta.displayName(Messages.getMessage("&f&l" + pName).decoration(TextDecoration.ITALIC, false));
        skullMeta.setOwningPlayer(p);
        List<Component> lore = new ArrayList<>();
        lore.add(Messages.getMessage("&r&8» &7Kod: &f" + code).decoration(TextDecoration.ITALIC, false));
        lore.add(Messages.getMessage("&r&8» &7Wykorzystano: &f" + used).decoration(TextDecoration.ITALIC, false));
        lore.add(Messages.getMessage("&r&8» &7Ranga: &f" + rank.replace("default", instance.getConfig().getString("text.defualtRoleReplace"))).decoration(TextDecoration.ITALIC, false));
        skullMeta.lore(lore);
        item.setItemMeta(skullMeta);
        inv.setItem(13, item);
        // Remove user from database
        item = new ItemStack(Material.LAVA_BUCKET, 1);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.displayName(Messages.getMessage("&c&lUsuń użytkownika").decoration(TextDecoration.ITALIC, false));
        lore.clear();
        lore.add((Messages.getMessage("&8» &7Usuwa dane użytkownika z bazy danych").decoration(TextDecoration.ITALIC, false)));
        itemMeta.lore(lore);
        item.setItemMeta(itemMeta);
        inv.setItem(11, item);
        // Reload user's code
        item = new ItemStack(Material.ENDER_PEARL, 1);
        itemMeta = item.getItemMeta();
        itemMeta.displayName(Messages.getMessage("&9&lNowy kod").decoration(TextDecoration.ITALIC, false));
        lore.clear();
        lore.add((Messages.getMessage("&8» &7Tworzy nowy kod dla użytkownika").decoration(TextDecoration.ITALIC, false)));
        itemMeta.lore(lore);
        item.setItemMeta(itemMeta);
        inv.setItem(15, item);
        // Reload usage of user's code
        item = new ItemStack(Material.COMPASS, 1);
        itemMeta = item.getItemMeta();
        itemMeta.displayName(Messages.getMessage("&d&lZresetuj użycie").decoration(TextDecoration.ITALIC, false));
        lore.clear();
        lore.add((Messages.getMessage("&8» &7Resetuje użycie kodu użytkownika").decoration(TextDecoration.ITALIC, false)));
        itemMeta.lore(lore);
        item.setItemMeta(itemMeta);
        inv.setItem(21, item);
        // Sets user's rank
        item = new ItemStack(Material.SPRUCE_SIGN, 1);
        itemMeta = item.getItemMeta();
        itemMeta.displayName(Messages.getMessage("&6&lUstaw rangę").decoration(TextDecoration.ITALIC, false));
        lore.clear();
        lore.add((Messages.getMessage("&8» &7Ustawia rangę użytkownika").decoration(TextDecoration.ITALIC, false)));
        itemMeta.lore(lore);
        item.setItemMeta(itemMeta);
        inv.setItem(23, item);
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
                        OfflinePlayer p = Bukkit.getPlayer(strings[0]);
                        if (p == null) {
                            commandSender.sendMessage(Messages.getMessage(instance.getConfig().getString("messages.playerNotFound")));
                            return true;
                        }
                        Player cs = (Player) commandSender;
                        try {
                            cs.openInventory(getManageGUI(p.getName(), p));
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
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
