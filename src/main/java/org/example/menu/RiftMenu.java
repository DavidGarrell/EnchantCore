package org.example.menu;

import org.apfloat.Apfloat;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.example.Main;
import org.example.api.UtilPlayer;
import org.example.economy.Currency;
import org.example.economy.Economy;
import org.example.economy.EconomyService;
import org.example.rankupSystem.DimensionalRift;
import org.example.rankupSystem.RiftBuff;
import org.example.rankupSystem.RiftConfig;
import org.example.utils.CustomGUIUtils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class RiftMenu {

    private final Main plugin;

    public RiftMenu(Main plugin) {
        this.plugin = plugin;
    }

    /**
     * Opens the main Dimensional Rift menu for the player.
     */
    public void open(Player player) {
        UtilPlayer utilPlayer = UtilPlayer.getPlayer(player.getUniqueId());
        if (utilPlayer == null) return;

        DimensionalRift rift = utilPlayer.getRiftProgression();
        CustomGUIUtils gui = new CustomGUIUtils("§8Dimensional Rift", 54);

        // --- Central Status Item ---
        // Displays the player's current overall progress.
        displayStatusItem(gui, rift);

        // --- Dynamic Action Button ---
        // This button changes depending on whether the player needs to buy layers or can leap to the next dimension.
        if (rift.isMaxLayer()) {
            displayLeapButton(gui, player, rift);
        } else {
            displayLayerButton(gui, player, rift);
        }

        displayBuffItems(gui, rift);

        gui.fillEmptySlots(Material.GRAY_STAINED_GLASS_PANE, " ");
        gui.open(player);
    }

    private void displayBuffItems(CustomGUIUtils gui, DimensionalRift rift) {
        int[] buffSlots = {37, 38, 40, 42, 43};
        int i = 0;

        for (RiftBuff buff : rift.getAllBuffs()) {
            if (i >= buffSlots.length) break;

            List<String> lore = buff.generateLore(rift.getCurrentDimension());
            gui.addItem(buffSlots[i], buff.getMaterial(), "§b§l" + buff.getDisplayName(), lore.toArray(new String[0]), null);
            i++;
        }
    }

    /**
     * Creates and adds the central status item to the GUI.
     */
    private void displayStatusItem(CustomGUIUtils gui, DimensionalRift rift) {
        ArrayList<String> lore = new ArrayList<>();
        lore.add("§7This is your progress through the dimensions.");
        lore.add("");
        lore.add("§a§lInformation");
        lore.add(String.format("§a┃ §7Current Dimension: §e%d", rift.getCurrentDimension()));
        lore.add(String.format("§a┃ §7Layer Progress: §f%d §7/ §f%d", rift.getCurrentLayer(), RiftConfig.LAYERS_PER_DIMENSION));
        lore.add("");
        lore.add("§6§lGlobal Buffs");
        lore.add(String.format("§6┃ §7Total Money Multiplier: x§b%s", Economy.format(rift.getTotalDimensionBuff())));
        lore.add(String.format("§6┃ §7Total Token Multiplier: x§b%s", 1+rift.getDimensionETokenBuff().floatValue()));

        gui.addItem(4, Material.NETHER_STAR, "§b§lDimensional Status", lore.toArray(new String[0]), null);
    }

    /**
     * Creates and adds the "Buy Next Layer" button to the GUI.
     */
    private void displayLayerButton(CustomGUIUtils gui, Player player, DimensionalRift rift) {
        Apfloat cost = rift.getNextLayerCost();
        boolean canAfford = EconomyService.hasEnough(player.getUniqueId(), Currency.MONEY, cost);

        Material material = canAfford ? Material.EMERALD_BLOCK : Material.REDSTONE_BLOCK;
        String title = "§aPurchase Layer §e" + (rift.getCurrentLayer() + 1);

        ArrayList<String> lore = new ArrayList<>();
        lore.add("§7Purchase the next rift layer to progress");
        lore.add("§7towards the next dimension.");
        lore.add("");
        lore.add("§a§lInformation");
        lore.add(" §a§l| §7This will grant a small, permanent buff.");
        lore.add("");
        lore.add("§c§lRequirement");
        lore.add(" §c§l| §7Cost: §e" + Economy.format(cost));
        lore.add("");
        if (canAfford) {
            lore.add("§eClick to purchase!");
        } else {
            lore.add("§cYou cannot afford this.");
        }

        gui.addItem(22, material, title, lore.toArray(new String[0]), (event, click) -> {
            if (rift.purchaseNextLayer(player)) {
                // Erfolg!
                player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1.5f);
                open(player); // Lade das Menü mit den neuen Daten neu
            } else {
                // Misserfolg! (Entweder nicht genug Geld oder ein anderer Fehler)
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 1f);
                player.sendMessage("§cYou don't have enough money to purchase the next layer.");
            }
        });
    }



    /**
     * Creates and adds the "Dimension Leap" button to the GUI.
     */
    private void displayLeapButton(CustomGUIUtils gui, Player player, DimensionalRift rift) {
        Apfloat cost = rift.getDimensionLeapCost(player);

        boolean canLeap = rift.isMaxLayer();

        Material material = canLeap ? Material.BEACON : Material.OBSIDIAN;
        String title = "§d§lPerform Dimension Leap!";

        ArrayList<String> lore = new ArrayList<>();
        lore.add("§7Break through to the next dimension!");
        lore.add("§7Your enchantments will NOT be reset.");
        lore.add("");
        lore.add("§a§lReward");
        lore.add(String.format(" §a§l| §7Ascend to Dimension: §e%d", rift.getCurrentDimension() + 1));
        lore.add(String.format(" §a§l §7Massive Global Money Multiplier!"));
        lore.add("");
        lore.add("§c§lRequirement");
        lore.add(" §c§l| §7Cost: §e" + Economy.format(cost));
        lore.add("");
        lore.add("§eClick to perform the Leap!");


        gui.addItem(22, material, title, lore.toArray(new String[0]), (event, click) -> {

            rift.performDimensionLeap(player);
            player.playSound(player.getLocation(), Sound.ENTITY_WITHER_SPAWN, 0.8f, 1f);
            player.sendTitle("§dDimension Leap!", "§7You have ascended to Dimension " + rift.getCurrentDimension(), 10, 70, 20);
            open(player); // Refresh the menu


        });
    }
}