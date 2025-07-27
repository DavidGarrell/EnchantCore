package org.example.api;

import org.bukkit.entity.Player;

public class Scroll {

    int duration;
    int level_bonus;
    Enchant enchant;
    int tier;

    public Scroll(int duration, int level_bonus, Enchant enchant, int tier) {
        this.duration = duration;
        this.level_bonus = level_bonus;
        this.enchant = enchant;
        this.tier = tier;
    }

    public int getDuration() {
        return duration;
    }
    public void decreaseDuration() {
        this.duration -= 1;
        if (this.duration < 0) {
            this.duration = 0;
        }
    }
    public void sendExpiredMessage(Player player) {
        player.sendMessage("§cYour " + enchant.getDisplayname() + " §cscroll has expired!");
    }

    public int getLevelBonus() {
        return level_bonus;
    }
}
