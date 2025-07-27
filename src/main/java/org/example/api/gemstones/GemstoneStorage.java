package org.example.api.gemstones;

import org.example.api.Enchant;

import java.util.*;
import java.util.stream.Collectors;

public class GemstoneStorage {

    private List<Gemstone> gemstones;

    public GemstoneStorage() {
        this.gemstones = new ArrayList<>();
    }

    public void addGemstone(Gemstone gemstone) {
        gemstones.add(gemstone);
    }

    public void removeGemstone(Gemstone gemstone) {
        gemstones.remove(gemstone);
    }
    public List<Gemstone> getGemstones() {
        return gemstones;
    }

    public List<Gemstone> getGemstonesByEnchant(Enchant enchant) {
        List<Gemstone> result = new ArrayList<>();
        for (Gemstone gemstone : gemstones) {
            if (gemstone.getEnchant().equals(enchant)) {
                result.add(gemstone);
            }
        }
        return result;
    }

    /**
     * Finds the gemstone with the highest buff value for a specific enchant type.
     * @param enchant The enchant type to search for.
     * @return An Optional containing the best gemstone, or an empty Optional if none are found.
     */
    public Optional<Gemstone> getBestGemstone(Enchant enchant) {
        return getGemstonesByEnchant(enchant).stream()
                .max(Comparator.comparing(Gemstone::getBuffValue));
    }

    /**
     * Attempts to merge three gemstones of the same enchant and tier.
     * If successful, it removes the three old gemstones and adds one new gemstone of the next tier.
     *
     * @param gemsToMerge A list containing exactly three gemstones to be merged.
     * @return true if the merge was successful, false otherwise.
     */
    public boolean mergeGemstones(List<Gemstone> gemsToMerge) {
        // --- Validation ---
        if (gemsToMerge == null || gemsToMerge.size() != 3) {
            return false; // Must be exactly three gemstones
        }

        // Check if all gemstones are of the same enchant and tier
        Gemstone firstGem = gemsToMerge.get(0);
        Enchant enchant = firstGem.getEnchant();
        int tier = firstGem.getTier();
        for (Gemstone gem : gemsToMerge) {
            if (gem.getEnchant() != enchant || gem.getTier() != tier) {
                return false; // All gems must match
            }
        }

        // Check if the player actually owns these gemstones in storage
        if (!this.gemstones.containsAll(gemsToMerge)) {
            return false;
        }

        // --- Execution ---

        // 1. Remove the three old gemstones from storage
        for (Gemstone gem : gemsToMerge) {
            this.gemstones.remove(gem);
        }

        // 2. Create a new gemstone of the next tier
        Gemstone newGem = new Gemstone(enchant, tier + 1);

        // 3. Add the new gemstone to storage
        this.gemstones.add(newGem);

        return true;
    }

    /**
     * Gets a list of all unique enchant types for which the player has at least 3 gemstones of the same tier.
     * This is used to populate the merge menu.
     * @return A list of Enchants that have mergeable gemstones.
     */
    public List<Enchant> getMergeableEnchantTypes() {
        return this.gemstones.stream()
                .collect(Collectors.groupingBy(gem -> gem.getEnchant().getId() + ":" + gem.getTier()))
                .values().stream()
                .filter(list -> list.size() >= 3)
                .map(list -> list.get(0).getEnchant())
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * Attempts to merge all possible groups of 3 gemstones of the same type and tier.
     * @return The number of successful merges performed.
     */
    public int mergeAllPossible() {
        int mergesPerformed = 0;
        boolean changed;

        do {
            changed = false;
            // Group all gemstones by a unique key: "enchantId:tier"
            Map<String, List<Gemstone>> mergeableGroups = this.gemstones.stream()
                    .collect(Collectors.groupingBy(gem -> gem.getEnchant().getId() + ":" + gem.getTier()));

            for (List<Gemstone> group : mergeableGroups.values()) {
                // Check if a merge is possible (at least 3 gems)
                if (group.size() >= 3) {
                    Gemstone firstGem = group.get(0);

                    // Take the first 3 gems for the merge
                    List<Gemstone> gemsToMerge = group.subList(0, 3);

                    // Perform the merge
                    this.gemstones.removeAll(gemsToMerge);
                    this.gemstones.add(new Gemstone(firstGem.getEnchant(), firstGem.getTier() + 1));

                    mergesPerformed++;
                    changed = true;
                    break; // Restart the loop to re-evaluate groups, as a new gem has been added
                }
            }
        } while (changed); // Continue as long as merges are being made (e.g., merging Tier 1s creates enough Tier 2s to merge)

        return mergesPerformed;
    }

    /**
     * Attempts to merge all possible groups for a SPECIFIC enchant type.
     * @param enchant The enchant type to merge.
     * @return The number of successful merges performed.
     */
    public int mergeAllPossible(Enchant enchant) {
        // This is a simplified version of the global mergeAll
        int mergesPerformed = 0;
        boolean changed;

        do {
            changed = false;
            List<Gemstone> gemsOfEnchant = getGemstonesByEnchant(enchant);

            Map<Integer, List<Gemstone>> mergeableGroups = gemsOfEnchant.stream()
                    .collect(Collectors.groupingBy(Gemstone::getTier));

            for (List<Gemstone> group : mergeableGroups.values()) {
                if (group.size() >= 3) {
                    Gemstone firstGem = group.get(0);
                    List<Gemstone> gemsToMerge = group.subList(0, 3);

                    this.gemstones.removeAll(gemsToMerge);
                    this.gemstones.add(new Gemstone(firstGem.getEnchant(), firstGem.getTier() + 1));

                    mergesPerformed++;
                    changed = true;
                    break;
                }
            }
        } while (changed);

        return mergesPerformed;
    }

}
