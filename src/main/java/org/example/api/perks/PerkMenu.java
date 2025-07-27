package org.example.api.perks;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.example.Main;
import org.example.api.UtilPlayer;
import org.example.api.perks.ActivePerk;
import org.example.api.perks.PerkManager;
import org.example.utils.CustomGUIUtils;

import java.util.*;
import java.util.stream.Collectors;

public class PerkMenu {
    private final Main plugin;
    private final PerkManager perkManager;
    private static final Map<UUID, BukkitRunnable> activeTasks = new HashMap<>();
    private static final Map<UUID, ActivePerk> lastRolledPerk = new HashMap<>();

    public PerkMenu(Main plugin, PerkManager perkManager) {
        this.plugin = plugin;
        this.perkManager = perkManager;
    }

    public void open(Player player) {
        UtilPlayer utilPlayer = UtilPlayer.getPlayer(player.getUniqueId());
        CustomGUIUtils gui = new CustomGUIUtils("§8Your Active Perk", 36);
        displayCurrentPerk(gui, 13, utilPlayer.getActivePerk());
        displayActionButtons(gui, player);
        gui.fillEmptySlots(Material.GRAY_STAINED_GLASS_PANE, " ");
        gui.open(player);
    }

    private void displayActionButtons(CustomGUIUtils gui, Player player) {
        UtilPlayer utilPlayer = UtilPlayer.getPlayer(player.getUniqueId());

        gui.addItem(20, Material.ENDER_CHEST, "§d§lRoll for a new Perk", createRollLore(utilPlayer.getActivePerk()), (e, c) -> {
            if (isTaskActive(player)) return;
            handleRollClick(player, gui, false);
        });

        if (player.hasPermission("perks.autospin")) {
            if (isTaskActive(player)) {
                gui.addItem(22, Material.BARRIER, "§c§lCancel Auto-Spin", createCancelLore(), (e, c) -> stopTask(player));
            } else {
                gui.addItem(22, Material.CLOCK, "§c§lAuto-Spin", createAutoSpinLore(), (e, c) -> handleRollClick(player, gui, true));
            }
        }

        if (player.hasPermission("perks.toggleanimation")) {
            boolean enabled = utilPlayer.areAnimationsEnabled();
            gui.addItem(24, enabled ? Material.LIME_DYE : Material.GRAY_DYE, "§bAnimations: " + (enabled ? "§aON" : "§cOFF"), createToggleLore(), (e, c) -> {
                if (isTaskActive(player)) return;
                utilPlayer.setAnimationsEnabled(!enabled);
                open(player);
            });
        }

        gui.addItem(31, Material.BOOK, "§a§lPerk Index", new String[]{"§7View all possible perks you can roll."}, (e, c) -> {
            if (isTaskActive(player)) return;
            openPerkViewer(player);
        });
    }

    private void handleRollClick(Player player, CustomGUIUtils gui, boolean isAutoSpin) {
        ActivePerk currentPerk = UtilPlayer.getPlayer(player.getUniqueId()).getActivePerk();
        if (currentPerk != null && (currentPerk.getTemplate().getRarity() == Rarity.LEGENDARY || currentPerk.getLevel() == 5) && !isAutoSpin) {
            displayConfirmationUI(gui, player);
        } else {
            startRoll(player, gui, isAutoSpin);
        }
    }

