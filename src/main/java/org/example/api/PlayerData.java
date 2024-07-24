package org.example.api;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class PlayerData {


    @NotNull
    UUID getUniqueId() {
        return null;
    }

    Player getPlayer() {
        return null;
    }
    public boolean isOnline(Player player){
        return player.isOnline();
    }


}