package me.creuch.dcroles.functions;

import me.creuch.dcroles.DCRoles;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GUI {

    DCRoles DCRoles = new DCRoles();
    Messages Messages = new Messages();
    me.creuch.dcroles.functions.Database Database = new Database();

    public @NotNull ItemStack getConfigItem(String path, String pName, String guiType) throws SQLException {
        DCRoles instance = DCRoles.getInstance();
        HashMap<String, String> user = Database.getUserProfile(Bukkit.getPlayer(pName));
        Material material = Material.getMaterial(instance.getConfig().getString(path + ".material"));
        Component displayName;
        if (guiType == "normal") {
            displayName = Messages.getMessage(instance.getConfig().getString(path + ".name"), Bukkit.getPlayer(pName));
        } else {
            displayName = Messages.getMessage(instance.getConfig().getString(path + ".name"), Bukkit.getPlayer(pName));
        }
        List<String> tempLore = instance.getConfig().getStringList(path + ".lore");
        List<Component> newLore = new ArrayList<>();
        for (String lLine : tempLore) {
            if (guiType == "normal") {
                newLore.add(Messages.getMessage(lLine, Bukkit.getPlayer(pName)).decoration(TextDecoration.ITALIC, false));
            } else {
                newLore.add(Messages.getMessage(lLine, Bukkit.getPlayer(pName)).decoration(TextDecoration.ITALIC, false));
            }
        }
        ItemStack itemStack = new ItemStack(material);
        ItemMeta meta = itemStack.getItemMeta();
        meta.displayName(displayName.decoration(TextDecoration.ITALIC, false));
        meta.lore(newLore);
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    public void saveInvToConfig(Inventory inv, String path) throws SQLException {
        DCRoles instance = DCRoles.getInstance();
        ItemStack is;
        List<String> itemsUsed = new ArrayList<>();
        for (int i = 0; i < inv.getSize(); i++) {
            if (inv.getItem(i) != null && !inv.getItem(i).getType().equals(Material.getMaterial(instance.getConfig().getString("gui.filler.material"))) && !inv.getItem(i).getType().equals(Material.AIR)) {
                is = inv.getItem(i);
                Bukkit.broadcast(Messages.getMessage(is.getType().toString(), null));
                itemsUsed.add(String.valueOf(i));
                if(instance.getConfig().getString(path + i + ".type") == null) {
                    instance.getConfig().set(path + i + ".type", "NONE");
                }
                instance.getConfig().set(path + i + ".permission", "NONE");
                instance.getConfig().set(path + i + ".material", is.getType().toString());
                instance.getConfig().set(path + i + ".name", is.getItemMeta().getDisplayName().replace("§", "&"));
                List<String> newLore = new ArrayList<>();
                if(is.lore() != null && is.lore().size() > 0) {
                    for (String lLine : is.getLore()) {
                        newLore.add(lLine.replace("§", "&"));
                    }
                    instance.getConfig().set(path + i + ".lore", newLore);
                }
                instance.saveConfig();
                instance.reloadConfig();
            }
        }
    }
}
