package org.example.menu;

import org.apfloat.Apfloat;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.example.Main;
import org.example.api.Enchant;
import org.example.api.Placeholders;
import org.example.api.UtilPlayer;
import org.example.api.perks.PerkMenu;
import org.example.economy.Currency;
import org.example.economy.Economy;
import org.example.economy.EconomyService;
import org.example.items.EnchantPickaxe;
import org.example.settings.GlobalSettings;
import org.example.utils.CustomGUIUtils;

import java.util.ArrayList;
import java.util.UUID;

public class EnchantMenu {

    private final GlobalSettings globalSettings;
    private static Main plugin = null;
    private final Placeholders placeholders;
    private final EnchantPickaxe enchantPickaxe;
    private static final String enchantMenuName = "Enchant-Menu";
    private static final String upgradeMenuName = "Upgrade Enchant";
    private final String itemEnchantTokens = "§f§lEnchant-Tokens";
    private static boolean enchantTokensEnabled;

    public EnchantMenu(Main plugin) {
        this.plugin = plugin;
        this.globalSettings = plugin.globalSettings;
        this.placeholders = plugin.placeholders;
        this.enchantPickaxe = plugin.enchantPickaxe;
        this.enchantTokensEnabled = globalSettings.isUseEnchantTokens();
    }

