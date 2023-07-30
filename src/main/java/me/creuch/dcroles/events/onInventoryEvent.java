package me.creuch.dcroles.events;

import me.creuch.dcroles.DCRoles;
import me.creuch.dcroles.functions.Database;
import me.creuch.dcroles.functions.GUI;
import me.creuch.dcroles.functions.Messages;
import me.creuch.dcroles.inventory.BuilderInventory;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.SQLException;
import java.util.List;

public class onInventoryEvent implements Listener {

    DCRoles DCRoles = new DCRoles();
    Messages Messages = new Messages();
    Database Database = new Database();

    @EventHandler
    public void onConfirmInventoryClick(InventoryClickEvent event) throws SQLException {
        DCRoles instance = DCRoles.getInstance();
        if(event.getInventory().getItem(12) != null && event.getInventory().getItem(14) != null && event.getInventory().getItem(12).getType().equals(Material.GREEN_CONCRETE) && event.getInventory().getItem(14).getType().equals(Material.RED_CONCRETE)) {
            event.setCancelled(true);
            if(event.getWhoClicked().hasMetadata("DCRolesOpened")) {
                event.getWhoClicked().removeMetadata("DCRolesOpened", instance);
            }
            if(event.getSlot() == 12) {
//                GUI.saveInvToConfig(TUTAJOTWÓRZGUI);
                for (String s : instance.getConfig().getConfigurationSection("tempGui").getKeys(true)) {
                    instance.getConfig().set("tempGui." + s, null);
                }
                instance.saveConfig();
                instance.reloadConfig();
            } else {
                for (String s : instance.getConfig().getConfigurationSection("tempGui").getKeys(true)) {
                    instance.getConfig().set("tempGui." + s, null);
                }
                instance.saveConfig();
                instance.reloadConfig();
            }
        }
    }

