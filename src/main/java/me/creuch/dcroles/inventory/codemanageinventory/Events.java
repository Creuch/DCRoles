package me.creuch.dcroles.inventory.codemanageinventory;

import me.creuch.dcroles.DCRoles;
import me.creuch.dcroles.Database;
import me.creuch.dcroles.TextHandling;
import me.creuch.dcroles.inventory.Items;
import me.creuch.dcroles.inventory.signinventory.SignInventory;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;

public class Events implements Listener {

    private final DCRoles instance;
    private Database Database;
    private TextHandling TextHandling;
    private SignInventory SignInventory;
    private YamlConfiguration config;
    private YamlConfiguration langConfig;
    private CMInventory CMInventory;
    private static Inventory inventory;

    public Events(DCRoles instance) {
        this.instance = instance;
    }

    public Inventory getInventory() {
        return inventory;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (!(e.getInventory().getHolder() instanceof CMInventory)) {
            return;
        }
        e.setCancelled(true);
        // Assign variables
        Database = new Database(instance);
        TextHandling = new TextHandling(instance);
        SignInventory = new SignInventory(instance, "role");
        config = instance.getMainConfig();
        langConfig = instance.getLangConfig();
        Items Items = new Items(instance);
        CMInventory = new CMInventory(instance, (Player) e.getWhoClicked());
        if (config.getString("gui.items." + e.getSlot() + ".permission") != null) {
            if (config.getString("gui.items." + e.getSlot() + ".permission").equalsIgnoreCase("none") ||  e.getWhoClicked().hasPermission(config.getString("gui.items." + e.getSlot() + ".permission"))) {
                String type = config.getString("gui.items." + e.getSlot() + ".type");
                if (type == null || type.isEmpty() || type == "null") {
                    return;
                }
                // Get player
                HashMap<String, String> userData;
                switch (type) {
                    case ("REMOVE_USER"):
                        Database.removeUserProfile();
                        e.getWhoClicked().sendMessage(TextHandling.getFormatted(langConfig.getString("minecraft.user.successProfileRemove")));
                        e.getWhoClicked().openInventory(CMInventory.getInventory());
                        return;
                    case ("NEW_CODE"):
                        userData = new HashMap<>();
                        userData.put("exists", "true");
                        userData.put("code", instance.getCode());
                        Database.setUserData(userData);
                        e.getWhoClicked().sendMessage(TextHandling.getFormatted(langConfig.getString("minecraft.user.successNewCode")));
                        e.getWhoClicked().openInventory(CMInventory.getInventory());
                        return;
                    case ("RESET_USAGE"):
                        userData = new HashMap<>();
                        userData.put("exists", "true");
                        userData.put("code", instance.getCode());
                        userData.put("used", "false");
                        Database.setUserData(userData);
                        e.getWhoClicked().sendMessage(TextHandling.getFormatted(langConfig.getString("minecraft.user.successUsageReset")));
                        e.getWhoClicked().openInventory(CMInventory.getInventory());
                        return;
                    case ("SET_RANK"):
                        if (Database.getUserData().get("exists") == "true") {
                            SignInventory.openSignGui((Player) e.getWhoClicked());
                        } else {
                            e.getWhoClicked().sendMessage(TextHandling.getFormatted(langConfig.getString("minecraft.user.playerNotFound")));
                        }
                }
            }
        }
    }
}