    public static Inventory enchantMenu(Player player) {
        UUID uuid = player.getUniqueId();
        CustomGUIUtils gui = new CustomGUIUtils(enchantMenuName, 54);
        UtilPlayer utilPlayer = UtilPlayer.getPlayer(uuid);

        for (Enchant enchant : UtilPlayer.getPlayer(uuid).enchants) {
            if (!enchant.isToggle()) {
                ArrayList<String> lore = new ArrayList<>();
                lore.add("§8Activation Chance: §71/" + enchant.getChance(UtilPlayer.getPlayer(uuid)));
                lore.add("");
                lore.add("§a§lDescription");
                lore.add(" §a§l| §7" + enchant.getDescription());
                if(enchant.getScroll() != null) {
                    lore.add("");
                    lore.add("§f§lScroll Information");
                    lore.add(" §f§l| §7duration: §e" + enchant.getScroll().getDuration() + " blocks");
                    lore.add(" §f§l| §7level bonus: §e" + enchant.getScroll().getLevelBonus());
                }
                lore.add("");
                lore.add("§6§lStatistics");

                if (enchant.getLevel() != enchant.getMax_level()) {
                    lore.add(" §6§l| §7level: §e" + enchant.getLevel() + "§7/§c" + enchant.getMax_level() + " " + getPrestigeStars(enchant.getPrestige()));

                } else {
                    lore.add(" §6§l| §7level: §e" + enchant.getLevel() + "§7/§c" + enchant.getMax_level() + " §e§lMAX" + " " + getPrestigeStars(enchant.getPrestige()));
                }

                lore.add(" §6§l| §7proc-count: §e" + enchant.getProc_counter());
                if (enchantTokensEnabled) {
                    lore.add(" §6§l| §7cost: §e" + Economy.format(enchant.calculateCost(1)) + " §eETokens");
                } else {
                    lore.add(" §6§l| §7cost: §e" + Economy.format(enchant.calculateCost(1)) + " §eEtokens");
                }
                lore.add("");
                lore.add("§e§lUpgrade");

                boolean isUnlocked = enchant.getUnlockLevel() <= utilPlayer.getPickaxeLevel();

                if(isUnlocked) {
                    lore.add(" §e§l| §7Click to upgrade");
                    lore.add(" §e§l| §7Shift + Click to upgrade to max level");
                } else {
                    lore.add(" §e§l| §cYou need to reach level §c" + enchant.getUnlockLevel() + "§7 to upgrade this enchant.");
                }

                Enchant finalEnchant = enchant;

                gui.addItem(gui.getNextEmptySlot(), enchant.getMaterial(), enchant.getDisplayname() + " §f§lEnchant", lore.toArray(new String[0]), (event, click) -> {
                    if (!isUnlocked) {
                        player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 1f);
                        return;
                    }
                    if(event.isShiftClick()) {
                        int finalAffordableLevels = enchant.getMaxAffordableLevels(player);
                        if (finalAffordableLevels > 0) {
                            boolean success = EconomyService.hasEnough(player.getUniqueId(), Currency.ETOKENS, enchant.calculateCost(finalAffordableLevels));
                            if (success) {
                                player.sendMessage(Placeholders.getEnchantBuyMessage(
                                        finalAffordableLevels,
                                        enchant.getDisplayname(),
                                        Economy.format(enchant.calculateCost(finalAffordableLevels))
                                ));

                                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f);
                                EconomyService.subtractBalance(player.getUniqueId(), Currency.ETOKENS, enchant.calculateCost(finalAffordableLevels));
                                enchant.upgrade(finalAffordableLevels);
                                player.openInventory(enchantMenu(player));
                            }
                        }
                    } else {
                        upgradeMenu(player, finalEnchant);
                    }
                });
            }


        }
        gui.addItem(49, Material.NETHER_STAR, "§b§lMilestones",
                new String[]{"§7Click to see your progress", "§7on all enchantment milestones."},
                (event, click) -> {
                    // Erstellt eine Instanz des MilestoneMenu und öffnet es
                    new MilestoneMenu(plugin).open(player);
                }
        );
        gui.addItem(50, Material.DIAMOND, "§b§lGemstones",
                new String[]{
                        "§7Socket powerful Gemstones to",
                        "§7temporarily boost your enchants.",
                        "",
                        "§e§lClick to open!"
                },
                (event, click) -> {
                    new GemstoneMenu(plugin).openMainMenu(player);
                }
        );
        gui.addItem(46, Material.BOOK, "§6§lPerks",
                new String[]{
                        "§7Unlock powerful perks to enhance",
                        "§7your mining experience.",
                        "",
                        "§e§lClick to open!"
                },
                (event, click) -> {
                    new PerkMenu(plugin, plugin.perkManager).open(player);
                }
        );
        return gui.getInventory();
    }
    public static String getPrestigeStars(int prestigeLevel) {
        StringBuilder stars = new StringBuilder("§f(");
        for (int i = 0; i < prestigeLevel; i++) {
            stars.append("§e✯");
        }
        stars.append("§f)");

        if(prestigeLevel == 0) {
            return "";
        }
        return stars.toString();
    }
    public static void upgradeMenu(Player player, Enchant enchant) {

        CustomGUIUtils gui = new CustomGUIUtils(upgradeMenuName + " - " + enchant.getDisplayname(), 27);
        UUID uuid = player.getUniqueId();
        int currentLevel = enchant.getLevel();

        int[] upgradeLevels = {1, 5,10, 25, 50, 100, 250, 500,1000};
        int slot = 9;

        for (int level : upgradeLevels) {
            ArrayList<String> lore = new ArrayList<>();
            if (currentLevel + level <= enchant.getMax_level()) {
                lore.add("§7Increase the level of this enchant,");
                lore.add("§7making it more powerful.");
                lore.add("");
                lore.add("§a§lInformation");
                lore.add(" §a§l| §7New Level: §e" + (currentLevel + level));
                lore.add(" §a§l| §7Cost: §e" + Economy.format(enchant.calculateCost(level)) + " §eEtokens");
                lore.add("");
                lore.add("§6§lAction");
                lore.add(" §6§l| §7Click to upgrade by " + level + " levels.");
            }


            gui.addItem(slot, enchant.material, level, "§a§l+" + level + " Levels", lore.toArray(new String[0]), (event, click) -> {
                if (EconomyService.hasEnough(player.getUniqueId(), Currency.ETOKENS, enchant.calculateCost(level))) {
                    EconomyService.subtractBalance(player.getUniqueId(), Currency.ETOKENS, enchant.calculateCost(level));
                    enchant.upgrade(level);
                    upgradeMenu(player, enchant); // Menü neu laden
                    player.sendMessage(Placeholders.getEnchantBuyMessage(
                            level,
                            enchant.getDisplayname(),
                            Economy.format(enchant.calculateCost(level))
                    ));
                    player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f);

                } else {
                    player.sendMessage(Placeholders.ENCHANT_NOT_ENOUGH);
                }
            });
            slot++;

        }
        int affordableLevels = enchant.getMaxAffordableLevels(player);

        if (affordableLevels > 0) {
            ArrayList<String> maxLore = new ArrayList<>();
            maxLore.add("§7Instantly purchase as many levels");
            maxLore.add("§7as you can afford with your tokens.");
            maxLore.add("");
            maxLore.add("§a§lInformation");
            maxLore.add(" §a§l| §7Levels to buy: §e" + affordableLevels);
            maxLore.add(" §a§l| §7New Level: §e" + (currentLevel + affordableLevels));
            maxLore.add(" §a§l| §7Cost: §e" + Economy.format(enchant.calculateCost(affordableLevels)));
            maxLore.add("");
            maxLore.add("§6§lAction");
            maxLore.add(" §6§l| §eClick to buy " + affordableLevels + " levels.");

            gui.addItem(22, Material.HOPPER, "§a§lBuy Max Affordable", maxLore.toArray(new String[0]), (event, click) -> {
                int finalAffordableLevels = enchant.getMaxAffordableLevels(player);
                if (finalAffordableLevels > 0) {
                    boolean success = EconomyService.hasEnough(player.getUniqueId(), Currency.ETOKENS, enchant.calculateCost(finalAffordableLevels));
                    if (success) {
                        player.sendMessage(Placeholders.getEnchantBuyMessage(
                                finalAffordableLevels,
                                enchant.getDisplayname(),
                                Economy.format(enchant.calculateCost(finalAffordableLevels))
                        ));
                        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f);
                        EconomyService.subtractBalance(player.getUniqueId(), Currency.ETOKENS, enchant.calculateCost(finalAffordableLevels));
                        enchant.upgrade(finalAffordableLevels);
                        upgradeMenu(player, enchant);
                    }
                } else {
                    player.sendMessage(Placeholders.ENCHANT_NOT_ENOUGH);
                    player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 1f);
                }
            });
        }

        gui.addItem(19, Material.BARRIER, "§c§lBack", null, (event, click) -> {
            player.openInventory(enchantMenu(player));
        });

        final boolean canPrestige = (enchant.getLevel() >= enchant.getMax_level() && enchant.getPrestige() < enchant.getMax_prestige());
        final int currentMaxLevel = enchant.getMax_level();

        ArrayList<String> prestigeLore = new ArrayList<>();
        prestigeLore.add("§7Once you reach the max level, you can");
        prestigeLore.add("§7prestige this enchantment for free.");
        prestigeLore.add("");
        prestigeLore.add("§a§lInformation");
        prestigeLore.add(" §a§l| §7Prestige Level: §a" + (enchant.getPrestige() + "§7/§a" + enchant.getMax_prestige()));
        prestigeLore.add(" §a§l| §7The enchant's level will be reset to §c0");
        prestigeLore.add(" §a§l| §7The proc chance will be permanently");
        prestigeLore.add(" §a§l| §7reduced by §a5%!");
        prestigeLore.add("");

        if (canPrestige) {
            prestigeLore.add("§e§lPrestige");
            prestigeLore.add(" §e§l| §7Click to prestige");
        } else {
            prestigeLore.add("§c§lRequirement:");
            prestigeLore.add(" §c§l| §7Reach Level §c" + currentMaxLevel);
        }

        Material prestigeMaterial = canPrestige ? Material.BEACON : Material.REDSTONE_BLOCK;
        String prestigeTitle = canPrestige ? "§b§lPrestige Now!" : "§b§lEnchant Prestige";

        gui.addItem(26, prestigeMaterial, prestigeTitle, prestigeLore.toArray(new String[0]), (event, click) -> {
            if (canPrestige) {

                enchant.prestige();

                player.sendMessage(Placeholders.getEnchantPrestigeMessage(
                        enchant.getDisplayname(),
                        String.valueOf(enchant.getLevel())
                ));
                player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1f, 1f);

                player.openInventory(enchantMenu(player));

            } else {
                player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 1f, 1f);
                player.sendMessage(Placeholders.ENCHANT_CAN_NOT_BE_PRESTIGED_LEVEL);
            }
        });

        gui.addItem(24, Material.NETHER_STAR, "§6§lEnchant Empower", null, (event, click) -> {

            player.openInventory(enchantMenu(player));
        });

        gui.fillEmptySlots(Material.GRAY_STAINED_GLASS_PANE, " ");

        gui.open(player);
    }

}