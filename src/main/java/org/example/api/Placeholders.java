package org.example.api;

public class Placeholders {


        public static final String PREFIX = "§e§lEnchant §f| ";
        public static final String GEMSTONE_PREFIX = "§e§lGemstone §f| ";
        public static final String ENCHANT_NOT_ENOUGH = PREFIX + "§cYou do not have enough ETokens to purchase this enchantment.";
        public static final String ENCHANT_CANNOT_BE_PRESTIGED = PREFIX + "§cThis enchantment cannot be prestiged.";
        public static final String ENCHANT_CAN_NOT_BE_PRESTIGED_LEVEL = PREFIX + "§cThis enchantment cannot be prestiged at this level.";

        public static String getEnchantBuyMessage(int levels, String enchantName, String price) {
                // Wähle das korrekte Wort (Singular oder Plural) basierend auf der Anzahl
                String levelWord = (levels == 1) ? "level" : "levels";

                // Baue die Nachricht zusammen
                return PREFIX + "§7Successfully purchased §b" + levels + "§7 " + levelWord + " of §b" + enchantName + "§7 for §b" + price + "§7 ETokens.";
        }
        public static String getEnchantPrestigeMessage(String enchantName, String level) {
                return PREFIX + "§7Successfully prestiged §b" + enchantName + " to Prestige Level §b" + level;
        }
    }

