package org.example.api.perks;

import org.bukkit.ChatColor;

public enum Rarity {
    COMMON("Common", ChatColor.GRAY, 0.70),    // 70%
    RARE("Rare", ChatColor.BLUE, 0.25),      // 25%
    EPIC("Epic", ChatColor.DARK_PURPLE, 0.04), // 4%
    LEGENDARY("Legendary", ChatColor.GOLD, 0.01); // 1%

    private final String displayName;
    private final ChatColor color;
    private final double probability;

    Rarity(String displayName, ChatColor color, double probability) {
        this.displayName = displayName;
        this.color = color;
        this.probability = probability;
    }

    public String getDisplayName() {
        return displayName;
    }

    public ChatColor getColor() {
        return color;
    }

    public double getProbability() {
        return probability;
    }

    public String getFormattedName() {
        return color + "§l" + displayName;
    }
}