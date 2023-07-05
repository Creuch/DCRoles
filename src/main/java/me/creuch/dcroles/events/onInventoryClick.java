package me.creuch.dcroles.events;

import me.creuch.dcroles.DCRoles;
import me.creuch.dcroles.functions.Database;
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

import java.sql.SQLException;
import java.util.List;

public class onInventoryClick implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        try {
            ItemStack clicked = event.getCurrentItem();
            DCRoles instance = DCRoles.instance;
            if (event.getView().title().equals(Messages.getMessage(instance.getConfig().getString("text.codeManageInvName")))) {
                event.setCancelled(true);
            }
            if (!clicked.getType().equals(Material.GRAY_STAINED_GLASS_PANE)) {
                event.getWhoClicked().closeInventory();
            }
            new BukkitRunnable() {
                @Override
                public void run() {
                    Inventory inventory = event.getInventory();
                    SkullMeta skullMeta = (SkullMeta) inventory.getItem(13).getItemMeta();
                    String pName = skullMeta.getOwningPlayer().getName();
                    OfflinePlayer player = Bukkit.getPlayer(pName);
                    assert event.getView().title().equals(Messages.getMessage(instance.getConfig().getString("text.codeManageInvName")));
                    if (clicked.getType().equals(Material.LAVA_BUCKET)) {
                        // Generate code
                        Long code = DCRoles.generateCode();
                        // Get messages from config and send them to user if online
                        List<String> messages = instance.getConfig().getStringList("messages.firstJoin");
                        event.getWhoClicked().sendMessage(Messages.getMessage(instance.getConfig().getString("messages.removedUser").replace("{USER}", pName)));
                        if (player.isOnline()) {
                            for (String s : messages) {
                                s = s.replace("{RANK}", instance.getConfig().getString("text.defualtRoleReplace"));
                                s = s.replace("{CODE}", code.toString());
                                ((Player) player).sendMessage(Messages.getMessage(s));
                            }
                        }
                        // Update user in database
                        Database.updateUser(pName, "removeUser", code.toString(), event.getWhoClicked());

                    } else if (clicked.getType().equals(Material.ENDER_PEARL)) {
                        // Generate code
                        Long code = DCRoles.generateCode();
                        event.getWhoClicked().sendMessage(Messages.getMessage(instance.getConfig().getString("messages.newCode").replace("{USER}", pName).replace("{CODE}", code.toString())));
                        // Update user in database
                        Database.updateUser(pName, "updateCode", code.toString(), event.getWhoClicked());
                    } else if (clicked.getType().equals(Material.COMPASS)) {
                        event.getWhoClicked().sendMessage(Messages.getMessage(instance.getConfig().getString("messages.codeUsageReset").replace("{USER}", pName)));
                        // Update user in database
                        Database.updateUser(pName, "resetUsage", null, event.getWhoClicked());
                    } else if (clicked.getType().equals(Material.SPRUCE_SIGN)) {
                        event.getWhoClicked().sendMessage(Messages.getMessage(instance.getConfig().getString("messages.grantRank").replace("{USER}", pName)));
                        // Inventory is closed before Async function starts
                    }
                }
            }.runTaskAsynchronously(instance);
            // Ignore exception thrown when user clicks outside of inventory
        } catch (NullPointerException ignored) {
        }
    }
}
