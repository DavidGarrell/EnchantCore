package org.example.items;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.example.Main;

import java.util.ArrayList;
import java.util.List;

public class GemstoneBox {

    private final int tier;

    public GemstoneBox(int tier) {
        this.tier = tier;
    }

    /**
     * Creates the physical ItemStack for this Gemstone Box.
     * It includes NBT data to make it uniquely identifiable.
     * @return The ItemStack representing the Gemstone Box.
     */
    public ItemStack getItemStack() {
        // Du kannst das Material anpassen, Ender Chests sind eine beliebte Wahl.
        ItemStack boxItem = new ItemStack(Material.ENDER_CHEST);
        ItemMeta meta = boxItem.getItemMeta();

        if (meta != null) {
            meta.setDisplayName("§aGemstone Box §7(Tier " + this.tier + ")");

            List<String> lore = new ArrayList<>();
            lore.add("§7Contains a random gemstone corresponding");
            lore.add("§7to the tier of this box.");
            lore.add("");
            lore.add("§eRight-click to open!");
            meta.setLore(lore);

            // --- WICHTIG: NBT-Daten zur Identifizierung ---
            NamespacedKey key = new NamespacedKey(Main.getInstance(), "gemstone_box_tier");
            meta.getPersistentDataContainer().set(key, PersistentDataType.INTEGER, this.tier);

            boxItem.setItemMeta(meta);
        }
        return boxItem;
    }
}
