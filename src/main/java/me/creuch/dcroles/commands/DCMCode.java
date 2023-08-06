package me.creuch.dcroles.commands;

import me.creuch.dcroles.DCRoles;
import me.creuch.dcroles.Database;
import me.creuch.dcroles.TextHandling;
import me.creuch.dcroles.inventory.codemanageinventory.CMInventory;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class DCMCode implements CommandExecutor, TabCompleter {

    private final DCRoles instance;
    private Database Database;
    private TextHandling TextHandling;
    private YamlConfiguration config;
    private YamlConfiguration langConfig;

    public DCMCode(DCRoles plugin) {
        this.instance = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (command.getName().equalsIgnoreCase("dcmcode")) {
            Database = new Database(instance);
            TextHandling = new TextHandling(instance);
            config = instance.getMainConfig();
            langConfig = instance.getLangConfig();
            if (commandSender instanceof HumanEntity) {
                if (strings.length == 0 || strings.length == 1 && strings[0].equalsIgnoreCase(commandSender.getName())) {
                    if (commandSender.hasPermission("dcr.dcmcode.self")) {
                        instance.setPlayer((OfflinePlayer) commandSender);
                        CMInventory inventory = new CMInventory(instance, (Player) commandSender);
                        ((HumanEntity) commandSender).openInventory(inventory.getInventory());
                    } else {
                        commandSender.sendMessage(TextHandling.getFormatted(langConfig.getString("minecraft.user.noPermission")));
                    }
                } else if(strings.length > 1 && strings[1].equalsIgnoreCase("setRole")) {
                    if(commandSender.hasPermission("dcr.dcmcode.others")) {
                        if (strings.length >= 3) {
                            instance.setPlayer(Bukkit.getOfflinePlayer(strings[0]));
                            if (Database.getUserData().get("exists") == "true") {
                                if(config.getConfigurationSection("roles").getKeys(false).contains(strings[2]) || strings[2].equalsIgnoreCase("default")) {
                                    HashMap<String, String> userData = new HashMap<>();
                                    userData.put("exists", "true");
                                    userData.put("role", strings[2]);
                                    Database.setUserData(userData);
                                    commandSender.sendMessage(TextHandling.getFormatted(langConfig.getString("minecraft.user.successRoleGive")));
                                } else {
                                    commandSender.sendMessage(TextHandling.getFormatted(langConfig.getString("minecraft.user.invalidRole")));
                                }
                            } else {
                                commandSender.sendMessage(TextHandling.getFormatted(langConfig.getString("minecraft.user.playerNotFound")));
                            }
                        } else {
                            commandSender.sendMessage(TextHandling.getFormatted(langConfig.getString("minecraft.user.invalidRole")));
                        }
                    } else {
                        commandSender.sendMessage(TextHandling.getFormatted(langConfig.getString("minecraft.user.noPermission")));
                    }
                } else {
                    if (commandSender.hasPermission("dcr.dcmcode.others")) {
                        instance.setPlayer(Bukkit.getOfflinePlayer(strings[0]));
                        if(Database.getUserData() != null && Database.getUserData().get("exists") == "true") {
                            CMInventory inventory = new CMInventory(instance, (Player) commandSender);
                            ((HumanEntity) commandSender).openInventory(inventory.getInventory());
                        } else {
                            commandSender.sendMessage(TextHandling.getFormatted(langConfig.getString("minecraft.user.playerNotFound")));
                        }
                    } else {
                        commandSender.sendMessage(TextHandling.getFormatted(langConfig.getString("minecraft.user.noPermission")));
                    }
                }
            } else {
                commandSender.sendMessage(TextHandling.getFormatted(langConfig.getString("minecraft.user.onlyPlayerCommand")));
            }
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        List<String> tabArgs = new ArrayList<>();
        config = instance.getMainConfig();
        if(commandSender.hasPermission("dcr.dcmcode.others")) {
            if(strings.length == 1) {
                for(OfflinePlayer p : Bukkit.getOfflinePlayers()) {
                    tabArgs.add(p.getName());
                }
            }
            if(strings.length == 2) {
                tabArgs.add("setRole");
            }
            if(strings.length == 3) {
                tabArgs.add("default");
                tabArgs.addAll(config.getConfigurationSection("roles").getKeys(false));
            }
            return tabArgs;
        }
        return null;
    }
}