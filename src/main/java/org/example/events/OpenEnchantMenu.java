package org.example.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.example.Main;
import org.example.menu.EnchantMenu;

public class OpenEnchantMenu implements Listener {

    Main plugin;

    public OpenEnchantMenu(Main plugin){
        this.plugin = plugin;
    }

    @EventHandler

    public void openMenu (PlayerJoinEvent event){


    }
}
