package org.example.menu;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.example.Main;
import org.example.api.Enchant;
import org.example.api.Placeholders;
import org.example.api.UtilPlayer;
import org.example.api.gemstones.Gemstone;
import org.example.api.gemstones.GemstoneSlot;
import org.example.api.gemstones.GemstoneStorage;
import org.example.utils.CustomGUIUtils;

import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Manages a multi-view GUI for socketing gemstones.
 */
public class GemstoneMenu {

    private final Main plugin;

    private static final int GEMS_PER_PAGE = 45;
    private static final DecimalFormat buffFormatter = new DecimalFormat("0.0#");


    public GemstoneMenu(Main plugin) {
        this.plugin = plugin;
    }

    /**
     * Opens the main view of the Gemstone menu.
     */
    public void openMainMenu(Player player) {
        UtilPlayer utilPlayer = UtilPlayer.getPlayer(player.getUniqueId());
        if (utilPlayer == null) return;

        // 6 rows for enough space
        CustomGUIUtils gui = new CustomGUIUtils("§8Gemstone Socketing", 54);

        // --- 1. Display Gemstone Slots in the top row ---
        displaySlots(gui, player, utilPlayer);

        // --- 2. Display available Gemstone categories from storage ---
        displayCategories(gui, player, utilPlayer);

        // Fillers and navigation
        gui.fillEmptySlots(Material.GRAY_STAINED_GLASS_PANE, " ");
        // Optional: Add a back button to the main enchant menu
        // gui.addItem(49, Material.BARRIER, "§cBack", null, (e,c) -> new EnchantMenu(plugin).openEnchantMenu(player));
        gui.addItem(48, Material.NETHER_STAR, "§b§lEquip Best",
                new String[]{"§7Automatically sockets the best", "§7available gemstone into each", "§7empty slot."},
                (e, c) -> {
                    equipBestAll(player, utilPlayer);
                    openMainMenu(player); // Refresh menu
                });

        gui.addItem(47, Material.ANVIL, "§6§lMerge All Gemstones",
                new String[]{"§7Automatically merges all possible", "§7gemstones in your storage."},
                (e, c) -> {
                    int merges = utilPlayer.getGemstoneStorage().mergeAllPossible();
                    if (merges > 0) {
                        player.sendMessage(Placeholders.GEMSTONE_PREFIX + "§7Successfully performed §a" + merges + " §7merge(s)!");
                        player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_USE, 1f, 1f);
                    } else {
                        player.sendMessage(Placeholders.GEMSTONE_PREFIX + "§cNo possible merges found.");
                        player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 1f);
                    }
                    openMainMenu(player); // Refresh menu
                });

        gui.open(player);
    }

    /**
     * Opens the view to select a specific gemstone from a category.
     */
    private void openSelectionMenu(Player player, Enchant enchantCategory, int page) {
        UtilPlayer utilPlayer = UtilPlayer.getPlayer(player.getUniqueId());
        GemstoneStorage storage = utilPlayer.getGemstoneStorage();
        List<Gemstone> availableGems = storage.getGemstonesByEnchant(enchantCategory);

        CustomGUIUtils gui = new CustomGUIUtils("§8Select a " + enchantCategory.getDisplayname() + " Gem", 54);



        int totalPages = (int) Math.ceil((double) availableGems.size() / GEMS_PER_PAGE);
        if (totalPages == 0) totalPages = 1;

        int startIndex = (page - 1) * GEMS_PER_PAGE;
        int endIndex = Math.min(startIndex + GEMS_PER_PAGE, availableGems.size());

        List<Gemstone> gemsForThisPage = availableGems.subList(startIndex, endIndex);

        for (Gemstone gemToSocket : gemsForThisPage) {
            gui.addItem(gui.getNextEmptySlot(), gemToSocket.getGemstoneItem(), (e, c) -> {
                // --- NEUE, ROBUSTE LOGIK ZUM SOCKELN ---

                // 1. Finde heraus, ob bereits ein Stein dieses Typs gesockelt ist.
                Optional<GemstoneSlot> existingSlotOpt = utilPlayer.getGemstoneSlots().stream()
                        .filter(slot -> slot.getGemstone() != null && slot.getGemstone().getEnchant().getId().equals(gemToSocket.getEnchant().getId()))
                        .findFirst();

                if (existingSlotOpt.isPresent()) {
                    // --- FALL 1: ERSETZE EINEN EXISTIERENDEN STEIN ---
                    GemstoneSlot slotToReplace = existingSlotOpt.get();
                    Gemstone oldGem = slotToReplace.getGemstone();

                    // Lege den alten Stein zurück ins Lager
                    storage.addGemstone(oldGem);
                    // Setze den neuen Stein in den Slot
                    slotToReplace.setGemstone(gemToSocket); // Wir brauchen eine setGemstone Methode
                    // Entferne den neuen Stein aus dem Lager
                    storage.removeGemstone(gemToSocket);

                    player.sendMessage(Placeholders.GEMSTONE_PREFIX + "§7Successfully replaced your " + oldGem.getEnchant().getDisplayname() + " §7Gemstone!");

                } else {
                    // --- FALL 2: FINDE EINEN LEEREN SLOT ---
                    Optional<GemstoneSlot> emptySlotOpt = utilPlayer.getGemstoneSlots().stream()
                            .filter(slot -> slot.getGemstone() == null)
                            .findFirst();

                    if (emptySlotOpt.isPresent()) {
                        GemstoneSlot targetSlot = emptySlotOpt.get();
                        targetSlot.addGemstone(gemToSocket);
                        storage.removeGemstone(gemToSocket);
                        player.sendMessage(Placeholders.GEMSTONE_PREFIX + "§7Successfully socketed the " + gemToSocket.getEnchant().getDisplayname() + " Gemstone!");
                    } else {
                        player.sendMessage(Placeholders.GEMSTONE_PREFIX + "§cYou have no empty slots to socket a new type of gemstone!");
                        player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 1f);
                        return; // Breche ab, wenn kein Platz ist
                    }
                }

                player.playSound(player.getLocation(), Sound.BLOCK_END_PORTAL_FRAME_FILL, 1f, 1.2f);
                openMainMenu(player); // Gehe immer zum Hauptmenü zurück
            });
        }

        if (page > 1) {
            gui.addItem(45, Material.ARROW, "§c§lPrevious Page", new String[]{"§7Go to page " + (page - 1)}, (e, c) -> {
                openSelectionMenu(player, enchantCategory, page - 1);
            });
        }

        // "Weiter"-Button (zur nächsten Seite)
        if (page < totalPages) {
            gui.addItem(53, Material.ARROW, "§a§lNext Page", new String[]{"§7Go to page " + (page + 1)}, (e, c) -> {
                openSelectionMenu(player, enchantCategory, page + 1);
            });
        }

        // Seitenanzeige
        gui.addItem(49, Material.BOOK, "§ePage " + page + "/" + totalPages, null, null);

        gui.fillEmptySlots(Material.GRAY_STAINED_GLASS_PANE, " ");
        gui.addItem(49, Material.BARRIER, "§c§lBack", null, (e, c) -> openMainMenu(player));
        gui.open(player);
    }

    /**
     * Displays the gemstone slots in the top row of the GUI.
     */
    private void displaySlots(CustomGUIUtils gui, Player player, UtilPlayer utilPlayer) {
        List<GemstoneSlot> slots = utilPlayer.getGemstoneSlots();
        int[] guiSlotPositions = {2, 3, 4, 5, 6}; // Centered in the top row

        for (int i = 0; i < guiSlotPositions.length; i++) {
            int currentGuiSlot = guiSlotPositions[i];
            if (i < slots.size()) {
                GemstoneSlot gemstoneSlot = slots.get(i);
                if (gemstoneSlot.getGemstone() != null) {
                    // --- SLOT IS FILLED ---
                    Gemstone equippedGem = gemstoneSlot.getGemstone();
                    ArrayList<String> lore = new ArrayList<>();
                    lore.add("");
                    lore.add("§a§lStatistics");
                    lore.add(" §a§l| §7Tier: §e" + equippedGem.getTier());
                    lore.add(" §a§l| §7Buff: §e" + buffFormatter.format(equippedGem.getBuffValue()) + "%");
                    lore.add("");
                    lore.add("§cClick to unsocket this Gemstone.");

                    gui.addItem(currentGuiSlot, equippedGem.getEnchant().getMaterial(), equippedGem.getEnchant().getDisplayname() + " Gemstone", lore.toArray(new String[0]), (e, c) -> {
                        utilPlayer.getGemstoneStorage().addGemstone(equippedGem); // Return to storage
                        gemstoneSlot.removeGemstone();
                        player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_PLACE, 0.8f, 1f);
                        openMainMenu(player);
                    });
                } else {
                    // --- SLOT IS EMPTY ---
                    ArrayList<String> lore = new ArrayList<>();
                    lore.add("§7This slot is empty.");
                    lore.add("§7Click a gemstone category below");
                    lore.add("§7to choose a gem to socket here.");
                    gui.addItem(currentGuiSlot, Material.GRAY_DYE, "§eEmpty Gemstone Slot #" + gemstoneSlot.getId(), lore.toArray(new String[0]), null);
                }
            } else {
                // --- SLOT IS LOCKED ---
                gui.addItem(currentGuiSlot, Material.BARRIER, "§cLocked Gemstone Slot", new String[]{"§7Unlock this slot to use it."}, null);
            }
        }
    }

    /**
     * Displays the gemstone categories from the player's storage.
     */
    private void displayCategories(CustomGUIUtils gui, Player player, UtilPlayer utilPlayer) {
        GemstoneStorage storage = utilPlayer.getGemstoneStorage();
        if (storage == null || storage.getGemstones().isEmpty()) {
            gui.addItem(22, Material.GLASS_BOTTLE, "§cNo Gemstones", new String[]{"§7You do not have any gemstones in your storage."}, null);
            return;
        }

        // Group gemstones by their enchant type
        storage.getGemstones().stream()
                .map(Gemstone::getEnchant)
                .distinct()
                .forEach(enchant -> {
                    long count = storage.getGemstonesByEnchant(enchant).size();

                    ArrayList<String> lore = new ArrayList<>();
                    lore.add("§7You have §e" + count + " §7gemstone(s) of this type.");
                    lore.add("");
                    lore.add("§eClick to view and select one!");

                    gui.addItem(gui.getNextEmptySlot(18), enchant.getMaterial(), "§b" + enchant.getDisplayname() + " Gemstones", lore.toArray(new String[0]), (e, c) -> {
                        // Find the first available empty slot to socket into
                        GemstoneSlot targetSlot = utilPlayer.getGemstoneSlots().stream()
                                .filter(s -> s.getGemstone() == null)
                                .findFirst()
                                .orElse(null);


                            openSelectionMenu(player, enchant, 1);

                    });
                });
    }
    private void equipBestAll(Player player, UtilPlayer utilPlayer) {
        GemstoneStorage storage = utilPlayer.getGemstoneStorage();
        if (storage == null || storage.getGemstones().isEmpty()) {
            player.sendMessage(Placeholders.GEMSTONE_PREFIX + "§cYour gemstone storage is empty.");
            return;
        }

        int upgradesMade = 0;
        int newEquips = 0;

        // --- Schritt 1: Verbessere bereits belegte Slots ---
        for (GemstoneSlot slot : utilPlayer.getGemstoneSlots()) {
            Gemstone equippedGem = slot.getGemstone();
            if (equippedGem != null) {
                // Finde den besten Stein DIESES Typs im Lager
                Optional<Gemstone> bestInStorageOpt = storage.getBestGemstone(equippedGem.getEnchant());

                if (bestInStorageOpt.isPresent()) {
                    Gemstone bestInStorage = bestInStorageOpt.get();
                    // Prüfe, ob der Stein im Lager besser ist als der ausgerüstete
                    if (bestInStorage.getBuffValue() > equippedGem.getBuffValue()) {
                        // Tausche die Steine
                        storage.removeGemstone(bestInStorage);
                        storage.addGemstone(equippedGem);
                        slot.setGemstone(bestInStorage);
                        upgradesMade++;
                    }
                }
            }
        }

        // --- Schritt 2: Fülle die jetzt leeren Slots (falls vorhanden) ---
        List<GemstoneSlot> emptySlots = utilPlayer.getGemstoneSlots().stream()
                .filter(s -> s.getGemstone() == null)
                .collect(Collectors.toList());

        if (!emptySlots.isEmpty()) {
            // Finde alle Enchant-Typen, die aktuell gesockelt sind, um Duplikate zu vermeiden
            Set<String> socketedEnchantIds = utilPlayer.getGemstoneSlots().stream()
                    .filter(s -> s.getGemstone() != null)
                    .map(s -> s.getGemstone().getEnchant().getId())
                    .collect(Collectors.toSet());

            // Finde die besten verfügbaren Steine für noch NICHT gesockelte Typen
            List<Gemstone> bestAvailableGems = storage.getGemstones().stream()
                    .map(Gemstone::getEnchant)
                    .distinct()
                    .filter(enchant -> !socketedEnchantIds.contains(enchant.getId()))
                    .map(storage::getBestGemstone)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .sorted(Comparator.comparing(Gemstone::getBuffValue).reversed())
                    .collect(Collectors.toList());

            // Fülle die leeren Slots mit den besten gefundenen Steinen
            int emptySlotIndex = 0;
            for (Gemstone gemToEquip : bestAvailableGems) {
                if (emptySlotIndex >= emptySlots.size()) break;

                GemstoneSlot targetSlot = emptySlots.get(emptySlotIndex);
                targetSlot.addGemstone(gemToEquip);
                storage.removeGemstone(gemToEquip);

                newEquips++;
                emptySlotIndex++;
            }
        }

        // --- Schritt 3: Gib dem Spieler eine zusammenfassende Nachricht ---
        if (upgradesMade > 0 || newEquips > 0) {
            StringBuilder feedback = new StringBuilder(Placeholders.GEMSTONE_PREFIX + "§7Equip Best successful!");
            if (upgradesMade > 0) {
                feedback.append(" §7(").append(upgradesMade).append(" upgraded)");
            }
            if (newEquips > 0) {
                feedback.append(" §7(").append(newEquips).append(" newly equipped)");
            }
            player.sendMessage(feedback.toString());
            player.playSound(player.getLocation(), Sound.ITEM_ARMOR_EQUIP_DIAMOND, 1f, 1.2f);
        } else {
            player.sendMessage(Placeholders.GEMSTONE_PREFIX + "§7No better gemstones were found to equip.");
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 1f);
        }
    }

}
