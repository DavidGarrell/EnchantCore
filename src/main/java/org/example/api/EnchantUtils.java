package org.example.api;

import org.bukkit.Location;
import org.example.Main;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EnchantUtils {
    private final Map<UUID, Map<String, Integer>> playerEnchantsLevel = new HashMap<>();

    private final Map<UUID, Map<String, Boolean>> playerEnchantsToggle = new HashMap<>();

    Main plugin;
    public EnchantUtils(Main plugin){
        this.plugin = plugin;
    }

    public void addDefaultEnchantment(UUID playerId) {
        playerEnchantsLevel.putIfAbsent(playerId, new HashMap<>());
        for(Enchant enchant : plugin.enchants) {
            if(enchant.getId().equalsIgnoreCase("fortune")) {
                playerEnchantsLevel.get(playerId).put(enchant.getId(), 10);
            } else {
                playerEnchantsLevel.get(playerId).put(enchant.id, 0);
            }
        }
    }

    public int getEnchantLevel(UUID playerId, String id) {
        return playerEnchantsLevel.getOrDefault(playerId, new HashMap<>()).getOrDefault(id, 0);
    }

    public void addEnchantLevel(UUID uuid, String id, int level) {
        playerEnchantsLevel.putIfAbsent(uuid, new HashMap<>());
        Map<String, Integer> enchants = playerEnchantsLevel.get(uuid);

        // Berechne den neuen Level und setze ihn
        int newLevel = enchants.getOrDefault(id, 0) + level;
        enchants.put(id, newLevel);
    }

    public float getProcChance(UUID uuid, String enchantID){
        float chance = 0;
        for(Enchant enchant : plugin.enchants){
            if(enchant.getId().equals(enchantID)) {
                chance = (float) (enchant.base_chance+(enchant.chance_increase*getEnchantLevel(uuid, enchantID)));
            }
        }
        return chance;
    }

    public void registerAllEnchantments(UUID playerId){

    }

    public void togglePlayerEnchant(UUID uuid, String id, boolean toggle){

    }

    public boolean checkIsEnchantLevelMax(UUID uuid, String id){
        for(Enchant enchant : plugin.enchants){
            if(enchant.getId().equals(id)){
                return (enchant.getMax_level()>=getEnchantLevel(uuid, enchant.getId()));
            }
        }
        return false;
    }

    public boolean checkIfBlockIsMineBlock(Location blocklocation){
        //if(blocklocation)
        return false;
    }

    public void resetAllEnchantLevels(UUID uuid){
        playerEnchantsLevel.get(uuid).clear();
    }
}


