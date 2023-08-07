package me.creuch.dcroles.inventory.itemsettingsinventory;

import me.creuch.dcroles.DCRoles;
import me.creuch.dcroles.TextHandling;
import me.creuch.dcroles.inventory.Items;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

public class ISInventory implements InventoryHolder {

    private final DCRoles instance;
    private final int slot;
    private Inventory inventory;
    private me.creuch.dcroles.TextHandling TextHandling;
    private Items GetItem;
    private YamlConfiguration config;
    private YamlConfiguration langConfig;

    public ISInventory(DCRoles instance, int slot) {
        this.instance = instance;
        this.slot = slot;
    }

    @Override
    public @NotNull Inventory getInventory() {
        inventory = instance.getInventory();
        TextHandling = new TextHandling(instance);
        GetItem = new Items(instance);
        Inventory inv;
        inv = Bukkit.createInventory(
                this,
                27,
                TextHandling.getFormatted("&a&lUstawienia"));
        ItemStack itemStack = new ItemStack(Material.AIR);
        ItemMeta itemMeta;
        // Confirm item
        itemStack.setType(Material.BARRIER);
        itemMeta = itemStack.getItemMeta();
        itemMeta.displayName(TextHandling.getFormatted("&c&lUstaw permisję").decoration(TextDecoration.ITALIC, false));
        itemStack.setItemMeta(itemMeta);
        inv.setItem(11, itemStack);
        // Reject item
        itemStack.setType(Material.BIRCH_SIGN);
        itemMeta = itemStack.getItemMeta();
        itemMeta.displayName(TextHandling.getFormatted("&b&lUstaw typ").decoration(TextDecoration.ITALIC, false));
        itemStack.setItemMeta(itemMeta);
        inv.setItem(15, itemStack);
        // Preview item
        inv.setItem(13, inventory.getItem(slot));

        // Filler
        for(int i = 0; i < 27; i++) {
            ItemStack filler;
            if(i == 4 || i == 12 || i == 14 || i == 22) {
                filler = new ItemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE);
            } else {
                filler = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
            }
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