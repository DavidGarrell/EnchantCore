package org.example.enchantments;

import org.apfloat.Apfloat;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.example.api.Enchant;
import org.example.api.EnchantType;

public class LaserEnchant extends Enchant {

    private final Material material = Material.REDSTONE;

    public LaserEnchant(){
        super("laser", 200, 5, 5000, 5, "§a§lLaser", "Test", new Apfloat(10000), 1.01f, EnchantType.ETOKENS);
        setMaterial(material);
    }
    @Override
    public void execute(Location location, Player player) {
        System.out.println(location);
    }

    @Override
    public void setMaterial(Material material){
        super.setMaterial(material);
    }
}

