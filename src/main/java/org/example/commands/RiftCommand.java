package org.example.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.example.Main;
import org.example.api.UtilPlayer;
import org.example.menu.RiftMenu;
import org.example.rankupSystem.DimensionalRift;
import org.example.rankupSystem.RiftConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RiftCommand implements CommandExecutor, TabCompleter {

    private final Main plugin;

    public RiftCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // --- Hauptbefehl: /rift ---
        // Öffnet das Menü für den Spieler
        if (args.length == 0) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("§cThis command can only be used by players. Use /rift set <dim> <layer> <player>.");
                return true;
            }
            Player player = (Player) sender;
            new RiftMenu(plugin).open(player);
            return true;
        }

        // --- Unterbefehl: /rift set ... ---
        if (args[0].equalsIgnoreCase("set")) {
            // Permission-Check
            if (!sender.hasPermission("rift.admin.set")) {
                sender.sendMessage("§cYou do not have permission to use this command.");
                return true;
            }

            // Argument-Check
            if (args.length < 3) {
                sender.sendMessage("§cUsage: /rift set <dimension> <layer> [player]");
                return true;
            }

            // Argumente parsen
            int dimension, layer;
            try {
                dimension = Integer.parseInt(args[1]);
                layer = Integer.parseInt(args[2]);
            } catch (NumberFormatException e) {
                sender.sendMessage("§cError: Dimension and layer must be valid numbers.");
                return true;
            }

            // Validierung der Eingaben
            if (dimension < 1 || layer < 0 || layer > RiftConfig.LAYERS_PER_DIMENSION) {
                sender.sendMessage("§cError: Invalid dimension or layer value. Dimension must be >= 1 and layer must be between 0 and " + RiftConfig.LAYERS_PER_DIMENSION + ".");
                return true;
            }

            // Zielspieler bestimmen
            Player target;
            if (args.length >= 4) {
                target = Bukkit.getPlayer(args[3]);
                if (target == null) {
                    sender.sendMessage("§cError: Player '" + args[3] + "' is not online.");
                    return true;
                }
            } else if (sender instanceof Player) {
                target = (Player) sender;
            } else {
                sender.sendMessage("§cError: You must specify a player when running this from the console.");
                return true;
            }

            // Fortschritt setzen
            UtilPlayer utilTarget = UtilPlayer.getPlayer(target.getUniqueId());
            if (utilTarget != null) {
                DimensionalRift rift = utilTarget.getRiftProgression();
                rift.setCurrentDimension(dimension);
                rift.setCurrentLayer(layer);

                sender.sendMessage("§aSuccessfully set " + target.getName() + "'s progress to Dimension " + dimension + ", Layer " + layer + ".");
                if (sender != target) {
                    target.sendMessage("§aYour rift progress has been set by an administrator.");
                }
            } else {
                sender.sendMessage("§cCould not find player data for " + target.getName() + ".");
            }
            return true;
        }

        // Wenn kein gültiger Unterbefehl gefunden wurde
        sender.sendMessage("§cUnknown subcommand. Usage: /rift [set ...]");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            List<String> completions = new ArrayList<>();
            if (sender.hasPermission("rift.admin.set")) {
                completions.add("set");
            }
            return completions;
        }

        if (args.length == 4 && args[0].equalsIgnoreCase("set") && sender.hasPermission("rift.admin.set")) {
            return Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(name -> name.toLowerCase().startsWith(args[3].toLowerCase()))
                    .collect(Collectors.toList());
        }

        return new ArrayList<>();
    }
}