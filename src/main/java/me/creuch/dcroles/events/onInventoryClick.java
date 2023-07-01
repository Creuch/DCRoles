package me.creuch.dcroles.events;

import me.creuch.dcroles.DCRoles;
import me.creuch.dcroles.functions.Messages;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
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
        DCRoles instance = DCRoles.instance;
        if (event.getView().title().equals(Messages.getMessage(instance.getConfig().getString("text.codeManageInvName")))) {
            event.setCancelled(true);
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                DCRoles instance = DCRoles.instance;
                ItemStack clicked = event.getCurrentItem();
                assert clicked != null;
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
                            Integer createPlayerRank = stmt.executeUpdate(String.format("INSERT INTO discordRoles(username, role, code, used) VALUES('%s', 'default', '%s', false)", pName, code));
                            conn.close();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }.runTaskAsynchronously(instance);
    }
}
