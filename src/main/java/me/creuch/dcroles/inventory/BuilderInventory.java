package me.creuch.dcroles.inventory;

import me.creuch.dcroles.DCRoles;
import me.creuch.dcroles.functions.Database;
import me.creuch.dcroles.functions.GUI;
import me.creuch.dcroles.functions.Messages;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BuilderInventory implements InventoryHolder {
    DCRoles DCRoles = new DCRoles();
    Messages Messages = new Messages();
    private Player p;

    public void setPlayer(Player player) {
        p = player;
    }

    @Override
    public @NotNull Inventory getInventory() {
        DCRoles instance = DCRoles.instance;
        Inventory inv;
        try {
            inv = Bukkit.createInventory(
                    null,
                    instance.getConfig().getInt("gui.size"),
                    Messages.getMessage(instance.getConfig().getString("text.codeManageInvName"), p));
            ItemStack itemStack;
            for (String slot : instance.getConfig().getConfigurationSection("gui.items").getKeys(false)) {
                itemStack = getItemStack(Integer.parseInt(slot), p);

            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return inv;
    }

    public ItemStack getItemStack(int slot, OfflinePlayer p) throws SQLException {
        String path = "gui.items." + slot + ".";
        DCRoles instance = DCRoles.instance;
        FileConfiguration config = instance.getConfig();
        ItemStack itemStack = new ItemStack(Material.getMaterial(config.getString(path + "material")));
        ItemMeta itemMeta = itemStack.getItemMeta();
        Component displayName = Messages.getMessage(config.getString(path + "name"), p);
        itemMeta.displayName(displayName);
        if (config.getStringList(path + "lore").size() > 0) {
            List<Component> lore = new ArrayList<>();
            for (String s : config.getStringList(path + "lore")) {
                lore.add(Messages.getMessage(s, p).decoration(TextDecoration.ITALIC, false));
            }
            itemMeta.lore(lore);
        }
        return itemStack;
    }

}
