package org.example.api.data;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.example.Main;
import org.example.api.PlayerData;
import org.example.api.EnchantUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DataSource {

    Main plugin;

    EnchantUtils enchantUtils;

    public DataSource (Main plugin){
        this.plugin = plugin;
        enchantUtils = plugin.enchantUtils;
    }

    final Map<UUID, PlayerData> players = new HashMap<>();

    final Map<UUID, PlayerData> onlinePlayers = new HashMap<>();
    public PlayerData getPlayer(UUID id) {
        return onlinePlayers.getOrDefault(id, players.get(id));
    }

    public Collection<PlayerData> getPlayers() {
        return players.values();
    }

    public void addPlayer(Player player) {
        UUID uuid = player.getUniqueId();
        if (!players.containsKey(uuid)) {
            players.put(uuid, new PlayerData());
            enchantUtils.addDefaultEnchantment(uuid);
            for (Player p : Bukkit.getServer().getOnlinePlayers())
            {
                p.sendMessage("New player joined: " + player.getName());
            }
            if (!onlinePlayers.containsKey(uuid)) {
                addOnlinePlayer(player);
            }
        } else {
            addOnlinePlayer(player);
            enchantUtils.addEnchantLevel(uuid, "fortune", 2);
            for (Player p : Bukkit.getServer().getOnlinePlayers())
            {
                p.sendMessage("Old player joined: " + player.getName());
            }
        }
    }
    public void addOnlinePlayer(Player player){
        UUID uuid = player.getUniqueId();
        onlinePlayers.put(uuid, players.get(uuid));
    }
    public void removeOnlinePlayer(Player player){
        UUID uuid = player.getUniqueId();
        if(!onlinePlayers.containsKey(uuid)){
            onlinePlayers.remove(uuid);
        }
    }

    public void addEnchantmentsToPlayer(Player player){
    }

    public PlayerData getPlayerData(Player player){
        return players.get(player.getUniqueId());
    }

}
