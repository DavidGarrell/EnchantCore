package org.example.menu;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.example.Main;
import org.example.api.Enchant;
import org.example.api.EnchantUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class EnchantMenu {

    Main plugin;
    public EnchantMenu(Main plugin){
        this.plugin = plugin;
    }

    EnchantUtils enchantUtils;


    public Inventory enchantMenu(Player player){

        UUID uuid = player.getUniqueId();

        enchantUtils = plugin.enchantUtils;

        Inventory inventory = Bukkit.createInventory(null, 27);

        for(Enchant enchant : getEnchants()) {
            if (!enchant.isToggle()) {
                ItemStack itemStack = new ItemStack(enchant.getMaterial());
                ItemMeta itemMeta = itemStack.getItemMeta();
                assert itemMeta != null;
                itemMeta.setDisplayName(enchant.getDisplayname());
                ArrayList<String> lore = new ArrayList<>();
                lore.add("level: " + enchantUtils.getEnchantLevel(uuid, enchant.getId()));
                lore.add("chance: " + enchantUtils.getProcChance(uuid, enchant.getId()));
                itemMeta.setLore(lore);
                itemStack.setItemMeta(itemMeta);

                inventory.addItem(itemStack);
            }
        }

        return inventory;
    }

    public List<Enchant> getEnchants () {
        return plugin.enchants;
    }

}
