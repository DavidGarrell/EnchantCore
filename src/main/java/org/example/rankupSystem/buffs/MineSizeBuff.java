package org.example.rankupSystem.buffs;

import org.apfloat.Apfloat;
import org.bukkit.Material;
import org.example.rankupSystem.RiftBuff;

public class MineSizeBuff extends RiftBuff {

    public MineSizeBuff() {
        super("mine_size", "Mine Size", "Increases the size of your private mine.",
                Material.TNT, 5, 40);
    }

    @Override
    public Apfloat calculateValue(int effectiveLevel) {
        return new Apfloat(effectiveLevel);
    }

    @Override
    protected String formatValue(Apfloat value) {
        return String.format(String.valueOf(value.intValue()));
    }
}