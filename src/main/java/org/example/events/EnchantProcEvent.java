package org.example.events;

import org.apfloat.Apfloat;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.example.Main;
import org.example.api.Enchant;
import org.example.api.UtilPlayer;
import org.example.api.milestones.Milestone;
import org.example.api.milestones.MilestoneConfigManager;
import org.example.enchantments.FortuneEnchant;
import org.example.enchantments.JackHammerEnchant;
import org.example.items.EnchantPickaxe;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EnchantProcEvent implements Listener {

    Main instance;
    private final MilestoneConfigManager milestoneManager;

    public EnchantProcEvent(Main instance) {
        this.instance = instance;
        this.milestoneManager = instance.getMilestoneConfigManager();
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (event instanceof EnchantBlockBreakEvent) {
            return;
        }

        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        EnchantBlockBreakEvent enchantEvent = new EnchantBlockBreakEvent(event.getBlock(), player, true);
        Bukkit.getServer().getPluginManager().callEvent(enchantEvent);

        if (!enchantEvent.isInMine() || enchantEvent.isServerLagging()) {
            return;
        }

        UtilPlayer utilPlayer = UtilPlayer.getPlayer(uuid);
        utilPlayer.incrementBlocksBroken();
        utilPlayer.pickaxeLeveling.addXp(player, new Apfloat(1));


        for (Enchant enchant : UtilPlayer.getPlayer(uuid).enchants) {
            if (enchant.getLevel() <= 0) continue;

            if(enchant.getScroll()!= null) {
                enchant.handleScrollExpiration(player);
            }

            int blocksToProc = Math.max(1, enchant.getChance(utilPlayer));
            double procChance = 1.0 / blocksToProc;
            double roll = Math.random();

            if (roll < procChance) {
                enchant.execute(event.getBlock().getLocation(), player);
                enchant.addProcCounter();
                // enchant.sendProcMessage(player);

                /*
                * handle Milestone unlock Message                 * Check for milestones and send notifications
                 */

                for (Milestone milestone : milestoneManager.getMilestonesFor(enchant.getId())) {
                    if (enchant.getProc_counter() == milestone.getRequiredProcCount()) {
                        milestone.sendMilestoneNotification(player, enchant, milestone);
                    }
                }
            }
        }
    }
}
