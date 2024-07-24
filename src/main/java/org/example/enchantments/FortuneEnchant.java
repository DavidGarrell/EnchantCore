package org.example.enchantments;

import org.bukkit.Material;
import org.example.api.Enchant;

public class FortuneEnchant extends Enchant {

    private final String description = "Test Enchantment";

    public FortuneEnchant() {
        super("fortune", 10000, 100, 0, "§6§lFortune", "Test");
        setDescription(description);
        setMaterial(Material.DIAMOND);
    }

    @Override
    public void setDescription(String description) {
        super.setDescription(description);
    }

    @Override
    public void setMaterial(Material material){
        super.setMaterial(material);
    }

    @Override
    public void execute(int level) {
        // Deine spezifische Logik für das Fortune Enchantment
        System.out.println("Fortune enchant activated at level " + level);
    }
}