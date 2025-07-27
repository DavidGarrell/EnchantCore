package org.example.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.example.Main;
import org.example.api.Enchant;

import java.util.UUID;

public class EnchantProgressCommand implements CommandExecutor {

    private final Main plugin;

    public EnchantProgressCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        Player player = (Player) sender;
        UUID playerId = player.getUniqueId();
        String enchantID;

        // Überprüfe den Befehl (/jackhammer oder /laser) und setze das Verzauberungs-ID
        if (label.equalsIgnoreCase("jackhammer")) {
            enchantID = "jackhammer";
        } else if (label.equalsIgnoreCase("laser")) {
            enchantID = "laser";
        } else {
            player.sendMessage("Unknown command. Use /jackhammer or /laser.");
            return true;
        }

        // Hole die Einstellungen für die Verzauberung


        // Hole die Anzahl der abgebauten Blöcke und die Blöcke, die für den Proc benötigt werden




        return true;
    }
}

