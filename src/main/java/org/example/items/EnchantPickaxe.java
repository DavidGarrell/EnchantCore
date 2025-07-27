package org.example.items;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.example.Main;
import org.example.api.Enchant;
import org.example.api.UtilPlayer;
import org.example.api.gemstones.Gemstone;
import org.example.api.gemstones.GemstoneSlot;
import org.example.menu.EnchantMenu;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class EnchantPickaxe implements Listener {

    static Main plugin;

    public EnchantPickaxe(Main plugin) {
        this.plugin = plugin;
    }

    private List<Enchant> enchants;



    @EventHandler

    public void onJoin(PlayerJoinEvent e){

        Player player = e.getPlayer();

        ItemStack item = new ItemStack(Material.DIAMOND_PICKAXE);
        getUpdatedPickaxeMeta(item, player);
        player.getInventory().setItem(0, item);



    }

    @EventHandler

    public void onRightClickEnchantPickaxe(PlayerInteractEvent e){

        Player player = e.getPlayer();

        EnchantMenu enchantMenu = new EnchantMenu(plugin);

        if(player.getInventory().getItemInMainHand().getType() == Material.DIAMOND_PICKAXE){
            if(e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK)){
                player.openInventory(enchantMenu.enchantMenu(player));
            }
        }
    }

    public static void getUpdatedPickaxeMeta(ItemStack pickaxe, Player player) {
        UtilPlayer utilPlayer = UtilPlayer.getPlayer(player.getUniqueId());
        if (utilPlayer == null) {
            return;
        }

        // Basis-Item

        ItemMeta meta = pickaxe.getItemMeta();
        if (meta == null) {
            meta = plugin.getServer().getItemFactory().getItemMeta(Material.DIAMOND_PICKAXE);
        }

        // --- NAME UND ALLGEMEINE LORE ---
        meta.setDisplayName("§e§l" + player.getName() + "§e§l's §f§lPickaxe");
        ArrayList<String> lore = new ArrayList<>();
        lore.add("");

        // --- STATISTIKEN-SEKTION ---
        lore.add("§6§lStatistics");
        // Du müsstest diese Getter in deiner UtilPlayer-Klasse haben
        //lore.add(" §6§l| §fLevel: §e" + utilPlayer.getPickaxeLevel() + " " + getPrestigeStars(utilPlayer.getPrestigeLevel()));
        lore.add(" §6§l| §fBlocks Broken: §e" + formatNumber(utilPlayer.getBlocksBroken()));
        lore.add(" §6§l| §fLevel: §e" + utilPlayer.pickaxeLeveling.getLevel());
        lore.add(" §6§l| §fXP: §e" + utilPlayer.pickaxeLeveling.getCurrentXp().intValue() + "§7/§e" + utilPlayer.pickaxeLeveling.getXpForNextLevel().intValue());

        if(utilPlayer.getActivePerk()!= null) {
            lore.add(" §6§l| §fActive Perk: " + utilPlayer.getActivePerk().getTemplate().getRarity().getColor() + (toRoman(utilPlayer.getActivePerk().getLevel())) + " " + utilPlayer.getActivePerk().getTemplate().getName());
        } else {
            lore.add(" §6§l| §fActive Perk: §7None");
        }
        lore.add("");
        // --- GEMSTONES-SEKTION ---
        lore.add("§d§lGemstones");
        List<GemstoneSlot> gemstoneSlots = utilPlayer.getGemstoneSlots();
        if (gemstoneSlots.isEmpty()) {
            lore.add(" §d§l| §7No Gemstone Slots unlocked.");
        } else {
            for (GemstoneSlot slot : gemstoneSlots) {
                Gemstone gem = slot.getGemstone();
                if (gem != null) {
                    lore.add(" §d§l| §fSlot " + slot.getId() + ": §a" + gem.getEnchant().getDisplayname().substring(4) + " §7(T" + gem.getTier() + ")");
                } else {
                    lore.add(" §d§l| §fSlot " + slot.getId() + ": §8Empty");
                }
            }
        }
        lore.add("");

        // --- ENCHANTMENTS-SEKTION ---
        lore.add("§b§lEnchantments");
        for (Enchant enchant : utilPlayer.enchants) {
            if(enchant.getLevel()>0) {
                if (enchant.getScroll() != null) {
                    lore.add(" §b§l| §f" + enchant.getDisplayname().substring(4) + " §a" + enchant.getLevel() + " §7(+" + enchant.getScroll().getLevelBonus() + "§7 bonus)");
                } else {
                    lore.add(" §b§l| §f" + enchant.getDisplayname().substring(4) + " §a" + enchant.getLevel());
                }
            }
        }
        lore.add("");

        // --- FOOTER ---

        meta.setLore(lore);

        // --- NBT-TAG HINZUFÜGEN, UM DAS ITEM ZU IDENTIFIZIEREN ---
        NamespacedKey key = new NamespacedKey(Main.getInstance(), "custom_pickaxe");
        meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, player.getUniqueId().toString());

        // Unzerbrechlich machen
        meta.setUnbreakable(true);

        pickaxe.setItemMeta(meta);
    }

    private String getPrestigeStars(int prestigeLevel) {
        if (prestigeLevel == 0) return "";
        StringBuilder stars = new StringBuilder("§f(");
        for (int i = 0; i < prestigeLevel; i++) {
            stars.append("§e✯");
        }
        stars.append("§f)");
        return stars.toString();
    }
    private static String toRoman(int number) {
        switch (number) {
            case 1: return "I";
            case 2: return "II";
            case 3: return "III";
            case 4: return "IV";
            case 5: return "V";
            default: return String.valueOf(number);
        }
    }

    private static String formatNumber(long number) {
        return NumberFormat.getNumberInstance(Locale.US).format(number);
    }
}


