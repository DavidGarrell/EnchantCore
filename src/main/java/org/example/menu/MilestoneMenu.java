package org.example.menu;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.example.Main;
import org.example.api.Enchant;
import org.example.api.UtilPlayer;
import org.example.api.milestones.Milestone;
import org.example.api.milestones.MilestoneConfigManager;
import org.example.utils.CustomGUIUtils;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Erstellt und verwaltet das GUI, das die Meilensteine für Verzauberungen anzeigt.
 */
public class MilestoneMenu {

    private final MilestoneConfigManager milestoneManager;
    private final Main plugin;

    public MilestoneMenu(Main plugin) {
        this.plugin = plugin;
        this.milestoneManager = plugin.getMilestoneConfigManager();
    }

    public void open(Player player) {
        CustomGUIUtils gui = new CustomGUIUtils("§8Enchant Prestiges", 54); // Titel wie im Beispiel
        UtilPlayer utilPlayer = UtilPlayer.getPlayer(player.getUniqueId());

        if (utilPlayer == null) return;

        // Gehe durch jede Verzauberung des Spielers
        for (Enchant enchant : utilPlayer.enchants) {
            List<Milestone> milestones = milestoneManager.getMilestonesFor(enchant.getId());
            if (milestones.isEmpty()) {
                continue;
            }

            // --- LORE WIRD EXAKT WIE IM SCREENSHOT AUFGEBAUT ---

            ArrayList<String> lore = new ArrayList<>();
            // Kurze Beschreibung
            lore.add("§7Progress this enchant to unlock");
            lore.add("§7powerful, permanent buffs.");
            lore.add("");

            // Sektion: Information
            lore.add("§a§lInformation");
            long currentProcCount = enchant.getProc_counter();
            int currentMilestoneLevel = getCurrentMilestoneLevel(currentProcCount, milestones);
            int totalMilestones = milestones.size();

            lore.add(" §a§l| §7Level: §a" + currentMilestoneLevel + "§8/§c" + totalMilestones);
            lore.add(" §a§l| §7Activations: §a" + formatNumber(currentProcCount));
            lore.add("");

            // Sektion: Upgrades (Meilensteine)
            lore.add("§6§lUpgrades");
            for (Milestone milestone : milestones) {
                boolean isUnlocked = currentProcCount >= milestone.getRequiredProcCount();

                if (isUnlocked) {
                    // Freigeschalteter Meilenstein
                    String rewardString = formatBuffs(milestone.getBuffs());
                    lore.add(" §6§l| §fLevel " + milestone.getLevel() + ": " + rewardString);
                } else {
                    // Gesperrter Meilenstein
                    lore.add(" §6§l| §7Level " + milestone.getLevel() + ": Unlocked at §c" + formatNumber(milestone.getRequiredProcCount()) + "§7 activations");
                }
            }

            // Füge das fertige Item zum GUI hinzu
            gui.addItem(
                    gui.getNextEmptySlot(),
                    enchant.getMaterial(),
                    "§d" + enchant.getDisplayname() + " §f§lMilestones", // Titel wie im Beispiel
                    lore.toArray(new String[0]),
                    (event, click) -> {} // Keine Aktion bei Klick
            );
        }

        gui.fillEmptySlots(Material.GRAY_STAINED_GLASS_PANE, " ");

        // "Zurück"-Button, wie im vorherigen Beispiel
        gui.addItem(49, Material.BARRIER, "§c§lBack", new String[]{"§7Return to the Enchant Menu"}, (event, click) -> {
            player.openInventory(EnchantMenu.enchantMenu(player)); // Öffnet das Enchant-Menü
        });

        gui.open(player);
    }

    /**
     * Berechnet das aktuell erreichte Meilenstein-Level des Spielers.
     */
    private int getCurrentMilestoneLevel(long currentProcs, List<Milestone> milestones) {
        int level = 0;
        for (Milestone milestone : milestones) {
            if (currentProcs >= milestone.getRequiredProcCount()) {
                level = milestone.getLevel();
            } else {
                break; // Meilensteine sind sortiert, wir können hier aufhören
            }
        }
        return level;
    }

    /**
     * Formatiert die Buffs eines Meilensteins in einen anzeigbaren String.
     * Beispiel: {SPEED_BOOST=0.1, FORTUNE_CHANCE=0.05} -> "§a+0.1 Speed Boost, +0.05 Fortune Chance"
     */
    private String formatBuffs(Map<String, Double> buffs) {
        if (buffs.isEmpty()) {
            return "§aNo direct buffs";
        }
        return buffs.entrySet().stream()
                .map(entry -> "§a+" + entry.getValue() + " " + formatBuffName(entry.getKey()))
                .collect(Collectors.joining(", "));
    }

    /**
     * Wandelt einen Buff-Key in einen lesbaren Namen um.
     * Beispiel: "BLOCK_BREAK_RADIUS" -> "Block Break Radius"
     */
    private String formatBuffName(String buffKey) {
        String[] parts = buffKey.toLowerCase().split("_");
        StringBuilder formatted = new StringBuilder();
        for (String part : parts) {
            formatted.append(Character.toUpperCase(part.charAt(0))).append(part.substring(1)).append(" ");
        }
        return formatted.toString().trim();
    }

    /**
     * Formatiert eine Zahl mit Kommas für bessere Lesbarkeit.
     */
    private String formatNumber(long number) {
        return NumberFormat.getNumberInstance(Locale.US).format(number);
    }
}