    private void displayConfirmationUI(CustomGUIUtils gui, Player player) {
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1f, 0.8f);
        gui.addItem(20, Material.LIME_WOOL, "§a§lConfirm Reroll", new String[]{"§eClick to confirm."}, (e, c) -> startRoll(player, gui, false));
        gui.addItem(22, Material.RED_WOOL, "§c§lCancel", new String[]{"§7Return to the main actions."}, (e, c) -> displayActionButtons(gui, player));
    }

    private void startRoll(Player player, CustomGUIUtils gui, boolean isAutoSpin) {
        if (isTaskActive(player)) return;
        // TODO: Kosten prüfen und abziehen

        UtilPlayer utilPlayer = UtilPlayer.getPlayer(player.getUniqueId());

        if (isAutoSpin) {
            gui.addItem(22, Material.BARRIER, "§c§lCancel Auto-Spin", createCancelLore(), (e, c) -> stopTask(player));
        } else {
            gui.addItem(20, Material.BARRIER, "§cRolling...", null, null);
        }

        BukkitRunnable task = new BukkitRunnable() {
            private int ticks = 0;
            private int pauseTicks = 0;
            private final long animationDuration = 40L;
            private final long autoSpinInterval = 10L;
            private final List<PerkTemplate> allTemplates = perkManager.getAllTemplates();
            private ActivePerk perkToFinishWith = null;

            @Override
            public void run() {
                if (pauseTicks > 0) {
                    pauseTicks--;
                    if (pauseTicks == 0 && perkToFinishWith != null) {
                        finish(perkToFinishWith);
                    }
                    return;
                }

                if (utilPlayer.areAnimationsEnabled()) {
                    displayRandomPerkAsAnimation(gui, 13, allTemplates);
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 1.0f + (ticks * 0.02f));
                }

                if (!isAutoSpin) {
                    if (ticks >= animationDuration) {
                        finish(perkManager.rollNewPerk());
                        return;
                    }
                } else {
                    if (ticks > 0 && ticks % autoSpinInterval == 0) {
                        ActivePerk newPerk = perkManager.rollNewPerk();
                        displayCurrentPerk(gui, 13, newPerk);
                        player.playSound(player.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 1f, 1.2f);
                        pauseTicks = (int) autoSpinInterval;
                        lastRolledPerk.put(player.getUniqueId(), newPerk);

                        if (newPerk.getLevel() == 5 || newPerk.getTemplate().getRarity() == Rarity.LEGENDARY) {
                            player.sendMessage("§aAuto-Spin stopped! Found a high-value perk!");
                            perkToFinishWith = newPerk;
                        }
                    }
                }
                ticks++;
            }

            private void finish(ActivePerk finalPerk) {
                this.cancel();
                activeTasks.remove(player.getUniqueId());
                utilPlayer.setActivePerk(finalPerk);
                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1.2f);
                displayCurrentPerk(gui, 13, finalPerk);
                displayActionButtons(gui, player);
            }
        };

        activeTasks.put(player.getUniqueId(), task);
        task.runTaskTimer(plugin, 0L, 1L);
    }

    private void stopTask(Player player) {
        BukkitRunnable task = activeTasks.remove(player.getUniqueId());
        if (task != null) {
            task.cancel();

            ActivePerk finalPerk = lastRolledPerk.remove(player.getUniqueId());
            if (finalPerk != null) {
                UtilPlayer.getPlayer(player.getUniqueId()).setActivePerk(finalPerk);
                player.sendMessage("§cSpinning cancelled. §aYou kept the last perk rolled!");
            } else {
                player.sendMessage("§cSpinning cancelled.");
            }
        }
        player.playSound(player.getLocation(), Sound.BLOCK_LEVER_CLICK, 1f, 0.8f);
        open(player);
    }

    private boolean isTaskActive(Player player) {
        return activeTasks.containsKey(player.getUniqueId());
    }

    private void displayRandomPerkAsAnimation(CustomGUIUtils gui, int slot, List<PerkTemplate> templates) {
        if (templates.isEmpty()) return;
        PerkTemplate randomTemplate = templates.get(new Random().nextInt(templates.size()));
        gui.addSkullItem(slot, "§d§k|||", new String[]{"§7Rolling..."}, randomTemplate.getTextureValue(), null);
    }

    private String[] createRollLore(ActivePerk currentPerk) {
        ArrayList<String> lore = new ArrayList<>();
        lore.add("§7Roll for a new random perk to boost");
        lore.add("§7your gameplay abilities.");
        lore.add("");
        if (currentPerk != null) {
            lore.add("§c§lWarning");
            lore.add(" §c§l| §7This will replace your current perk!");
            lore.add("");
        }
        lore.add("§a§lRequirement");
        lore.add(" §a§l| §7Cost: §e1 Perk Key");
        return lore.toArray(new String[0]);
    }

    private String[] createAutoSpinLore() {
        ArrayList<String> lore = new ArrayList<>();
        lore.add("§7Automatically rolls for new perks");
        lore.add("§7until a high-value one is found.");
        lore.add("");
        lore.add("§a§lInformation");
        lore.add(" §a§l| §7Stops at: §eLevel 5 §7or §6Legendary");
        lore.add("");
        lore.add("§c§lWarning");
        lore.add(" §c§l| §7This can consume a lot of keys quickly!");
        return lore.toArray(new String[0]);
    }

    private String[] createCancelLore() {
        return new String[]{"§cClick to stop the auto-spinner."};
    }

    private String[] createToggleLore() {
        ArrayList<String> lore = new ArrayList<>();
        lore.add("§7Toggles the visual rolling animation");
        lore.add("§7for a faster or more immersive experience.");
        return lore.toArray(new String[0]);
    }

    private void displayCurrentPerk(CustomGUIUtils gui, int slot, ActivePerk perk) {
        if (perk != null) {
            ArrayList<String> lore = new ArrayList<>();
            lore.add("§7This is your currently active perk.");
            lore.add("");
            lore.add(perk.getTemplate().getRarity().getFormattedName());
            lore.add("");
            lore.add("§a§lInformation");
            lore.add(" §a§l| §7Level: §f" + perk.getLevel());
            String buffLine = perk.getTemplate().getAffectedTypes().stream()
                    .map(type -> "§f" + type.getDisplayName() + " §a" + String.format("%.0f%%", getBuffValueForLevel(type, perk.getLevel()) * 100))
                    .collect(Collectors.joining(" "));

            lore.add(" §a§l| " + buffLine);
            gui.addSkullItem(slot, "§b" + perk.getTemplate().getName(), lore.toArray(new String[0]), perk.getTemplate().getTextureValue(), null);
        } else {
            ArrayList<String> lore = new ArrayList<>();
            lore.add("§7You have not rolled a perk yet.");
            lore.add("§7Roll one to get powerful boosts!");
            gui.addItem(slot, Material.BARRIER, "§cNo Active Perk", lore.toArray(new String[0]), null);
        }
    }

    private void openPerkViewer(Player player) {
        CustomGUIUtils viewerGui = new CustomGUIUtils("§8Perk Index", 54);

        for (PerkTemplate template : perkManager.getAllTemplates()) {
            ArrayList<String> lore = new ArrayList<>();
            // Kategorie (z.B. PICKAXE PERK)
            lore.add(template.getCategory());
            lore.add("");
            // Beschreibung
            lore.add("§7" + template.getDescription());
            lore.add("");
            lore.add("§a§lBuffs");

            // Level-Breakdown
            for (int level = 1; level <= 5; level++) {
                // Baue den String für die Buffs dieses Levels
                int finalLevel = level;
                String buffLine = template.getAffectedTypes().stream()
                        .map(type -> "§f" + type.getDisplayName() + " §a" + String.format("%.0f%%", getBuffValueForLevel(type, finalLevel) * 100))
                        .collect(Collectors.joining(" "));

                lore.add(" §a§l| §8" + toRoman(level) + " §7- " + buffLine);
            }
            lore.add("");

            // Chance
            lore.add("§cChance: §f" + String.format("%.0f%%", template.getRarity().getProbability() * 100));

            viewerGui.addSkullItem(viewerGui.getNextEmptySlot(), template.getRarity().getColor() + template.getName(), lore.toArray(new String[0]), template.getTextureValue(), null);
        }

        viewerGui.addItem(49, Material.ARROW, "§c§lBack", new String[]{"§7Return to the Perk Menu"}, (e, c) -> open(player));
        viewerGui.fillEmptySlots(Material.GRAY_STAINED_GLASS_PANE, " ");
        viewerGui.open(player);
    }

    /**
     * Statische Hilfsmethode, um den Buff-Wert für ein beliebiges Level zu berechnen.
     */
    private static double getBuffValueForLevel(PerkType type, int level) {
        if (type == PerkType.ENCHANT_PROC_BOOST) {
            return level * 0.05; // 2% pro Level
        } else {
            return level * 0.5; // 5% pro Level für andere
        }
    }

    /**
     * Statische Hilfsmethode, um Zahlen in römische Ziffern umzuwandeln.
     */
    private static String toRoman(int number) {
        switch (number) {
            case 1: return "I";
            case 2: return "II";
            case 3: return "III";
            case 4: return "IV";
            case 5: return "V";
            default: return String.valueOf(number);
        }
    }
}
