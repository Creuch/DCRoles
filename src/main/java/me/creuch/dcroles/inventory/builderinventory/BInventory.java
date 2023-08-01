package me.creuch.dcroles.inventory.builderinventory;

import me.creuch.dcroles.DCRoles;
import me.creuch.dcroles.TextHandling;
import me.creuch.dcroles.inventory.Items;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BInventory implements InventoryHolder {

    private final DCRoles instance;
    private me.creuch.dcroles.TextHandling TextHandling;
    private static int activeItem;
    private Items GetItem;
    private YamlConfiguration config;
    private YamlConfiguration langConfig;
    private static HashMap<String, String> modification;

    public Inventory getUpdatedInventory() {
        Inventory inv = getInventory();
        for(Map.Entry<String, String> entry : modification.entrySet()) {
                if(entry.getKey().equalsIgnoreCase("permission")) {
                    ItemStack itemStack = inv.getItem(activeItem);
                    ItemMeta itemMeta = itemStack.getItemMeta();
                    NamespacedKey key = new NamespacedKey(instance, "dcrolesPermission");
                    itemMeta.getPersistentDataContainer().set(key, PersistentDataType.STRING, entry.getValue());
                    List<Component> lore = itemMeta.lore();
                    for(int i = 0; i < 4; i++) {
                        lore.remove(lore.size() - 1);
                    }
                    key = new NamespacedKey(instance, "dcrolesType");
                    lore.add(TextHandling.getFormatted("&8 "));
                    lore.add(TextHandling.getFormatted("&9&lUstawienia:"));
                    lore.add(TextHandling.getFormatted("&8» &3Typ: &7" + itemMeta.getPersistentDataContainer().get(key, PersistentDataType.STRING)));
                    lore.add(TextHandling.getFormatted("&8» &3Permisja: &7" + entry.getValue()));
                    itemMeta.lore(lore);
                    itemStack.setItemMeta(itemMeta);
                    inv.setItem(activeItem, itemStack);
            }
        }
        instance.setInventory(inv);
        return inv;
    }

    public void setModification(HashMap<String, String> passedModification) {
        modification = passedModification;
    }

    public HashMap<String, String> setModification() {
        return modification;
    }

    public BInventory(DCRoles plugin) {
        this.instance = plugin;
    }

    public int getActiveItem() {
        return activeItem;
    }

    public void setActiveItem(int slot) {
        activeItem = slot;
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
                TextHandling.getFormatted("<#0034FF>&lI<#003BFF>&ln<#0042FF>&lv<#0049FF>&le<#004FFF>&ln<#0056FF>&lt<#005DFF>&lo<#0064FF>&lr<#006BFF>&ly <#0072FF>&lB<#0079FF>&lu<#0080FF>&li<#0086FF>&ll<#008DFF>&ld<#0094FF>&le<#009BFF>&lr")
        );
        // Set config items
        for (String slot : config.getConfigurationSection("gui.items").getKeys(false)) {
            ItemStack itemStack = GetItem.fromConfig("gui.items." + slot, true);
            inventory.setItem(Integer.parseInt(slot), itemStack);
        }
        return inventory;
    }
}
