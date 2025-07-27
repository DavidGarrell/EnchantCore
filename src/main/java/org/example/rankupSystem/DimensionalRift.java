package org.example.rankupSystem;

import org.apfloat.Apfloat;
import org.apfloat.ApfloatMath;
import org.bukkit.entity.Player;
import org.example.economy.Currency;
import org.example.economy.EconomyService;
import org.example.rankupSystem.buffs.*;

import java.math.RoundingMode;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class DimensionalRift {

    private int currentDimension;
    private int currentLayer;
    private final Map<String, RiftBuff> availableBuffs = new HashMap<>();


    public DimensionalRift() {
        this.currentDimension = 1;
        this.currentLayer = 0;
        registerBuff(new EnchantBoostBuff());
        registerBuff(new MineSizeBuff());
        registerBuff(new MoneyBuff());
        registerBuff(new ETokenBuff());
        registerBuff(new XPBuff());
    }

    // --- Getters and Setters (for saving/loading player data) ---
    public int getCurrentDimension() { return currentDimension; }
    public int getCurrentLayer() { return currentLayer; }
    public void setCurrentDimension(int dimension) { this.currentDimension = dimension; }
    public void setCurrentLayer(int layer) { this.currentLayer = layer; }


    // --- Core Gameplay Methods ---

    /**
     * Attempts to purchase the next layer.
     * @return true if successful, false otherwise.
     */
    public boolean purchaseNextLayer(Player player) {
        if (isMaxLayer()) {
            return false; // Already at max layer
        }

        Apfloat cost = getNextLayerCost();

        // --- HIER IST DER WICHTIGE TEIL ---
        // Versuche, das Geld abzuziehen.
        if (EconomyService.subtractBalance(player.getUniqueId(), Currency.MONEY, cost)) {
            // Wenn es erfolgreich war, erhöhe das Layer und gib true zurück.
            this.currentLayer++;
            return true;
        }

        // Wenn das Geld nicht abgezogen werden konnte, gib false zurück.
        return false;
    }

    /**
     * Attempts to perform a dimension leap. This is the complete version.
     * @return true if successful, false otherwise.
     */
    public void performDimensionLeap(Player player) {
        if (!isMaxLayer()) {
            return; // Must complete all layers first
        }
        EconomyService.setBalance(player.getUniqueId(), Currency.MONEY, new Apfloat(0));
        this.currentDimension++;
        this.currentLayer = 0;
    }


    // --- Cost Calculation Methods ---

    /**
     * Calculates the cost for the VERY NEXT layer.
     * @return The cost of the next layer, or Apfloat.ZERO if at max layer.
     */
    public Apfloat getNextLayerCost() {
        if (isMaxLayer()) {
            return Apfloat.ZERO;
        }

        Apfloat layerBaseCost = RiftConfig.LAYER_BASE_COST.multiply(
                ApfloatMath.pow(RiftConfig.LAYER_GROWTH_FACTOR, this.currentLayer)
        );

        Apfloat dimensionMultiplier = ApfloatMath.pow(
                RiftConfig.DIMENSION_GROWTH_FACTOR,
                this.currentDimension - 1
        );
        Apfloat costAfterDimensionMultiplier = layerBaseCost.multiply(dimensionMultiplier);

        Apfloat finalCost = costAfterDimensionMultiplier;
        if (this.currentDimension < RiftConfig.CONVERGENCE_HORIZON_DIMENSION) {
            Apfloat earlyGameMultiplier = ApfloatMath.pow(
                    RiftConfig.EARLY_GAME_EXTRA_COST_GROWTH,
                    this.currentDimension - 1
            );
            finalCost = finalCost.multiply(earlyGameMultiplier);
        }

        return ApfloatMath.roundToInteger(finalCost, RoundingMode.CEILING);
    }

    /**
     * Calculates the cost for the next dimension leap.
     * Switches to the endgame formula if the convergence horizon is reached.
     * @return The cost of the next leap.
     */
    public Apfloat getDimensionLeapCost(Player player) {
        if (this.currentDimension < RiftConfig.CONVERGENCE_HORIZON_DIMENSION) {
            // --- Phase 1: Exponential Growth ---
            // Formula: BaseCost * (GrowthFactor ^ (currentDimension - 1))
            return RiftConfig.DIMENSION_BASE_COST.multiply(
                    ApfloatMath.pow(RiftConfig.DIMENSION_GROWTH_FACTOR, this.currentDimension - 1)
            );
        } else {
           return new Apfloat("1E" + (12 + (this.currentDimension - RiftConfig.CONVERGENCE_HORIZON_DIMENSION) * 3));
        }
    }

    // --- Buff Calculation ---

    /**
     * Calculates the total money buff from the current dimension.
     * @return The buff as a multiplier (e.g., 2.0 for +200%).
     */
    public Apfloat getTotalDimensionBuff() {
        if (this.currentDimension <= 0) {
            return Apfloat.ZERO;
        }
        return RiftConfig.DIMENSION_BUFF_PER_LEVEL.multiply(
                ApfloatMath.pow(RiftConfig.DIMENSION_BUFF_PER_LEVEL, this.currentDimension - 1)
        );
    }


    // --- Helper Methods ---

    public boolean isMaxLayer() {
        return this.currentLayer >= RiftConfig.LAYERS_PER_DIMENSION;
    }

    public Apfloat getLayerMultiplier() {

        return RiftConfig.LAYER_MULTIPLIER.multiply(
                ApfloatMath.pow(RiftConfig.LAYER_MULTIPLIER, this.currentLayer)
        );

    }
    public Apfloat calculateCostForLayers(int layersToBuy) {
        if (layersToBuy <= 0) {
            return Apfloat.ZERO;
        }


        Apfloat a = getNextLayerCost();
        Apfloat q = RiftConfig.LAYER_GROWTH_FACTOR;
        int m = layersToBuy;

        Apfloat q_pow_m = ApfloatMath.pow(q, m);
        Apfloat numerator = q_pow_m.subtract(Apfloat.ONE);
        Apfloat denominator = q.subtract(Apfloat.ONE);

        if (denominator.equals(Apfloat.ZERO)) {
            return a.multiply(new Apfloat(m));
        }

        Apfloat totalCost = a.multiply(numerator).divide(denominator);

        return ApfloatMath.roundToInteger(totalCost, RoundingMode.CEILING);
    }


    public int getMaxAffordableLayers(Player player) {
        Apfloat balance = EconomyService.getBalance(player.getUniqueId(), Currency.MONEY);
        int missingLayers = RiftConfig.LAYERS_PER_DIMENSION - this.currentLayer;

        if (missingLayers <= 0 || getNextLayerCost().compareTo(balance) > 0) {
            return 0;
        }

        int low = 1;
        int high = missingLayers;
        int bestGuess = 0;

        while (low <= high) {
            int mid = low + (high - low) / 2;
            if (mid == 0) break;

            Apfloat costForMid = calculateCostForLayers(mid);

            if (costForMid.compareTo(balance) <= 0) {
                bestGuess = mid;
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }

        return bestGuess;
    }

    public Apfloat getDimensionETokenBuff() {
        return RiftConfig.DIMENSION_ETOKEN_BUFF.multiply(new Apfloat(this.currentDimension-1));
    }

    private void registerBuff(RiftBuff buff) {
        availableBuffs.put(buff.getId(), buff);
    }

    public RiftBuff getBuff(String id) {
        return availableBuffs.get(id);
    }

    public Collection<RiftBuff> getAllBuffs() {
        return availableBuffs.values();
    }
}
