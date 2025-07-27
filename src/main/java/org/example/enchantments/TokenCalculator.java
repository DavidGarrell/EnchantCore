package org.example.enchantments;

import org.apfloat.Apfloat;
import org.apfloat.ApfloatMath;
import org.example.api.UtilPlayer;
import org.example.api.perks.PerkType;
import org.example.enchantments.TokenMiner;
import org.example.rankupSystem.DimensionalRift;

import java.math.RoundingMode;

public class TokenCalculator {

    static Apfloat calculateTokensGained(UtilPlayer utilPlayer, int blocksBroken) {
        TokenMiner tokenMiner = (TokenMiner) utilPlayer.getEnchantByName("tokenminer");
        if (tokenMiner == null) {
            return new Apfloat(0);
        }
        DimensionalRift rift = utilPlayer.getRiftProgression();
        Apfloat baseTokensPerBlock = tokenMiner.getTokensPerBlock();
        Apfloat dimensionBuff = rift.getDimensionETokenBuff().add(new Apfloat(1));
        double etokenBonus = utilPlayer.getTotalBuff(PerkType.ETOKEN_BOOST);

        Apfloat finalTokensPerBlock = baseTokensPerBlock.multiply(
                new Apfloat(1.0).add(new Apfloat(etokenBonus))
        );
        finalTokensPerBlock = finalTokensPerBlock.multiply(dimensionBuff);
        return ApfloatMath.roundToInteger(finalTokensPerBlock.multiply(new Apfloat(blocksBroken)), RoundingMode.CEILING);

    }
}
