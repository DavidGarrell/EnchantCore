package org.example.rankupSystem.buffs;

import org.apfloat.Apfloat;
import org.bukkit.Material;
import org.example.rankupSystem.RiftBuff;

public class EnchantBoostBuff extends RiftBuff {

    public EnchantBoostBuff() {
        super("enchant_boost", "Enchant Boost", "Slightly increases proc chance of all enchants.",
                Material.ENCHANTED_BOOK, 10, 50); // Upgrades every 10 dimensions, max level 50
    }

    @Override
    public Apfloat calculateValue(int effectiveLevel) {
        // Formula: 1.0 + (level * 0.005) -> +0%, +0.5%, +1.0% ...
        return new Apfloat(1.0 + (effectiveLevel * 0.005));
    }

    @Override
    protected String formatValue(Apfloat value) {
        Apfloat percentValue = value.subtract(Apfloat.ONE).multiply(new Apfloat(100));
        return "+" + percentValue.toString(true).substring(0, Math.min(3, percentValue.toString(true).length())) + "%";
    }
}
