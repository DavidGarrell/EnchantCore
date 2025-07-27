package org.example.api;

import org.apfloat.Apfloat;
import org.apfloat.ApfloatMath;

/**
 * Central configuration for the Pickaxe Leveling system.
 */
public class PickaxeConfig {

    /**
     * The base XP cost at Level 0. This is the starting point of the curve.
     */
    public static final Apfloat XP_BASE = new Apfloat(375);

    /**
     * The multiplier that scales with the player's level.
     */
    public static final Apfloat XP_MULTIPLIER = new Apfloat("0.05");

    /**
     * The power to which the level is raised.
     * 1.0 = linear growth
     * 2.0 = quadratic growth (costs accelerate over time)
     */
    public static final Apfloat XP_POWER = new Apfloat(2);


    /**
     * The base amount of XP gained per block broken.
     * Enchants and perks will multiply this value.
     */
    public static final long XP_PER_BLOCK = 1;
}
