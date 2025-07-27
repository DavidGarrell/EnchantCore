package org.example.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.example.Main;
import org.example.api.UtilPlayer;
import org.example.api.gemstones.Gemstone;

public class PlayerJoinEvents implements Listener {

    Main plugin;

    public PlayerJoinEvents(Main plugin){
        this.plugin = plugin;
    }


    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        UtilPlayer.registerPlayer(player.getUniqueId(), event, (utilPlayer) -> {

            utilPlayer.tick();

            new BukkitRunnable() {
                @Override
                public void run() {
                    if (player.isOnline()) {
                        utilPlayer.tick();
                    } else {
                        cancel();
                    }
                }
            }.runTaskTimer(Main.getInstance(), 5L, 1);
        });
    }

    @EventHandler

    public void onQuitRegister(PlayerQuitEvent event){
        Player player = event.getPlayer();
    }
}
