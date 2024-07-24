package org.example.api;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.example.Main;
import org.jetbrains.annotations.NotNull;

public abstract class Enchant {

    String id;
    int max_level;
    double base_chance;
    double chance_increase;
    String name;
    String displayname;
    float proc_chance;

    String description;

    Material material;

    boolean toggle;

    Main plugin;

    protected Enchant(@NotNull String id, int max_level, double base_chance, double chance_increase, String displayname, String description) {
        this.id = id;
        this.max_level = max_level;
        this.base_chance = base_chance;
        this.chance_increase = chance_increase;
        this.displayname = displayname;
        this.description = description;
    }

    protected  Enchant(Main plugin){
        this.plugin = plugin;

    }

    public String getId() {
        return id;
    }

    public int getMax_level() {
        return max_level;
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

    public abstract void execute(int level);

    public double calculateProcChance(int level) {
        return base_chance + (chance_increase * (level - 1));
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

    public void onBlockBreak(BlockBreakEvent event){

    }


}