package org.example.events;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.example.Main;
import org.example.api.EnchantUtils;

import java.util.UUID;

public class EnchantProcEvent implements Listener {

    Main instance;
    EnchantUtils enchantUtils;

    public EnchantProcEvent(Main instance) {
        this.instance = instance;

    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        // Check if the event is already an instance of MineBlockBreakEvent
        if (event instanceof EnchantBlockBreakEvent) {
            return;
        }
        enchantUtils = instance.enchantUtils;
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        boolean isInMine = enchantUtils.checkIfBlockIsMineBlock(event.getBlock().getLocation());
        EnchantBlockBreakEvent enchantEvent = new EnchantBlockBreakEvent(event.getBlock(), player, isInMine);
        Bukkit.getServer().getPluginManager().callEvent(enchantEvent);
        if (!enchantEvent.isInMine()) {
            return;
        }
        if (!enchantEvent.isServerLagging()) {
            return;
        }

        double random = 0 + Math.random() * (100 - 0);
        if (random <= enchantUtils.getProcChance(uuid, "fortune") && enchantUtils.getEnchantLevel(uuid, "fortune") >= 1) {
            System.out.println("Fortune proc");
        }
    }

}
