package org.example.api.perks;

public enum PerkType {
    MONEY_BOOST("Money Boost"),
    ETOKEN_BOOST("EToken Boost"),
    ENCHANT_PROC_BOOST("Enchant Proc Boost"),
    XP_BOOST("XP Boost"),
    RANK_XP_BOOST("Rank XP Boost");

    private final String displayName;

    PerkType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}