package org.example.api;

import org.apfloat.Apfloat;
import org.apfloat.ApfloatMath;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.example.api.UtilPlayer;
import org.example.api.perks.PerkType;

/**
 * Manages the leveling progression of a player's pickaxe.
 */
public class PickaxeLeveling {

    private int level;
    private Apfloat currentXp;

    public PickaxeLeveling() {
        this.level = 0;
        this.currentXp = Apfloat.ZERO;
    }

    // --- Getters and Setters (for saving/loading data) ---
    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }
    public Apfloat getCurrentXp() { return currentXp; }
    public void setCurrentXp(Apfloat xp) { this.currentXp = xp; }


    // --- Core Methods ---

    /**
     * Adds XP to the pickaxe and handles level-ups.
     * @param player The player gaining XP.
     * @param amount The amount of XP to add.
     */
    public void addXp(Player player, Apfloat amount) {
        if (amount.compareTo(Apfloat.ZERO) <= 0) return;

        this.currentXp = this.currentXp.add(amount);

        // Check for level-ups repeatedly in case of multiple level-ups at once
        while (this.currentXp.compareTo(getXpForNextLevel()) >= 0) {
            levelUp(player);
        }
    }

    /**
     * Handles the logic for a single level-up.
     */
    private void levelUp(Player player) {
        // Subtract the required XP for the level we just completed
        this.currentXp = this.currentXp.subtract(getXpForNextLevel());
        this.level++;

        // --- Player Feedback ---
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1.2f);
        player.sendTitle("§bPickaxe Level Up!", "§7You reached Level §e" + this.level, 10, 40, 20);

        // You could add rewards here, e.g., unlocking a Gemstone Slot
        // if (this.level % 100 == 0) {
        //     // Unlock new feature
        // }
    }


    // --- Calculation Methods ---

    /**
     * Calculates the total XP required to reach the next level using a polynomial formula.
     * Formula: Base + (Multiplier * (currentLevel ^ Power))
     * @return The XP needed to level up.
     */
    public Apfloat getXpForNextLevel() {
        Apfloat currentLevelApfloat = new Apfloat(this.level);

        // Calculate (level ^ Power)
        Apfloat levelPowered = ApfloatMath.pow(currentLevelApfloat, PickaxeConfig.XP_POWER);

        // Calculate Multiplier * (level ^ Power)
        Apfloat xpFromLevel = PickaxeConfig.XP_MULTIPLIER.multiply(levelPowered);

        // Calculate Base + result
        return PickaxeConfig.XP_BASE.add(xpFromLevel);
    }


    /**
     * Calculates the final amount of XP gained from breaking a number of blocks,
     * including all buffs.
     * @param utilPlayer The player data object.
     * @param blocksBroken The number of blocks broken.
     * @return The final calculated XP amount.
     */
    public static Apfloat calculateXpGained(UtilPlayer utilPlayer, int blocksBroken) {
        Apfloat baseXP = new Apfloat(PickaxeConfig.XP_PER_BLOCK * blocksBroken);

        // --- Apply Multipliers ---
        double perkBonus = utilPlayer.getTotalBuff(PerkType.XP_BOOST);
        // You can add other multipliers here (e.g., from dimensions, global boosters)

        double totalMultiplier = 1.0 + perkBonus;

        return baseXP.multiply(new Apfloat(totalMultiplier));
    }
}
