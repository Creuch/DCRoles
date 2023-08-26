package me.creuch.dcroles.commands;

import lombok.AccessLevel;
import lombok.experimental.ExtensionMethod;
import lombok.experimental.FieldDefaults;
import me.creuch.dcroles.DCRoles;
import me.creuch.dcroles.Database;
import me.creuch.dcroles.TextFormat;
import me.creuch.dcroles.Config;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.HumanEntity;
import org.jetbrains.annotations.NotNull;


@ExtensionMethod({java.lang.String.class, TextFormat.class})
@FieldDefaults(level= AccessLevel.PRIVATE)
public class DCCode extends BukkitCommand  {

    final DCRoles instance;
    String commandName;
    Config config;
    Database database;
    TextFormat textFormat;

    public DCCode(DCRoles instance, String cName) {
        super(cName);
        this.commandName = cName;
        this.instance = instance;
        this.config = DCRoles.config;
        this.database = instance.getDatabase();
        this.textFormat = instance.getTextFormat();
    }


    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        if(args.length == 0) {
            if(!sender.hasPermission(config.getValue("config.yml", commandName + ".permissionSelf"))) sender.sendMessage("{P}&c Nie masz permisji.".getFormatted());
            if(!(sender instanceof HumanEntity)) sender.sendMessage("{P}&c Nie jesteś graczem!".getFormatted());

            // Get code methpd
        }
        return true;
    }
}