package org.example.events;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.example.Main;
import org.example.api.Enchant;
import org.example.api.PlayerData;
import org.example.api.data.DataSource;
import org.example.menu.EnchantMenu;

import java.util.UUID;
import java.util.function.Consumer;

public class PlayerJoinEvents implements Listener {

    Main plugin;

    public PlayerJoinEvents(Main plugin){
        this.plugin = plugin;
    }

    public DataSource dataSource(){
        return plugin.dataSource;
    }

    @EventHandler

    public void onJoinRegister(PlayerJoinEvent event){
        Player player = event.getPlayer();
        dataSource().addPlayer(player);
        EnchantMenu enchantMenu = new EnchantMenu(plugin);
        event.getPlayer().openInventory(enchantMenu.enchantMenu(event.getPlayer()));
    }

    @EventHandler

    public void onQuitRegister(PlayerQuitEvent event){
        Player player = event.getPlayer();
        dataSource().removeOnlinePlayer(player);
    }
}
