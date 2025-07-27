package org.example.api.gemstones;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.example.Main;
import org.example.api.Enchant;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class Gemstone {

    private final Enchant enchant;
    private final int tier;
    private final float buffValue; // Stored as a percentage, e.g., 15.5 for 15.5%

    // --- NEUER, INTELLIGENTER KONSTRUKTOR ---
    /**
     * Creates a new Gemstone with a randomized buff value based on its tier.
     * @param enchant The enchant type for this gemstone.
     * @param tier The tier of the gemstone (1, 2, 3...).
     */
    public Gemstone(Enchant enchant, int tier) {
        this.enchant = enchant;
        this.tier = tier;
        this.buffValue = generateBuffValueForTier(tier);
    }

    // --- Alter Konstruktor, jetzt privat, um Konsistenz zu gewährleisten ---
    /**
     * Private constructor for loading existing gemstones from data storage.
     * @param enchant The enchant type.
     * @param tier The tier.
     * @param buffValue The specific, pre-existing buff value.
     */
    private Gemstone(Enchant enchant, int tier, float buffValue) {
        this.enchant = enchant;
        this.tier = tier;
        this.buffValue = buffValue;
    }

    // --- NEUE, STATISCHE "FACTORY"-METHODE ---
    /**
     * Use this method to recreate a Gemstone instance when loading from a database or file,
     * as it bypasses the random generation.
     */
    public static Gemstone fromData(Enchant enchant, int tier, float buffValue) {
        return new Gemstone(enchant, tier, buffValue);
    }

    /**
     * Generates a random buff value within the defined range for a given tier.
     * Tier 1: 0-10%
     * Tier 2: 10-20%
     * ...and so on.
     * @param tier The tier of the gemstone.
     * @return A randomized float value for the buff.
     */
    private float generateBuffValueForTier(int tier) {
        if (tier < 1) tier = 1; // Prevent negative ranges

        // Define the range
        float minBuff = (tier - 1) * 10f; // Tier 1 -> 0, Tier 2 -> 10
        float maxBuff = tier * 10f;       // Tier 1 -> 10, Tier 2 -> 20

        // Generate a random float within this range
        // ThreadLocalRandom is the modern, preferred way for random numbers in concurrent environments
        return ThreadLocalRandom.current().nextFloat() * (maxBuff - minBuff) + minBuff;
    }

    // --- Getters ---
    public Enchant getEnchant() { return enchant; }
    public int getTier() { return tier; }
    public float getBuffValue() { return buffValue; }

    // --- Item Generation Methods (angepasst für bessere Anzeige) ---

    private static final DecimalFormat buffFormatter = new DecimalFormat("0.0#");

    public ItemStack getGemstoneItem() {
        ItemStack item = getDraggableItemStack(); // Reuse the main item creation logic
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            // Update lore for the simpler view in the selection menu
            List<String> lore = new ArrayList<>();
            lore.add("§bTier: §f" + tier);
            lore.add("§bBuff: §f+" + buffFormatter.format(buffValue) + "% " + enchant.getDisplayname());
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        return item;
    }

    public ItemStack getDraggableItemStack() {
        ItemStack item = new ItemStack(enchant.getMaterial());
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName("§a" + enchant.getDisplayname() + " Gemstone");

            List<String> lore = new ArrayList<>();
            lore.add("§7A powerful Gemstone that can be");
            lore.add("§7socketed to boost an enchant.");
            lore.add("");
            lore.add("§a§lInformation");
            lore.add(" §a§l| §7Tier: §f" + tier);
            // Formatiere den Buff-Wert auf 1-2 Nachkommastellen für eine saubere Anzeige
            lore.add(" §a§l| §7Buff: §f+" + buffFormatter.format(buffValue) + "%");
            lore.add("");
            lore.add("§6§lAction");
            lore.add(" §6§l| §eDrag & Drop this into a Gemstone Slot.");
            meta.setLore(lore);

            // --- NBT Data (unverändert) ---
            NamespacedKey enchantIdKey = new NamespacedKey(Main.getInstance(), "gemstone_enchant_id");
            NamespacedKey tierKey = new NamespacedKey(Main.getInstance(), "gemstone_tier");
            NamespacedKey buffKey = new NamespacedKey(Main.getInstance(), "gemstone_buff_value");

            meta.getPersistentDataContainer().set(enchantIdKey, PersistentDataType.STRING, enchant.getId());
            meta.getPersistentDataContainer().set(tierKey, PersistentDataType.INTEGER, tier);
            meta.getPersistentDataContainer().set(buffKey, PersistentDataType.FLOAT, buffValue);

            item.setItemMeta(meta);
        }
        return item;
    }
}
