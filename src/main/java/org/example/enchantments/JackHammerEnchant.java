package org.example.enchantments;

import org.bukkit.Location;
import org.bukkit.Material;
import org.example.api.Enchant;

public class JackHammerEnchant extends Enchant {

    public JackHammerEnchant(){
        super("jackhammer", 500, 10, 0.1, "§5§lJackhammer", "Test");
        setMaterial(Material.HOPPER);
    }
    @Override
    public void execute(Location location) {
        System.out.println(location);
    }

    @Override
    public void setMaterial(Material material){
        super.setMaterial(material);
    }
}
