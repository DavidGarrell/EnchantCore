package org.example.enchantments;

import org.apfloat.Apfloat;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.example.api.Enchant;
import org.example.api.EnchantType;

public class MeteorEnchant extends Enchant {

    private final Material material = Material.FIREWORK_STAR;

    public MeteorEnchant(){
        super("meteor", 200, 0, 8000, 7, "§c§lMeteor", "Test", new Apfloat(10000), 1.02f, EnchantType.ETOKENS);
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
