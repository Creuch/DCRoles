package me.creuch.dcroles.inventory.builderinventory;

import me.creuch.dcroles.Config;
import me.creuch.dcroles.DCRoles;
import me.creuch.dcroles.Database;
import me.creuch.dcroles.TextHandling;
import me.creuch.dcroles.inventory.Items;
import me.creuch.dcroles.inventory.codemanageinventory.CMInventory;
import me.creuch.dcroles.inventory.itemsettingsinventory.ISInventory;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class Events implements Listener {

    private final DCRoles instance;
    private static Inventory inventory;
    private BInventory BInventory;
    private Items Items;
    private Database Database;
    private TextHandling TextHandling;

    public Events(DCRoles instance) {
        this.instance = instance;
    }

    public Inventory getInventory() {
        return inventory;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        Config config = new Config(instance);
        BInventory = new BInventory(instance);
        if ((e.getInventory().getHolder() instanceof BInventory)) {
            if (e.getClick().isRightClick()) {
                e.setCancelled(true);
                if(e.getCurrentItem() != null && e.getCurrentItem().getType() != Material.AIR) {
                    assert (e.getCurrentItem() != null);
                    BInventory.setActiveItem(e.getSlot());
                    inventory = e.getInventory();
                    instance.setInventory(inventory);
                    ISInventory ISInventory = new ISInventory(instance, e.getSlot());
                    e.getWhoClicked().openInventory(ISInventory.getInventory());
                }
            }
        } else if (((e.getInventory().getHolder() instanceof ConfirmInventory))) {
            e.setCancelled(true);
            // Assign variables
            Items = new Items(instance);
            Database = new Database(instance);
            TextHandling = new TextHandling(instance);
            if (e.getSlot() == 12) {
                e.getWhoClicked().closeInventory();
                e.getWhoClicked().sendMessage(TextHandling.getFormatted(config.getValue("langConfig", "minecraft.server.savedGui")));
                Inventory bInventory = instance.getInventory();
                ConfigurationSection section = config.getMainConfig().getConfigurationSection("gui.items");
                for(String s : section.getKeys(true)) {
                    section.set(s, null);
                }
                try {
                    config.getMainConfig().save(new File(instance.getDataFolder(), "config.yml"));
                    config.loadConfigs();
                    config = new Config(instance);
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
                int i = 0;
                for(ItemStack is : bInventory) {
                    if(is != null) {
                        if(is.getType() != Material.AIR) {
                            ItemMeta itemMeta = is.getItemMeta();
                            List<Component> lore = itemMeta.lore();
                            if (lore != null && lore.size() >= 4) {
                                for (int lLine = 0; lLine < 4; lLine++) {
                                    lore.remove(lore.size() - 1);
                                }
                                itemMeta.lore(lore);
                            }
                            is.setItemMeta(itemMeta);
                            Items.setToConfig("gui.items." + i, is);
                        }
                    }
                    i += 1;
                }
                try {
                    config.getMainConfig().save(new File(instance.getDataFolder(), "config.yml"));
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                config.loadConfigs();
            } else if (e.getSlot() == 14) {
                e.getWhoClicked().openInventory(instance.getInventory());
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        if ((e.getInventory().getHolder() instanceof BInventory)) {
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
                    ConfirmInventory confirmInventory = new ConfirmInventory(instance);
                    player.openInventory(confirmInventory.getInventory());
                }
            }.runTaskLater(instance, 1);
        }
    }
}
