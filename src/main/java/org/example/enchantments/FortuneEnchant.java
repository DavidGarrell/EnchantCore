package org.example.enchantments;

import org.apfloat.Apfloat;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.example.api.Enchant;
import org.example.api.EnchantType;
import org.example.api.UtilPlayer;
import org.example.economy.Currency;
import org.example.economy.Economy;
import org.example.economy.EconomyService;

public class FortuneEnchant extends Enchant {

    // Using constants for common values
    private static final String DEFAULT_DESCRIPTION = "Test Enchantment";
    private static final Material DEFAULT_MATERIAL = Material.DIAMOND;

    public FortuneEnchant() {
        this(DEFAULT_DESCRIPTION, DEFAULT_MATERIAL);
    }

    public FortuneEnchant(String description, Material material) {
        super("fortune", 10000, 0, 1, 0, "§6§lFortune", description, new Apfloat(100), 1.01f, EnchantType.ETOKENS);
        setDescription(description);
        setMaterial(material);
    }

    @Override
    public void setDescription(String description) {
        super.setDescription(description);
    }

    @Override
    public void setMaterial(Material material) {
        super.setMaterial(material);
    }

    @Override
    public void execute(Location location, Player player) {
        if (location == null) return;

        player.sendMessage(Economy.format(MoneyCalculator.calculateMoneyGained(UtilPlayer.getPlayer(player), 1)));
        EconomyService.addBalance(player.getUniqueId(), Currency.MONEY, MoneyCalculator.calculateMoneyGained(UtilPlayer.getPlayer(player), 1000));
    }

    public float getMultiplier() {
       return 1 + ((float) getLevel() /100);
    }
}