package org.example.enchantments;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.world.block.BlockType;
import com.sk89q.worldedit.world.block.BlockTypes;
import org.apfloat.Apfloat;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.example.api.Enchant;
import org.example.api.EnchantType;
import org.example.api.UtilPlayer;
import org.example.economy.Currency;
import org.example.economy.Economy;
import org.example.economy.EconomyService;
import org.example.events.EnchantBlockBreakEvent;
import skyblock.api.IslandManager;
import skyblock.main.Main;

import java.util.HashSet;
import java.util.Set;


public class JackHammerEnchant extends Enchant {

    public JackHammerEnchant(){
        super("jackhammer", 300, 0, 400, 1, "§5§lJackhammer", "Test", new Apfloat(5000), 1.05f, EnchantType.ETOKENS);
        setMaterial(Material.HOPPER);
    }
    @Override
    public void execute(Location location, Player player) {
        if (location == null) return;
        sendProcMessage(player);

        IslandManager islandManager = Main.islandManager;
        Location minelocation = islandManager.island.get(player.getUniqueId()).getIslandLocation();

        int startX = minelocation.getBlockX() - islandManager.island.get(player.getUniqueId()).getISLAND_SIZE() / 2;
        int startY = location.getBlockY(); // Verschiebung um die Höhe des Cubes nach unten
        int startZ = minelocation.getBlockZ() - islandManager.island.get(player.getUniqueId()).getISLAND_SIZE() / 2;

        int airBlocks = 0;
        for (int x = startX; x < startX + islandManager.island.get(player.getUniqueId()).getISLAND_SIZE(); x++) {
            for (int z = startZ; z < startZ + islandManager.island.get(player.getUniqueId()).getISLAND_SIZE(); z++) {
                if (minelocation.getWorld().getBlockAt(x, startY, z).getType() == Material.AIR) {
                    airBlocks++;
                }
            }

        }

        EditSession session = WorldEdit.getInstance().getEditSessionFactory().getEditSession(new BukkitWorld(player.getWorld()), -1);
        BlockVector3 vector1 = BlockVector3.at(startX, startY, startZ);
        BlockVector3 vector2 = BlockVector3.at(startX + islandManager.island.get(player.getUniqueId()).getISLAND_SIZE() - 1, startY, startZ + islandManager.island.get(player.getUniqueId()).getISLAND_SIZE() - 1);
        CuboidRegion region = new CuboidRegion(new BukkitWorld(player.getWorld()), vector1, vector2);
        session.setFastMode(true);
        session.disableBuffering();
        BlockType air = BlockTypes.AIR;
        session.setBlocks((Region) region, air.getDefaultState());
        session.flushSession();

        updatePlayerEnvironment(player);

        int blocksBroken = session.getBlockChangeCount() - airBlocks;
        int effectiveness_blocks = (session.getBlockChangeCount() - airBlocks)/20;

        Apfloat tokensGained = TokenCalculator.calculateTokensGained(UtilPlayer.getPlayer(player.getUniqueId()), blocksBroken);
        EconomyService.addBalance(player.getUniqueId(), Currency.ETOKENS, tokensGained);

        Apfloat moneyGained = MoneyCalculator.calculateMoneyGained(UtilPlayer.getPlayer(player), blocksBroken);
        EconomyService.addBalance(player.getUniqueId(), Currency.MONEY, MoneyCalculator.calculateMoneyGained(UtilPlayer.getPlayer(player), blocksBroken));


        player.sendMessage("§5§lJackhammer §7» §aYou received " + Economy.format(tokensGained) + " ETokens and " + Economy.format(moneyGained) + " Money for breaking blocks!");

    }

    @Override
    public void setMaterial(Material material){
        super.setMaterial(material);
    }

    public void updatePlayerEnvironment(Player player) {
        World world = player.getWorld();
        Location playerLocation = player.getLocation();
        int radius = 10; // Radius um den Spieler herum, den du aktualisieren möchtest

        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    Location blockLocation = playerLocation.clone().add(x, y, z);
                    Block block = blockLocation.getBlock();
                    block.getState().update(true, false);
                }
            }
        }
    }

    public Apfloat calculateTokensGained(UtilPlayer utilPlayer, int blocksBroken) {
        TokenMiner tokenMiner = (TokenMiner) utilPlayer.getEnchantByName("tokenminer");
        if (tokenMiner == null) {
            return new Apfloat(0);
        }
        Apfloat tokensPerBlock = tokenMiner.getTokensPerBlock();
        return tokensPerBlock.multiply(new Apfloat(blocksBroken));

    }
}
