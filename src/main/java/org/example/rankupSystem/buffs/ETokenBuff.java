package org.example.rankupSystem.buffs;

import org.apfloat.Apfloat;
import org.bukkit.Material;
import org.example.rankupSystem.RiftBuff;
import org.example.rankupSystem.RiftConfig;

public class ETokenBuff extends RiftBuff {

    private static final Apfloat BASE_VALUE = Apfloat.ONE;

    /**
     * Der Anstieg des Multiplikators pro effektivem Level.
     * 0.05 entspricht einem additiven Anstieg von 5% pro Level.
     */
    private static final Apfloat INCREASE_PER_LEVEL = RiftConfig.DIMENSION_ETOKEN_BUFF;

    public ETokenBuff() {
        // ID, Name, Beschreibung, Material, Upgrade-Intervall, Max-Level
        super("etoken_boost", "EToken Boost", "Provides a boost to your EToken income.",
                Material.EMERALD, 1, -1); // Upgrades jede Dimension, unendlich Level
    }

    /**
     * Berechnet den Wert des Buffs.
     * Formel: 1.0 + (Level * 0.08) -> Startet bei 100%, +8% pro Level (Dimension)
     *
     * @param effectiveLevel Das effektive Level des Buffs.
     * @return Der berechnete Multiplikator.
     */
    @Override
    public Apfloat calculateValue(int effectiveLevel) {
        return new Apfloat(1.0 + ((effectiveLevel - 1)) * 0.05); // +5% pro Level
    }

    /**
     * Formatiert den Wert für die Anzeige im GUI.
     */
    @Override
    protected String formatValue(Apfloat value) {
        // toString(true) gibt eine saubere Dezimaldarstellung
        String asString = value.toString(true);

        // Finde den Dezimalpunkt
        int decimalIndex = asString.indexOf('.');

        // Kürze auf 2 Nachkommastellen, falls mehr vorhanden sind
        if (decimalIndex != -1 && asString.length() > decimalIndex + 3) {
            asString = asString.substring(0, decimalIndex + 3);
        }

        return "x" + asString;
    }
}