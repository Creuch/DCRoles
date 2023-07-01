package me.creuch.dcroles.commands;

import me.creuch.dcroles.DCRoles;
import me.creuch.dcroles.functions.Messages;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class DCMCode implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if(command.getName().equalsIgnoreCase("dcmcode")) {
            DCRoles instance = DCRoles.instance;
            if(commandSender.hasPermission("dcr.dcmcode")) {
                if(strings.length == 0) {
                    List<String> messages = instance.getConfig().getStringList("messages.helpFormat");
                    for (String msg : messages) {
                        commandSender.sendMessage(Messages.getMessage(msg));
                    }
                } else {
                    OfflinePlayer p = Bukkit.getPlayer(strings[0]);
                    if(p == null) {
                        commandSender.sendMessage(Messages.getMessage(instance.getConfig().getString("messages.playerNotFound")));
                        return true;
                    }
                    String code = DCRoles.getData(p, "code");
                    String rank = DCRoles.getData(p, "rank");
                    Boolean used = Boolean.valueOf(DCRoles.getData(p, "used"));
                    Player cs = (Player) commandSender;
                    cs.openInventory(getManageGUI(p.getName(), p));
                }
            } else {
                commandSender.sendMessage(Messages.getMessage(instance.getConfig().getString("messages.noPermission")));
            }
        }
        return true;
    }

    public Inventory getManageGUI(String pName, OfflinePlayer p) {
        String code = DCRoles.getData(p, "code");
        String rank = DCRoles.getData(p, "rank");
        Boolean used = Boolean.valueOf(DCRoles.getData(p, "used"));
        DCRoles instance = DCRoles.instance;
        Inventory inv = Bukkit.createInventory(null, 27, Messages.getMessage(instance.getConfig().getString("text.codeManageInvName").replace("{USER}", pName)));
        // Background of the inventory
        for(int i = 0; i < inv.getSize(); i++) {
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
        lore.add((Messages.getMessage("&7Usuwa całkowicie z bazy danych").decoration(TextDecoration.ITALIC, false)));
        itemMeta.lore(lore);
        item.setItemMeta(itemMeta);
        inv.setItem(11, item);
        // Reload user's code
        return inv;
    }
}
