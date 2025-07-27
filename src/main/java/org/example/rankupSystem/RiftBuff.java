package org.example.rankupSystem;

import org.apfloat.Apfloat;
import org.bukkit.Material;
import java.util.ArrayList;
import java.util.List;

public abstract class RiftBuff {

    private final String id;
    private final String displayName;
    private final String description;
    private final Material material;
    private final int upgradeInterval;
    private final int maxLevel;

    public RiftBuff(String id, String displayName, String description, Material material, int upgradeInterval, int maxLevel) {
        this.id = id;
        this.displayName = displayName;
        this.description = description;
        this.material = material;
        this.upgradeInterval = upgradeInterval;
        this.maxLevel = maxLevel;
    }

    public int getEffectiveLevel(int dimensionLevel) {
        if (dimensionLevel < 0) return 0;
        int potentialLevel = dimensionLevel / upgradeInterval;
        if (maxLevel != -1) {
            return Math.min(potentialLevel, maxLevel);
        }
        return potentialLevel;
    }

    /**
     * Calculates the value of the buff at a specific effective level.
     * @param effectiveLevel The level of the buff.
     * @return The calculated value as an Apfloat.
     */
    public abstract Apfloat calculateValue(int effectiveLevel);

    /**
     * Generates the lore for the GUI, using Apfloat for all calculations.
     */
    public List<String> generateLore(int dimensionLevel) {
        int currentLevel = getEffectiveLevel(dimensionLevel);
        Apfloat currentValue = calculateValue(currentLevel);

        ArrayList<String> lore = new ArrayList<>();
        lore.add("§7" + description);
        lore.add("");
        lore.add("§a§lBoost Statistics");
        lore.add(" §a§l| §7Current Boost: §a" + formatValue(currentValue));

        if (maxLevel == -1 || currentLevel < maxLevel) {
            int nextLevel = currentLevel + 1;
            Apfloat nextValue = calculateValue(nextLevel);
            int nextDim = nextLevel * upgradeInterval;
            lore.add(" §a§l| §7Next Boost: §a" + formatValue(nextValue));
        } else {
            lore.add(" §a§l| §7Next Boost: §cMAXED OUT");
        }

        lore.add(" §a§l| §7Level: §f" + currentLevel + "§7 / §f" + (maxLevel == -1 ? "∞" : maxLevel));
        lore.add("");
        lore.add("§6§lUpgrade Statistics");
        lore.add(" §6§l| §7Upgrades every §e" + upgradeInterval + " §7Dimensions.");

        return lore;
    }

    /**
     * Formats the Apfloat value for display.
     */
    protected abstract String formatValue(Apfloat value);

    // Standard Getters
    public String getId() { return id; }
    public String getDisplayName() { return displayName; }
    public Material getMaterial() { return material; }
}