package org.example.enchantments;

import org.apfloat.Apfloat;
import org.apfloat.ApfloatMath;
import org.example.api.UtilPlayer;
import org.example.api.perks.PerkType;
import org.example.economy.Economy;
import org.example.rankupSystem.DimensionalRift;

import java.math.RoundingMode;

public class MoneyCalculator {

    static Apfloat calculateMoneyGained(UtilPlayer utilPlayer, int blocksBroken) {
        FortuneEnchant fortuneEnchant = (FortuneEnchant) utilPlayer.getEnchantByName("fortune"); // Sicherer: getEnchantById verwenden
        if (fortuneEnchant == null) {
            return Apfloat.ZERO;
        }

        // 1. Definiere die Basis-Einkommensquelle (z.B. 1 Geld pro Block)
        Apfloat baseMoneyPerBlock = new Apfloat(1);

        // 2. Berechne das Einkommen basierend auf dem Enchant-Level
        // KORREKTUR #1: Weise das Ergebnis der Multiplikation wieder der Variable zu.
        Apfloat moneyFromEnchant = baseMoneyPerBlock.multiply(new Apfloat(fortuneEnchant.getMultiplier()));

        // 3. Hole alle relevanten Boni
        double moneyPerkBonus = utilPlayer.getTotalBuff(PerkType.MONEY_BOOST);
        DimensionalRift rift = utilPlayer.getRiftProgression();
        Apfloat dimensionBuff = rift.getTotalDimensionBuff();
        Apfloat layerBuff = rift.getLayerMultiplier();

        // 4. Erstelle den GESAMT-Multiplikator, indem du alle Boni addierst
        // Formel: 1 + PerkBonus + DimensionBonus + LayerBonus
        Apfloat totalMultiplier = new Apfloat(layerBuff.doubleValue()).multiply(new Apfloat(1.0 + moneyPerkBonus));

        // 5. Wende den Gesamt-Multiplikator auf das Einkommen aus dem Enchant an
        // KORREKTUR #2: Weise auch hier das Ergebnis wieder zu.
        Apfloat finalMoneyPerBlock = moneyFromEnchant.multiply(totalMultiplier).multiply(dimensionBuff);

        // --- DEBUGGING (kannst du danach entfernen) ---
        System.out.println(String.format("FortuneLvl: %d | Base: %.2f | Multiplier: %.2f (Perk: %.2f, Dim: %s, Layer: %.2f) | Final/Block: %.2f",
                fortuneEnchant.getLevel(),
                moneyFromEnchant.doubleValue(),
                totalMultiplier.doubleValue(),
                moneyPerkBonus,
                Economy.format(dimensionBuff),
                layerBuff.doubleValue(),
                finalMoneyPerBlock.doubleValue()
        ));

        // 6. Multipliziere mit der Anzahl der Blöcke für das Endergebnis
        return ApfloatMath.roundToInteger(finalMoneyPerBlock.multiply(new Apfloat(blocksBroken)), RoundingMode.CEILING);
    }
}