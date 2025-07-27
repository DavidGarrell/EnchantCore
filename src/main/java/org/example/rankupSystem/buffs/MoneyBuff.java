package org.example.rankupSystem.buffs;

import org.apfloat.Apfloat;
import org.apfloat.ApfloatMath;
import org.bukkit.Material;
import org.example.economy.Economy;
import org.example.rankupSystem.RiftBuff;
import org.example.rankupSystem.RiftConfig;

public class MoneyBuff extends RiftBuff {

    public MoneyBuff() {
        // ID, Name, Beschreibung, Material, Upgrade-Intervall, Max-Level
        super("money_boost", "Money Boost", "Provides a boost to your Money income.",
                Material.DIAMOND, 1, -1); // Upgrades jede Dimension, unendlich Level
    }

    /**
     * Berechnet den Wert des Buffs.
     * Formel: 1.0 + (Level * 0.1) -> Startet bei 100%, +10% pro Level (Dimension)
     *
     * @param effectiveLevel Das effektive Level des Buffs.
     * @return Der berechnete Multiplikator.
     */
    @Override
    public Apfloat calculateValue(int effectiveLevel) {
        return RiftConfig.DIMENSION_BUFF_PER_LEVEL.multiply(
                ApfloatMath.pow(RiftConfig.DIMENSION_BUFF_PER_LEVEL, effectiveLevel - 1)
        );
    }

    /**
     * Formatiert den Wert für die Anzeige im GUI.
     */
    @Override
    protected String formatValue(Apfloat value) {
        return "x" + Economy.format(value);
    }
}
