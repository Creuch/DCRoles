package me.creuch.dcroles.inventory.codemanageinventory;

import me.creuch.dcroles.DCRoles;
import me.creuch.dcroles.TextHandling;
import me.creuch.dcroles.inventory.Items;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class CMInventory implements InventoryHolder {

    private final DCRoles instance;
    private me.creuch.dcroles.TextHandling TextHandling;
    private Items GetItem;
    private YamlConfiguration config;
    private YamlConfiguration langConfig;

    public CMInventory(DCRoles plugin) {
        this.instance = plugin;
    }

    @Override
    public @NotNull Inventory getInventory() {
        TextHandling = new TextHandling(instance);
        GetItem = new Items(instance);
        config = instance.getMainConfig();
        langConfig = instance.getLangConfig();
        Inventory inventory = Bukkit.createInventory(
                this,
                config.getInt("gui.size"),
                TextHandling.getFormatted(config.getString("gui.title"))
        );
        // Set config items
        for (String slot : config.getConfigurationSection("gui.items").getKeys(false)) {
            ItemStack itemStack = GetItem.fromConfig("gui.items." + slot, false);
            inventory.setItem(Integer.parseInt(slot), itemStack);
        }
        return inventory;
    }
}
