package org.example.enchantments;

import org.bukkit.Location;
import org.bukkit.Material;
import org.example.api.Enchant;

public class FortuneEnchant extends Enchant {

    private final String description = "Test Enchantment";

    public FortuneEnchant() {
        super("fortune", 10000, 100, 10, "§6§lFortune", "Test");
        setDescription(description);
        setMaterial(Material.DIAMOND);
    }

    @Override
    public void setDescription(String description) {
        super.setDescription(description);
    }

    @Override
    public void execute(Location location) {

    }

    @Override
    public void setMaterial(Material material){
        super.setMaterial(material);
    }


}