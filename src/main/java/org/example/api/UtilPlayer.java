package org.example.api;

import org.apfloat.Apfloat;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.example.Main;
import org.example.api.gemstones.Gemstone;
import org.example.api.gemstones.GemstoneSlot;
import org.example.api.gemstones.GemstoneStorage;
import org.example.api.perks.ActivePerk;
import org.example.api.perks.PerkType;
import org.example.economy.Currency;
import org.example.economy.Economy;
import org.example.economy.EconomyService;
import org.example.enchantments.*;
import org.example.items.EnchantPickaxe;
import org.example.rankupSystem.DimensionalRift;
import org.example.scoreboard.ScoreboardManager;

import java.util.*;
import java.util.function.Consumer;

public class UtilPlayer {


    public static HashMap<UUID, UtilPlayer> playerRegistry = new HashMap<>();
    public List<Enchant> enchants;
    final Player player;
    public List<GemstoneSlot> gemstoneSlots = new ArrayList<>();
    public int blocksBroken = 0;
    private int tickCount = 0;  // Keep track of the number of ticks
    private static DimensionalRift riftProgression;
    final UUID uuid;
    private ScoreboardManager scoreboardManager;
    public PickaxeLeveling pickaxeLeveling;
    private GemstoneStorage gemstoneStorage;// Default pickaxe level


    public UtilPlayer() {
        throw new UnsupportedOperationException("Cannot instantiate an instance of UtilPlayer - Use UtilPlayer#getPlayer instead.");

    }

    public void tick() {
        tickCount++;

        /**
         * Update Player Actionbar
         */

        if (tickCount % 2 == 0) {
            scoreboardManager.updateScoreboard();
            player.setFoodLevel(30);

        }

        if( tickCount % 20 == 0) {
            ItemStack item = player.getInventory().getItemInMainHand();
            NamespacedKey key = new NamespacedKey(Main.getInstance(), "custom_pickaxe");

            if(item.getItemMeta().getPersistentDataContainer().has(key, PersistentDataType.STRING)) {
                EnchantPickaxe.getUpdatedPickaxeMeta(item, player);
            }
        }

        /**
         * Save Player Data in the Database
         */
        if (tickCount % 50 == 0) {
            int affordableLayers = getRiftProgression().getMaxAffordableLayers(player);
            if(affordableLayers>0) {
                Apfloat cost = getRiftProgression().calculateCostForLayers(affordableLayers);
                if (EconomyService.subtractBalance(player.getUniqueId(), Currency.MONEY, cost)) {
                    getRiftProgression().setCurrentLayer(getRiftProgression().getCurrentLayer() + affordableLayers);
                    player.sendMessage("§aSuccessfully purchased " + affordableLayers + " layers!");
                }
            }

        }

    }

    public static UtilPlayer getPlayer(Player player) {
        return playerRegistry.get(player.getUniqueId());
    }

    public static UtilPlayer getPlayer(UUID uuid) {
        return playerRegistry.get(uuid);
    }
    public UUID getBukkitPlayersUUID() {
        return uuid;
    }

    public static void registerPlayer(UUID uuid, PlayerJoinEvent event, Consumer<UtilPlayer> after) {
        playerRegistry.put(uuid, new UtilPlayer(uuid));
        UtilPlayer player = playerRegistry.get(uuid);
        after.accept(player);
    }

    private UtilPlayer(UUID uuid) {
        this.player = Bukkit.getPlayer(uuid);
        this.enchants = new ArrayList<>();
        this.uuid = uuid;
        riftProgression = new DimensionalRift();
        scoreboardManager = new ScoreboardManager(this);
        scoreboardManager.updateScoreboard();
        pickaxeLeveling = new PickaxeLeveling();
        registerEnchantments();

        Gemstone test = new Gemstone(getEnchantByName("fortune"), 1);
        Gemstone test2 = new Gemstone(getEnchantByName("fortune"), 1);
        Gemstone test3 = new Gemstone(getEnchantByName("fortune"), 1);
        Gemstone test4 = new Gemstone(getEnchantByName("fortune"), 1);
        Gemstone test5 = new Gemstone(getEnchantByName("jackhammer"), 9);

        gemstoneStorage = new GemstoneStorage();

        gemstoneStorage.addGemstone(test);
        gemstoneStorage.addGemstone(test2);
        gemstoneStorage.addGemstone(test3);
    }

    private void registerEnchantments(){

        EconomyService.setBalance(player.getUniqueId(), Currency.ETOKENS, new Apfloat(1000000000000000L));

        FortuneEnchant fortuneEnchant = new FortuneEnchant();
        JackHammerEnchant jackHammerEnchant = new JackHammerEnchant();
        LaserEnchant laserEnchant = new LaserEnchant();
        EfficientMiner efficientMiner = new EfficientMiner();
        TokenMiner tokenMiner = new TokenMiner();
        Scroll scroll = new Scroll(100, 5, jackHammerEnchant, 5);
        jackHammerEnchant.addScroll(scroll);

        tokenMiner.upgrade(50);
        jackHammerEnchant.upgrade(25);

        enchants.add(fortuneEnchant);
        enchants.add(jackHammerEnchant);
        enchants.add(laserEnchant);
        enchants.add(efficientMiner);
        enchants.add(tokenMiner);

        for (int i = 0; i < 5; i++) {
            gemstoneSlots.add(new GemstoneSlot(i+1, null));
        }


    }

    public List<GemstoneSlot> getGemstoneSlots() {
        return gemstoneSlots;
    }

    public long getBlocksBroken() {
        return blocksBroken;
    }
    public void incrementBlocksBroken() {
        this.blocksBroken++;
    }

    private ActivePerk activePerk;
    private boolean animationsEnabled = true;


    public boolean areAnimationsEnabled() {
        return this.animationsEnabled;
    }

    public void setAnimationsEnabled(boolean enabled) { // NEU
        this.animationsEnabled = enabled;
    }

    public void setActivePerk(ActivePerk perk) {
        this.activePerk = perk;
    }


    public ActivePerk getActivePerk() {
        return this.activePerk;
    }


    public double getTotalBuff(PerkType type) {
        if (activePerk == null) {
            return 0.0;
        }
        return activePerk.getBuffValue(type);
    }

    public double getActivePerkValue(PerkType perkType) {
        if (activePerk == null) {
            return 0.0;
        }
        return activePerk.getBuffValue(perkType);
    }

    public Enchant getEnchantByName(String enchant) {
        for (Enchant e : enchants) {
            if (e.getId().equalsIgnoreCase(enchant)) {
                return e;
            }
        }
        return null;
    }

    public DimensionalRift getRiftProgression() {
        return this.riftProgression;
    }

    public Player getPlayer() {
        return player;
    }

    public PickaxeLeveling getPickaxeLeveling() {
        return pickaxeLeveling;
    }

    public int getPickaxeLevel() {
        return pickaxeLeveling.getLevel();
    }

    public GemstoneStorage getGemstoneStorage() {
        return gemstoneStorage;
    }

    public List<Enchant> getEnchants() {
        return enchants;
    }
}
