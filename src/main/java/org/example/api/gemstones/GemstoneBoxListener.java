package org.example.api.gemstones;

import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.example.Main;
import org.example.api.Enchant;
import org.example.api.UtilPlayer; // Wichtiger Import
import org.example.api.gemstones.Gemstone;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GemstoneBoxListener implements Listener {

    private final Main plugin;
    private final Random random = new Random();

    public GemstoneBoxListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBoxOpen(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        ItemStack itemInHand = event.getItem();
        if (itemInHand == null || !itemInHand.hasItemMeta()) {
            return;
        }

        NamespacedKey key = new NamespacedKey(plugin, "gemstone_box_tier");
        ItemMeta meta = itemInHand.getItemMeta();

        if (meta.getPersistentDataContainer().has(key, PersistentDataType.INTEGER)) {
            event.setCancelled(true);
            Player player = event.getPlayer();
            int tier = meta.getPersistentDataContainer().get(key, PersistentDataType.INTEGER);
            itemInHand.setAmount(itemInHand.getAmount() - 1);
            giveRandomGemstone(player, tier);
        }
    }

    /**
     * Generiert einen zufälligen Edelstein und gibt ihn dem Spieler.
     * Holt die Liste der möglichen Enchants direkt aus dem UtilPlayer-Objekt.
     *
     * @param player Der Spieler, der die Belohnung erhält.
     * @param tier Das Tier des zu erstellenden Edelsteins.
     */
    private void giveRandomGemstone(Player player, int tier) {
        // --- HIER IST DIE ANPASSUNG ---
        // 1. Hole das UtilPlayer-Objekt
        UtilPlayer utilPlayer = UtilPlayer.getPlayer(player.getUniqueId());
        if (utilPlayer == null) {
            player.sendMessage("§cError: Could not load your player data.");
            return;
        }

        // 2. Hole die Liste der Enchants direkt vom Spieler
        List<Enchant> possibleEnchants = utilPlayer.getEnchants();
        if (possibleEnchants.isEmpty()) {
            player.sendMessage("§cError: No enchantments are registered for you to create a gemstone from!");
            return;
        }
        // --- ENDE DER ANPASSUNG ---

        // Der Rest der Logik bleibt gleich
        Enchant randomEnchant = possibleEnchants.get(random.nextInt(possibleEnchants.isEmpty() ? 1 : possibleEnchants.size()));
        Gemstone newGem = new Gemstone(randomEnchant, tier);

        utilPlayer.getGemstoneStorage().addGemstone(newGem);

        player.playSound(player.getLocation(), Sound.BLOCK_CHEST_OPEN, 1f, 1.2f);
    }
}