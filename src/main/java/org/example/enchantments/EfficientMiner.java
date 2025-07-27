package org.example.enchantments;

import org.apfloat.Apfloat;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.example.api.Enchant;
import org.example.api.EnchantType;

public class EfficientMiner extends Enchant {

    private static final Material DEFAULT_MATERIAL = Material.GOLDEN_CARROT;
    private static final String DEFAULT_DESCRIPTION = "Reduces blocks needed for next enchant proc";


    public EfficientMiner() {
        this(DEFAULT_DESCRIPTION, DEFAULT_MATERIAL);
    }

    public EfficientMiner(String description, Material material) {
        super("efficientminer", 500, 0, 300, 1, "§a§lEfficient Miner", "Reduces blocks needed for next enchant proc", new Apfloat(500), 1.1f, EnchantType.ETOKENS);
        setMaterial(material);
    }

    @Override
    public void execute(Location location, Player player) {

    }

    public int applyBlockReduction(int originalBlocks, int enchantLevel) {
        int reduction = 25 + (enchantLevel - 1) * 10; // 25% für Level 1, +10% für jedes weitere Level
        return (int) (originalBlocks * (1 - (reduction / 100.0)));
    }

}
