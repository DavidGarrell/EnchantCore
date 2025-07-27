package org.example.rankupSystem;

import org.apfloat.Apfloat;

/**
 * A central configuration class to manage all balancing variables for the
 * Dimensional Rift rankup system.
 */
public class RiftConfig {

    // --- Core Progression ---
    public static final int LAYERS_PER_DIMENSION = 100;

    // --- Layer Cost Formula ---
    // Cost(layer) = LAYER_BASE_COST * (LAYER_GROWTH_FACTOR ^ (layer - 1))
    public static final Apfloat LAYER_BASE_COST = new Apfloat("50"); // Cost for the very first layer (Layer 1)
    public static final Apfloat LAYER_GROWTH_FACTOR = new Apfloat("1.15"); // 15% more expensive per layer
    public static final Apfloat LAYER_MULTIPLIER = new Apfloat("1.15"); // 15% increase per layer

    // --- Dimension Leap Cost Formula (Phase 1: Growth) ---
    // Cost(dimension) = DIMENSION_BASE_COST * (DIMENSION_GROWTH_FACTOR ^ (dimension - 1))
    public static final Apfloat DIMENSION_BASE_COST = new Apfloat("100"); // 1 Trillion for the first leap
    public static final Apfloat DIMENSION_GROWTH_FACTOR = new Apfloat(2);
    public static final Apfloat EARLY_GAME_EXTRA_COST_GROWTH = new Apfloat("1.05");
    // --- Dimension Leap Buff Formula ---
    // Buff(dimension) = DIMENSION_BUFF_PER_LEVEL * dimension
    public static final Apfloat DIMENSION_BUFF_PER_LEVEL = new Apfloat(2); // +100% per dimension level (1.0 = 100%)
    // --- Endgame Convergence ---
    public static final int CONVERGENCE_HORIZON_DIMENSION = 30; // The dimension at which the formula changes

    public static final Apfloat DIMENSION_ETOKEN_BUFF = new Apfloat("0.05"); // 50% more eTokens per dimension level
    // The time in minutes the player should grind to afford the next dimension leap in the endgame.
}
