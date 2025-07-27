package org.example.economy;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.EnumMap;
import java.util.UUID;

public class PlayerBalanceInitializer implements Listener {

    Economy economy;

    /**
     * Constructs a PlayerBalanceInitializer.
     *
     * @param economy The API of Economy.
     */
    public PlayerBalanceInitializer(Economy economy) {
        this.economy = economy;
    }

    /**
     * Initializes a new player's balance when they join the server for the first time.
     *
     * @param event The player join event.
     */
    @EventHandler
    public void onPlayerJoin(final PlayerJoinEvent event) {
        final UUID playerUUID = event.getPlayer().getUniqueId();
        economy.getPlayerBalances().putIfAbsent(playerUUID, new EnumMap<>(Currency.class));
    }
}
