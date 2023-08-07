package me.creuch.dcroles.inventory.signinventory;

import com.bergerkiller.bukkit.common.block.SignEditDialog;
import me.creuch.dcroles.DCRoles;
import me.creuch.dcroles.Database;
import me.creuch.dcroles.TextHandling;
import me.creuch.dcroles.inventory.builderinventory.BInventory;
import me.creuch.dcroles.Config;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class SignInventory {

    private final DCRoles instance;
    private final String type;
    private BInventory BInventory;
    private Database Database;
    private TextHandling TextHandling;
    private Config config;
    private SignEditDialog signEditDialog;

    public SignInventory(DCRoles instance, String type) {
        this.instance = instance;
        this.type = type;
    }

    public void openSignGui(Player p) {
        BInventory = new BInventory(instance);
        Database = new Database(instance);
        config = new Config(instance);
        TextHandling = new TextHandling(instance);
        signEditDialog = new SignEditDialog() {
            @Override
            public void onClosed(Player player, String[] lines) {
                YamlConfiguration yamlConfig = (YamlConfiguration) instance.getConfig();
                if(lines[0] == null || lines[0].isEmpty()) { openSignGui(player); return; }
                HashMap<String, String> modification = new HashMap<>();
                if(type.equalsIgnoreCase("permission")) {
                    modification.put("permission", lines[0]);
                } else {
                    if( lines[0].equalsIgnoreCase("default") || yamlConfig.getConfigurationSection("roles") != null && yamlConfig.getConfigurationSection("roles").getKeys(false).contains(lines[0])) {
                        HashMap<String, String> userData = new HashMap<>();
                        userData.put("exists", "true");
                        userData.put("role", lines[0]);
                        Database.setUserData(userData);
                        player.sendMessage(TextHandling.getFormatted(config.getValue("langConfig", "minecraft.user.successRoleGive")));
                    } else {
                        player.sendMessage(TextHandling.getFormatted(config.getValue("langConfig", "minecraft.user.invalidRole")));
                    }
                    return;
                }
                BInventory.setModification(modification);
                player.openInventory(BInventory.getUpdatedInventory());
            }
        };
        String[] lines;
        if(type.equalsIgnoreCase("permission")) {
            lines = new String[]{"", "================", "Podaj permisję", " "};
        } else {
            lines = new String[]{"", "================", "Podaj rangę", " "};
        }
        signEditDialog.open(p, lines);
    }
}