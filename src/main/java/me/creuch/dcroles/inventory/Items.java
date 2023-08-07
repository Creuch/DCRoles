package me.creuch.dcroles.inventory;

import me.creuch.dcroles.Config;
import me.creuch.dcroles.DCRoles;
import me.creuch.dcroles.TextHandling;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.inventory.meta.tags.ItemTagType;
import org.bukkit.persistence.PersistentDataType;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Items {

    private final DCRoles instance;
    private Config config;
    private TextHandling TextHandling;

    public Items(DCRoles plugin) {
        this.instance = plugin;
    }

    public ItemStack fillerItem() {
        config = new Config(instance);
        TextHandling = new TextHandling(instance);
        ItemStack itemStack = new ItemStack(Material.getMaterial(config.getValue("mainConfig", "gui.filler")));
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.displayName(TextHandling.getFormatted("&7 "));
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public ItemStack fromConfig(String path, Boolean detailedDesc) {
        config = new Config(instance);
        TextHandling = new TextHandling(instance);
        ItemStack itemStack = new ItemStack(Material.getMaterial(config.getValue("mainConfig", path + ".material")));
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.displayName(TextHandling.getFormatted(config.getValue("mainConfig", path + ".name")).decoration(TextDecoration.ITALIC, false));
        List<Component> lore = new ArrayList<>();
        for(String loreLine : config.getList("mainConfig", path + ".lore")) {
            lore.add(TextHandling.getFormatted(loreLine).decoration(TextDecoration.ITALIC, false));
        }
        if(detailedDesc) {
            lore.add(TextHandling.getFormatted("&8 "));
            lore.add(TextHandling.getFormatted("&9&lUstawienia:"));
            lore.add(TextHandling.getFormatted("&8» &3Typ: &7" + config.getValue("mainConfig", path + ".type")));
            lore.add(TextHandling.getFormatted("&8» &3Permisja: &7" + config.getValue("mainConfig", path + ".permission")));
        }
        NamespacedKey key = new NamespacedKey(instance, "dcrolesType");
        itemMeta.getPersistentDataContainer().set(key, PersistentDataType.STRING, config.getValue("mainConfig", path + ".type"));
        key = new NamespacedKey(instance, "dcrolesPermission");
        itemMeta.getPersistentDataContainer().set(key, PersistentDataType.STRING, config.getValue("mainConfig", path + ".permission"));
        itemMeta.lore(lore);
        itemStack.setItemMeta(itemMeta);
        // Set owning player
        if(itemStack.getType() == Material.PLAYER_HEAD) {
            SkullMeta skullMeta = (SkullMeta) itemStack.getItemMeta();
            skullMeta.setOwningPlayer(instance.getPlayer());
            itemStack.setItemMeta(skullMeta);
        }
        return itemStack;
    }

    public void setToConfig(String path, ItemStack itemStack) {
        YamlConfiguration yamlConfig = (YamlConfiguration) instance.getConfig();
        TextHandling = new TextHandling(instance);
        String material = itemStack.getType().toString();
        ItemMeta itemMeta = itemStack.getItemMeta();
        String displayName = MiniMessage.miniMessage().serialize(itemMeta.displayName());
        List<String> lore = new ArrayList<>();
        for(Component loreLine : itemStack.lore()) {
            lore.add(MiniMessage.miniMessage().serialize(loreLine));
        }
        NamespacedKey key = new NamespacedKey(instance, "dcrolesType");
        String type = itemMeta.getPersistentDataContainer().get(key, PersistentDataType.STRING);
        key = new NamespacedKey(instance, "dcrolesPermission");
        String permission = itemMeta.getPersistentDataContainer().get(key, PersistentDataType.STRING);
        // Save to config
        yamlConfig.set(path + ".type", type);
        yamlConfig.set(path + ".permission", permission);
        yamlConfig.set(path + ".material", material);
        yamlConfig.set(path + ".name", displayName);
        yamlConfig.set(path + ".lore", lore);
        try {
            yamlConfig.save(new File(instance.getDataFolder(), "config.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}