    // Main GUI
    @EventHandler(priority =  EventPriority.LOWEST)
    public void onInventoryClick(InventoryClickEvent event) throws SQLException {
        DCRoles instance = DCRoles.instance;
        try {
            if(
                    !(event.getView().getTopInventory().getSize() == 36)
                    || !(event.getClickedInventory().getSize() == 36)
                    || !event.getView().title().equals(Messages.getMessage(instance.getConfig().getString("text.codeManageInvName"), (Player) event.getWhoClicked()))
                    || !event.getInventory().getItem(13).getType().equals(Material.PLAYER_HEAD)
            ) {
            } else {
                HumanEntity p = event.getWhoClicked();
                event.getWhoClicked().closeInventory();
                event.setCancelled(true);
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        Inventory inventory = event.getInventory();
                        SkullMeta skullMeta;
                        String type = " ";
                        if(instance.getConfig().getString("gui.items." + event.getSlot() + ".type") !=null) {type = instance.getConfig().getString("gui.items." + event.getSlot() + ".type");}
                        if (inventory.getItem(13).getType().equals(Material.PLAYER_HEAD)) {
                            skullMeta = (SkullMeta) inventory.getItem(13).getItemMeta();
                        } else {return;}
                        String pName = skullMeta.getOwningPlayer().getName();
                        OfflinePlayer player = Bukkit.getPlayer(pName);
                            try {
                                switch (type) {
                                    case "REMOVE_USER": {
                                        // Generate code
                                        Long code = DCRoles.generateCode();
                                        // Get messages from config and send them to user if online
                                        List<String> messages = instance.getConfig().getStringList("messages.firstJoin");
                                        event.getWhoClicked().sendMessage(Messages.getMessage(instance.getConfig().getString("messages.removedUser").replace("{USER}", pName), (Player) event.getWhoClicked()));
                                        if (player.isOnline()) {
                                            for (String s : messages) {
                                                s = s.replace("{RANK}", instance.getConfig().getString("text.defualtRoleReplace"));
                                                s = s.replace("{CODE}", code.toString());
                                                ((Player) player).sendMessage(Messages.getMessage(s, (Player) event.getWhoClicked()));
                                            }
                                        }
                                        // Update user in database
                                        Database.updateUser(pName, "removeUser", code.toString(), event.getWhoClicked());

                                        break;
                                    }
                                    case "NEW_CODE": {
                                        // Generate code
                                        Long code = DCRoles.generateCode();
                                        event.getWhoClicked().sendMessage(Messages.getMessage(instance.getConfig().getString("messages.newCode"), (Player) event.getWhoClicked()));
                                        // Update user in database
                                        Database.updateUser(pName, "updateCode", code.toString(), event.getWhoClicked());
                                        break;
                                    }
                                    case "RESET_USAGE":
                                        event.getWhoClicked().sendMessage(Messages.getMessage(instance.getConfig().getString("messages.codeUsageReset"), (Player) event.getWhoClicked()));
                                        // Update user in database
                                        Database.updateUser(pName, "resetUsage", null, event.getWhoClicked());
                                        break;
                                    case "SET_RANK":
                                        event.getWhoClicked().sendMessage(Messages.getMessage(instance.getConfig().getString("messages.grantRank"), (Player) event.getWhoClicked()));
                                        // Inventory is closed before Async function starts
                                        break;
                                }
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                    }
                }.runTaskAsynchronously(instance);
            }
            // Ignore exception thrown when user clicks outside of inventory
        } catch (NullPointerException ignored) {
        }
    }

    // Test GUI
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) throws SQLException {
        DCRoles instance = DCRoles.instance;
        if(event.getInventory().getItem(12) != null && event.getInventory().getItem(14) != null && event.getInventory().getItem(12).getType().equals(Material.GREEN_CONCRETE) && event.getInventory().getItem(14).getType().equals(Material.RED_CONCRETE)) {
            if(event.getPlayer().hasMetadata("DCRolesOpened")) {
                event.getPlayer().removeMetadata("DCRolesOpened", instance);
            }
            for (String s : instance.getConfig().getConfigurationSection("tempGui").getKeys(true)) {
                instance.getConfig().set("tempGui." + s, null);
            }
            instance.saveConfig();
            instance.reloadConfig();
        }
        if(event.getView().title().equals(Messages.getMessage(instance.getConfig().getString("text.codeManageTestInvName"), (Player) event.getPlayer()))) {
            Inventory inv = event.getInventory();
            Inventory confirmInventory = Bukkit.createInventory(null, 27, Messages.getMessage("&a&lPotwierdź zmiany", (Player) event.getPlayer()));
            ItemStack itemStack = new ItemStack(Material.AIR);
            ItemMeta itemMeta;
            // Confirm item
            itemStack.setType(Material.GREEN_CONCRETE);
            itemMeta = itemStack.getItemMeta();
            itemMeta.displayName(Messages.getMessage("&a&lPOTWIERDŹ", (Player) event.getPlayer()));
            itemStack.setItemMeta(itemMeta);
            confirmInventory.setItem(12, itemStack);
            // Reject item
            itemStack.setType(Material.RED_CONCRETE);
            itemMeta = itemStack.getItemMeta();
            itemMeta.displayName(Messages.getMessage("&c&lANULUJ", (Player) event.getPlayer()));
            itemStack.setItemMeta(itemMeta);
            confirmInventory.setItem(14, itemStack);
            // Filler
            for(int i = 0; i < 27; i++) {
                ItemStack filler = new ItemStack(Material.getMaterial(instance.getConfig().getString("gui.filler.material")));
                ItemMeta fillerMeta = filler.getItemMeta();
                fillerMeta.displayName(Messages.getMessage("&7 ", (Player) event.getPlayer()));
                filler.setItemMeta(fillerMeta);
                if(confirmInventory.getItem(i) == null || confirmInventory.getItem(i).getType() == Material.AIR) {
                    confirmInventory.setItem(i, filler);
                }
            }
            if(!event.getPlayer().hasMetadata("DCRolesOpened") || event.getPlayer().getMetadata("DCRolesOpened") == new FixedMetadataValue(instance, false)) {
                event.getPlayer().setMetadata("DCRolesOpened", new FixedMetadataValue(instance, true));
                if(!event.getPlayer().getOpenInventory().title().contains(Messages.getMessage("Crafting", (Player) event.getPlayer()))) {
                    event.getPlayer().closeInventory();
                }
//                GUI.saveInvToConfig(inv, "tempGui.");
                event.getPlayer().openInventory(confirmInventory);
            }
        }
    }
}

