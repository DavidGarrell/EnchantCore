package org.example.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.example.Main;
import org.example.api.EnchantUtils;
import org.example.menu.EnchantMenu;

public class PickaxeCommands implements CommandExecutor {

    Main instance;

    public PickaxeCommands(Main instance){
        this.instance = instance;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (label.equalsIgnoreCase("adminpickaxe") || label.equalsIgnoreCase("ap")) {
                if (player.hasPermission("admin.pickaxe")) {
                    if(args.length==0){
                        player.sendMessage("Test");
                    }
                    EnchantUtils enchantUtils = instance.enchantUtils;
                    if (args[0].equalsIgnoreCase("addlevel")) {
                        if (args.length == 4) {
                            Player target;
                            try {
                                target = Bukkit.getPlayer(args[1]);
                            } catch (Exception e) {
                                player.sendMessage(ChatColor.RED + "Invalid player.");
                                return false;
                            }
                            String enchantment = args[2];
                            if (!enchantUtils.enchantmentExists(enchantment)) {
                                player.sendMessage(ChatColor.RED + "Enchantment not exist");
                                return false;
                            }
                            int level;
                            try {
                                level = Integer.parseInt(args[3]);
                            } catch (NumberFormatException e) {
                                player.sendMessage(ChatColor.RED + "Invalid level number.");
                                return false;
                            }
                            assert target != null;
                            enchantUtils.addEnchantLevel(target.getUniqueId(), enchantment, level);
                            player.sendMessage(ChatColor.GREEN + "Add " + enchantment + " to level " + level);
                        } else {
                            player.sendMessage(ChatColor.RED + "Usage: /ap addlevel <player> <enchantment> <level>");
                        }
                    } else if (args[0].equalsIgnoreCase("setlevel")) {
                        if (args.length == 4) {
                            Player target;
                            try {
                                target = Bukkit.getPlayer(args[1]);
                            } catch (Exception e) {
                                player.sendMessage(ChatColor.RED + "Invalid player.");
                                return false;
                            }
                            String enchantment = args[2];
                            if (!enchantUtils.enchantmentExists(enchantment)) {
                                player.sendMessage(ChatColor.RED + "Enchantment not exist");
                                return false;
                            }
                            int level;
                            try {
                                level = Integer.parseInt(args[3]);
                            } catch (NumberFormatException e) {
                                player.sendMessage(ChatColor.RED + "Invalid level number.");
                                return false;
                            }
                            assert target != null;
                            enchantUtils.setEnchantLevel(target.getUniqueId(), enchantment, level);
                            player.sendMessage(ChatColor.GREEN + "Set " + enchantment + " to level " + level);
                        } else {
                            player.sendMessage(ChatColor.RED + "Usage: /ap setlevel <player> <enchantment> <level>");
                        }
                    }
                } else {
                    player.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
                }
            } else if (label.equalsIgnoreCase("pickaxe")) {
                if (args.length == 1 && args[0].equalsIgnoreCase("openmenu")) {
                    EnchantMenu enchantMenu = instance.enchantMenu;
                    player.openInventory(enchantMenu.enchantMenu(player));
                } else {
                    player.sendMessage(ChatColor.RED + "Usage: /pickaxe openmenu");
                }
            }

            return true;
        }

        sender.sendMessage(ChatColor.RED + "Only players can use this command.");
        return false;
    }
}
