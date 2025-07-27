package org.example.enchantments;

import org.apfloat.Apfloat;
import org.apfloat.ApfloatMath;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.example.api.Enchant;
import org.example.api.EnchantType;
import org.example.api.UtilPlayer;
import org.example.economy.Currency;
import org.example.economy.Economy;
import org.example.economy.EconomyService;

public class TokenMiner extends Enchant {

    private static final Material DEFAULT_MATERIAL = Material.SUNFLOWER;
    private static final String DEFAULT_DESCRIPTION = "";
    private static final Apfloat BASE_TOKENS_PER_BLOCK = new Apfloat(10);
    private static final Apfloat TOKEN_INCREASE_PER_LEVEL = new Apfloat("1.001");

    public TokenMiner() {
        this(DEFAULT_DESCRIPTION, DEFAULT_MATERIAL);
    }

    public TokenMiner(String description, Material material) {
        super("tokenminer", 10000, 0, 1, 0, "§6§lToken Miner", DEFAULT_DESCRIPTION, new Apfloat(500), 1.0014f, EnchantType.ETOKENS);
        setMaterial(material);
    }

    @Override
    public void execute(Location location, Player player) {

        /*
        Apfloat tokensGained = TokenCalculator.calculateTokensGained(UtilPlayer.getPlayer(player.getUniqueId()), 1);
        Economy.addBalance(player.getUniqueId(), Currency.ETOKENS, tokensGained);
        player.sendMessage("§6§lToken Miner §7» §aYou received " + Economy.format(tokensGained) + " tokens for breaking a block!");
        player.sendMessage("§6§lToken Miner §7» §aYou now have " + Economy.format(Economy.getBalance(player.getUniqueId(), Currency.ETOKENS)) + " tokens.");


         */
        EconomyService.addBalance(
                player.getUniqueId(),
                Currency.ETOKENS,
                TokenCalculator.calculateTokensGained(UtilPlayer.getPlayer(player.getUniqueId()), 1)
        );
    }

    public Apfloat getTokensPerBlock() {
        Apfloat levelMultiplier = ApfloatMath.pow(new Apfloat(priceIncrease), new Apfloat(getLevel()));
        if (getLevel() > 0) {
            levelMultiplier = ApfloatMath.pow(new Apfloat(priceIncrease), new Apfloat(getLevel()));
        }
        if(getPrestige() > 0) {
            levelMultiplier.multiply(ApfloatMath.pow(new Apfloat(1.05), new Apfloat(getPrestige())));
        }
        return BASE_TOKENS_PER_BLOCK.multiply(levelMultiplier);
    }
}
