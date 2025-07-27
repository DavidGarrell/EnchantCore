package org.example.api;

import org.apfloat.Apfloat;
import org.apfloat.ApfloatMath;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.example.Main;
import org.example.api.milestones.Milestone;
import org.example.api.perks.PerkType;
import org.example.economy.Currency;
import org.example.economy.Economy;
import org.example.economy.EconomyService;
import org.jetbrains.annotations.NotNull;

import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public abstract class Enchant {

    private static final double PRESTIGE_CHANCE_PENALTY = 0.05;
    public String id;
    public int max_level;
    public double base_chance;
    public double chance_increase;
    public String name;
    public String displayname;
    public float proc_chance;
    public int level;

    public String description;

    public String proc_message = "proc-message";

    public Material material;

    public boolean toggle;
    Apfloat cost;

    public int blocksToProc;
    public int blocksToProcDecrease;
    public int proc_counter;


    public int prestige;
    public int max_prestige = 5;
    public int unlockLevel;
    public EnchantType enchantType;
    public Scroll scroll;
    public float priceIncrease;

    public List<Milestone> milestones;


    Main plugin;

    protected Enchant(@NotNull String id, int max_level, int unlockLevel, double base_chance, double chance_increase, String displayname, String description, Apfloat cost, float priceIncrease, EnchantType enchantType) {
        this.id = id;
        this.unlockLevel = unlockLevel;
        this.max_level = max_level;
        this.base_chance = base_chance;
        this.chance_increase = chance_increase;
        this.displayname = displayname;
        this.description = description;
        this.cost = cost;
        this.enchantType = enchantType;
        this.milestones = new ArrayList<>();
        this.priceIncrease = priceIncrease;
    }

    protected Enchant(@NotNull String id, int max_level, int blocksToProc, int blocksToProcDecrease, String displayname, String description, Apfloat cost) {
        this.id = id;
        this.max_level = max_level;
        this.blocksToProc = blocksToProc;
        this.blocksToProcDecrease = blocksToProcDecrease;
        this.displayname = displayname;
        this.description = description;
        this.cost = cost;
    }

    protected Enchant(Main plugin){
        this.plugin = plugin;

    }

    public String getId() {
        return id;
    }

    public int getMax_level() {
        return max_level;
    }

    public int getMax_prestige() {
        return max_prestige;
    }

    public double getBase_chance() {
        return base_chance;
    }

    public double getChance_increase() {
        return chance_increase;
    }

    public String getName() {
        return name;
    }

    public String getDisplayname() {
        return displayname;
    }

    public float getProc_chance() {
        return proc_chance;
    }

    public void setDescription(String description){
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public abstract void execute(Location location, Player player);

    public double calculateProcChance(int level, UtilPlayer utilPlayer) {
        double chanceAfterLevel = base_chance - (chance_increase * (level + getBonusLevels()));

        int prestigeLevel = getPrestige();
        if (prestigeLevel > 0) {
            double prestigeMultiplier = 1-(PRESTIGE_CHANCE_PENALTY * prestigeLevel);

            chanceAfterLevel *= prestigeMultiplier;
        }

        chanceAfterLevel *= (double) 1-(utilPlayer.getActivePerkValue(PerkType.ENCHANT_PROC_BOOST));
        System.out.println(1-(utilPlayer.getRiftProgression().getBuff("enchant_boost").calculateValue(utilPlayer.getRiftProgression().getCurrentDimension()).doubleValue()/100));

        return (int) Math.max(1.0, Math.round(chanceAfterLevel));
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public boolean isToggle(){
        return toggle;
    }

    public void setToggle(Boolean toggle){
        this.toggle = toggle;
    }

    public Apfloat getCost() {
        return cost;
    }

    public void setCost(Apfloat cost) {
        this.cost = cost;
    }

    public void onBlockBreak(BlockBreakEvent event){

    }public EnchantType getEnchantType() {
        return enchantType;
    }

    public void sendProcMessage(Player player) {
        String message = getDisplayname() + " " + proc_message;
        player.sendMessage(message);
    }

    public int getBlocksToProc(){
        return blocksToProc - (blocksToProcDecrease * level);
    }

    public int getBlocksToProcDecrease() {
        return blocksToProcDecrease;
    }

    public void setBlocksToProcDecrease(int blocksToProcDecrease) {
        this.blocksToProcDecrease = blocksToProcDecrease;
    }

    public int getLevel() {
        return level;
    }

    public int getProc_counter() {
        return proc_counter;
    }

    public int getPrestige() {
        return prestige;
    }
    public void upgrade(int level) {
        if (this.level + level > max_level) {
            return;
        }
        this.level += level;
    }
    public void prestige(){
        if(prestige >= max_prestige || level<max_level){
            return;
        }
        this.level = 1;
        this.prestige++;
    }
    public Apfloat calculateCost(int levelsToBuy) {
        if (levelsToBuy <= 0) {
            return Apfloat.ZERO;
        }

        final Apfloat q = new Apfloat(priceIncrease);
        final Apfloat prestigeMultiplier = ApfloatMath.pow(new Apfloat("1.5"), new Apfloat(getPrestige()));
        final Apfloat K = cost.multiply(prestigeMultiplier);
        long currentEffectiveLevel = getLevel();
        int m = levelsToBuy;
        Apfloat q_pow_m = ApfloatMath.pow(q, m);
        Apfloat numerator = q_pow_m.subtract(Apfloat.ONE);
        Apfloat denominator = q.subtract(Apfloat.ONE);
        Apfloat a = K.multiply(ApfloatMath.pow(q, currentEffectiveLevel));
        Apfloat totalCostPrecise = a.multiply(numerator).divide(denominator);

        Apfloat totalCostRounded = ApfloatMath.roundToInteger(totalCostPrecise, RoundingMode.CEILING);

        return totalCostRounded;
    }

    public int getMaxAffordableLevels(Player player) {
        Apfloat balance = EconomyService.getBalance(player.getUniqueId(), Currency.ETOKENS);
        int missingLevels = getMissingLevels();

        if (missingLevels <= 0) {
            return 0;
        }

        Apfloat costOfOneLevel = calculateCost(1);
        if (costOfOneLevel.compareTo(balance) > 0) {
            return 0;
        }



        int low = 1;
        int high = missingLevels;
        int bestGuess = 0;

        while (low <= high) {

            int mid = low + (high - low) / 2;

            if (mid == 0) {
                break;
            }
            Apfloat costForMid = calculateCost(mid);


            if (costForMid.compareTo(balance) <= 0) {
                bestGuess = mid;

                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }

        return bestGuess;
    }



    public int getMissingLevels() {
        return max_level - level;
    }

    public int getChance(UtilPlayer utilPlayer) {
        return (int) Math.round(calculateProcChance(level, utilPlayer));
    }

    public void addProcCounter() {
        this.proc_counter++;
    }

    public void addScroll(Scroll scroll) {
        this.scroll = scroll;
    }

    public int getBonusLevels() {
        if (scroll != null) {
            return scroll.level_bonus;
        }
        return 0;
    }

    public void handleScrollExpiration(Player player) {
        if (getScroll().getDuration() > 0) {
            getScroll().decreaseDuration();
        }
        if (getScroll() != null && getScroll().getDuration() <= 0) {
            scroll.sendExpiredMessage(player);
            scroll = null;
        }
    }
    public Scroll getScroll() {
        return scroll;
    }

    public int getUnlockLevel() {
        return unlockLevel;
    }
}