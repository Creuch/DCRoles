package me.creuch.dcroles.inventory.codemanageinventory;

import me.creuch.dcroles.DCRoles;
import me.creuch.dcroles.TextHandling;
import me.creuch.dcroles.inventory.Items;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public class CMInventory implements InventoryHolder {

    private final DCRoles instance;
    private final Player openP;
    private me.creuch.dcroles.TextHandling TextHandling;
    private Items GetItem;
    private YamlConfiguration config;
    private YamlConfiguration langConfig;
    private Items Items;

    public CMInventory(DCRoles plugin, Player openP) {
        this.instance = plugin;
        this.openP = openP;
    }

    @Override
    public @NotNull Inventory getInventory() {
        TextHandling = new TextHandling(instance);
        GetItem = new Items(instance);
        config = instance.getMainConfig();
        langConfig = instance.getLangConfig();
        Items = new Items(instance);
        Inventory inventory = Bukkit.createInventory(
                this,
                config.getInt("gui.size"),
                TextHandling.getFormatted(config.getString("gui.title"))
        );
        // Set config items
        for (String slot : config.getConfigurationSection("gui.items").getKeys(false)) {
            ItemStack itemStack = GetItem.fromConfig("gui.items." + slot, false);
            ItemMeta itemMeta = itemStack.getItemMeta();
            NamespacedKey  key = new NamespacedKey(instance, "dcrolesPermission");
            String permission = itemMeta.getPersistentDataContainer().get(key, PersistentDataType.STRING);
            if(permission.equalsIgnoreCase("none") || openP.hasPermission(permission)) {
                inventory.setItem(Integer.parseInt(slot), itemStack);
            }
        }
        for(int i = 0; i < inventory.getSize(); i++) {
            if(inventory.getItem(i) == null || inventory.getItem(i).getType() == Material.AIR) {
                inventory.setItem(i, Items.fillerItem());
            }
        }
        return inventory;
    }
}
