package me.creuch.dcroles.inventory.itemsettingsinventory;

import me.creuch.dcroles.DCRoles;
import me.creuch.dcroles.Database;
import me.creuch.dcroles.TextHandling;
import me.creuch.dcroles.inventory.Items;
import me.creuch.dcroles.inventory.builderinventory.BInventory;
import me.creuch.dcroles.inventory.builderinventory.ConfirmInventory;
import me.creuch.dcroles.inventory.codemanageinventory.CMInventory;
import me.creuch.dcroles.inventory.signinventory.SignInventory;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;

public class Events implements Listener {

    private final DCRoles instance;
    private static Inventory inventory;
    private BInventory BInventory;
    private SignInventory SignInventory;
    private Items Items;
    private Database Database;
    private TextHandling TextHandling;
    private YamlConfiguration config;
    private YamlConfiguration langConfig;

    public Events(DCRoles instance) {
        this.instance = instance;
    }

    public Inventory getInventory() {
        return inventory;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (((e.getInventory().getHolder() instanceof ISInventory))) {
            e.setCancelled(true);
            // Assign variables
            Items = new Items(instance);
            Database = new Database(instance);
            TextHandling = new TextHandling(instance);
            BInventory = new BInventory(instance);
            config = instance.getMainConfig();
            langConfig = instance.getLangConfig();
            Inventory cmInventory = new CMInventory(instance).getInventory();
            if (e.getSlot() == 11) {
                SignInventory = new SignInventory(instance, "permission");
                SignInventory.openSignGui((Player) e.getWhoClicked());
            } else if (e.getSlot() == 15) {
                SignInventory = new SignInventory(instance, "role");
                SignInventory.openSignGui((Player) e.getWhoClicked());
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        InventoryHolder ih = e.getInventory().getHolder();
        if (!(ih instanceof BInventory) && (!(ih instanceof ISInventory))) {
            return;
        }
        if (e.getReason().equals(InventoryCloseEvent.Reason.OPEN_NEW)) {
            return;
        }
        inventory = e.getInventory();
        instance.setInventory(inventory);
        Player player = (Player) e.getPlayer();
        // Open ConfirmInventory after a 1 tick (0.05 seconds) delay
        new BukkitRunnable() {
            @Override
            public void run() {
                if(ih instanceof BInventory) {
                    ConfirmInventory confirmInventory = new ConfirmInventory(instance);
                    player.openInventory(confirmInventory.getInventory());
                } else {
                    player.openInventory(instance.getInventory());
                }
            }
        }.runTaskLater(instance, 1);
    }
}
