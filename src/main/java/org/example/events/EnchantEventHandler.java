package org.example.events;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class EnchantEventHandler implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onMineBlockBreak(EnchantBlockBreakEvent event) {
        if (event.isInMine()) {
            System.out.println(Bukkit.getServer().getServerTickManager().getTickRate());
            System.out.println("Block is in the mine!");
        } else {
            System.out.println(Bukkit.getServer().getServerTickManager().getTickRate());
            System.out.println("Block is not in the mine.");
        }
        if(event.isServerLagging()){
            System.out.println("Lagging");
        }
    }
   
}
