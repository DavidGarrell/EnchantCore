package org.example.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.example.items.GemstoneBox;

public class GemstoneBoxCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("gemstones.admin.givebox")) {
            sender.sendMessage("§cYou do not have permission.");
            return true;
        }

        // Usage: /gemstonebox <tier> [player] [amount]
        if (args.length < 1) {
            sender.sendMessage("§cUsage: /gemstonebox <tier> [player] [amount]");
            return true;
        }

        int tier, amount;
        try {
            tier = Integer.parseInt(args[0]);
            amount = args.length > 2 ? Integer.parseInt(args[2]) : 1;
        } catch (NumberFormatException e) {
            sender.sendMessage("§cTier and amount must be valid numbers.");
            return true;
        }

        Player target;
        if (args.length > 1) {
            target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                sender.sendMessage("§cPlayer not found.");
                return true;
            }
        } else if (sender instanceof Player) {
            target = (Player) sender;
        } else {
            sender.sendMessage("§cYou must specify a player from the console.");
            return true;
        }

        GemstoneBox box = new GemstoneBox(tier);
        ItemStack boxItem = box.getItemStack();
        boxItem.setAmount(amount);

        target.getInventory().addItem(boxItem);
        sender.sendMessage("§aGave " + amount + "x Tier " + tier + " Gemstone Box to " + target.getName() + ".");
        return true;
    }
}
