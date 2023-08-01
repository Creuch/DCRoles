package me.creuch.dcroles.inventory.builderinventory;

import me.creuch.dcroles.DCRoles;
import me.creuch.dcroles.TextHandling;
import me.creuch.dcroles.inventory.Items;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

public class ConfirmInventory implements InventoryHolder {

    private final DCRoles instance;
    private me.creuch.dcroles.TextHandling TextHandling;
    private Items GetItem;
    private YamlConfiguration config;
    private YamlConfiguration langConfig;

    public ConfirmInventory(DCRoles plugin) {
        this.instance = plugin;
    }

    @Override
    public @NotNull Inventory getInventory() {
        TextHandling = new TextHandling(instance);
        GetItem = new Items(instance);
        config = instance.getMainConfig();
        langConfig = instance.getLangConfig();
        Inventory inv;
        inv = Bukkit.createInventory(
                this,
                27,
                TextHandling.getFormatted("&a&lPotwierdź zmiany"));
        ItemStack itemStack = new ItemStack(Material.AIR);
        ItemMeta itemMeta;
        // Confirm item
        itemStack.setType(Material.GREEN_CONCRETE);
        itemMeta = itemStack.getItemMeta();
        itemMeta.displayName(TextHandling.getFormatted("&a&lPOTWIERDŹ"));
        itemStack.setItemMeta(itemMeta);
        inv.setItem(12, itemStack);
        // Reject item
        itemStack.setType(Material.RED_CONCRETE);
        itemMeta = itemStack.getItemMeta();
        itemMeta.displayName(TextHandling.getFormatted("&c&lANULUJ"));
        itemStack.setItemMeta(itemMeta);
        inv.setItem(14, itemStack);
        // Filler
        for(int i = 0; i < 27; i++) {
            ItemStack filler = new ItemStack(Material.getMaterial(config.getString("gui.filler")));
            ItemMeta fillerMeta = filler.getItemMeta();
            fillerMeta.displayName(TextHandling.getFormatted("&7 "));
            filler.setItemMeta(fillerMeta);
            if(inv.getItem(i) == null || inv.getItem(i).getType() == Material.AIR) {
                inv.setItem(i, filler);
            }
        }
        return inv;
    }
}

