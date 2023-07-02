package me.creuch.dcroles.events;

import me.creuch.dcroles.DCRoles;
import me.creuch.dcroles.commands.DCMCode;
import me.creuch.dcroles.functions.Messages;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class onInventoryClick implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        ItemStack clicked = event.getCurrentItem();
        assert event.getCurrentItem().getType() != null && event.getCurrentItem() != null;
        DCRoles instance = DCRoles.instance;
        if (event.getView().title().equals(Messages.getMessage(instance.getConfig().getString("text.codeManageInvName")))) {
            event.setCancelled(true);
        }
        if (clicked.getType().equals(Material.SPRUCE_SIGN)) {
            event.getWhoClicked().closeInventory();
        }
        if (clicked.getType().equals(Material.LAVA_BUCKET)) {
            Inventory inventory = event.getInventory();
            SkullMeta skullMeta = (SkullMeta) inventory.getItem(13).getItemMeta();
            String pName = skullMeta.getOwningPlayer().getName();
            OfflinePlayer player = Bukkit.getPlayer(pName);
            DCMCode.getManageGUI(pName, player);
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                DCRoles instance = DCRoles.instance;
                Inventory inventory = event.getInventory();
                SkullMeta skullMeta = (SkullMeta) inventory.getItem(13).getItemMeta();
                String pName = skullMeta.getOwningPlayer().getName();
                OfflinePlayer player = Bukkit.getPlayer(pName);
                if (event.getView().title().equals(Messages.getMessage(instance.getConfig().getString("text.codeManageInvName")))) {
                    if (clicked.getType().equals(Material.LAVA_BUCKET)) {
                        try {
                            Long code = DCRoles.generateCode();
                            List<String> messages = instance.getConfig().getStringList("messages.firstJoin");
                            event.getWhoClicked().sendMessage(Messages.getMessage(instance.getConfig().getString("messages.removedUser").replace("{USER}", pName)));
                            if (player.isOnline()) {
                                for (String s : messages) {
                                    s = s.replace("{RANK}", instance.getConfig().getString("text.defualtRoleReplace"));
                                    s = s.replace("{CODE}", code.toString());
                                    ((Player) player).sendMessage(Messages.getMessage(s));
                                }
                            }
                            Connection conn = DCRoles.dbConnect(instance.getServer().getConsoleSender());
                            Statement stmt = conn.createStatement();
                            Integer removeUser = stmt.executeUpdate(String.format("DELETE FROM discordRoles WHERE username = '%s'", pName));
                            Integer createUser = stmt.executeUpdate(String.format("INSERT INTO discordRoles(username, role, code, used) VALUES('%s', 'default', '%s', false)", pName, code.toString()));
                            stmt.close();
                            conn.close();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                    if (clicked.getType().equals(Material.ENDER_PEARL)) {
                        try {
                            Long code = DCRoles.generateCode();
                            event.getWhoClicked().sendMessage(Messages.getMessage(instance.getConfig().getString("messages.newCode").replace("{USER}", pName).replace("{CODE}", code.toString())));
                            Connection conn = DCRoles.dbConnect(instance.getServer().getConsoleSender());
                            Statement stmt = conn.createStatement();
                            Integer resetCode = stmt.executeUpdate(String.format("UPDATE discordRoles SET code = '%s' WHERE username = '%s'", code.toString(), pName));
                            conn.close();
                            stmt.close();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                    if (clicked.getType().equals(Material.COMPASS)) {
                        try {
                            event.getWhoClicked().sendMessage(Messages.getMessage(instance.getConfig().getString("messages.codeUsageReset").replace("{USER}", pName)));
                            Connection conn = DCRoles.dbConnect(instance.getServer().getConsoleSender());
                            Statement stmt = conn.createStatement();
                            Integer resetCodeUsage = stmt.executeUpdate(String.format("UPDATE discordRoles SET used = 0 WHERE username = '%s'", pName));
                            conn.close();
                            stmt.close();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                    if (clicked.getType().equals(Material.SPRUCE_SIGN)) {
                        event.getWhoClicked().sendMessage(Messages.getMessage(instance.getConfig().getString("messages.grantRank").replace("{USER}", pName)));
                    }
                }
            }
        }.runTaskAsynchronously(instance);
    }
}
