package org.example.events;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.jetbrains.annotations.NotNull;

public class EnchantBlockBreakEvent extends BlockBreakEvent {
    private boolean isInMine;
    private boolean isServerLagging;
    public EnchantBlockBreakEvent(@NotNull Block theBlock, @NotNull Player player, boolean isInMine) {
        super(theBlock, player);
        this.isInMine = isInMine;
    }
    public boolean isInMine() {
        return isInMine;
    }

    public boolean isServerLagging() {
        return Bukkit.getServer().getServerTickManager().getTickRate()<=15;
    }
}
