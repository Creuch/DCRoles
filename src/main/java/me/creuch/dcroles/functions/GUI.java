package me.creuch.dcroles.functions;

import me.creuch.dcroles.DCRoles;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GUI {

    public static @NotNull ItemStack getConfigItem(String path, String pName, String guiType) throws SQLException {
        DCRoles instance = DCRoles.instance;
        HashMap<String, String> user = Database.getUserProfile(Bukkit.getPlayer(pName));
        Material material = Material.getMaterial(instance.getConfig().getString(path + ".material"));
        Component displayName;
        if (guiType == "normal") {
            displayName = Messages.getMessage(instance.getConfig().getString(path + ".name").replace("§", "&").replace("{USER}", pName).replace("{CODE}", user.get("code")).replace("{RANK}", user.get("role")).replace("{USED}", user.get("used")));
        } else {
            displayName = Messages.getMessage(instance.getConfig().getString(path + ".name").replace("§", "&"));
        }
        List<String> tempLore = instance.getConfig().getStringList(path + ".lore");
        List<Component> newLore = new ArrayList<>();
        for (String lLine : tempLore) {
            if (guiType == "normal") {
                newLore.add(Messages.getMessage(lLine.replace("§", "&").replace("{USER}", pName).replace("{CODE}", user.get("code")).replace("{RANK}", user.get("role")).replace("{USED}", user.get("used"))).decoration(TextDecoration.ITALIC, false));
            } else {
                newLore.add(Messages.getMessage(lLine.replace("§", "&")).decoration(TextDecoration.ITALIC, false));
            }
        }
        ItemStack itemStack = new ItemStack(material);
        ItemMeta meta = itemStack.getItemMeta();
        meta.displayName(displayName.decoration(TextDecoration.ITALIC, false));
        meta.lore(newLore);
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    public static Inventory getGui(String pName, OfflinePlayer p, String guiType, String path) throws SQLException {
        DCRoles instance = DCRoles.instance;
        Inventory inv;
        if(guiType == "normal") {
            inv = Bukkit.createInventory(null, instance.getConfig().getInt("gui.size"), Messages.getMessage(instance.getConfig().getString("text.codeManageInvName")));
        } else {
            inv = Bukkit.createInventory(null, instance.getConfig().getInt("gui.size"), Messages.getMessage(instance.getConfig().getString("text.codeManageTestInvName")));
        }
        ItemStack is;
        for (String s : instance.getConfig().getConfigurationSection("gui.items").getKeys(false)) {
            is = getConfigItem(path + s, pName, guiType);
            String permission = instance.getConfig().getString(path + s + ".permission");
            if (guiType != "normal" || !permission.equalsIgnoreCase("NONE") && ((Player) p).hasPermission(permission)) {
                inv.setItem(Integer.parseInt(s), is);
            }

        }
        if (guiType == "normal") {
            for (int i = 0; i < inv.getSize(); i++) {
                assert inv.getItem(i).getType().equals(Material.AIR);
                ItemStack itemStack = new ItemStack(Material.getMaterial(instance.getConfig().getString("gui.filler.material")));
                if (itemStack.getType() != Material.AIR) {
                    ItemMeta itemMeta = itemStack.getItemMeta();
                    itemMeta.displayName(Messages.getMessage("&7 "));
                    itemStack.setItemMeta(itemMeta);
                    inv.setItem(i, itemStack);
                }
            }
        }
        return inv;
    }

    public static void saveInvToConfig(Inventory inv, String path) {
        DCRoles instance = DCRoles.instance;
        ItemStack is;
        List<String> itemsUsed = new ArrayList<>();
        for (int i = 0; i < inv.getSize(); i++) {
            if (inv.getItem(i) != null && !inv.getItem(i).getType().equals(Material.getMaterial(instance.getConfig().getString("gui.filler.material"))) && !inv.getItem(i).getType().equals(Material.AIR)) {
                is = inv.getItem(i);
                Bukkit.broadcast(Messages.getMessage(is.getType().toString()));
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
