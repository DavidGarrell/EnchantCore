package org.example.api.perks;

public class ActivePerk {
    private final PerkTemplate template;
    private final int level; // 1-5

    public ActivePerk(PerkTemplate template, int level) {
        this.template = template;
        this.level = level;
    }

    public PerkTemplate getTemplate() { return template; }
    public int getLevel() { return level; }

    // Berechnet den Buff-Wert für einen bestimmten Perk-Typ
    public double getBuffValue(PerkType type) {
        if (!template.getAffectedTypes().contains(type)) {
            return 0.0; // Dieser Perk beeinflusst diesen Typ nicht
        }

        if (type == PerkType.ENCHANT_PROC_BOOST) {
            // Annahme: +2% auf die finale Proc-Rate pro Level
            return level * 0.05;
        } else {
            // Annahme: +5% auf Money/Tokens/XP pro Level
            return level * 0.5;
        }
    }

}
