package org.example.enchantments;

import org.bukkit.Material;
import org.example.api.Enchant;

public class JackHammerEnchant extends Enchant {

    public JackHammerEnchant(){
        super("jackhammer", 500, 1, 1, "§5§lJackhammer", "Test");
        setMaterial(Material.HOPPER);
    }
    @Override
    public void execute(int level) {

    }

    @Override
    public void setMaterial(Material material){
        super.setMaterial(material);
    }
}
