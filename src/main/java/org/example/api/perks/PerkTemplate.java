package org.example.api.perks;

import java.util.List;

// Eine Vorlage für einen möglichen Perk, der gerollt werden kann.
public class PerkTemplate {
    private final String name;
    private final String category; // NEU
    private final String description; // NEU
    private final Rarity rarity;
    private final List<PerkType> affectedTypes;
    private final String textureValue;

    public PerkTemplate(String name, String category, String description, Rarity rarity, List<PerkType> affectedTypes, String textureValue) {
        this.name = name;
        this.category = category; // NEU
        this.description = description; // NEU
        this.rarity = rarity;
        this.affectedTypes = affectedTypes;
        this.textureValue = textureValue;
    }

    public String getName() { return name; }
    public String getCategory() { return category; } // NEU
    public String getDescription() { return description; } // NEU
    public Rarity getRarity() { return rarity; }
    public List<PerkType> getAffectedTypes() { return affectedTypes; }
    public String getTextureValue() { return textureValue; }
}
