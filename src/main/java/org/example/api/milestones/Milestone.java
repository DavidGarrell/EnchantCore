package org.example.api.milestones;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.example.api.Enchant;
import org.example.events.EnchantProcEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Milestone {
    private final String enchantId;
    private final int level;
    private final long requiredProcCount;
    private final Map<String, Double> buffs;


    public Milestone(String enchantId, int level, long requiredProcCount, Map<String, Double> buffs) {
        this.enchantId = enchantId;
        this.level = level;
        this.requiredProcCount = requiredProcCount;
        this.buffs = buffs != null ? new HashMap<>(buffs) : new HashMap<>();
    }

    public String getEnchantId() {
        return enchantId;
    }


    public int getLevel() {
        return level;
    }

    public long getRequiredProcCount() {
        return requiredProcCount;
    }

    public Map<String, Double> getBuffs() {
        return new HashMap<>(buffs);
    }

    public double getBuffValue(String buffType, double defaultValue) {
        return buffs.getOrDefault(buffType, defaultValue);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Milestone milestone = (Milestone) o;
        return level == milestone.level && requiredProcCount == milestone.requiredProcCount && Objects.equals(buffs, milestone.buffs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(level, requiredProcCount, buffs);
    }

    @Override
    public String toString() {
        return "Milestone{" +
                "level=" + level +
                ", requiredProcCount=" + requiredProcCount +
                ", buffs=" + buffs +
                '}';
    }

    public void sendMilestoneNotification(Player player, Enchant enchant, Milestone milestone) {
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.2f);

        player.sendMessage("");
        player.sendMessage("§8§m---------------------------------");
        player.sendMessage("      §b§lMILESTONE REACHED!");
        player.sendMessage("");
        player.sendMessage(" §fEnchantment: §a" + enchant.getDisplayname());
        player.sendMessage(" §fMilestone Level: §e" + milestone.getLevel());
        player.sendMessage("");

        Map<String, Double> buffs = milestone.getBuffs();
        if (!buffs.isEmpty()) {
            player.sendMessage(" §6§lRewards Unlocked:");
            for (Map.Entry<String, Double> buffEntry : buffs.entrySet()) {
                player.sendMessage("  §e- §f" + formatBuffName(buffEntry.getKey()) + ": §a+" + buffEntry.getValue());
            }
        } else {
            player.sendMessage(" §6§lReward: §aPermanent progress unlocked!");
        }
        player.sendMessage("§8§m---------------------------------");
        player.sendMessage("");
    }

    private String formatBuffName(String buffKey) {
        String[] parts = buffKey.toLowerCase().replace('_', ' ').split(" ");
        StringBuilder formatted = new StringBuilder();
        for (String part : parts) {
            formatted.append(Character.toUpperCase(part.charAt(0))).append(part.substring(1)).append(" ");
        }
        return formatted.toString().trim();
    }
}